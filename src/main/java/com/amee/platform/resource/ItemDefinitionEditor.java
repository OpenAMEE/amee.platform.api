package com.amee.platform.resource;

import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyEditorSupport;

/**
 * Custom editor to convert a UID into an ItemDefinition.
 */
@Component
public class ItemDefinitionEditor extends PropertyEditorSupport {

    @Autowired
    private DefinitionService definitionService;

    @Override
    public void setAsText(String text) {
        if (text != null) {
            setValue(definitionService.getItemDefinitionByUid(text));
        }
    }
}
