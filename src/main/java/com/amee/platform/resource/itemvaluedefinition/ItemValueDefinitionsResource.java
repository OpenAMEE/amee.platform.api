package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;

public interface ItemValueDefinitionsResource {

    public static interface Builder extends ResourceBuilder {
    }

    public static interface Renderer extends ResourceRenderer {

        public void newItemValueDefinition(ItemValueDefinitionResource.Renderer renderer);
    }
}
