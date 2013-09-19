package com.amee.base.resource;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link ResourceException} indicating that the user is not authorized to access the resource.
 */
public class NotAuthorizedException extends ResourceException {

    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String reason) {
        super(reason);
    }

    /**
     * Produces a {@link JSONObject} where the 'status' node contains 'NOT_AUTHORIZED'. If there is an exception
     * message this will be included as a 'reason' node.
     *
     * @return the {@link JSONObject} response representation.
     */
    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            o.put("status", "NOT_AUTHORIZED");
            if (StringUtils.isNotBlank(getMessage())) {
                o.put("reason", getMessage());
            }
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    /**
     * Produces a {@link Document} where the 'Status' node contains 'NOT_AUTHORIZED'. If there is an exception
     * message this will be included as a 'Reason' node.
     *
     * @return the {@link Document} response representation.
     */
    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("NOT_AUTHORIZED"));
        if (StringUtils.isNotBlank(getMessage())) {
            rootElem.addContent(new Element("Reason").setText(getMessage()));
        }
        return new Document(rootElem);
    }
}
