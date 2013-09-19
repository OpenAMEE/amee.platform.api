package com.amee.platform.resource.returnvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionFormAcceptor_3_1_0 implements ReturnValueDefinitionResource.FormAcceptor {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Autowired
    private ResourceService resourceService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Get ReturnValueDefinition.
        ReturnValueDefinition returnValueDefinition = resourceService.getReturnValueDefinition(requestWrapper, itemDefinition);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForModify(
                requestWrapper.getAttributes().get("activeUserUid"), returnValueDefinition);

        // Handle ReturnValueDefinition.
        return handle(requestWrapper, returnValueDefinition);
    }

    protected Object handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition) {
        ReturnValueDefinitionResource.ReturnValueDefinitionValidator validator = getValidator(requestWrapper);
        validator.setObject(returnValueDefinition);
        validator.initialise();
        if (validator.isValid(requestWrapper.getFormParameters())) {
            // If default is true, update the others.
            definitionService.unsetDefaultTypes(returnValueDefinition);
            definitionService.invalidate(returnValueDefinition.getItemDefinition());
            return ResponseHelper.getOK(requestWrapper, null, returnValueDefinition.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    protected ReturnValueDefinitionResource.ReturnValueDefinitionValidator getValidator(RequestWrapper requestWrapper) {
        return (ReturnValueDefinitionResource.ReturnValueDefinitionValidator)
                resourceBeanFinder.getBaseValidator(
                        ReturnValueDefinitionResource.ReturnValueDefinitionValidator.class, requestWrapper);
    }
}