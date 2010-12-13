package com.amee.platform.resource;

import org.apache.commons.lang.StringUtils;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.List;

public class CSVListEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) {
        List<String> values = new ArrayList<String>();
        if (StringUtils.isNotEmpty(text)) {
            for (String value : text.split(",")) {
                values.add(value.trim());
            }
        }
        setValue(values);
    }
}