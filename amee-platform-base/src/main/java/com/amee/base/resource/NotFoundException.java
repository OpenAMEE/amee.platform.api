package com.amee.base.resource;

import org.jdom2.Document;
import org.jdom2.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link ResourceException} indicating that the resource is not available (404).
 */
public class NotFoundException extends ResourceException {

    public NotFoundException() {
        super();
    }

    /**
     * Produces a {@link JSONObject} where the 'status' node contains 'NOT_FOUND'.
     *
     * @return the {@link JSONObject} response representation.
     */
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

    /**
     * Produces a {@link Document} where the 'Status' node contains 'NOT_FOUND'.
     *
     * @return the {@link Document} response representation.
     */
    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("NOT_FOUND"));
        return new Document(rootElem);
    }
}
