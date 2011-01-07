package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;

public interface ItemDefinitionsResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public void newItemDefinition(ItemDefinitionResource.Renderer renderer);

        public void setTruncated(boolean truncated);
    }
}
