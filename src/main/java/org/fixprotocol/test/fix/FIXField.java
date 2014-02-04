package org.fixprotocol.test.fix;

import quickfix.FieldType;

/**
 * Only the value field is mutable
 * 
 * @author V594520
 * 
 */
public class FIXField {

    private String name;
    private Integer tag;
    private Object value;
    private FieldType fieldType;

    public FIXField(String name, int tag, Object value) {
        super();
        this.name = name;
        this.tag = tag;
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FIXField [").append(tag).append("(").append(name)
                .append(")=").append(value).append("(").append(fieldType)
                .append(")]");
        return builder.toString();
    }

    public String getName() {
        return name;
    }

    public Integer getTag() {
        return tag;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

}
