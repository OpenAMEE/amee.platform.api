package com.amee.base.resource;

import org.jdom2.Document;
import org.jdom2.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link ResourceException} indicating that the application does not support the requested media type.
 */
public class MediaTypeNotSupportedException extends ResourceException {

    public MediaTypeNotSupportedException() {
        super();
    }
    
    /**
     * Constructor that accepts an error message.
     * 
     * @param message the message to include along with the exception.
     */
    public MediaTypeNotSupportedException(String message) {
        super(message);
    }

    /**
     * Produces a {@link JSONObject} where the 'status' node contains 'MEDIA_TYPE_NOT_SUPPORTED'.
     *
     * @return the {@link JSONObject} response representation.
     */
    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            o.put("status", "MEDIA_TYPE_NOT_SUPPORTED");
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    /**
     * Produces a {@link Document} where the 'Status' node contains 'MEDIA_TYPE_NOT_SUPPORTED'.
     *
     * @return the {@link Document} response representation.
     */
    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("MEDIA_TYPE_NOT_SUPPORTED"));
        return new Document(rootElem);
    }
}
