package org.fixprotocol.ui;

import quickfix.MessageFactory;

/**
 * @author chavez.j Sep 4, 2014
 */
public interface MessageFactoryAware {

	public MessageFactory getMessageFactory();

	public void setMessageFactory(MessageFactory dd);

}
