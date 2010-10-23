package com.amee.domain.data.builder;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.ItemBuilder;
import com.amee.domain.TimeZoneHolder;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemValue;
import com.amee.domain.data.builder.v2.ItemValueBuilder;
import com.amee.domain.environment.Environment;
import com.amee.platform.science.StartEndDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DataItemBuilder implements ItemBuilder {

    private DataItem item;

    public DataItemBuilder(DataItem item) {
        this.item = item;
    }

    private void buildElement(Document document, Element element, boolean detailed, boolean showHistory) {
        element.setAttribute("uid", item.getUid());
        element.appendChild(XMLUtils.getElement(document, "Name", item.getDisplayName()));
        Element itemValuesElem = document.createElement("ItemValues");
        if (showHistory) {
            buildElementItemValuesWithHistory(document, itemValuesElem);
        } else {
            buildElementItemValues(document, itemValuesElem);
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

    private void buildElementItemValues(Document document, Element itemValuesElem) {
        for (ItemValue itemValue : item.getItemValues()) {
            itemValuesElem.appendChild(new ItemValueBuilder(itemValue, this).getElement(document, false));
        }
    }

    private void buildElementItemValuesWithHistory(Document document, Element itemValuesElem) {
        for (Object o1 : item.getItemValuesMap().keySet()) {
            String path = (String) o1;
            Element itemValueSeries = document.createElement("ItemValueSeries");
            itemValueSeries.setAttribute("path", path);
            for (Object o2 : item.getAllItemValues(path)) {
                ItemValue itemValue = (ItemValue) o2;
                itemValueSeries.appendChild(new ItemValueBuilder(itemValue).getElement(document, false));
            }
            itemValuesElem.appendChild(itemValueSeries);
        }
    }

    public Element getIdentityElement(Document document) {
        return XMLUtils.getIdentityElement(document, "DataItem", item);
    }

    private void buildJSON(JSONObject obj, boolean detailed, boolean showHistory) throws JSONException {
        obj.put("uid", item.getUid());
        obj.put("name", item.getDisplayName());
        JSONArray itemValues = new JSONArray();
        if (showHistory) {
            buildJSONItemValuesWithHistory(itemValues);
        } else {
            buildJSONItemValues(itemValues);
        }
        obj.put("itemValues", itemValues);
        if (detailed) {
            obj.put("created", item.getCreated());
            obj.put("modified", item.getModified());
            obj.put("environment", Environment.ENVIRONMENT.getJSONObject());
            obj.put("itemDefinition", item.getItemDefinition().getJSONObject());
            obj.put("dataCategory", item.getDataCategory().getIdentityJSONObject());
        }
    }

    private void buildJSONItemValues(JSONArray itemValues) throws JSONException {
        for (ItemValue itemValue : item.getItemValues()) {
            itemValues.put(new ItemValueBuilder(itemValue, this).getJSONObject(false));
        }
    }

    private void buildJSONItemValuesWithHistory(JSONArray itemValues) throws JSONException {
        for (Object o1 : item.getItemValuesMap().keySet()) {
            String path = (String) o1;
            JSONObject values = new JSONObject();
            JSONArray valueSet = new JSONArray();
            for (Object o2 : item.getAllItemValues(path)) {
                ItemValue itemValue = (ItemValue) o2;
                valueSet.put(new ItemValueBuilder(itemValue).getJSONObject(false));
            }
            values.put(path, valueSet);
            itemValues.put(values);
        }
    }

    /**
     * Get the JSON representation of this DataItem.
     *
     * @param detailed    - true if a detailed representation is required.
     * @param showHistory - true if the representation should include any historical sequences of {@link com.amee.domain.data.LegacyItemValue)s.
     * @return the JSON representation.
     * @throws JSONException
     */
    public JSONObject getJSONObject(boolean detailed, boolean showHistory) throws JSONException {
        JSONObject obj = new JSONObject();
        buildJSON(obj, detailed, showHistory);
        obj.put("path", item.getPath());
        obj.put("label", item.getLabel());
        obj.put("startDate", StartEndDate.getLocalStartEndDate(item.getStartDate(), TimeZoneHolder.getTimeZone()).toString());
        obj.put("endDate",
                (item.getEndDate() != null) ? StartEndDate.getLocalStartEndDate(item.getEndDate(), TimeZoneHolder.getTimeZone()).toString() : "");
        return obj;
    }

    @Override
    public JSONObject getJSONObject() throws JSONException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        return getJSONObject(detailed, false);
    }

    @Override
    public Element getElement(Document document) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the DOM representation of this DataItem.
     *
     * @param detailed    - true if a detailed representation is required.
     * @param showHistory - true if the representation should include any historical sequences of {@link com.amee.domain.data.LegacyItemValue)s.
     * @return the DOM representation.
     */
    public Element getElement(Document document, boolean detailed, boolean showHistory) {
        Element dataItemElement = document.createElement("DataItem");
        buildElement(document, dataItemElement, detailed, showHistory);
        dataItemElement.appendChild(XMLUtils.getElement(document, "Path", item.getDisplayPath()));
        dataItemElement.appendChild(XMLUtils.getElement(document, "Label", item.getLabel()));
        dataItemElement.appendChild(XMLUtils.getElement(document, "StartDate",
                StartEndDate.getLocalStartEndDate(item.getStartDate(), TimeZoneHolder.getTimeZone()).toString()));
        dataItemElement.appendChild(XMLUtils.getElement(document, "EndDate",
                (item.getEndDate() != null) ? StartEndDate.getLocalStartEndDate(item.getEndDate(), TimeZoneHolder.getTimeZone()).toString() : ""));
        return dataItemElement;
    }

    public Element getElement(Document document, boolean detailed) {
        return getElement(document, detailed, false);
    }

    @Override
    public JSONObject getIdentityJSONObject() throws JSONException {
        return XMLUtils.getIdentityJSONObject(item);
    }
}
