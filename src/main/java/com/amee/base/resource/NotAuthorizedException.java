package com.amee.base.resource;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class NotAuthorizedException extends ResourceException {

    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String reason) {
        super(reason);
    }

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
