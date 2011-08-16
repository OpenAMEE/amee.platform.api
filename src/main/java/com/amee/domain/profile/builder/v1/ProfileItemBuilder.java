package com.amee.domain.profile.builder.v1;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.DataItemService;
import com.amee.domain.ItemBuilder;
import com.amee.domain.ProfileItemService;
import com.amee.domain.TimeZoneHolder;
import com.amee.domain.data.builder.DataItemBuilder;
import com.amee.domain.data.builder.v1.ItemValueBuilder;
import com.amee.domain.environment.Environment;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.profile.ProfileItem;
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

    private ProfileItem item;
    private ProfileItemService profileItemService;
    private DataItemService dataItemService;

    public ProfileItemBuilder(ProfileItem item, DataItemService dataItemService, ProfileItemService profileItemService) {
        this.item = item;
        this.dataItemService = dataItemService;
        this.profileItemService = profileItemService;
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
        double value;
        if (profileItemService.isSingleFlight(item)) {
            value = item.getAmounts().defaultValueAsDouble();
        } else {
            value = item.getAmounts().defaultValueAsAmount().convert(AmountPerUnit.MONTH).getValue();
        }

        // Check for NaN or Infinity which are invalid in JSON.
        if (Double.isInfinite(value)) {
            obj.put("amountPerMonth", "Infinity");
        } else if (Double.isNaN(value)) {
            obj.put("amountPerMonth", "NaN");
        } else {
            obj.put("amountPerMonth", value);
        }
        obj.put("validFrom", DAY_DATE_FMT.format(StartEndDate.getLocalStartEndDate(item.getStartDate(), TimeZoneHolder.getTimeZone())));
        obj.put("end", Boolean.toString(item.isEnd()));
        obj.put("dataItem", new DataItemBuilder(item.getDataItem(), dataItemService).getIdentityJSONObject());
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
        element.appendChild(new DataItemBuilder(item.getDataItem(), dataItemService).getIdentityElement(document));
        if (detailed) {
            element.appendChild(item.getProfile().getIdentityElement(document));
        }
        return element;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, "ItemValue", item);
    }
}