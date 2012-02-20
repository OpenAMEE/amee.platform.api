package com.amee.platform.resource.returnvaluedefinition;

import java.util.Map;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ValidationResult;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;

public interface ReturnValueDefinitionResource {

    public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition);

        public ReturnValueDefinitionResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newReturnValueDefinition(ReturnValueDefinition returnValueDefinition);

        public void addBasic();
        
        public void addName();

        public void addItemDefinition(ItemDefinition id);

        public void addValueDefinition(ValueDefinition vd);

        public void addType();

        public void addUnits();

        public void addFlags();

        public void addAudit();
    }

    public static interface FormAcceptor extends ResourceAcceptor {
    }

    public static interface ReturnValueDefinitionValidator {

        public void initialise();

        public boolean isValid(Map<String, String> queryParameters);

        public ReturnValueDefinition getObject();

        public void setObject(ReturnValueDefinition object);

        public ValidationResult getValidationResult();
    }

    public static interface Remover extends ResourceRemover {
    }
}
