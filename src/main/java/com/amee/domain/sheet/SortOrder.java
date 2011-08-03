package com.amee.domain.sheet;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.LinkedHashMap;
import java.util.Map;

public enum SortOrder {

    ASC("ASC", "ASC"),
    DESC("DESC", "DESC");

    private final String name;
    private final String label;

    SortOrder(String name, String label) {
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
        for (SortOrder sortOrder : values()) {
            choices.put(sortOrder.name, sortOrder.label);
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
        Element element = document.createElement("SortOrders");
        Map<String, String> choices = getChoices();
        for (String name : choices.keySet()) {
            Element elem = document.createElement("SortOrder");
            elem.setAttribute("name", name);
            elem.setAttribute("label", choices.get(name));
        }
        return element;
    }
}