package com.amee.platform.resource.itemvaluedefinition;

import com.amee.domain.data.ItemValueDefinition;

import java.beans.PropertyEditorSupport;

/**
 * An Editor implementation for validating values associated with a given {@link ItemValueDefinition}.
 *
 */
public class ItemValueEditor extends PropertyEditorSupport {

    private ItemValueDefinition itemValueDefinition;

    public ItemValueEditor() {
        super();
    }

    public ItemValueEditor(ItemValueDefinition itemValueDefinition) {
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