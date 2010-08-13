package com.amee.platform.resource.dataitem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class DataItemsJSONRenderer implements DataItemsRenderer {

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

    public void newDataItem(DataItemRenderer dataItemRenderer) {
        try {
            itemsArr.put(((JSONObject) dataItemRenderer.getObject()).getJSONObject("item"));
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
