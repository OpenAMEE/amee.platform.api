package com.amee.base.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class XMLUtils {

    public static Element getElement(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.setTextContent(value);
        return element;
    }
}
