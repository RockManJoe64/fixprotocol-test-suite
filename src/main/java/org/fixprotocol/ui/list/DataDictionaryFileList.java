package org.fixprotocol.ui.list;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JPanel;

import org.fixprotocol.ui.model.DataDictionaryFile;

import quickfix.ConfigError;

/**
 * @author chavez.j
 * Sep 5, 2014
 */
public class DataDictionaryFileList extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DataDictionaryFileList() {
		initUI();
	}

	private void initUI() {
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		String fileList = preferences.get("FileList", null);
		List<DataDictionaryFile> list;
		if (fileList != null)
			list = getDictionaries(fileList);
		else
			list = new ArrayList<DataDictionaryFile>();
	}

	/**
	 * @param fileList
	 * @return
	 */
	private List<DataDictionaryFile> getDictionaries(String fileList) {
		List<DataDictionaryFile> list = new ArrayList<DataDictionaryFile>();
		String[] tokens = fileList.split("\\|");
		for (String string : tokens) {
			String[] kv = string.split("\\=");
			String name = kv[0];
			String filePath = kv[1];
			try {
				DataDictionaryFile item = DataDictionaryFile.load(name, new File(filePath));
				list.add(item);
			} catch (FileNotFoundException | ConfigError e) {
				System.err.println("Could not load dictionary: " + filePath);
			}
		}
		return list;
	}
	
}
