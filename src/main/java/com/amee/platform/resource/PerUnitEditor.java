package com.amee.platform.resource;

import com.amee.platform.science.AmountPerUnit;
import java.beans.PropertyEditorSupport;

public class PerUnitEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (text != null) {
            setValue(AmountPerUnit.valueOf(text));
        } else {
            throw new IllegalArgumentException("value was null");
        }
    }
}
