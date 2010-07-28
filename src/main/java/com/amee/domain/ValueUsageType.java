package com.amee.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public enum ValueUsageType implements Serializable {

    // The order of these values must not be changed!
    // Hibernate has mapped them to ordinal values.
    // Any new values must be appended to the list.
    UNDEFINED("UNDEFINED", "Undefined"),
    REQUIRED("REQUIRED", "Required"),
    FORBIDDEN("FORBIDDEN", "Forbidden"),
    OPTIONAL("OPTIONAL", "Optional"),
    IGNORED("IGNORED", "Ignored");

    ValueUsageType(String name, String label) {
        this.name = name;
        this.label = label;
    }

    private final String name;
    private final String label;

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }

    public static Map<String, String> getChoices() {
        Map<String, String> choices = new LinkedHashMap<String, String>();
        for (ValueUsageType valueUsageType : ValueUsageType.values()) {
            choices.put(valueUsageType.name, valueUsageType.label);
        }
        return choices;
    }

    public static JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        Map<String, String> choices = ValueUsageType.getChoices();
        for (Map.Entry<String, String> e : choices.entrySet()) {
            obj.put(e.getKey(), e.getValue());
        }
        return obj;
    }

    public static Element getElement(Document document) {
        Element element = document.createElement("ValueUsages");
        Map<String, String> choices = ValueUsageType.getChoices();
        for (Map.Entry<String, String> e : choices.entrySet()) {
            Element valueUsageElem = document.createElement("ValueUsage");
            valueUsageElem.setAttribute("name", e.getKey());
            valueUsageElem.setAttribute("label", e.getValue());
            element.appendChild(valueUsageElem);
        }
        return element;
    }
}