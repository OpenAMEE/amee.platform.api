package com.amee.platform.resource;

import com.amee.domain.data.DataCategory;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

/**
 * Custom editor to convert a UID into a ValueDefinition
 */
@Component
public class DataCategoryEditor extends PropertyEditorSupport {

    @Autowired
    private DataService dataService;

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            setValue(dataService.getDataCategoryByIdentifier(text));
        }
    }
}
