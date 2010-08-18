package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.service.definition.DefinitionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionsFormAcceptor extends ReturnValueDefinitionsAcceptor {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ReturnValueDefinitionValidationHelper validationHelper;

    protected Object handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition) {
        validationHelper.setReturnValueDefinition(returnValueDefinition);
        if (validationHelper.isValid(requestWrapper.getFormParameters())) {
            log.debug("handle() Persist ReturnValueDefinition.");
            definitionService.save(returnValueDefinition);
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }
}