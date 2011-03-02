package com.amee.platform.resource.itemvaluedefinition.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.*;
import com.amee.base.transaction.AMEETransaction;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionValidationHelper;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionsResource;
import com.amee.platform.resource.itemvaluedefinition.ItemValueUsageValidationHelper;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.definition.DefinitionService;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Scope("prototype")
@Since("3.4.0")
public class ItemValueDefinitionsDOMAcceptor_3_4_0 implements ItemValueDefinitionsResource.DOMAcceptor {

    @Autowired
    DefinitionService definitionService;

    @Autowired
    ResourceAuthorizationService resourceAuthorizationService;

    @Autowired
    private ItemValueDefinitionValidationHelper validationHelper;

    @Autowired
    private ItemValueUsageValidationHelper itemValueUsageValidationHelper;

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
                    return handle(requestWrapper, itemValueDefinition, parameters);
                } else {

                    // Unexpected node
                    throw new ValidationException();
                }
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new MissingAttributeException("itemDefinitionIdentifier");
        }
    }

    protected Object handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition, Map<String, String> parameters) {
        validationHelper.setItemValueDefinition(itemValueDefinition);
        if (validationHelper.isValid(parameters)) {

            // ItemValueDefinition validation passed.
            // Handle and validate ItemValueUsages.
            handleItemValueUsages(requestWrapper.getBodyAsDocument().getRootElement(), itemValueDefinition);

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

    // TODO: pull up
    protected void handleItemValueUsages(Element rootElement, ItemValueDefinition itemValueDefinition) {
        // Do we have ItemValueUsages to parse?
        Element ItemValueUsagesElem = rootElement.getChild("Usages");
        if (ItemValueUsagesElem != null) {
            // Create collections for ItemValueUsages and ValidationResults.
            Set<ItemValueUsage> itemValueUsages = new HashSet<ItemValueUsage>();
            List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
            // Parse all ItemValueUsages.
            for (Object o : ItemValueUsagesElem.getChildren()) {
                Element childElem = (Element) o;
                if (childElem.getName().equals("Usage")) {
                    // Create parameters map containing the name and type values.
                    Map<String, String> parameters = new HashMap<String, String>();
                    Element nameElem = childElem.getChild("Name");
                    if (nameElem != null) {
                        parameters.put("name", nameElem.getText());
                    }
                    Element typeElem = childElem.getChild("Type");
                    if (typeElem != null) {
                        parameters.put("type", typeElem.getText());
                    }
                    // Create and validate ItemValueUsage object.
                    ItemValueUsage itemValueUsage = new ItemValueUsage();
                    itemValueUsageValidationHelper.setItemValueUsage(itemValueUsage);
                    if (itemValueUsageValidationHelper.isValid(parameters)) {
                        // Validation passed.
                        if (!itemValueUsages.add(itemValueUsage)) {
                            // Should not have more than one equivalent ItemValueUsage.
                            throw new ValidationException();
                        }
                    } else {
                        // Validation failed.
                        validationResults.add(itemValueUsageValidationHelper.getValidationResult());
                    }
                } else {
                    // Unexpected node found.
                    throw new ValidationException();
                }
            }
            // Any validation errors?
            if (validationResults.isEmpty()) {
                // Validation passed.
                itemValueDefinition.setItemValueUsages(itemValueUsages);
            } else {
                // Validation failed.
                throw new ValidationException(validationResults);
            }
        }
    }
}
