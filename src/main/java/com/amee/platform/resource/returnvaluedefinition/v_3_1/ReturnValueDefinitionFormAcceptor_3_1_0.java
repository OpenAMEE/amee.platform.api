package com.amee.platform.resource.returnvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.*;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
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
    protected DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceBeanFinder resourceBeanFinder;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        // Get ItemDefinition identifier.
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            // Get ItemDefinition.
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(itemDefinitionIdentifier);
            if (itemDefinition != null) {
                // Get ReturnValueDefinition identifier.
                String returnValueDefinitionIdentifier = requestWrapper.getAttributes().get("returnValueDefinitionIdentifier");
                if (returnValueDefinitionIdentifier != null) {
                    // Get ReturnValueDefinition.
                    ReturnValueDefinition returnValueDefinition = definitionService.getReturnValueDefinitionByUid(returnValueDefinitionIdentifier);
                    if (returnValueDefinition != null) {
                        // Authorized?
                        resourceAuthorizationService.ensureAuthorizedForModify(
                                requestWrapper.getAttributes().get("activeUserUid"), returnValueDefinition);
                        // Handle ReturnValueDefinition.
                        return handle(requestWrapper, returnValueDefinition);
                    } else {
                        throw new NotFoundException();
                    }
                } else {
                    throw new MissingAttributeException("returnValueDefinitionIdentifier");
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }

    protected Object handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition) {
        ReturnValueDefinitionResource.ReturnValueDefinitionValidator validator = getValidator(requestWrapper);
        validator.setObject(returnValueDefinition);
        validator.initialise();
        if (validator.isValid(requestWrapper.getFormParameters())) {
            // If default is true, update the others.
            definitionService.unsetDefaultTypes(returnValueDefinition);
            definitionService.invalidate(returnValueDefinition.getItemDefinition());
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }

    protected ReturnValueDefinitionResource.ReturnValueDefinitionValidator getValidator(RequestWrapper requestWrapper) {
        return (ReturnValueDefinitionResource.ReturnValueDefinitionValidator)
                resourceBeanFinder.getValidationHelper(
                        ReturnValueDefinitionResource.ReturnValueDefinitionValidator.class, requestWrapper);
    }
}