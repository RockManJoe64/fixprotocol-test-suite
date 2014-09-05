package org.fixprotocol.ui;

import quickfix.DataDictionary;

/**
 * @author chavez.j Sep 4, 2014
 */
public interface DataDictionaryAware {

	public DataDictionary getDataDictionary();

	public void setDataDictionary(DataDictionary dd);

}
