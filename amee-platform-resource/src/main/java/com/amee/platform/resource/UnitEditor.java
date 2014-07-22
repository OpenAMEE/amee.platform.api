package com.amee.platform.resource;

import com.amee.platform.science.AmountUnit;

import java.beans.PropertyEditorSupport;

public class UnitEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            setValue(AmountUnit.valueOf(text));
        } else {
            setValue(AmountUnit.ONE);
        }
    }
}
