package com.amee.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public enum PagerSetType implements Serializable {

    ALL("ALL", "All"),
    EXCLUDE("EXCLUDE", "Exclude"),
    INCLUDE("INCLUDE", "Include");

    private final String name;
    private final String label;

    PagerSetType(String name, String label) {
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
        for (PagerSetType pagerSetType : values()) {
            choices.put(pagerSetType.getName(), pagerSetType.getLabel());
        }
        return choices;
    }

    public static JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        Map<String, String> choices = getChoices();
        for (String key : choices.keySet()) {
            obj.put(key, choices.get(key));
        }
        return obj;
    }

    public static Element getElement(Document document) {
        Element element = document.createElement("PagerSets");
        Map<String, String> choices = getChoices();
        for (String name : choices.keySet()) {
            Element elem = document.createElement("PagerSetType");
            elem.setAttribute("name", name);
            elem.setAttribute("label", choices.get(name));
        }
        return element;
    }
}
