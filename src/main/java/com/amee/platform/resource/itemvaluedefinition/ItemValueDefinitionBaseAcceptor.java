package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBeanFinder;
import com.amee.base.resource.ValidationResult;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import org.jdom.Attribute;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

public abstract class ItemValueDefinitionBaseAcceptor {

    @Autowired
    protected ResourceBeanFinder resourceBeanFinder;

    protected void handleItemValueUsages(RequestWrapper requestWrapper, Element rootElement, ItemValueDefinition itemValueDefinition) {
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
                    Attribute activeAttr = childElem.getAttribute("active");
                    if (activeAttr != null) {
                        parameters.put("active", activeAttr.getValue());
                    }
                    // Create and validate ItemValueUsage object.
                    ItemValueUsage itemValueUsage = new ItemValueUsage();
                    ItemValueDefinitionResource.ItemValueUsageValidator validator = getItemValueUsageValidator(requestWrapper);
                    validator.setObject(itemValueUsage);
                    validator.initialise();
                    if (validator.isValid(parameters)) {
                        // Validation passed.
                        if (!itemValueUsages.add(itemValueUsage)) {
                            // Should not have more than one equivalent ItemValueUsage.
                            throw new ValidationException();
                        }
                    } else {
                        // Validation failed.
                        validationResults.add(validator.getValidationResult());
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

    protected ItemValueDefinitionResource.ItemValueUsageValidator getItemValueUsageValidator(RequestWrapper requestWrapper) {
        return (ItemValueDefinitionResource.ItemValueUsageValidator)
                resourceBeanFinder.getValidator(
                        ItemValueDefinitionResource.ItemValueUsageValidator.class, requestWrapper);
    }

    protected ItemValueDefinitionResource.ItemValueDefinitionValidator getItemValueDefinitionValidator(RequestWrapper requestWrapper) {
        return (ItemValueDefinitionResource.ItemValueDefinitionValidator)
                resourceBeanFinder.getValidator(
                        ItemValueDefinitionResource.ItemValueDefinitionValidator.class, requestWrapper);
    }
}
