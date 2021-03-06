package com.amee.domain.data.builder.v1;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.Builder;
import com.amee.domain.ItemBuilder;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ItemValueBuilder implements Builder {

    private BaseItemValue itemValue;
    private Builder itemValueDefinitionRenderer;
    private ItemBuilder itemBuilder;

    public ItemValueBuilder(BaseItemValue itemValue) {
        this.itemValue = itemValue;
        this.itemValueDefinitionRenderer = new ItemValueDefinitionBuilder(itemValue.getItemValueDefinition());
    }

    public ItemValueBuilder(BaseItemValue itemValue, ItemBuilder itemBuilder) {
        this(itemValue);
        this.itemBuilder = itemBuilder;
    }

    @Override
    public JSONObject getJSONObject() throws JSONException {
        throw new UnsupportedOperationException();
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", itemValue.getUid());
        obj.put("path", itemValue.getPath());
        obj.put("name", itemValue.getName());
        obj.put("value", itemValue.getValueAsString());
        ItemValueDefinition itemValueDefinition = itemValue.getItemValueDefinition();
        itemValueDefinition.setBuilder(itemValueDefinitionRenderer);
        obj.put("itemValueDefinition", itemValueDefinition.getJSONObject(false));
        if (detailed) {
            obj.put("created", itemValue.getCreated());
            obj.put("modified", itemValue.getModified());
            obj.put("item", itemBuilder.getIdentityJSONObject());
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
        ItemValueDefinition itemValueDefinition = itemValue.getItemValueDefinition();
        itemValueDefinition.setBuilder(itemValueDefinitionRenderer);
        element.appendChild(itemValueDefinition.getElement(document, false));
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
