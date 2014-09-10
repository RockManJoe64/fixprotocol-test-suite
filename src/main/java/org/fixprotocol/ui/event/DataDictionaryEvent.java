package org.fixprotocol.ui.event;

import java.util.EventObject;

import quickfix.DataDictionary;

/**
 * @author Jose Chavez <chavez.j@eoxlive.com>
 * @since Sep 9, 2014
 */
public class DataDictionaryEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private DataDictionary dataDictionary;

	/**
	 * @param source
	 */
	public DataDictionaryEvent(Object source, DataDictionary dataDictionary) {
		super(source);
		this.dataDictionary = dataDictionary;
	}
	
	/**
	 * @return the dataDictionary
	 */
	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

}
