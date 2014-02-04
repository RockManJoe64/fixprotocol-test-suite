package org.fixprotocol.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.ApplicationExtended;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

public class AbstractApplication extends MessageCracker implements ApplicationExtended {

	static final Logger log = LoggerFactory.getLogger(AbstractApplication.class);

	public void onCreate(SessionID sessionID) {
		log.debug(String.format("onCreate: %s", sessionID));
	}

	public boolean canLogon(SessionID sessionID) {
		return true;
	}

	public void onLogon(SessionID sessionID) {
		log.debug(String.format("onLogon: %s", sessionID));
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
		log.debug(String.format("fromAdmin: %s - %s", sessionID, message));
	}

	public void toAdmin(Message message, SessionID sessionID) {
		log.debug(String.format("toAdmin: %s - %s", sessionID, message));
	}

	public void fromApp(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		log.debug(String.format("fromApp: %s - %s", sessionID, message));
	}

	public void toApp(Message message, SessionID sessionID) throws DoNotSend {
		log.debug(String.format("toApp: %s - %s", sessionID, message));
	}

}
