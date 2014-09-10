package org.fixprotocol.ui.list;

import org.apache.commons.lang3.StringUtils;
import org.fixprotocol.fix.FIXMessage;
import org.fixprotocol.fix.FIXProtocol;

import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.field.BeginString;
import quickfix.field.MsgSeqNum;
import quickfix.field.MsgType;
import quickfix.field.SenderCompID;
import quickfix.field.SenderSubID;
import quickfix.field.TargetCompID;
import quickfix.field.TargetSubID;


public class FIXMessageListItem {

    private FIXMessage message;

    private String beginString;
    private String senderCompId;
    private String senderSubId;
    private String targetCompId;
    private String targetSubId;
    private String msgType;
    private String msgTypeDesc;
    private Integer msgSeqNum;
    
    public FIXMessageListItem(FIXMessage message, DataDictionary dataDictionary) {
        this.message = message;
        parse(dataDictionary);
    }
    
	public FIXMessage getMessage() {
        return message;
    }
	
	public void applyDataDictionary(DataDictionary dataDictionary) {
		this.message.applyDataDictionary(dataDictionary);
		parse(dataDictionary);
	}

    private void parse(DataDictionary dataDictionary) {
        FIXProtocol fixProtocol = FIXProtocol.getInstance();

        String msgStr = message.toString();
        msgType = FIXProtocol.getMsgType(msgStr);
        Field<Object> field = fixProtocol.getField(msgStr, MsgType.FIELD, dataDictionary);
        msgTypeDesc = (String) field.getObject();
        try {
            msgTypeDesc = FIXProtocol.getMsgTypeDescription(msgStr);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        field = fixProtocol.getField(msgStr, SenderCompID.FIELD, dataDictionary);
        senderCompId = (String) field.getObject();
        
        field = fixProtocol.getField(msgStr, SenderSubID.FIELD, dataDictionary);
        if (field != null)
        	senderSubId = (String) field.getObject();

        field = fixProtocol.getField(msgStr, TargetCompID.FIELD, dataDictionary);
        targetCompId = (String) field.getObject();
        
        field = fixProtocol.getField(msgStr, TargetSubID.FIELD, dataDictionary);
        if (field != null)
        	targetSubId = (String) field.getObject();

        field = fixProtocol.getField(msgStr, BeginString.FIELD, dataDictionary);
        beginString = (String) field.getObject();
        
        field = fixProtocol.getField(msgStr, MsgSeqNum.FIELD, dataDictionary);
        msgSeqNum = Integer.valueOf((String) field.getObject());
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(beginString).append(":")
        		.append(senderCompId);
        if (StringUtils.isNotBlank(senderSubId))
        	builder.append("/").append(senderSubId);
        builder.append("->").append(targetCompId);
        if (StringUtils.isNotBlank(targetSubId))
        	builder.append("/").append(targetSubId);
        builder.append(":").append(msgSeqNum);
		builder.append(":").append(msgType).append("/").append(msgTypeDesc);

        return builder.toString();
    }

}
