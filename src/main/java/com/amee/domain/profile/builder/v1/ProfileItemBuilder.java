/**
 * This file is part of AMEE.
 *
 * AMEE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * AMEE is free software and is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by http://www.dgen.net.
 * Website http://www.amee.cc
 */
package com.amee.domain.profile.builder.v1;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.IProfileItemService;
import com.amee.domain.ItemBuilder;
import com.amee.domain.TimeZoneHolder;
import com.amee.domain.data.builder.DataItemBuilder;
import com.amee.domain.data.builder.v1.ItemValueBuilder;
import com.amee.domain.environment.Environment;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.profile.NuProfileItem;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.StartEndDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ProfileItemBuilder implements ItemBuilder {

    private static final String DAY_DATE = "yyyyMMdd";
    private static DateFormat DAY_DATE_FMT = new SimpleDateFormat(DAY_DATE);

    private NuProfileItem item;
    private IProfileItemService profileItemService;

    public ProfileItemBuilder(NuProfileItem item) {
        this.item = item;
    }

    public void buildElement(JSONObject obj, boolean detailed) throws JSONException {
        obj.put("uid", item.getUid());
        obj.put("name", item.getDisplayName());
        JSONArray itemValues = new JSONArray();
        for (BaseItemValue itemValue : profileItemService.getItemValues(item)) {
            itemValues.put(new ItemValueBuilder(itemValue, this).getJSONObject(false));
        }
        obj.put("itemValues", itemValues);
        if (detailed) {
            obj.put("created", item.getCreated());
            obj.put("modified", item.getModified());
            obj.put("environment", Environment.ENVIRONMENT.getIdentityJSONObject());
            obj.put("itemDefinition", item.getItemDefinition().getIdentityJSONObject());
            obj.put("dataCategory", item.getDataCategory().getIdentityJSONObject());
        }
    }

    public void buildElement(Document document, Element element, boolean detailed) {
        element.setAttribute("uid", item.getUid());
        element.appendChild(XMLUtils.getElement(document, "Name", item.getDisplayName()));
        Element itemValuesElem = document.createElement("ItemValues");
        for (BaseItemValue itemValue : profileItemService.getItemValues(item)) {
            itemValuesElem.appendChild(new ItemValueBuilder(itemValue, this).getElement(document, false));
        }
        element.appendChild(itemValuesElem);
        if (detailed) {
            element.setAttribute("created", item.getCreated().toString());
            element.setAttribute("modified", item.getModified().toString());
            element.appendChild(Environment.ENVIRONMENT.getIdentityElement(document));
            element.appendChild(item.getItemDefinition().getIdentityElement(document));
            element.appendChild(item.getDataCategory().getIdentityElement(document));
        }
    }

    @Override
    public JSONObject getJSONObject() throws JSONException {
        throw new UnsupportedOperationException();
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        JSONObject obj = new JSONObject();
        buildElement(obj, detailed);
        if (!profileItemService.isSingleFlight(item)) {
            obj.put("amountPerMonth", item.getAmounts().defaultValueAsAmount().convert(AmountPerUnit.MONTH).getValue());
        } else {
            obj.put("amountPerMonth", item.getAmounts().defaultValueAsDouble());
        }
        obj.put("validFrom", DAY_DATE_FMT.format(StartEndDate.getLocalStartEndDate(item.getStartDate(), TimeZoneHolder.getTimeZone())));
        obj.put("end", Boolean.toString(item.isEnd()));
        obj.put("dataItem", new DataItemBuilder(item.getDataItem()).getIdentityJSONObject());
        if (detailed) {
            obj.put("profile", item.getProfile().getIdentityJSONObject());
        }
        return obj;
    }

    @Override
    public Element getElement(Document document) {
        throw new UnsupportedOperationException();
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", item.getUid());
        obj.put("path", item.getPath());
        return obj;
    }

    public Element getElement(Document document, boolean detailed) {
        Element element = document.createElement("ProfileItem");
        buildElement(document, element, detailed);

        if (!profileItemService.isSingleFlight(item)) {
            element.appendChild(XMLUtils.getElement(document, "AmountPerMonth",
                    item.getAmounts().defaultValueAsAmount().convert(AmountPerUnit.MONTH).getValue() + ""));
        } else {
            element.appendChild(XMLUtils.getElement(document, "AmountPerMonth", item.getAmounts().defaultValueAsDouble() + ""));
        }
        element.appendChild(XMLUtils.getElement(document, "ValidFrom",
                DAY_DATE_FMT.format(StartEndDate.getLocalStartEndDate(item.getStartDate(), TimeZoneHolder.getTimeZone()))));
        element.appendChild(XMLUtils.getElement(document, "End", Boolean.toString(item.isEnd())));
        element.appendChild(new DataItemBuilder(item.getDataItem()).getIdentityElement(document));
        if (detailed) {
            element.appendChild(item.getProfile().getIdentityElement(document));
        }
        return element;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, "ItemValue", item);
    }
}