package com.amee.base.resource;

import org.json.JSONException;
import org.json.JSONObject;

public class NotFoundException extends ResourceException {

    public NotFoundException() {
        super();
    }

    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            o.put("status", "NOT_FOUND");
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }
}
