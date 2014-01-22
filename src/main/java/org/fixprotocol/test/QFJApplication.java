package org.fixprotocol.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import quickfix.Acceptor;
import quickfix.ApplicationExtended;
import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.InvalidMessage;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.MessageUtils;
import quickfix.RejectLogon;
import quickfix.RuntimeError;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.UnsupportedMessageType;
import quickfix.field.MsgType;

public class QFJApplication implements ApplicationExtended, SmartLifecycle,
		ApplicationContextAware {

	static final Logger log = LoggerFactory.getLogger(QFJApplication.class);

	private SessionSettings sessionSettings;
	private MessageStoreFactory messageStoreFactory;
	private LogFactory logFactory;
	private MessageFactory messageFactory;
	private Acceptor acceptor;

	private CountDownLatch logonSignal = new CountDownLatch(1);
	private AtomicBoolean loggedOn = new AtomicBoolean(false);
	private BlockingQueue<FIXMessage> queue = new LinkedBlockingQueue<FIXMessage>(
			32);

	private ApplicationContext applicationContext;
	private SessionID sessionID;
	private Session session;

	private DataDictionary dataDictionary;

	/*
	 * Public methods for send/receive
	 */

	public boolean send(FIXMessage fixmsg) {
		return session.send(new FIXMessageAdapter(fixmsg).convert());
	}

	public FIXMessage receive() {
		return receive(0);
	}

	public FIXMessage receive(int seconds) {
		FIXMessage fixmsg = null;
		try {
			fixmsg = seconds <= 0 ? queue.take() : queue.poll(seconds,
					TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error("Interrupted while removing message on queue", e);
		}

		if (fixmsg == null) {
			fixmsg = FIXMessage.UNDEFINED;
		}

		return fixmsg;
	}

	/*
	 * QuickFIXJ Callback methods
	 */

	public void onCreate(SessionID sessionID) {
		log.debug(String.format("onCreate: %s", sessionID));
	}

	public boolean canLogon(SessionID sessionID) {
		return true;
	}

	public void onLogon(SessionID sessionID) {
		log.debug(String.format("onLogon: %s", sessionID));
		loggedOn.set(true);
		logonSignal.countDown();
		this.sessionID = sessionID;
		ScenarioScriptEngine engine = (ScenarioScriptEngine) applicationContext
				.getBean("scriptEngine");
		engine.setBinding("sessionID", this.sessionID);

		session = Session.lookupSession(sessionID);
		dataDictionary = session.getDataDictionary();
	}

	public void onLogout(SessionID sessionID) {
		log.debug(String.format("onLogout: %s", sessionID));
	}

	public void onBeforeSessionReset(SessionID sessionID) {
		log.debug(String.format("onBeforeSessionReset: %s", sessionID));
	}

	public void fromAdmin(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			RejectLogon {
		log.info(String.format("fromAdmin: %s - %s", sessionID, message));
		try {
			String messageType = MessageUtils
					.getMessageType(message.toString());
			if (MsgType.REJECT.equals(messageType)
					|| MsgType.BUSINESS_MESSAGE_REJECT.equals(messageType)) {
				queue.put(new QFJMessageAdapter(message, dataDictionary)
						.convert());
			}
		} catch (InvalidMessage e) {
			log.error("Could not process reject message", e);
		} catch (InterruptedException e) {
			log.error("Interrupted while placing message on queue", e);
		}
	}

	public void toAdmin(Message message, SessionID sessionID) {
		log.info(String.format("toAdmin: %s - %s", sessionID, message));
	}

	public void fromApp(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		log.info(String.format("fromApp: %s - %s", sessionID, message));
		try {
			queue.put(new QFJMessageAdapter(message, dataDictionary).convert());
		} catch (InterruptedException e) {
			log.error("Interrupted while placing message on queue", e);
		}
	}

	public void toApp(Message message, SessionID sessionID) throws DoNotSend {
		log.info(String.format("toApp: %s - %s", sessionID, message));
	}

	/*
	 * LifeCycle methods
	 */

	private boolean running = false;

	public boolean isRunning() {
		return running;
	}

	public void start() {
		try {
			acceptor.start();
			running = true;
		} catch (RuntimeError e) {
			running = false;
			log.error("Could not start QFJ acceptor", e);
		} catch (ConfigError e) {
			running = false;
			log.error("Could not start QFJ acceptor due to configuration", e);
		}
	}

	public void stop() {
		acceptor.stop();
		running = false;
	}

	public int getPhase() {
		return 0;
	}

	public boolean isAutoStartup() {
		return true;
	}

	public void stop(Runnable runnable) {
		runnable.run();
		stop();
	}

	/*
	 * Getter/setters
	 */

	public SessionSettings getSessionSettings() {
		return sessionSettings;
	}

	public void setSessionSettings(SessionSettings sessionSettings) {
		this.sessionSettings = sessionSettings;
	}

	public MessageStoreFactory getMessageStoreFactory() {
		return messageStoreFactory;
	}

	public void setMessageStoreFactory(MessageStoreFactory messageStoreFactory) {
		this.messageStoreFactory = messageStoreFactory;
	}

	public LogFactory getLogFactory() {
		return logFactory;
	}

	public void setLogFactory(LogFactory logFactory) {
		this.logFactory = logFactory;
	}

	public MessageFactory getMessageFactory() {
		return messageFactory;
	}

	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}

	public Acceptor getAcceptor() {
		return acceptor;
	}

	public void setAcceptor(Acceptor acceptor) {
		this.acceptor = acceptor;
	}

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
