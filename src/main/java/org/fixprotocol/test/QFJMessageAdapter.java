package org.fixprotocol.test;

import quickfix.DataDictionary;
import quickfix.Message;

public class QFJMessageAdapter {

	private Message message;
	private DataDictionary dataDictionary;

	public QFJMessageAdapter(Message message, DataDictionary dataDictionary) {
		this.message = message;
		this.dataDictionary = dataDictionary;
	}

	public FIXMessage convert() {
		return null;
	}

}
