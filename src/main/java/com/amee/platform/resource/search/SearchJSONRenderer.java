package com.amee.platform.resource.search;

import com.amee.platform.resource.datacategory.DataCategoryJSONRenderer;
import com.amee.platform.resource.datacategory.DataCategoryRenderer;
import com.amee.platform.resource.dataitem.DataItemRenderer;
import com.amee.platform.resource.dataitem.v_3_1.DataItemJSONRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchJSONRenderer implements SearchRenderer {

    private DataCategoryJSONRenderer dataCategoryRenderer;
    private DataItemJSONRenderer dataItemRenderer;
    private JSONObject rootObj;
    private JSONArray resultsArr;

    public SearchJSONRenderer() {
        super();
        this.dataCategoryRenderer = new DataCategoryJSONRenderer(false);
        this.dataItemRenderer = new DataItemJSONRenderer(false);
        start();
    }

    public void start() {
        rootObj = new JSONObject();
        resultsArr = new JSONArray();
        put(rootObj, "results", resultsArr);
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    public void newDataCategory() {
        resultsArr.put(dataCategoryRenderer.getDataCategoryJSONObject());
    }

    public void newDataItem() {
        resultsArr.put(dataItemRenderer.getDataItemJSONObject());
    }

    public void setTruncated(boolean truncated) {
        put(rootObj, "resultsTruncated", truncated);
    }

    public DataCategoryRenderer getDataCategoryRenderer() {
        return dataCategoryRenderer;
    }

    public DataItemRenderer getDataItemRenderer() {
        return dataItemRenderer;
    }

    protected JSONObject put(JSONObject o, String key, Object value) {
        try {
            return o.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public String getMediaType() {
        return "application/json";
    }

    public Object getObject() {
        return rootObj;
    }
}
