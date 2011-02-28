package com.amee.platform.resource.itemdefinition.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.itemdefinition.ItemDefinitionAcceptor;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
import com.amee.platform.resource.itemdefinition.ItemDefinitionsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class ItemDefinitionsFormAcceptor_3_4_0 extends ItemDefinitionAcceptor implements ItemDefinitionsResource.FormAcceptor {

    @Autowired
    DefinitionService definitionService;

    @Autowired
    ResourceAuthorizationService resourceAuthorizationService;

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Object handle(RequestWrapper requestWrapper) throws ValidationException {

        // Authorised?
        resourceAuthorizationService.ensureAuthorizedForAccept(requestWrapper.getAttributes().get("activeUserUid"));

        // Create a new ItemDefinition
        ItemDefinition itemDefinition = new ItemDefinition();
        definitionService.persist(itemDefinition);
        return handle(requestWrapper, itemDefinition);
    }

    protected Object handle(RequestWrapper requestWrapper, ItemDefinition itemDefinition) {
        ItemDefinitionResource.ItemDefinitionValidator validator = getValidator(requestWrapper);
        validator.setObject(itemDefinition);
        if (validator.isValid(requestWrapper.getFormParameters())) {
            String location = "/" + requestWrapper.getVersion() + "/definitions/" + itemDefinition.getUid();
            return ResponseHelper.getOK(requestWrapper, location);
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }
}
