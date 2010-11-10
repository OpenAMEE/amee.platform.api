package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.Since;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.dataitem.DataItemsResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemsJSONRenderer_3_0_0 implements DataItemsResource.Renderer {

    private JSONObject rootObj;
    private JSONArray itemsArr;

    public void start() {
        rootObj = new JSONObject();
        itemsArr = new JSONArray();
        put(rootObj, "items", itemsArr);
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    public void newDataItem(DataItemResource.Renderer renderer) {
        try {
            itemsArr.put(((JSONObject) renderer.getObject()).getJSONObject("item"));
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
