package com.amee.platform.resource;

import com.amee.domain.ValueDefinition;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

/**
 * Custom editor to convert a UID into a ValueDefinition
 */
@Component
public class ValueDefinitionEditor extends PropertyEditorSupport {

    @Autowired
    private DefinitionService definitionService;

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            ValueDefinition valueDefinition = definitionService.getValueDefinition(text);
            if (valueDefinition != null) {
                setValue(valueDefinition);
            } else {
                throw new IllegalArgumentException("Invalid UID: " + text);
            }
        } else {
            throw new IllegalArgumentException("UID is empty");
        }
    }
}
