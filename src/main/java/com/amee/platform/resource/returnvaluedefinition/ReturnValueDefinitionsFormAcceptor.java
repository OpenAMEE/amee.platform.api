package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.domain.tag.EntityTag;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResourceService;
import com.amee.platform.resource.tag.TagValidationHelper;
import com.amee.service.definition.DefinitionService;
import com.amee.service.environment.EnvironmentService;
import com.amee.service.tag.TagService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
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