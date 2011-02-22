package com.amee.platform.resource.dataitem;

import com.amee.domain.data.ItemValueDefinition;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.PropertyEditorSupport;

/**
 * An Editor implementation for validating values associated with a given {@link ItemValueDefinition}.
 */
public class DataItemValueEditor extends PropertyEditorSupport {

    @Autowired
    private DataService dataService;

    private ItemValueDefinition itemValueDefinition;

    public DataItemValueEditor() {
        super();
    }

    public DataItemValueEditor(ItemValueDefinition itemValueDefinition) {
        this();
        this.itemValueDefinition = itemValueDefinition;
    }

    @Override
    public void setAsText(String text) {
        if (!text.isEmpty()) {
            try {
                if (itemValueDefinition.isDouble()) {
                    setValue(Double.parseDouble(text));
                } else if (itemValueDefinition.isInteger()) {
                    setValue(Integer.parseInt(text));
                } else {
                    setValue(text);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + text);
            }
        } else {
            throw new IllegalArgumentException("Value was empty.");
        }
    }
}