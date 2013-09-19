package com.amee.domain.data.builder.v2;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.Builder;
import com.amee.domain.ItemService;
import com.amee.domain.TimeZoneHolder;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.NumberValue;
import com.amee.platform.science.StartEndDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ItemValueInListBuilder implements Builder {

    private BaseItemValue itemValue;
    private ItemService itemService;

    public ItemValueInListBuilder(BaseItemValue itemValue, ItemService itemService) {
        this.itemValue = itemValue;
        this.itemService = itemService;
    }

    @Override
    public JSONObject getJSONObject() throws JSONException {
        throw new UnsupportedOperationException();
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        // Meta.
        obj.put("uid", itemValue.getUid());
        obj.put("created", itemValue.getCreated());
        obj.put("modified", itemValue.getModified());
        // Data.
        obj.put("path", itemValue.getPath());
        obj.put("name", itemValue.getName());
        obj.put("value", itemValue.getValueAsString());
        if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
            NumberValue numberValue = (NumberValue) itemValue;
            obj.put("unit", numberValue.getUnit());
            obj.put("perUnit", numberValue.getPerUnit());
        } else {
            obj.put("unit", "");
            obj.put("perUnit", "");
        }
        obj.put("startDate", StartEndDate.getLocalStartEndDate(itemService.getStartDate(itemValue), TimeZoneHolder.getTimeZone()).toString());
        // Related entities.
        obj.put("itemValueDefinition", getItemValueDefinitionJSONObject(itemValue.getItemValueDefinition()));
        return obj;
    }

    @Override
    public Element getElement(Document document) {
        throw new UnsupportedOperationException();
    }

    protected JSONObject getItemValueDefinitionJSONObject(ItemValueDefinition itemValueDefinition) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("valueDefinition", itemValueDefinition.getValueDefinition().getJSONObject());
        return obj;
    }

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement("ItemValue");
        // Meta.
        element.setAttribute("uid", itemValue.getUid());
        element.setAttribute("created", itemValue.getCreated().toString());
        element.setAttribute("modified", itemValue.getModified().toString());
        // Data.
        element.appendChild(XMLUtils.getElement(document, "Path", itemValue.getPath()));
        element.appendChild(XMLUtils.getElement(document, "Name", itemValue.getName()));
        element.appendChild(XMLUtils.getElement(document, "Value", itemValue.getValueAsString()));
        if (NumberValue.class.isAssignableFrom(itemValue.getClass())) {
            NumberValue numberValue = (NumberValue) itemValue;
            element.appendChild(XMLUtils.getElement(document, "Unit", numberValue.getUnit().toString()));
            element.appendChild(XMLUtils.getElement(document, "PerUnit", numberValue.getPerUnit().toString()));
        } else {
            element.appendChild(XMLUtils.getElement(document, "Unit", ""));
            element.appendChild(XMLUtils.getElement(document, "PerUnit", ""));
        }
        element.appendChild(XMLUtils.getElement(document, "StartDate",
                StartEndDate.getLocalStartEndDate(itemService.getStartDate(itemValue), TimeZoneHolder.getTimeZone()).toString()));
        // Related entities.
        element.appendChild(getItemValueDefinitionElement(document, itemValue.getItemValueDefinition()));
        return element;
    }

    protected Element getItemValueDefinitionElement(Document document, ItemValueDefinition itemValueDefinition) {
        Element element = document.createElement("ItemValueDefinition");
        element.appendChild(itemValueDefinition.getValueDefinition().getElement(document));
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