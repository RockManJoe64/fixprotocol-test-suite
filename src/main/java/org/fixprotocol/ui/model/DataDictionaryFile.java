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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.getAbsolutePath().hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof DataDictionaryFile)) return false;
		DataDictionaryFile other = (DataDictionaryFile) obj;
		if (file == null) {
			if (other.file != null) return false;
		} else if (!file.equals(other.file)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		return true;
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
