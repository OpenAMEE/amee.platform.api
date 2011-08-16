package com.amee.domain.profile.builder.v2;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.DataItemService;
import com.amee.domain.ItemBuilder;
import com.amee.domain.ProfileItemService;
import com.amee.domain.TimeZoneHolder;
import com.amee.domain.data.builder.DataItemBuilder;
import com.amee.domain.data.builder.v2.ItemValueBuilder;
import com.amee.domain.environment.Environment;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.platform.science.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Map;

public class ProfileItemBuilder implements ItemBuilder {

    private ProfileItem item;
    private AmountCompoundUnit returnUnit = CO2AmountUnit.DEFAULT;
    private DataItemService dataItemService;
    private ProfileItemService profileItemService;

    public ProfileItemBuilder(ProfileItem item, DataItemService dataItemService, ProfileItemService profileItemService, AmountCompoundUnit returnUnit) {
        this.item = item;
        this.dataItemService = dataItemService;
        this.profileItemService = profileItemService;
        this.returnUnit = returnUnit;
    }

    public ProfileItemBuilder(ProfileItem item, DataItemService dataItemService, ProfileItemService profileItemService) {
        this.item = item;
        this.dataItemService = dataItemService;
        this.profileItemService = profileItemService;
    }

    public void buildElement(JSONObject obj, boolean detailed) throws JSONException {
        obj.put("uid", item.getUid());
        obj.put("created", new StartEndDate(item.getCreated()));
        obj.put("modified", new StartEndDate(item.getModified()));

        obj.put("name", item.getName().isEmpty() ? JSONObject.NULL : item.getName());
        JSONArray itemValues = new JSONArray();
        // Find all matching active ItemValues at the item startDate
        for (BaseItemValue itemValue : profileItemService.getItemValues(item)) {
            itemValues.put(new ItemValueBuilder(itemValue, this, profileItemService).getJSONObject(false));
        }
        obj.put("itemValues", itemValues);
        if (detailed) {
            obj.put("environment", Environment.ENVIRONMENT.getJSONObject(false));
            obj.put("itemDefinition", item.getItemDefinition().getJSONObject(false));
            obj.put("dataCategory", item.getDataCategory().getIdentityJSONObject());
        }
    }

    public void buildElement(Document document, Element element, boolean detailed) {
        element.setAttribute("uid", item.getUid());
        element.setAttribute("created", new StartEndDate(item.getCreated()).toString());
        element.setAttribute("modified", new StartEndDate(item.getModified()).toString());

        element.appendChild(XMLUtils.getElement(document, "Name", item.getName()));
        Element itemValuesElem = document.createElement("ItemValues");
        // Find all matching active ItemValues at the item startDate
        for (BaseItemValue itemValue : profileItemService.getItemValues(item)) {
            itemValuesElem.appendChild(new ItemValueBuilder(itemValue, this, profileItemService).getElement(document, false));
        }
        element.appendChild(itemValuesElem);
        if (detailed) {
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

        JSONObject amount = new JSONObject();
        double value = item.getAmounts().defaultValueAsAmount().convert(returnUnit).getValue();

        // Check for NaN or Infinity which are invalid in JSON.
        if (Double.isInfinite(value)) {
            amount.put("value", "Infinity");
        } else if (Double.isNaN(value)) {
            amount.put("value", "NaN");
        } else {
            amount.put("value", value);
        }
        amount.put("unit", returnUnit.toString());
        obj.put("amount", amount);

        // Multiple return values
        JSONObject amounts = new JSONObject();

        // Create an array of amount objects
        JSONArray amountArray = new JSONArray();
        for (Map.Entry<String, ReturnValue> entry : item.getAmounts().getReturnValues().entrySet()) {

            // Create an Amount object
            JSONObject amountObj = new JSONObject();
            double returnValue = entry.getValue().getValue();

            // Check for NaN or Infinity which are invalid in JSON.
            if (Double.isInfinite(returnValue)) {
                amountObj.put("value", "Infinity");
            } else if (Double.isNaN(returnValue)) {
                amountObj.put("value", "NaN");
            } else {
                amountObj.put("value", returnValue);
            }
            amountObj.put("type", entry.getKey());
            amountObj.put("unit", entry.getValue().getUnit());
            amountObj.put("perUnit", entry.getValue().getPerUnit());
            if (entry.getKey().equals(item.getAmounts().getDefaultType())) {
                amountObj.put("default", "true");
            }

            // Add the object to the amounts array
            amountArray.put(amountObj);
        }

        // Add the amount array to the amounts object.
        amounts.put("amount", amountArray);

        // Create an array of note objects
        JSONArray noteArray = new JSONArray();
        for (Note note : item.getAmounts().getNotes()) {
            JSONObject noteObj = new JSONObject();
            noteObj.put("type", note.getType());
            noteObj.put("value", note.getValue());

            // Add the note object to the notes array
            noteArray.put(noteObj);
        }

        // Add the notes array to the amounts object.
        if (noteArray.length() > 0) {
            amounts.put("note", noteArray);
        }

        obj.put("amounts", amounts);

        // Convert to user's time zone
        obj.put("startDate", StartEndDate.getLocalStartEndDate(item.getStartDate(), TimeZoneHolder.getTimeZone()).toString());
        obj.put("endDate", (item.getEndDate() != null) ? StartEndDate.getLocalStartEndDate(item.getEndDate(), TimeZoneHolder.getTimeZone()).toString() : "");
        obj.put("dataItem", new DataItemBuilder(item.getDataItem(), dataItemService).getIdentityJSONObject());

        // DataItem
        DataItem dataItem = item.getDataItem();
        JSONObject dataItemObj = new DataItemBuilder(dataItem, dataItemService).getIdentityJSONObject();
        dataItemObj.put("Label", dataItemService.getLabel(dataItem));
        obj.put("dataItem", dataItemObj);

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

        Element amount = document.createElement("Amount");

        amount.setTextContent(item.getAmounts().defaultValueAsAmount().convert(returnUnit).toString());
        amount.setAttribute("unit", returnUnit.toString());
        element.appendChild(amount);

        // Multiple return values
        Element amounts = document.createElement("Amounts");
        for (Map.Entry<String, ReturnValue> entry : item.getAmounts().getReturnValues().entrySet()) {
            Element multiAmount = document.createElement("Amount");
            multiAmount.setAttribute("type", entry.getKey());
            multiAmount.setAttribute("unit", entry.getValue().getUnit());
            multiAmount.setAttribute("perUnit", entry.getValue().getPerUnit());
            if (entry.getKey().equals(item.getAmounts().getDefaultType())) {
                multiAmount.setAttribute("default", "true");
            }
            multiAmount.setTextContent(entry.getValue().getValue() + "");
            amounts.appendChild(multiAmount);
        }
        for (Note note : item.getAmounts().getNotes()) {
            Element noteElm = document.createElement("Note");
            noteElm.setAttribute("type", note.getType());
            noteElm.setTextContent(note.getValue());
            amounts.appendChild(noteElm);
        }
        element.appendChild(amounts);

        // Convert to user's time zone
        element.appendChild(XMLUtils.getElement(document, "StartDate",
                StartEndDate.getLocalStartEndDate(item.getStartDate(), TimeZoneHolder.getTimeZone()).toString()));
        element.appendChild(XMLUtils.getElement(document, "EndDate",
                (item.getEndDate() != null) ? StartEndDate.getLocalStartEndDate(item.getEndDate(), TimeZoneHolder.getTimeZone()).toString() : ""));

        // DataItem
        DataItem dataItem = item.getDataItem();
        Element dataItemElement = new DataItemBuilder(dataItem, dataItemService).getIdentityElement(document);
        dataItemElement.appendChild(XMLUtils.getElement(document, "Label", dataItemService.getLabel(dataItem)));

        element.appendChild(dataItemElement);

        if (detailed) {
            element.appendChild(item.getProfile().getIdentityElement(document));
        }
        return element;
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, "ItemValue", item);
    }
}
