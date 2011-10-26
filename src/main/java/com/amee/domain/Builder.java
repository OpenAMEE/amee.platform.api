package com.amee.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Builder {

    JSONObject getJSONObject() throws JSONException;

    JSONObject getJSONObject(boolean detailed) throws JSONException;

    Element getElement(Document document);

    Element getElement(Document document, boolean detailed);

    JSONObject getIdentityJSONObject() throws JSONException;

    Element getIdentityElement(Document document);

}
