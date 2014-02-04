package org.fixprotocol.test.fix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import quickfix.ConfigError;

public class FIXMessage {

    private List<FIXField> fields = new ArrayList<FIXField>();
    private String messageString;

    public FIXMessage(String messageString) throws ConfigError, IOException {
        this.messageString = messageString;
        this.fields = FIXProtocol.getInstance().getFields(messageString);
    }

    public FIXMessage(List<FIXField> fields) {
        this.fields = fields;
    }

    public List<FIXField> getFields() {
        return fields;
    }

    public void setFields(List<FIXField> fields) {
        this.fields = fields;
    }

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

}
