package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.*;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public abstract class ItemDefinitionAcceptor implements ResourceAcceptor {

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
                // Authorized?
                resourceAuthorizationService.ensureAuthorizedForModify(
                        requestWrapper.getAttributes().get("activeUserUid"), itemDefinition);
                // Handle the ItemDefinition.
                return handle(requestWrapper, itemDefinition);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }

    protected ItemDefinitionResource.ItemDefinitionValidator getValidator(RequestWrapper requestWrapper) {
        return (ItemDefinitionResource.ItemDefinitionValidator)
                resourceBeanFinder.getValidationHelper(
                        ItemDefinitionResource.ItemDefinitionValidator.class, requestWrapper);
    }

    protected abstract Object handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition);
}