package com.amee.platform.resource;

import com.amee.platform.science.AmountUnit;

import java.beans.PropertyEditorSupport;

public class UnitEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (text != null) {
            setValue(AmountUnit.valueOf(text));
        } else {
            throw new IllegalArgumentException("value was null");
        }
    }
}
