package com.amee.base.resource;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ValidationResult implements Serializable {

    private HashMap<String, String> values = new HashMap<String, String>();
    private List<Map<String, String>> errors = new ArrayList<Map<String, String>>();

    public ValidationResult() {
        super();
    }

    public ValidationResult(JSONObject obj) {
        super();
        try {
            // Load values.
            if (obj.has("values")) {
                JSONObject valuesObj = obj.getJSONObject("values");
                Iterator iterator = valuesObj.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    getValues().put(key, valuesObj.getString(key));
                }
            }
            // Load Errors.
            if (obj.has("errors")) {
                JSONArray errorsArr = obj.getJSONArray("errors");
                for (int i = 0; i < errorsArr.length(); i++) {
                    Map<String, String> fieldAndCode = new HashMap<String, String>();
                    fieldAndCode.put("field", errorsArr.getJSONObject(i).getString("field"));
                    fieldAndCode.put("code", errorsArr.getJSONObject(i).getString("code"));
                    if (errorsArr.getJSONObject(i).has("value")) {
                        fieldAndCode.put("value", errorsArr.getJSONObject(i).getString("value"));
                    }
                    errors.add(fieldAndCode);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public ValidationResult(Element element) {
        super();

        // Load Values.
        Element valuesElem = element.getChild("Values");
        if (valuesElem != null) {
            for (Element valueElem : (List<Element>) valuesElem.getChildren()) {
                getValues().put(valueElem.getName(), valueElem.getValue());
            }
        }

        // Load Errors.
        Element errorsElem = element.getChild("Errors");
        if (errorsElem != null) {
            for (Element errorElem : (List<Element>) errorsElem.getChildren()) {
                if ((errorElem.getChild("Field") != null) && (errorElem.getChild("Code") != null)) {
                    Map<String, String> fieldAndCode = new HashMap<String, String>();
                    fieldAndCode.put("field", errorElem.getChild("Field").getValue());
                    fieldAndCode.put("code", errorElem.getChild("Code").getValue());
                    if (errorElem.getChild("Value") != null) {
                        fieldAndCode.put("value", errorElem.getChild("Value").getValue());
                    }
                    getErrors().add(fieldAndCode);
                }
            }
        }
    }

    public void addValue(String key, String value) {
        values.put(key, value);
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        // add values
        if (!getValues().isEmpty()) {
            JSONObject valuesObj = new JSONObject();
            for (String key : getValues().keySet()) {
                valuesObj.put(key, getValues().get(key));
            }
            obj.put("values", valuesObj);
        }
        // add errors
        if (getErrors() != null) {
            JSONArray errorsArr = new JSONArray();
            for (Map<String, String> fieldAndCode : getErrors()) {
                JSONObject fieldObj = new JSONObject();
                fieldObj.put("field", fieldAndCode.get("field"));
                fieldObj.put("code", fieldAndCode.get("code"));
                if (fieldAndCode.containsKey("value")) {
                    fieldObj.put("value", fieldAndCode.get("value"));
                }
                errorsArr.put(fieldObj);
            }
            obj.put("errors", errorsArr);
        }
        return obj;
    }

    public Element getElement() {
        Element elem = new Element("ValidationResult");
        // values
        if (!getValues().isEmpty()) {
            Element valuesElem = new Element("Values");
            for (String key : getValues().keySet()) {
                valuesElem.addContent(new Element(StringUtils.capitalize(key)).setText(getValues().get(key)));
            }
            elem.addContent(valuesElem);
        }
        // errors
        if (getErrors() != null) {
            Element errorsElem = new Element("Errors");
            for (Map<String, String> fieldAndCode : getErrors()) {
                Element errorElem = new Element("Error");
                errorElem.addContent(new Element("Field").setText(fieldAndCode.get("field")));
                errorElem.addContent(new Element("Code").setText(fieldAndCode.get("code")));
                if (fieldAndCode.containsKey("value")) {
                    errorElem.addContent(new Element("Value").setText(fieldAndCode.get("value")));
                }
                errorsElem.addContent(errorElem);
            }
            elem.addContent(errorsElem);
        }
        return elem;
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    public List<Map<String, String>> getErrors() {
        return errors;
    }

    public void setErrors(Errors e) {
        for (Object error : e.getAllErrors()) {
            if (error.getClass().isAssignableFrom(FieldError.class)) {
                FieldError fieldError = (FieldError) error;
                Map<String, String> fieldAndCode = new HashMap<String, String>();
                fieldAndCode.put("field", fieldError.getField());
                fieldAndCode.put("code", fieldError.getCode());
                if ((fieldError.getRejectedValue() != null) && (fieldError.getRejectedValue() instanceof String)) {
                    fieldAndCode.put("value", (String) fieldError.getRejectedValue());
                }
                getErrors().add(fieldAndCode);
            }
        }
    }
}