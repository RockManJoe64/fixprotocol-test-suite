package org.fixprotocol.fix;

import java.util.HashMap;
import java.util.Map;

import quickfix.MessageFactory;

/**
 * @author chavez.j Sep 4, 2014
 */
public class MessageFactories {

	private Map<String, MessageFactory> factories = new HashMap<String, MessageFactory>();

	public MessageFactory put(String name, MessageFactory factory) {
		return factories.put(name, factory);
	}

	public MessageFactory getMessageFactory(String name) {
		return factories.get(name);
	}

	public MessageFactory getMessageFactory(FIXVersion fixVer) {
		MessageFactory factory = factories.get(fixVer.getBeginString());
		if (factory == null) {
			factory = createStandardMessageFactory(fixVer);
			factories.put(fixVer.getBeginString(), factory);
		}

		return factory;
	}

	private MessageFactory createStandardMessageFactory(FIXVersion fixVer) {
		MessageFactory factory = null;
		switch (fixVer) {
		case FIX40:
			factory = new quickfix.fix40.MessageFactory();
			break;
		case FIX41:
			factory = new quickfix.fix41.MessageFactory();
			break;
		case FIX42:
			factory = new quickfix.fix42.MessageFactory();
			break;
		case FIX43:
			factory = new quickfix.fix43.MessageFactory();
			break;
		case FIX44:
			factory = new quickfix.fix44.MessageFactory();
			break;
		case FIX50:
			factory = new quickfix.fix50.MessageFactory();
			break;
		case FIX50SP1:
			factory = new quickfix.fix50.MessageFactory();
			break;
		case FIX50SP2:
			factory = new quickfix.fix50.MessageFactory();
			break;
		case FIXT11:
			factory = new quickfix.fixt11.MessageFactory();
			break;
		default:
			throw new IllegalArgumentException("Unknown FIX version " + fixVer);
		}

		return factory;
	}

}
