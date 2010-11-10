package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ResourceBuilder;

public interface ItemValueDefinitionsResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public void newItemValueDefinition(ItemValueDefinitionResource.Renderer renderer);
    }
}
