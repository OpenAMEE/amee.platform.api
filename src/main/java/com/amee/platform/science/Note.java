package com.amee.platform.science;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Note {
    public static final int MAX_TYPE_LENGTH = 255;
    public static final int MAX_VALUE_LENGTH = 255;

    private String type = "comment";
    private String value = "";

    public Note(String type, String value) {
        setType(type);
        setValue(value);
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type.length() > MAX_TYPE_LENGTH) {
            throw new IllegalArgumentException("Note type must be <= " + MAX_TYPE_LENGTH +
                " characters. Tried to add type of length " + type.length());
        }
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value.length() > MAX_VALUE_LENGTH) {
            throw new IllegalArgumentException("Note value must be <= " + MAX_VALUE_LENGTH +
                " characters. Tried to add note of length " + value.length());
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("type", type).
            append("value", value).
            toString();
    }

    // TODO: handle creating JSON and XML.
}
