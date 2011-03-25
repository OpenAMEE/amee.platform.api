package com.amee.base.resource;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link ResourceException} indicating that the application experienced an internal error (500).
 */
public class InternalErrorException extends ResourceException {

    public InternalErrorException() {
        super();
    }

    /**
     * Produces a {@link JSONObject} where the 'status' node contains 'INTERNAL_ERROR'.
     *
     * @return the {@link JSONObject} response representation.
     */
    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            o.put("status", "INTERNAL_ERROR");
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    /**
     * Produces a {@link Document} where the 'Status' node contains 'INTERNAL_ERROR'.
     *
     * @return the {@link Document} response representation.
     */
    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("INTERNAL_ERROR"));
        return new Document(rootElem);
    }
}
