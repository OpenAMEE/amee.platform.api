package com.amee.platform.resource.itemdefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.itemdefinition.ItemDefinitionAcceptor;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
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
public class ItemDefinitionFormAcceptor_3_1_0 extends ItemDefinitionAcceptor implements ItemDefinitionResource.FormAcceptor {

    @Autowired
    protected DefinitionService definitionService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ResourceService resourceService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Get ItemDefinition.
        ItemDefinition itemDefinition = resourceService.getItemDefinition(requestWrapper);

        // Authorized?
        resourceAuthorizationService.ensureAuthorizedForModify(
                requestWrapper.getAttributes().get("activeUserUid"), itemDefinition);

        // Handle the ItemDefinition.
        return handle(requestWrapper, itemDefinition);
    }

    protected Object handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {
        ItemDefinitionResource.ItemDefinitionValidator validator = getValidator(requestWrapper);
        validator.setObject(itemDefinition);
        if (validator.isValid(requestWrapper.getFormParameters())) {
            definitionService.invalidate(itemDefinition);
            return ResponseHelper.getOK(requestWrapper, null, itemDefinition.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }
}