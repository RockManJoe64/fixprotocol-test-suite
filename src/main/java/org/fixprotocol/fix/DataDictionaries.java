package org.fixprotocol.fix;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import quickfix.ConfigError;
import quickfix.DataDictionary;

/**
 * @author chavez.j Sep 4, 2014
 */
public class DataDictionaries {

	private Map<String, DataDictionary> dictionaries = new HashMap<String, DataDictionary>();

	public void loadDictionary(String name, File file) throws ConfigError,
			IOException {
		DataDictionary dataDictionary = new DataDictionary(
				new FileInputStream(file));
		dictionaries.put(name, dataDictionary);
	}
	
	public void loadDictionary(String name, String path) throws ConfigError,
			IOException {
		URL resource = FIXProtocol.class.getResource(path);
		DataDictionary dataDictionary = new DataDictionary(
				resource.openStream());
		dictionaries.put(name, dataDictionary);
	}

	public DataDictionary getDictionary(String name) {
		return dictionaries.get(name);
	}

	public DataDictionary getDataDictionary(String name) throws ConfigError,
			IOException {
		name = name.replaceAll("\\.", "");
		DataDictionary dataDictionary = dictionaries.get(name);
		if (dataDictionary == null) {
			String url = "/" + name + ".xml";
			URL resource = FIXProtocol.class.getResource(url);
			dataDictionary = new DataDictionary(resource.openStream());
			dictionaries.put(name, dataDictionary);
		}

		return dataDictionary;
	}

}
