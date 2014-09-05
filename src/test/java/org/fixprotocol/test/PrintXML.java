package org.fixprotocol.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.DefaultMessageFactory;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageUtils;

public class PrintXML {

	public static void main(String[] args) throws InvalidMessage, ConfigError,
			TransformerFactoryConfigurationError, TransformerException {
		String line = null;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				PrintXML.class.getResourceAsStream("fixmsg.txt")))) {
			line = reader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		DataDictionary dd = new DataDictionary("FIX44.xml");
		DefaultMessageFactory messageFactory = new DefaultMessageFactory();
		Message message = messageFactory.create("FIX.4.4",
				MessageUtils.getMessageType(line));
		message.fromString(line, dd, false);
		String xmlString = message.toXML(dd);

		System.out.println(prettyFormat(xmlString));
	}
	
	public static String prettyFormat(String input, int indent) {
	    try {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	        throw new RuntimeException(e); // simple exception handling, please review it
	    }
	}

	public static String prettyFormat(String input) {
	    return prettyFormat(input, 2);
	}

}
