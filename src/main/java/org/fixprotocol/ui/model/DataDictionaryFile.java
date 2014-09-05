package org.fixprotocol.ui.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import quickfix.ConfigError;
import quickfix.DataDictionary;

/**
 * @author chavez.j Sep 5, 2014
 */
public class DataDictionaryFile {

	private String name;
	private DataDictionary dataDictionary;
	private File file;
	
	public static DataDictionaryFile load(String name, File file)
			throws FileNotFoundException, ConfigError {
		DataDictionary dataDictionary = new DataDictionary(new FileInputStream(
				file));
		return new DataDictionaryFile(name, dataDictionary, file);
	}

	public DataDictionaryFile(String name, DataDictionary dataDictionary,
			File file) {
		this.name = name;
		this.dataDictionary = dataDictionary;
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public DataDictionary getDataDictionary() {
		return dataDictionary;
	}

	public File getFile() {
		return file;
	}

}
