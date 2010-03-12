package com.amee.base;

import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.hibernate.util.DTDEntityResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;

public abstract class XMLUtils {

    public static Element getElement(Document document, String name, String value) {
        Element element = document.createElement(name);
        element.setTextContent(value);
        return element;
    }

    public static org.dom4j.Element getRootElement(InputStream stream) throws DocumentException {
        if (stream != null) {
            SAXReader saxReader = new SAXReader();
            saxReader.setEntityResolver(new DTDEntityResolver());
            saxReader.setMergeAdjacentText(true);
            return saxReader.read(stream).getRootElement();
        } else {
            return null;
        }
    }
}
