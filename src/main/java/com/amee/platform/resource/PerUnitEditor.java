package com.amee.platform.resource;

import com.amee.platform.science.AmountPerUnit;
import java.beans.PropertyEditorSupport;

public class PerUnitEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            setValue(AmountPerUnit.valueOf(text));
        } else {
            setValue(AmountPerUnit.ONE);
        }
    }
}
