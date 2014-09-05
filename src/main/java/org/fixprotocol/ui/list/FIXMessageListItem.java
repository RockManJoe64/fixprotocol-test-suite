package org.fixprotocol.ui.list;

import org.fixprotocol.fix.FIXMessage;
import org.fixprotocol.fix.FIXProtocol;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.field.BeginString;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;


public class FIXMessageListItem {

    private FIXMessage message;

    private String beginString;
    private String senderCompId;
    private String targetCompId;
    private String msgType;

    public FIXMessageListItem(FIXMessage message, DataDictionary dataDictionary) {
        this.message = message;
        parse(dataDictionary);
    }
    
	public FIXMessage getMessage() {
        return message;
    }

    private void parse(DataDictionary dataDictionary) {
        FIXProtocol fixProtocol = FIXProtocol.getInstance();

        String msgStr = message.toString();
        Field<Object> field = fixProtocol.getField(msgStr, MsgType.FIELD, dataDictionary);
        msgType = (String) field.getObject();
        try {
            msgType = FIXProtocol.getMsgTypeDescription(msgStr);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        field = fixProtocol.getField(msgStr, SenderCompID.FIELD, dataDictionary);
        senderCompId = (String) field.getObject();

        field = fixProtocol.getField(msgStr, TargetCompID.FIELD, dataDictionary);
        targetCompId = (String) field.getObject();

        field = fixProtocol.getField(msgStr, BeginString.FIELD, dataDictionary);
        beginString = (String) field.getObject();
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(beginString).append(" ").append(senderCompId)
                .append(" -> ").append(targetCompId).append(": ")
                .append(msgType);

        return builder.toString();
    }

}
