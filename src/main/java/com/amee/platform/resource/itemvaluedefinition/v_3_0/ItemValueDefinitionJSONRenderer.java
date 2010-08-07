package com.amee.platform.resource.itemvaluedefinition.v_3_0;

import com.amee.base.domain.Since;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Version 3.0 does not include the value, usages, choices, units or flags that are part of the 3.1 representation.
 */
@Service("itemValueDefinitionJSONRenderer_3_0_0")
@Scope("prototype")
@Since("3.0.0")
public class ItemValueDefinitionJSONRenderer extends com.amee.platform.resource.itemvaluedefinition.v_3_1.ItemValueDefinitionJSONRenderer {

    @Override
    public void addValue() {
        // Not in 3.0.
    }

    @Override
    public void addUsages() {
        // Not in 3.0.
    }

    @Override
    public void addChoices() {
        // Not in 3.0.
    }

    @Override
    public void addUnits() {
        // Not in 3.0.
    }

    @Override
    public void addFlags() {
        // Not in 3.0.
    }
}
