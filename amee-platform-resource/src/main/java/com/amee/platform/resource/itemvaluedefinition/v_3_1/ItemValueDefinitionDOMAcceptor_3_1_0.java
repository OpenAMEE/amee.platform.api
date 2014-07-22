package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionAcceptor;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionDOMAcceptor_3_1_0 extends ItemValueDefinitionAcceptor implements ItemValueDefinitionResource.DOMAcceptor {

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
            ItemValueDefinitionResource.ItemValueDefinitionValidator validator = getItemValueDefinitionValidator(requestWrapper);
            validator.setObject(itemValueDefinition);
            validator.initialise();
            if (validator.isValid(parameters)) {
                // ItemValueDefinition validation passed.
                // Handle and validate ItemValueUsages.
                handleItemValueUsages(requestWrapper, rootElem, itemValueDefinition);
                // ItemValueUsages validation passed.
                // Invalidate and return a response.
                definitionService.invalidate(itemValueDefinition.getItemDefinition());
                return ResponseHelper.getOK(requestWrapper, null, itemValueDefinition.getUid());
            } else {
                // Validation failed.
                throw new ValidationException(validator.getValidationResult());
            }
        } else {
            // Unexpected node found.
            throw new ValidationException();
        }
    }
}