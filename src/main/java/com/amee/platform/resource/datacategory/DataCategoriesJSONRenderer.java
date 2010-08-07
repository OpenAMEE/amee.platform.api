package com.amee.platform.resource.datacategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class DataCategoriesJSONRenderer implements DataCategoriesRenderer {

    private DataCategoryJSONRenderer dataCategoryRenderer;
    private JSONObject rootObj;
    private JSONArray categoriesArr;

    public DataCategoriesJSONRenderer() {
        super();
        this.dataCategoryRenderer = new DataCategoryJSONRenderer(false);
        start();
    }

    public void start() {
        rootObj = new JSONObject();
        categoriesArr = new JSONArray();
        put(rootObj, "categories", categoriesArr);
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    public void newDataCategory() {
        categoriesArr.put(dataCategoryRenderer.getDataCategoryJSONObject());
    }

    public void setTruncated(boolean truncated) {
        put(rootObj, "resultsTruncated", truncated);
    }

    public DataCategoryRenderer getDataCategoryRenderer() {
        return dataCategoryRenderer;
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

    public JSONObject getObject() {
        return rootObj;
    }
}
