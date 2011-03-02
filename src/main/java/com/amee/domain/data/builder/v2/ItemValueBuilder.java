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
package com.amee.domain.data.builder.v2;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.Builder;
import com.amee.domain.IItemService;
import com.amee.domain.ItemBuilder;
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
    private IItemService itemService;

    public ItemValueBuilder(BaseItemValue itemValue, IItemService itemService) {
        this.itemValue = itemValue;
        this.itemService = itemService;
    }

    public ItemValueBuilder(BaseItemValue itemValue, ItemBuilder itemBuilder, IItemService itemService) {
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
            element.appendChild(XMLUtils.getElement(document, "Unit", numberValue.getUnit()));
            element.appendChild(XMLUtils.getElement(document, "PerUnit", numberValue.getPerUnit()));
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