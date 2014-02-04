package org.fixprotocol.test.js;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.fixprotocol.test.fix.ASCII;


public class FIXMessage {

    public static final FIXField UNDEFINED = new FIXField();

    private ArrayList<FIXField> list = new ArrayList<FIXField>();
    private HashMap<String, FIXField> map = new HashMap<String, FIXField>();

    public FIXMessage(Collection<FIXField> list) {
        this.list = new ArrayList<FIXField>(list);
        for (FIXField field : list) {
            map.put(field.getName(), field);
            map.put(field.getTag(), field);
        }
    }

    public FIXField get(String key) {
        if (!map.containsKey(key)) { return UNDEFINED; }

        return map.get(key);
    }

    private String createMsg(String delim) {
        String msg = "";
        for (FIXField f : list) {
            msg += f.getTag() + "=" + f.getValue() + delim;
        }

        return msg;
    }

    public String ulm() {
        return createMsg("|");
    }

    public String fix() {
        return createMsg(String.valueOf(ASCII.SOH));
    }

}
