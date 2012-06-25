package com.amee.base.resource;

import org.jdom2.Document;
import org.jdom2.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link ResourceException} indicating that the request timed-out whilst the resource was working.
 */
public class TimedOutException extends ResourceException {

    /**
     * Construct a TimedOutException.
     */
    public TimedOutException() {
        super();
    }

    /**
     * Produces a {@link JSONObject} where the 'status' node contains 'TIMED_OUT'.
     *
     * @return the {@link JSONObject} response representation.
     */
    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            o.put("status", "TIMED_OUT");
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    /**
     * Produces a {@link Document} where the 'Status' node contains 'TIMED_OUT'.
     *
     * @return the {@link Document} response representation.
     */
    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("TIMED_OUT"));
        return new Document(rootElem);
    }
}