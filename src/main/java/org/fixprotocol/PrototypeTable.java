package org.fixprotocol;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.fixprotocol.fix.FIXMessage;

public class PrototypeTable {

	public PrototypeTable(String prototypeFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(prototypeFile));
		try {
			String line = reader.readLine();
			processHeader(line);

			line = reader.readLine();
			while (line != null) {
				processLine(line);
				line = reader.readLine();
			}
		} finally {
			reader.close();
		}
	}

	private void processHeader(String line) {
		// TODO Auto-generated method stub

	}

	private void processLine(String line) {
		// TODO Auto-generated method stub

	}

	public List<FIXMessage> getPrototypes() {
		return null;
	}

}
