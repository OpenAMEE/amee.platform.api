package com.amee.domain.data.builder.v2;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.Builder;
import com.amee.domain.ItemBuilder;
import com.amee.domain.ItemService;
import com.amee.domain.TimeZoneHolder;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.NumberValue;
import com.amee.platform.science.StartEndDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ItemValueBuilder implements Builder {

    private BaseItemValue itemValue;
    private ItemBuilder itemBuilder;
    private ItemService itemService;

    public ItemValueBuilder(BaseItemValue itemValue, ItemService itemService) {
        this.itemValue = itemValue;
        this.itemService = itemService;
    }

    public ItemValueBuilder(BaseItemValue itemValue, ItemBuilder itemBuilder, ItemService itemService) {
        this(itemValue, itemService);
        this.itemBuilder = itemBuilder;
    }

    public JSONObject getJSONObject() throws JSONException {
        return getJSONObject(true);
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", itemValue.getUid());
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
        obj.put("startDate",
                StartEndDate.getLocalStartEndDate(itemService.getStartDate(itemValue), TimeZoneHolder.getTimeZone()).toString());
        obj.put("itemValueDefinition", itemValue.getItemValueDefinition().getJSONObject(false));
        obj.put("displayName", itemValue.getDisplayName());
        obj.put("displayPath", itemValue.getDisplayPath());
        if (detailed) {
            obj.put("created", itemValue.getCreated());
            obj.put("modified", itemValue.getModified());
            obj.put("item", itemBuilder.getJSONObject(true));
        }
        return obj;
    }

    public Element getElement(Document document) {
        return getElement(document, true);
    }

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement("ItemValue");
        element.setAttribute("uid", itemValue.getUid());
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
        element.appendChild(itemValue.getItemValueDefinition().getElement(document, false));
        if (detailed) {
            element.setAttribute("Created", itemValue.getCreated().toString());
            element.setAttribute("Modified", itemValue.getModified().toString());
            element.appendChild(itemBuilder.getIdentityElement(document));
        }
        return element;
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", itemValue.getUid());
        obj.put("path", itemValue.getPath());
        return obj;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, "ItemValue", itemValue);
    }
}