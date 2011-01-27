package com.amee.platform.resource.returnvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionAcceptor;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionResource;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionFormAcceptor_3_1_0 extends ReturnValueDefinitionAcceptor implements ReturnValueDefinitionResource.FormAcceptor {

    @Autowired
    private ReturnValueDefinitionValidationHelper validationHelper;

    protected Object handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition) {
        validationHelper.setReturnValueDefinition(returnValueDefinition);
        if (validationHelper.isValid(requestWrapper.getFormParameters())) {
            // If default is true, update the others.
            definitionService.unsetDefaultTypes(returnValueDefinition);
            definitionService.invalidate(returnValueDefinition.getItemDefinition());
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }
}