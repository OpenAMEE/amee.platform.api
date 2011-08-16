package com.amee.domain.data.builder.v2;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.APIVersion;
import com.amee.domain.Builder;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.environment.Environment;
import org.json.JSONArray;
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
        obj.put("valueDefinition", itemValueDefinition.getValueDefinition().getJSONObject());
        obj.put("fromProfile", itemValueDefinition.isFromProfile());
        obj.put("fromData", itemValueDefinition.isFromData());
        obj.put("drillDown", itemValueDefinition.isDrillDown());

        if (itemValueDefinition.hasUnit()) {
            obj.put("unit", itemValueDefinition.getUnit());
        }

        if (itemValueDefinition.hasPerUnit()) {
            obj.put("perUnit", itemValueDefinition.getPerUnit());
        }

        if (itemValueDefinition.isChoicesAvailable()) {
            obj.put("choices", itemValueDefinition.getChoices());
        }

        if (detailed) {
            obj.put("created", itemValueDefinition.getCreated());
            obj.put("modified", itemValueDefinition.getModified());
            obj.put("value", itemValueDefinition.getValue());
            obj.put("choices", itemValueDefinition.getChoices());

            // AllowedRoles has been removed. See: https://jira.amee.com/browse/PL-10448
            obj.put("allowedRoles", "");
            obj.put("environment", Environment.ENVIRONMENT.getIdentityJSONObject());
            obj.put("itemDefinition", itemValueDefinition.getItemDefinition().getIdentityJSONObject());
            obj.put("aliasedTo", itemValueDefinition.getAliasedTo() == null ? JSONObject.NULL : itemValueDefinition.getAliasedTo().getIdentityJSONObject());
            JSONArray apiVersions = new JSONArray();
            for (APIVersion v : itemValueDefinition.getApiVersions()) {
                apiVersions.put(v.getJSONObject(false));
            }
            obj.put("apiVersions", apiVersions);
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
        element.appendChild(itemValueDefinition.getValueDefinition().getElement(document));

        if (itemValueDefinition.hasUnit()) {
            element.appendChild(XMLUtils.getElement(document, "Unit", itemValueDefinition.getUnit().toString()));
        }

        if (itemValueDefinition.hasPerUnit()) {
            element.appendChild(XMLUtils.getElement(document, "PerUnit", itemValueDefinition.getPerUnit().toString()));
        }

        element.appendChild(XMLUtils.getElement(document, "FromProfile", Boolean.toString(itemValueDefinition.isFromProfile())));
        element.appendChild(XMLUtils.getElement(document, "FromData", Boolean.toString(itemValueDefinition.isFromData())));
        element.appendChild((XMLUtils.getElement(document, "DrillDown", Boolean.toString(itemValueDefinition.isDrillDown()))));
        if (detailed) {
            element.setAttribute("created", itemValueDefinition.getCreated().toString());
            element.setAttribute("modified", itemValueDefinition.getModified().toString());
            element.appendChild(XMLUtils.getElement(document, "Value", itemValueDefinition.getValue()));
            element.appendChild(XMLUtils.getElement(document, "Choices", itemValueDefinition.getChoices()));

            // AllowedRoles has been removed. See: https://jira.amee.com/browse/PL-10448
            element.appendChild(XMLUtils.getElement(document, "AllowedRoles", ""));
            element.appendChild(Environment.ENVIRONMENT.getIdentityElement(document));
            element.appendChild(itemValueDefinition.getItemDefinition().getIdentityElement(document));
            if (itemValueDefinition.getAliasedTo() != null) {
                element.appendChild(itemValueDefinition.getAliasedTo().getIdentityElement(document));
                element.appendChild(XMLUtils.getIdentityElement(document, "AliasedTo", itemValueDefinition.getAliasedTo()));
            } else {
                element.appendChild(XMLUtils.getElement(document, "AliasedTo", ""));
            }
            Element apiVersions = document.createElement("APIVersions");
            for (APIVersion v : itemValueDefinition.getApiVersions()) {
                apiVersions.appendChild(v.getElement(document, false));
            }
            element.appendChild(apiVersions);
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