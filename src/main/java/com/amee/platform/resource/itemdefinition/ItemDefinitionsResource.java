package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.base.validation.ValidationException;

public interface ItemDefinitionsResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        void newItemDefinition(ItemDefinitionResource.Renderer renderer);

        void setTruncated(boolean truncated);
    }

    interface FormAcceptor extends ResourceAcceptor {

        Object handle(RequestWrapper requestWrapper) throws ValidationException;
    }
}
