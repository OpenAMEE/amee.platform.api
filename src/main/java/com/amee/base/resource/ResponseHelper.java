package com.amee.base.resource;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class ResponseHelper {

    public static Object getOK(RequestWrapper requestWrapper) {
        return getOK(requestWrapper, null);
    }

    public static Object getOK(RequestWrapper requestWrapper, String location) {
        if (requestWrapper.getAcceptedMediaTypes().contains("application/json")) {
            return getStatusJSONObject("OK", location);
        } else {
            return getStatusDocument("OK", location);
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
        return getStatusJSONObject(status, null);
    }

    public static JSONObject getStatusJSONObject(String status, String location) {
        try {
            JSONObject o = new JSONObject();
            o.put("status", status);
            if (location != null) {
                o.put("location", location);
            }
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public static Document getStatusDocument(String status) {
        return getStatusDocument(status, null);
    }

    public static Document getStatusDocument(String status, String location) {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText(status));
        if (location != null) {
            rootElem.addContent(new Element("Location").setText(location));
        }
        return new Document(rootElem);
    }
}