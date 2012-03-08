package com.amee.base.resource;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A utility class containing helper methods for producing response representation.
 */
public class ResponseHelper {

    /**
     * Private constructor to prevent direct instantiation.
     */
    private ResponseHelper() {
        throw new AssertionError();
    }

    /**
     * Get an 'OK' response representation.
     *
     * @param requestWrapper RequestWrapper for this request
     * @return the response representation object
     */
    public static Object getOK(RequestWrapper requestWrapper) {
        return getOK(requestWrapper, null);
    }

    /**
     * Get an 'OK' response representation.
     *
     * @param requestWrapper RequestWrapper for this request
     * @param location       the location to include in the response
     * @return the response representation object
     */
    public static Object getOK(RequestWrapper requestWrapper, String location) {
        return getOK(requestWrapper, location, null);
    }
    
    public static Object getOK(RequestWrapper requestWrapper, String location, String uid) {
        if (requestWrapper.getAcceptedMediaTypes().contains("application/json")) {
            return getStatusJSONObject("OK", location, uid);
        } else {
            return getStatusDocument("OK", location, uid);
        }
    }

    /**
     * Get a {@link JSONObject} response representation with the given status identifier.
     *
     * @param status identifier
     * @return the response representation object
     */
    public static JSONObject getStatusJSONObject(String status) {
        return getStatusJSONObject(status, null, null);
    }

    /**
     * Get a {@link JSONObject} response representation with the given status identifier.
     *
     * @param status   identifier
     * @param location the location to include in the response
     * @return the response representation object
     */
    public static JSONObject getStatusJSONObject(String status, String location, String uid) {
        try {
            JSONObject o = new JSONObject();
            o.put("status", status);

            if (location != null) {
                o.put("location", location);
            }

            if (uid != null) {
                JSONObject entityObj = new JSONObject();
                entityObj.put("uid", uid);
                o.put("entity", entityObj);
            }
            
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    /**
     * Get a {@link Document} response representation with the given status identifier.
     *
     * @param status identifier
     * @return the response representation object
     */
    public static Document getStatusDocument(String status) {
        return getStatusDocument(status, null, null);
    }

    /**
     * Get a {@link Document} response representation with the given status identifier.
     *
     * @param status   identifier
     * @param location the location to include in the response
     * @return the response representation object
     */
    public static Document getStatusDocument(String status, String location, String uid) {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText(status));

        if (location != null) {
            rootElem.addContent(new Element("Location").setText(location));
        }

        if (uid != null) {
            rootElem.addContent(new Element("Entity").setAttribute("uid", uid));
        }

        return new Document(rootElem);
    }

    /**
     * Add an object to a {@link JSONObject} with the given key. Any {@link JSONException} is instead thrown
     * as a {@link RuntimeException}.
     *
     * @param jsonObject {@link JSONObject} to add value to
     * @param key        the value key
     * @param value      the value object
     * @return the {@link JSONObject} passed in, to support chained building
     */
    public static JSONObject put(JSONObject jsonObject, String key, Object value) {
        try {
            return jsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }
}