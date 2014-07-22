package com.amee.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;

public interface APIObject extends Serializable {

    JSONObject getJSONObject() throws JSONException;

    JSONObject getJSONObject(boolean detailed) throws JSONException;

    JSONObject getIdentityJSONObject() throws JSONException;

    Element getElement(Document document);

    Element getElement(Document document, boolean detailed);

    Element getIdentityElement(Document document);
}
