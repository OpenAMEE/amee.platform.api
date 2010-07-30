package com.amee.base.resource;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ResponseHelper {

    public static Object getOK(RequestWrapper requestWrapper) {
        if (requestWrapper.getAcceptedMediaTypes().contains("application/json")) {
            return getStatusJSONObject("OK");
        } else {
            return getStatusDocument("OK");
        }
    }

    public static Object getNotFound(RequestWrapper requestWrapper) {
        if (requestWrapper.getAcceptedMediaTypes().contains("application/json")) {
            return getStatusJSONObject("NOT_FOUND");
        } else {
            return getStatusDocument("NOT_FOUND");
        }
    }

    public static JSONObject getStatusJSONObject(String status) {
        try {
            JSONObject o = new JSONObject();
            o.put("status", status);
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public static Document getStatusDocument(String status) {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText(status));
        return new Document(rootElem);
    }
}