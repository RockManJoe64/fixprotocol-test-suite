package org.fixprotocol.test.fix;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
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

    private Map<String, DataDictionary> ddMap = new HashMap<String, DataDictionary>();
    private Map<String, MessageFactory> mfMap = new HashMap<String, MessageFactory>();

    public enum FIXVersion {
        FIX40("FIX.4.0"),
        FIX41("FIX.4.1"),
        FIX42("FIX.4.2"),
        FIX43("FIX.4.3"),
        FIX44("FIX.4.4"),
        FIX50("FIX.5.0"),
        FIX50SP1("FIX.5.0SP1"),
        FIX50SP2("FIX.5.0SP2"),
        FIXT11("FIXT.1.1");

        private String beginString;

        private FIXVersion(String beginString) {
            this.beginString = beginString;
        }

        public String getBeginString() {
            return beginString;
        }
    }

    public static FIXProtocol getInstance() {
        if (instance == null) {
            instance = new FIXProtocol();
        }

        return instance;
    }

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
            dd = getDataDictionary(beginString);
        } catch (ConfigError | IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        if (dd == null) {
            return FieldType.Unknown;
        } else {
            return dd.getFieldTypeEnum(field);
        }
    }

    public Map<Integer, FieldType> getFieldTypes(String messageString) {
        Message message = getMessage(messageString);

        String beginString = getFIXVersion(messageString);
        DataDictionary dd = null;
        try {
            dd = getDataDictionary(beginString);
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
                FieldType fieldType = dd.getFieldTypeEnum(field.getField());
                map.put(field.getField(), fieldType);
            }

            return map;
        }
    }

    public Message getMessage(String messageString) {
        String fixVer = MessageUtils.getStringField(messageString, 8);

        Message message = null;
        try {
            DataDictionary dataDictionary = getDataDictionary(fixVer);
            MessageFactory messageFactory = getMessageFactory(fixVer);
            message = parse(messageFactory, dataDictionary, messageString);
        } catch (ConfigError | IOException | InvalidMessage e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }

        return message;
    }

    public DataDictionary getDataDictionary(String fixVer) throws ConfigError,
            IOException {
        fixVer = fixVer.replaceAll("\\.", "");
        DataDictionary dataDictionary = ddMap.get(fixVer);
        if (dataDictionary == null) {
            String url = "/" + fixVer + ".xml";
            URL resource = FIXProtocol.class.getResource(url);
            dataDictionary = new DataDictionary(resource.openStream());
            ddMap.put(fixVer, dataDictionary);
        }

        return dataDictionary;
    }

    public MessageFactory getMessageFactory(String fixVer) {
        fixVer = fixVer.replaceAll("\\.", "");
        MessageFactory factory = mfMap.get(fixVer);
        if (factory == null) {
            factory = createMessageFactory(fixVer);
            mfMap.put(fixVer, factory);
        }

        return factory;
    }

    private MessageFactory createMessageFactory(String fixVer) {
        MessageFactory factory = null;
        FIXVersion fixVersion = Enum.valueOf(FIXVersion.class, fixVer);
        switch (fixVersion) {
        case FIX40:
            factory = new quickfix.fix40.MessageFactory();
            break;
        case FIX41:
            factory = new quickfix.fix41.MessageFactory();
            break;
        case FIX42:
            factory = new quickfix.fix42.MessageFactory();
            break;
        case FIX43:
            factory = new quickfix.fix43.MessageFactory();
            break;
        case FIX44:
            factory = new quickfix.fix44.MessageFactory();
            break;
        case FIX50:
            factory = new quickfix.fix50.MessageFactory();
            break;
        case FIX50SP1:
            factory = new quickfix.fix50.MessageFactory();
            break;
        case FIX50SP2:
            factory = new quickfix.fix50.MessageFactory();
            break;
        case FIXT11:
            factory = new quickfix.fixt11.MessageFactory();
            break;
        default:
            throw new IllegalArgumentException("Unknown FIX version " + fixVer);
        }

        return factory;
    }

    public List<FIXField> getFields(String messageString) throws ConfigError,
            IOException {
        List<FIXField> list = new ArrayList<FIXField>();

        String fixVersion = getFIXVersion(messageString);
        DataDictionary dataDictionary = getDataDictionary(fixVersion);

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
