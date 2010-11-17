package com.amee.base.resource;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class TimedOutException extends ResourceException {

    public TimedOutException() {
        super();
    }

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

    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        rootElem.addContent(new Element("Status").setText("TIMED_OUT"));
        return new Document(rootElem);
    }
}