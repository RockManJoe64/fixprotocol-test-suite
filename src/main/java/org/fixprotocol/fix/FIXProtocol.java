package org.fixprotocol.fix;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import quickfix.ConfigError;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldType;
import quickfix.InvalidMessage;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageUtils;
import quickfix.field.BeginString;
import quickfix.field.MsgType;

public class FIXProtocol {

    private static final Logger log = Logger.getLogger(FIXProtocol.class
            .getName());

    private static FIXProtocol instance;

    public static FIXProtocol getInstance() {
        if (instance == null) {
            instance = new FIXProtocol();
        }

        return instance;
    }

    private DataDictionaries dictionaries = new DataDictionaries();
    private MessageFactories factories = new MessageFactories();

    public static String normalize(String messageString) {
        String normalized = messageString.trim();

        // For Aegis Client FIX msgs
        if (normalized.contains("\n")) {
            normalized = normalized.replaceAll("\n", "|");
        }

        if (!normalized.endsWith("|")) {
            normalized = normalized + "|";
        }

        // For FIX msgs from ULBRIDGE logs
        if (normalized.contains("|")) {
            normalized = normalized.replace('|', ASCII.SOH);
        }

        return normalized;
    }

    public static String getFIXVersion(String messageString) {
        return MessageUtils.getStringField(messageString, 8);
    }

    public static String getMsgType(String messageString) {
        try {
            return MessageUtils.getMessageType(messageString);
        } catch (InvalidMessage e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    public static String getMsgTypeDescription(String messageString)
            throws IllegalArgumentException, IllegalAccessException {
        MsgType msgType = new MsgType(getMsgType(messageString));

        java.lang.reflect.Field[] declaredFields = MsgType.class
                .getDeclaredFields();
        for (java.lang.reflect.Field field : declaredFields) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers)
                    && field.getType().equals(String.class)) {
                String value = (String) field.get(null);
                if (msgType.getValue().equals(value)) { return field.getName(); }
            }
        }

        return null;
    }

    public <T> Field<T> getField(String messageString, Integer tag) {
        Message message = getMessage(messageString);

        return getField(message, tag);
    }

    public <T> Field<T> getField(String messageString, Integer tag, DataDictionary dd) {
        Message message = getMessage(messageString, dd);

        return getField(message, tag);
    }

    public <T> Field<T> getField(Message message, Integer tag) {
        if (message == null) { return null; }

        Iterator<Field<?>> iterator = message.iterator();
        Field field = searchField(tag, iterator);
        if (field == null) {
            iterator = message.getHeader().iterator();
            field = searchField(tag, iterator);
            if (field == null) {
                iterator = message.getTrailer().iterator();
                field = searchField(tag, iterator);
            }
        }

        return field;
    }

    private Field searchField(Integer tag, Iterator<Field<?>> iterator) {
        while (iterator.hasNext()) {
            Field next = iterator.next();
            int nextTag = next.getTag();
            if (nextTag == tag.intValue()) { return next; }
        }

        return null;
    }

    public FieldType getFieldType(int field, String beginString) {
        DataDictionary dd = null;
        try {
            dd = dictionaries.getDataDictionary(beginString);
        } catch (ConfigError | IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        if (dd == null) {
            return FieldType.UNKNOWN;
        } else {
            return dd.getFieldType(field);
        }
    }

    public Map<Integer, FieldType> getFieldTypes(String messageString) {
        Message message = getMessage(messageString);

        String beginString = getFIXVersion(messageString);
        DataDictionary dd = null;
        try {
            dd = dictionaries.getDataDictionary(beginString);
        } catch (ConfigError | IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        if (dd == null) {
            return new HashMap<Integer, FieldType>();
        } else {
            HashMap<Integer, FieldType> map = new HashMap<Integer, FieldType>();
            Iterator<Field<?>> it = message.iterator();
            while (it.hasNext()) {
                Field<?> field = it.next();
                FieldType fieldType = dd.getFieldType(field.getField());
                map.put(field.getField(), fieldType);
            }

            return map;
        }
    }

	public Message getMessage(String messageString) {
		String fixVer = MessageUtils.getStringField(messageString, 8);

		Message message = null;
		try {
			DataDictionary dataDictionary = dictionaries
					.getDataDictionary(fixVer);
			MessageFactory messageFactory = factories.getMessageFactory(fixVer);
			message = parse(messageFactory, dataDictionary, messageString);
		} catch (ConfigError | IOException | InvalidMessage e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		return message;
	}

	public Message getMessage(String messageString,
			DataDictionary dataDictionary) {
		String fixVer = MessageUtils.getStringField(messageString, 8);
		FIXVersion fixVersion = FIXVersion.valueOf(fixVer.replaceAll("\\.", ""));
		Message message = null;
		try {
			MessageFactory messageFactory = factories.getMessageFactory(fixVersion);
			message = parse(messageFactory, dataDictionary, messageString);
		} catch (InvalidMessage e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		return message;
	}

	public Message getMessage(String messageString,
			DataDictionary dataDictionary, MessageFactory messageFactory) {
		Message message = null;
		try {
			message = parse(messageFactory, dataDictionary, messageString);
		} catch (InvalidMessage e) {
			log.log(Level.SEVERE, e.getMessage(), e);
		}

		return message;
	}

	public List<FIXField> getFields(String messageString) throws ConfigError,
			IOException {
		String fixVersion = getFIXVersion(messageString);
		return getFields(messageString, fixVersion);
	}

	public List<FIXField> getFields(String messageString, String dataDictName)
			throws ConfigError, IOException {
        DataDictionary dataDictionary = dictionaries.getDataDictionary(dataDictName);
        return getFields(messageString, dataDictionary);
	}

	public List<FIXField> getFields(String messageString, DataDictionary dataDictionary)
			throws ConfigError, IOException {
		List<FIXField> list = new ArrayList<FIXField>();
        String[] tagValuePairs = messageString.split(String.valueOf(ASCII.SOH));
        for (String tvp : tagValuePairs) {
            String[] tv = tvp.split("\\=");
            Integer tag = Integer.valueOf(tv[0]);
            String value = tv[1];
            String name = dataDictionary.getFieldName(tag);
            if (name == null || "".equals(name)) {
                name = "CustomTag";
            }
            FIXField p = new FIXField(name, tag, value);
            list.add(p);
        }

        return list;
	}

    private Message parse(MessageFactory messageFactory,
            DataDictionary dataDictionary, String messageString)
            throws InvalidMessage {
        final int index = messageString.indexOf(ASCII.SOH);
        if (index < 0) { throw new InvalidMessage(
                "Message does not contain any field separator"); }
        final String beginString = MessageUtils.getStringField(messageString,
                BeginString.FIELD);
        final String messageType = MessageUtils.getMessageType(messageString);
        final quickfix.Message message = messageFactory.create(beginString,
                messageType);
        message.fromString(messageString, dataDictionary, false);
        return message;
    }

}
