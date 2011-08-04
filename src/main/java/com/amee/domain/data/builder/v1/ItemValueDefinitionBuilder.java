package com.amee.domain.data.builder.v1;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.Builder;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.environment.Environment;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ItemValueDefinitionBuilder implements Builder {

    private ItemValueDefinition itemValueDefinition;

    public ItemValueDefinitionBuilder(ItemValueDefinition itemValueDefinition) {
        this.itemValueDefinition = itemValueDefinition;
    }

    @Override
    public JSONObject getJSONObject() throws JSONException {
        throw new UnsupportedOperationException();
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", itemValueDefinition.getUid());
        obj.put("path", itemValueDefinition.getPath());
        obj.put("name", itemValueDefinition.getName());
        obj.put("valueDefinition", itemValueDefinition.getValueDefinition().getJSONObject(false));
        if (detailed) {
            obj.put("created", itemValueDefinition.getCreated());
            obj.put("modified", itemValueDefinition.getModified());
            obj.put("value", itemValueDefinition.getValue());
            obj.put("choices", itemValueDefinition.getChoices());
            obj.put("fromProfile", itemValueDefinition.isFromProfile());
            obj.put("fromData", itemValueDefinition.isFromData());

            // AllowedRoles has been removed. See: https://jira.amee.com/browse/PL-10448
            obj.put("allowedRoles", "");
            obj.put("environment", Environment.ENVIRONMENT.getIdentityJSONObject());
            obj.put("itemDefinition", itemValueDefinition.getItemDefinition().getIdentityJSONObject());
        }
        return obj;
    }

    @Override
    public Element getElement(Document document) {
        throw new UnsupportedOperationException();
    }

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement("ItemValueDefinition");
        element.setAttribute("uid", itemValueDefinition.getUid());
        element.appendChild(XMLUtils.getElement(document, "Path", itemValueDefinition.getPath()));
        element.appendChild(XMLUtils.getElement(document, "Name", itemValueDefinition.getName()));
        element.appendChild(XMLUtils.getElement(document, "FromProfile", Boolean.toString(itemValueDefinition.isFromProfile())));
        element.appendChild(XMLUtils.getElement(document, "FromData", Boolean.toString(itemValueDefinition.isFromData())));
        element.appendChild(itemValueDefinition.getValueDefinition().getElement(document, false));
        if (detailed) {
            element.setAttribute("created", itemValueDefinition.getCreated().toString());
            element.setAttribute("modified", itemValueDefinition.getModified().toString());
            element.appendChild(XMLUtils.getElement(document, "Value", itemValueDefinition.getValue()));
            element.appendChild(XMLUtils.getElement(document, "Choices", itemValueDefinition.getChoices()));

            // AllowedRoles has been removed. See: https://jira.amee.com/browse/PL-10448
            element.appendChild(XMLUtils.getElement(document, "AllowedRoles", ""));
            element.appendChild(Environment.ENVIRONMENT.getIdentityElement(document));
            element.appendChild(itemValueDefinition.getItemDefinition().getIdentityElement(document));
        }
        return element;
    }

    @Override
    public JSONObject getIdentityJSONObject() throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Element getIdentityElement(Document document) {
        throw new UnsupportedOperationException();
    }
}
