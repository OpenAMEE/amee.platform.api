package com.amee.platform.resource.itemvaluedefinition;

import com.amee.domain.ValueUsageType;

import java.beans.PropertyEditorSupport;

public class ValueUsageTypeEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (text != null) {
            setValue(ValueUsageType.valueOf(text.trim().toUpperCase()));
        } else {
            throw new IllegalArgumentException("value was null");
        }
    }
}