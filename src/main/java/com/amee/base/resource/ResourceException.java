package com.amee.base.resource;

import org.json.JSONObject;

public abstract class ResourceException extends RuntimeException {

    public ResourceException() {
        super();
    }

    public abstract JSONObject getJSONObject();
}
