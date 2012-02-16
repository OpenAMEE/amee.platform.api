package com.amee.domain.sheet;

import com.amee.base.utils.XMLUtils;
import com.amee.domain.APIObject;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A Choice is a name value pair. Eg, used for data item value selections.
 */
public class Choice implements Serializable, Comparable, APIObject {

    private String name = "";
    private String value = "";

    private Choice() {
        super();
    }

    public Choice(String value) {
        this();
        this.name = value;
        this.value = value;
    }

    public Choice(String name, String value) {
        this();
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Choice)) {
            return false;
        }
        
        Choice other = (Choice) o;
        return getName().equalsIgnoreCase(other.getName());
    }

    @Override
    public int compareTo(Object o) {
        Choice other = (Choice) o;
        return getName().compareToIgnoreCase(other.getName());
    }

    @Override
    public int hashCode() {
        return getName().toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Converts the given name/value pair to a choice.
     *
     * @param nameAndValue a name/value String in the format name=value. A single value may also be provided in which
     *                     case the returned Choice's name and value fields will be equal.
     * @return a Choice representing the given name/value String.
     */
    public static Choice parseNameAndValue(String nameAndValue) {
        Choice choice = new Choice();
        if (nameAndValue != null) {
            String[] arr = nameAndValue.trim().split("=");
            if (arr.length > 1) {
                choice = new Choice(arr[0],arr[1]);
            } else if (arr.length > 0) {
                choice = new Choice(arr[0]);
            }
        }
        return choice;
    }

    /**
     * Converts a list of name/value pairs to a List of Choices.
     *
     * @param c a comma separated list of name/value pairs in the format name=value. A single value may also be
     *          provided for each choice in which case the name and value fields will be equal.
     * @return a List of Choices representing the given name/value pairs.
     */
    public static List<Choice> parseChoices(String c) {
        List<Choice> choices = new ArrayList<Choice>();
        if ((c != null) && !c.isEmpty()) {
            String[] arr = c.split(",");
            for (String s : arr) {
                choices.add(Choice.parseNameAndValue(s));
            }
        }
        return choices;
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("name", getName());
        obj.put("value", getValue());
        return obj;
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        return getJSONObject();
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        return new JSONObject();
    }

    public Element getElement(Document document) {
        Element element = document.createElement("Choice");
        element.appendChild(XMLUtils.getElement(document, "Name", getName()));
        element.appendChild(XMLUtils.getElement(document, "Value", getValue()));
        return element;
    }

    public Element getElement(Document document, boolean detailed) {
        return getElement(document);
    }

    public Element getIdentityElement(Document document) {
        return document.createElement("Choice");
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
