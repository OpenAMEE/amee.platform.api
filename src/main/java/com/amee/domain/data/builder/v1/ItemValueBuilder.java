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

/**
 * This file is part of AMEE.
 * <p/>
 * AMEE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * AMEE is free software and is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by http://www.dgen.net.
 * Website http://www.amee.cc
 */
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
