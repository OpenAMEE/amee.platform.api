package com.amee.platform.resource;

import com.amee.domain.AMEEStatus;

import java.beans.PropertyEditorSupport;

public class StatusEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            setValue(AMEEStatus.valueOf(text.trim().toUpperCase()));
        } else {
            setValue(AMEEStatus.ACTIVE);
        }
    }
}