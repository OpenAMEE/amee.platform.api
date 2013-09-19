package com.amee.base.resource;

import org.jdom2.Document;
import org.jdom2.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link com.amee.base.resource.ResourceException} indicating that an expected request parameter is missing.
 */
public class MissingParameterException extends ResourceException {

    private String parameterName;

    /**
     * Construct a MissingParameterException with a specific parameter name.
     *
     * @param parameterName the name of the missing parameter
     */
    public MissingParameterException(String parameterName) {
        super();
        this.setParameterName(parameterName);
    }

    /**
     * Produces a {@link org.json.JSONObject} where the 'status' node contains 'INVALID'.
     *
     * @return the {@link org.json.JSONObject} response representation.
     */
    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            o.put("status", "INVALID");
            o.put("error", "A required parameter was missing: " + getParameterName());
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    /**
     * Produces a {@link org.jdom2.Document} where the 'Status' node contains 'INVALID'.
     *
     * @return the {@link org.jdom2.Document} response representation.
     */
    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("INVALID"));
        rootElem.addContent(new Element("Error").setText("A required parameter was missing: " + getParameterName()));
        return new Document(rootElem);
    }

    /**
     * Get the parameter name.
     *
     * @return the parameter name
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * Set the parameter name.
     *
     * @param parameterName the parameter name
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }
}