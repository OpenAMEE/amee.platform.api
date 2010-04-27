package com.amee.base.utils;

import com.amee.base.domain.IdentityObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class XMLUtils {

    public static Element getElement(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.setTextContent(value);
        return element;
    }

    public static Element getIdentityElement(Document document, String name, IdentityObject obj) {
        Element element = document.createElement(name);
        element.setAttribute("uid", obj.getUid());
        return element;
    }

    public static Element getIdentityElement(Document document, IdentityObject obj) {
        return getIdentityElement(document, obj.getClass().getSimpleName(), obj);
    }

    public static JSONObject getIdentityJSONObject(IdentityObject object) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", object.getUid());
        return obj;
    }
}
