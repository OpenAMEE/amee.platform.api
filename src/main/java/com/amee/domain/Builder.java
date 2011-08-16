package com.amee.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Builder {

    public JSONObject getJSONObject() throws JSONException;

    public JSONObject getJSONObject(boolean detailed) throws JSONException;

    public Element getElement(Document document);

    public Element getElement(Document document, boolean detailed);

    public JSONObject getIdentityJSONObject() throws JSONException;

    public Element getIdentityElement(Document document);

}
