package org.fixprotocol.test.fix.ui.list;

import org.fixprotocol.test.fix.FIXMessage;
import org.fixprotocol.test.fix.FIXProtocol;

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

    public FIXMessageListItem(FIXMessage message) {
        this.message = message;
        parse();
    }

    public FIXMessage getMessage() {
        return message;
    }

    private void parse() {
        FIXProtocol fixProtocol = FIXProtocol.getInstance();

        String msgStr = message.toString();
        Field<Object> field = fixProtocol.getField(msgStr, MsgType.FIELD);
        msgType = (String) field.getObject();
        try {
            msgType = FIXProtocol.getMsgTypeDescription(msgStr);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        field = fixProtocol.getField(msgStr, SenderCompID.FIELD);
        senderCompId = (String) field.getObject();

        field = fixProtocol.getField(msgStr, TargetCompID.FIELD);
        targetCompId = (String) field.getObject();

        field = fixProtocol.getField(msgStr, BeginString.FIELD);
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
