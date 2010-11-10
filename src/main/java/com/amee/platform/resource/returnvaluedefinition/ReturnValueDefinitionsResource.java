package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ResourceBuilder;

public interface ReturnValueDefinitionsResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        public void newReturnValueDefinition(ReturnValueDefinitionResource.Renderer renderer);
    }

    interface FormAcceptor {
    }
}
