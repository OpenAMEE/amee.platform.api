package com.amee.platform.resource.itemvaluedefinition.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionValidationHelper;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
@Since("3.4.0")
public class ItemValueDefinitionsFormAcceptor_3_4_0 implements ItemValueDefinitionsResource.FormAcceptor {

    @Autowired
    DefinitionService definitionService;

    @Autowired
    ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ItemValueDefinitionValidationHelper validationHelper;

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

                // Handle the ItemDefinition submission.
                ItemValueDefinition itemValueDefinition = new ItemValueDefinition(itemDefinition);
                return handle(requestWrapper, itemValueDefinition);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }
        
    public Object handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition) {
        validationHelper.setItemValueDefinition(itemValueDefinition);
        if (validationHelper.isValid(requestWrapper.getFormParameters())) {

            // Add the ItemValueDefinition to the ItemDefinition
            itemValueDefinition.getItemDefinition().add(itemValueDefinition);

            definitionService.persist(itemValueDefinition);

            // Invalidate the ItemDefinition
            definitionService.invalidate(itemValueDefinition.getItemDefinition());

            String location = "/" + requestWrapper.getVersion() +
                "/definitions/" + requestWrapper.getAttributes().get("itemDefinitionIdentifier") +
                "/values/" + itemValueDefinition.getUid();
            return ResponseHelper.getOK(requestWrapper, location);
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }
}
