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
import java.util.HashMap;

public class Feedback implements Serializable {

    private HashMap<String, String> values;
    private Errors errors;

    public Feedback() {
        super();
        values = new HashMap<String, String>();
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
            for (Object error : getErrors().getAllErrors()) {
                if (error.getClass().isAssignableFrom(FieldError.class)) {
                    JSONObject fieldObj = new JSONObject();
                    FieldError fieldError = (FieldError) error;
                    fieldObj.put("field", fieldError.getField());
                    fieldObj.put("code", fieldError.getCode());
                    errorsArr.put(fieldObj);
                }
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
            for (Object error : getErrors().getAllErrors()) {
                if (error.getClass().isAssignableFrom(FieldError.class)) {
                    Element errorElem = document.createElement("Error");
                    FieldError fieldError = (FieldError) error;
                    errorElem.appendChild(XMLUtils.getElement(document, "Field", fieldError.getField()));
                    errorElem.appendChild(XMLUtils.getElement(document, "Code", fieldError.getCode()));
                    errorsElem.appendChild(errorElem);
                }
            }
            elem.appendChild(errorsElem);
        }
        return elem;
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }
}