package com.amee.platform.resource.search;

import com.amee.platform.resource.datacategory.DataCategoryRenderer;
import com.amee.platform.resource.dataitem.DataItemRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class SearchJSONRenderer implements SearchRenderer {

    private JSONObject rootObj;
    private JSONArray resultsArr;

    public void start() {
        rootObj = new JSONObject();
        resultsArr = new JSONArray();
        put(rootObj, "results", resultsArr);
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    @Override
    public void newDataCategory(DataCategoryRenderer dataCategoryRenderer) {
        try {
            resultsArr.put(((JSONObject) dataCategoryRenderer.getObject()).getJSONObject("category"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @Override
    public void newDataItem(DataItemRenderer dataItemRenderer) {
        try {
            resultsArr.put(((JSONObject) dataItemRenderer.getObject()).getJSONObject("item"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public void setTruncated(boolean truncated) {
        put(rootObj, "resultsTruncated", truncated);
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
