package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemValueDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class ItemValueDefinitionFormAcceptor extends ItemValueDefinitionAcceptor {

    @Autowired
    private ItemValueDefinitionValidationHelper validationHelper;

    protected Object handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition) {
        validationHelper.setItemValueDefinition(itemValueDefinition);
        if (validationHelper.isValid(requestWrapper.getFormParameters())) {
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }
}