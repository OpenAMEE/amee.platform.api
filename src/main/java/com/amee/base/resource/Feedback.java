package com.amee.base.resource;

import com.amee.base.utils.XMLUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Feedback implements Serializable {

    private HashMap<String, String> values = new HashMap<String, String>();
    private List<Map<String, String>> errors = new ArrayList<Map<String, String>>();

    public Feedback() {
        super();
        values = new HashMap<String, String>();
    }

    public Feedback(JSONObject obj) {
        super();
        try {
            // Load values.
            if (obj.has("values")) {
                JSONObject valuesObj = obj.getJSONObject("values");
                Iterator iterator = valuesObj.keys();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    values.put(key, valuesObj.getString(key));
                }
            }
            // Load Errors.
            if (obj.has("errors")) {
                JSONArray errorsArr = obj.getJSONArray("errors");
                for (int i = 0; i < errorsArr.length(); i++) {
                    Map<String, String> fieldAndCode = new HashMap<String, String>();
                    fieldAndCode.put("field", errorsArr.getJSONObject(i).getString("field"));
                    fieldAndCode.put("code", errorsArr.getJSONObject(i).getString("code"));
                    errors.add(fieldAndCode);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public void addValue(String key, String value) {
        values.put(key, value);
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        // add values
        if (!values.isEmpty()) {
            JSONObject valuesObj = new JSONObject();
            for (String key : values.keySet()) {
                valuesObj.put(key, values.get(key));
            }
            obj.put("values", valuesObj);
        }
        // add errors
        if (getErrors() != null) {
            JSONArray errorsArr = new JSONArray();
            for (Map<String, String> fieldAndCode : errors) {
                JSONObject fieldObj = new JSONObject();
                fieldObj.put("field", fieldAndCode.get("field"));
                fieldObj.put("code", fieldAndCode.get("code"));
                errorsArr.put(fieldObj);
            }
            obj.put("errors", errorsArr);
        }
        return obj;
    }

    public Element getElement(Document document) {
        Element elem = document.createElement("Feedback");
        // values
        if (!values.isEmpty()) {
            Element valuesElem = document.createElement("Values");
            for (String key : values.keySet()) {
                valuesElem.appendChild(XMLUtils.getElement(document, StringUtils.capitalize(key), values.get(key)));
            }
            elem.appendChild(valuesElem);
        }
        // errors
        if (getErrors() != null) {
            Element errorsElem = document.createElement("Errors");
            for (Map<String, String> fieldAndCode : errors) {
                Element errorElem = document.createElement("Error");
                errorElem.appendChild(XMLUtils.getElement(document, "Field", fieldAndCode.get("field")));
                errorElem.appendChild(XMLUtils.getElement(document, "Code", fieldAndCode.get("code")));
                errorsElem.appendChild(errorElem);
            }
            elem.appendChild(errorsElem);
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
                errors.add(fieldAndCode);
            }
        }
    }
}