package com.amee.platform.resource.itemdefinition;

import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public abstract class ItemDefinitionAcceptor implements ResourceAcceptor {

    @Autowired
    protected DefinitionService definitionService;

    @Transactional(rollbackFor = {ValidationException.class})
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {
        // Get ItemDefinition identifier.
        String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
        if (itemDefinitionIdentifier != null) {
            // Get ItemDefinition.
            ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(itemDefinitionIdentifier);
            if (itemDefinition != null) {
                return handle(requestWrapper, itemDefinition);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }

    protected abstract Object handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition);
}