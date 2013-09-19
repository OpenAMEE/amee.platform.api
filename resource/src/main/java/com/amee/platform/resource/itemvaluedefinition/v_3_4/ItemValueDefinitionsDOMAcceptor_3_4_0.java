package com.amee.platform.resource.itemvaluedefinition.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.ResourceService;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionBaseAcceptor;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionsResource;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.jdom2.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope("prototype")
@Since("3.4.0")
public class ItemValueDefinitionsDOMAcceptor_3_4_0 extends ItemValueDefinitionBaseAcceptor implements ItemValueDefinitionsResource.DOMAcceptor {

    @Autowired
    private DefinitionService definitionService;

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

        // Handle the ItemValueDefinition submission.
        Element rootElem = requestWrapper.getBodyAsDocument().getRootElement();
        if (rootElem.getName().equals("ItemValueDefinition")) {

            // Create parameters map containing all name / value pairs.
            Map<String, String> parameters = new HashMap<String, String>();
            for (Object o : rootElem.getChildren()) {
                Element childElem = (Element) o;
                if (childElem.getChildren().isEmpty()) {
                    parameters.put(StringUtils.uncapitalize(childElem.getName()), childElem.getText());
                }
            }

            ItemValueDefinition itemValueDefinition = new ItemValueDefinition(itemDefinition);
            return handle(requestWrapper, rootElem, parameters, itemValueDefinition);
        } else {

            // Unexpected node.
            // TODO: Be more precise with a ValidationResult.
            throw new ValidationException();
        }
    }

    protected Object handle(
            RequestWrapper requestWrapper,
            Element rootElem,
            Map<String, String> parameters,
            ItemValueDefinition itemValueDefinition) {

        // Validate the ItemValueDefinition.
        ItemValueDefinitionResource.ItemValueDefinitionValidator validator = getItemValueDefinitionValidator(requestWrapper);
        validator.setObject(itemValueDefinition);
        validator.initialise();
        if (validator.isValid(parameters)) {

            // Persist the ItemValueDefinition (do this now so other entities below can use the IVD ID).
            definitionService.persist(itemValueDefinition);

            // ItemValueDefinition validation passed.
            // Handle and validate ItemValueUsages.
            handleItemValueUsages(requestWrapper, rootElem, itemValueDefinition);

            // Add the ItemValueDefinition to the ItemDefinition
            itemValueDefinition.getItemDefinition().add(itemValueDefinition);

            // Invalidate the ItemDefinition
            definitionService.invalidate(itemValueDefinition.getItemDefinition());

            String location = "/" + requestWrapper.getVersion() +
                    "/definitions/" + requestWrapper.getAttributes().get("itemDefinitionIdentifier") +
                    "/values/" + itemValueDefinition.getUid();
            return ResponseHelper.getOK(requestWrapper, location, itemValueDefinition.getUid());
        } else {
            throw new ValidationException(validator.getValidationResult());
        }
    }
}
