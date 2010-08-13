package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ReturnValueDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class ReturnValueDefinitionFormAcceptor extends ReturnValueDefinitionAcceptor {

    @Autowired
    private ReturnValueDefinitionValidationHelper validationHelper;

    protected Object handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition) {
        validationHelper.setReturnValueDefinition(returnValueDefinition);
        if (validationHelper.isValid(requestWrapper.getFormParameters())) {
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }
}