package com.amee.platform.resource.search.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.search.SearchResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class SearchJSONRenderer_3_0_0 implements SearchResource.Renderer {

    private JSONObject rootObj;
    private JSONArray resultsArr;

    public void start() {
        rootObj = new JSONObject();
        resultsArr = new JSONArray();
        ResponseHelper.put(rootObj, "results", resultsArr);
    }

    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newDataCategory(DataCategoryResource.Renderer dataCategoryRenderer) {
        try {
            JSONObject category = ((JSONObject) dataCategoryRenderer.getObject()).getJSONObject("category");
            ResponseHelper.put(category, "type", "category");
            resultsArr.put(category);
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @Override
    public void newDataItem(DataItemResource.Renderer dataItemRenderer) {
        try {
            JSONObject item = ((JSONObject) dataItemRenderer.getObject()).getJSONObject("item");
            ResponseHelper.put(item, "type", "item");
            resultsArr.put(item);
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public void setTruncated(boolean truncated) {
        ResponseHelper.put(rootObj, "resultsTruncated", truncated);
    }

    public String getMediaType() {
        return "application/json";
    }

    public Object getObject() {
        return rootObj;
    }
}
