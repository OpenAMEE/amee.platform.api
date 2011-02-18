package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;

public interface ReturnValueDefinitionsResource {

    public static interface Builder extends ResourceBuilder {
    }

    public static interface Renderer extends ResourceRenderer {

        public void newReturnValueDefinition(ReturnValueDefinitionResource.Renderer renderer);
    }

    public static interface FormAcceptor {
    }
}
