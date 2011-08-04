package com.amee.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public enum AMEEStatus implements Serializable {

    // The order of these values must not be changed!
    // Hibernate has mapped them to ordinal values.
    // Any new values must be appended to the list.
    TRASH("TRASH", "Trash"),
    ACTIVE("ACTIVE", "Active"),
    DEPRECATED("DEPRECATED", "Deprecated");

    private final String name;
    private final String label;

    AMEEStatus(String name, String label) {
        this.name = name;
        this.label = label;
    }
    
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
        for (AMEEStatus status : AMEEStatus.values()) {
            choices.put(status.name, status.label);
        }
        return choices;
    }

    public static JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        Map<String, String> choices = AMEEStatus.getChoices();
        for (Map.Entry<String, String> e : choices.entrySet()) {
            obj.put(e.getKey(), e.getValue());
        }
        return obj;
    }

    public static Element getElement(Document document) {
        Element statesElem = document.createElement("States");
        Map<String, String> choices = AMEEStatus.getChoices();
        for (Map.Entry<String, String> e : choices.entrySet()) {
            Element statusElem = document.createElement("Status");
            statusElem.setAttribute("name", e.getKey());
            statusElem.setAttribute("label", e.getValue());
            statesElem.appendChild(statusElem);
        }
        return statesElem;
    }
}