package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;

public interface ReturnValueDefinitionsResource {

    interface Builder extends ResourceBuilder {
    }

    interface Renderer extends ResourceRenderer {

        void newReturnValueDefinition(ReturnValueDefinitionResource.Renderer renderer);
    }

    interface FormAcceptor extends ResourceAcceptor {
    }
}
