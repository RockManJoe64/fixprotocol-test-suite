package org.fixprotocol.fix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

public class LogParser {

	private final File file;
	private String delimiter = "\\s";
	private int fieldPosition = 1;
	private List<String> messages;

	public LogParser(File file) {
		this.file = file;
	}

	public void parse() {
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line = reader.readLine();
			while (line != null) {
				String[] tokens = line.split(getDelimiter());
				if (tokens.length < fieldPosition) {
					String message = tokens[fieldPosition];
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public int getFieldPosition() {
		return fieldPosition;
	}

	public void setFieldPosition(int fieldPosition) {
		this.fieldPosition = fieldPosition;
	}

	public File getFile() {
		return file;
	}

}
