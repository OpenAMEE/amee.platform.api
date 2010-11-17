package com.amee.base.resource;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class MediaTypeNotSupportedException extends ResourceException {

    public MediaTypeNotSupportedException() {
        super();
    }

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

    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("MEDIA_TYPE_NOT_SUPPORTED"));
        return new Document(rootElem);
    }
}
