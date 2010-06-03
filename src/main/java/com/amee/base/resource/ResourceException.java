package com.amee.base.resource;

import org.jdom.Document;
import org.json.JSONObject;

public abstract class ResourceException extends RuntimeException {

    public ResourceException() {
        super();
    }

    public Object getResponse(RequestWrapper requestWrapper) {
        if (requestWrapper.getAcceptedMediaTypes().contains("application/json")) {
            return getJSONObject();
        } else {
            return getDocument();
        }
    }

    public abstract JSONObject getJSONObject();

    public abstract Document getDocument();
}
