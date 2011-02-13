package com.amee.platform.resource.itemdefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.itemdefinition.ItemDefinitionAcceptor;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemDefinitionFormAcceptor_3_1_0 extends ItemDefinitionAcceptor implements ItemDefinitionResource.FormAcceptor {

    @Override
    protected Object handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {
        ItemDefinitionResource.ItemDefinitionValidator validator = getValidator(requestWrapper);
        validator.setObject(itemDefinition);
        if (validator.isValid(requestWrapper.getFormParameters())) {
            definitionService.invalidate(itemDefinition);
            return ResponseHelper.getOK(requestWrapper);
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }
}