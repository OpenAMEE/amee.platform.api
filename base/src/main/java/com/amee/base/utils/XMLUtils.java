package com.amee.base.utils;

import com.amee.base.domain.IdentityObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A collection of utility methods for working with XML documents.
 */
public class XMLUtils {

    /**
     * Private constructor to prevent direct instantiation.
     */
    private XMLUtils() {
        throw new AssertionError();
    }

    /**
     * Get a new {@link Element} for the {@link Document} with the given name and value.
     *
     * @param document the current {@link Document}
     * @param name     the element name
     * @param value    the element value
     * @return the new element
     */
    public static Element getElement(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.setTextContent(value);
        return element;
    }

    /**
     * Get a new 'identity' element with the UID attribute set to the UID of the {@link IdentityObject}.
     *
     * @param document the current {@link Document}
     * @param name     the element name
     * @param obj      the {@link IdentityObject}
     * @return the new element
     */
    public static Element getIdentityElement(Document document, String name, IdentityObject obj) {
        Element element = document.createElement(name);
        element.setAttribute("uid", obj.getUid());
        return element;
    }

    /**
     * Get a new 'identity' element with the UID attribute set to the UID of the {@link IdentityObject}. The
     * element name is set the simple class name of the {@link IdentityObject} instance.
     *
     * @param document the current {@link Document}
     * @param obj      the {@link IdentityObject}
     * @return the new element
     */
    public static Element getIdentityElement(Document document, IdentityObject obj) {
        return getIdentityElement(document, obj.getClass().getSimpleName(), obj);
    }

    /**
     * Get a new 'identity' @{link JSONObject} with the UID attribute set to the UID of the {@link IdentityObject}.
     * <p/>
     * TODO: This should probably belong somewhere elsewhere as this returns a JSONObject.
     *
     * @param object the {@link IdentityObject}
     * @return the {@link JSONObject}
     * @throws JSONException
     */
    public static JSONObject getIdentityJSONObject(IdentityObject object) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("uid", object.getUid());
        return obj;
    }
}
