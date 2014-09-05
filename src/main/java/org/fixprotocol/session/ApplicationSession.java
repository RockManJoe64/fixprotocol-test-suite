package org.fixprotocol.session;

import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

public class ApplicationSession implements Application {

    @Override
    public void fromAdmin(Message message, SessionID session)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
            RejectLogon {
        // TODO Auto-generated method stub

    }

    @Override
    public void fromApp(Message message, SessionID session)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
            UnsupportedMessageType {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCreate(SessionID session) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLogon(SessionID session) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLogout(SessionID session) {
        // TODO Auto-generated method stub

    }

    @Override
    public void toAdmin(Message message, SessionID session) {
        // TODO Auto-generated method stub

    }

    @Override
    public void toApp(Message message, SessionID session) throws DoNotSend {
        // TODO Auto-generated method stub

    }

}
