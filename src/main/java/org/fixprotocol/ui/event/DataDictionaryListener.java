package org.fixprotocol.ui.event;

import java.util.EventListener;

/**
 * @author Jose Chavez <chavez.j@eoxlive.com>
 * @since Sep 9, 2014
 */
public interface DataDictionaryListener extends EventListener {

	public void dataDictionaryAdded(DataDictionaryEvent e);
	
	public void dataDictionaryRemoved(DataDictionaryEvent e);
	
	public void dataDictionarySelected(DataDictionaryEvent e);
	
}
