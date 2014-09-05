package org.fixprotocol.ui.event;

import java.util.EventListener;

/**
 * @author chavez.j
 * Sep 4, 2014
 */
public interface FIXMessageListener extends EventListener {
	
	/**
	 * 
	 * @param e
	 */
	public void fixMessageAdded(FIXMessageEvent e);

	/**
	 * 
	 * @param e
	 */
	public void fixMessageUpdated(FIXMessageEvent e);
	
	/**
	 * 
	 * @param e
	 */
	public void fixMessageRemoved(FIXMessageEvent e);
	
}
