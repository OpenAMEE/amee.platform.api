package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResponseHelper;
import com.amee.base.validation.ValidationException;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionValidationHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionDOMAcceptor extends com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionDOMAcceptor {

    @Autowired
    private ItemValueDefinitionValidationHelper validationHelper;

    protected Object handle(RequestWrapper requestWrapper, ItemValueDefinition itemValueDefinition) {
        Document document = requestWrapper.getBodyAsDocument();
        Element rootElem = document.getRootElement();
        if (rootElem.getName().equals("ItemValueDefinition")) {
            Map<String, String> parameters = new HashMap<String, String>();
            for (Object o : rootElem.getChildren()) {
                Element childElem = (Element) o;
                if (childElem.getChildren().isEmpty()) {
                    parameters.put(StringUtils.uncapitalize(childElem.getName()), childElem.getText());
                }
            }
            validationHelper.setItemValueDefinition(itemValueDefinition);
            if (validationHelper.isValid(parameters)) {
                return ResponseHelper.getOK(requestWrapper);
            } else {
                throw new ValidationException(validationHelper.getValidationResult());
            }
        } else {
            throw new ValidationException();
        }
    }
}