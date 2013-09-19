package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.datacategory.DataCategoriesResource;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataCategoriesJSONRenderer_3_0_0 implements DataCategoriesResource.Renderer {

    private JSONObject rootObj;
    private JSONArray categoriesArr;

    public void start() {
        rootObj = new JSONObject();
        categoriesArr = new JSONArray();
        ResponseHelper.put(rootObj, "categories", categoriesArr);
    }

    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    public void newDataCategory(DataCategoryResource.Renderer renderer) {
        try {
            categoriesArr.put(((JSONObject) renderer.getObject()).getJSONObject("category"));
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

    public JSONObject getObject() {
        return rootObj;
    }
}
