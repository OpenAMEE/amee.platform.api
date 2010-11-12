package com.amee.base.resource;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class MissingAttributeException extends ResourceException {

    private String attributeName;

    public MissingAttributeException(String attributeName) {
        super();
        this.setAttributeName(attributeName);
    }

    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            o.put("status", "ERROR");
            o.put("error", "An attribute was missing: " + getAttributeName());
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("ERROR"));
        rootElem.addContent(new Element("Error").setText("An attribute was missing: " + getAttributeName()));
        return new Document(rootElem);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}