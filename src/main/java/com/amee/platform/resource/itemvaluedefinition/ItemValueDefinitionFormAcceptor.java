package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.resource.MissingAttributeException;
import com.amee.base.resource.NotFoundException;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.service.definition.DefinitionService;
import com.amee.service.environment.EnvironmentService;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class ItemValueDefinitionFormAcceptor implements ResourceAcceptor {

    @Autowired
    private EnvironmentService environmentService;

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private ItemValueDefinitionValidationHelper validationHelper;

    @Transactional(rollbackFor = {ValidationException.class})
    public JSONObject handle(RequestWrapper requestWrapper) throws ValidationException {
        try {
            // Get ItemDefinition identifier.
            String itemDefinitionIdentifier = requestWrapper.getAttributes().get("itemDefinitionIdentifier");
            if (itemDefinitionIdentifier != null) {
                // Get ItemDefinition.
                ItemDefinition itemDefinition = definitionService.getItemDefinitionByUid(
                        environmentService.getEnvironmentByName("AMEE"), itemDefinitionIdentifier);
                if (itemDefinition != null) {
                    // Get ItemValueDefinition identifier.
                    String itemValueDefinitionIdentifier = requestWrapper.getAttributes().get("itemValueDefinitionIdentifier");
                    if (itemValueDefinitionIdentifier != null) {
                        // Get ItemValueDefinition.
                        ItemValueDefinition itemValueDefinition = definitionService.getItemValueDefinitionByUid(itemValueDefinitionIdentifier);
                        if (itemValueDefinition != null) {
                            return handle(requestWrapper, itemValueDefinition);
                        } else {
                            throw new NotFoundException();
                        }
                    } else {
                        throw new MissingAttributeException("itemValueDefinitionIdentifier");
                    }
                } else {
                    throw new NotFoundException();
                }
            } else {
                throw new MissingAttributeException("itemDefinitionIdentifier");
            }
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    protected JSONObject handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition) throws JSONException {
        validationHelper.setItemValueDefinition(itemValueDefinition);
        if (validationHelper.isValid(requestWrapper.getFormParameters())) {
            JSONObject o = new JSONObject();
            o.put("status", "OK");
            return o;
        } else {
            throw new ValidationException(validationHelper.getValidationResult());
        }
    }
}