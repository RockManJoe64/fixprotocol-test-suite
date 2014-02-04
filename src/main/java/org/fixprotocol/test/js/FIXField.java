package org.fixprotocol.test.js;

public class FIXField {

    public static final String UNDEFINED = "UNDEF!";

    private String name;
    private String tag;
    private String value;

    public FIXField(String name, String tag, String value) {
        this.name = name;
        this.tag = tag;
        this.value = value;
    }

    public FIXField() {
        this(UNDEFINED, "0", UNDEFINED);
    }

    public FIXField(String tag, String value) {
        this(UNDEFINED, tag, value);
    }

    public FIXField(org.fixprotocol.test.fix.FIXField field) {
        this(field.getName(), String.valueOf(field.getTag()), field.getValue()
                .toString());
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getValue() {
        return value;
    }

}
