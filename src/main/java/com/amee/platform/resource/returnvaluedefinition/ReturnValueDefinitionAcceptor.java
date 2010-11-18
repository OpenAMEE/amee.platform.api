package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class ReturnValueDefinitionAcceptor implements ResourceAcceptor {

    @Autowired
    protected DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Transactional(rollbackFor = {ValidationException.class})
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

    protected abstract Object handle(RequestWrapper requestWrapper, ReturnValueDefinition returnValueDefinition);
}