package com.amee.platform.resource.itemdefinition;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemDefinitionFormAcceptor extends ItemDefinitionAcceptor {

    @Autowired
    private ItemDefinitionValidationHelper validationHelper;

    protected Object handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {
        validationHelper.setItemDefinition(itemDefinition);
        if (validationHelper.isValid(requestWrapper.getFormParameters())) {
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }
}