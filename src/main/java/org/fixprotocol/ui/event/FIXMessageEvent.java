package org.fixprotocol.ui.event;

import java.util.EventObject;

import org.fixprotocol.fix.FIXMessage;

/**
 * @author chavez.j
 * Sep 4, 2014
 */
public class FIXMessageEvent extends EventObject {

	private static final long serialVersionUID = 1L;
	private FIXMessage message;

	/**
	 * @param source
	 */
	public FIXMessageEvent(Object source, FIXMessage message) {
		super(source);
		this.message = message;
	}
	
	/**
	 * @return the message
	 */
	public FIXMessage getFIXMessage() {
		return message;
	}

}
