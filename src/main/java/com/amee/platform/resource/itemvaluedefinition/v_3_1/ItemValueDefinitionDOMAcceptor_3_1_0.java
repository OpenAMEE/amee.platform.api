package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.resource.ValidationResult;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemvaluedefinition.*;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionDOMAcceptor_3_1_0 extends ItemValueDefinitionAcceptor implements ItemValueDefinitionResource.DOMAcceptor {

    @Autowired
    private ItemValueDefinitionValidationHelper itemValueDefinitionValidationHelper;

    @Autowired
    private ItemValueUsageValidationHelper itemValueUsageValidationHelper;

    protected Object handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition) {
        Document document = requestWrapper.getBodyAsDocument();
        Element rootElem = document.getRootElement();
        if (rootElem.getName().equals("ItemValueDefinition")) {
            // Create parameters map containing all name / value pairs.
            Map<String, String> parameters = new HashMap<String, String>();
            for (Object o : rootElem.getChildren()) {
                Element childElem = (Element) o;
                if (childElem.getChildren().isEmpty()) {
                    parameters.put(StringUtils.uncapitalize(childElem.getName()), childElem.getText());
                }
            }
            // Update the ItemValueDefinition and validate. 
            itemValueDefinitionValidationHelper.setItemValueDefinition(itemValueDefinition);
            if (itemValueDefinitionValidationHelper.isValid(parameters)) {
                // ItemValueDefinition validation passed.
                // Handle and validate ItemValueUsages.
                handleItemValueUsages(rootElem, itemValueDefinition);
                // ItemValueUsages validation passed.
                // Invalidate and return a response.
                definitionService.invalidate(itemValueDefinition.getItemDefinition());
                return ResponseHelper.getOK(requestWrapper);
            } else {
                // Validation failed.
                throw new ValidationException(itemValueDefinitionValidationHelper.getValidationResult());
            }
        } else {
            // Unexpected node found.
            throw new ValidationException();
        }
    }

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