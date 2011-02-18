package com.amee.base.resource;

import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import java.io.Serializable;
import java.util.*;

public class ValidationResult implements Serializable {

    private MessageSource messageSource;
    private HashMap<String, String> values = new HashMap<String, String>();
    private List<Map<String, String>> errors = new ArrayList<Map<String, String>>();

    public ValidationResult() {
        super();
    }

    public ValidationResult(MessageSource messageSource) {
        this();
        this.messageSource = messageSource;
    }

    public ValidationResult(MessageSource messageSource, String field, String code) {
        this(messageSource);
        addError(field, code);
    }

    public ValidationResult(JSONObject obj) {
        this();
        try {
            // Load values.
            if (obj.has("values")) {
                JSONArray valuesArr = obj.getJSONArray("values");
                for (int i = 0; i < valuesArr.length(); i++) {
                    String name = valuesArr.getJSONObject(i).getString("name");
                    String value = valuesArr.getJSONObject(i).getString("value");
                    getValues().put(name, value);
                }
            }
            // Load Errors.
            if (obj.has("errors")) {
                JSONArray errorsArr = obj.getJSONArray("errors");
                for (int i = 0; i < errorsArr.length(); i++) {
                    addError(
                            errorsArr.getJSONObject(i).getString("field"),
                            errorsArr.getJSONObject(i).getString("code"),
                            errorsArr.getJSONObject(i).getString("message"),
                            errorsArr.getJSONObject(i).has("value") ? errorsArr.getJSONObject(i).getString("value") : "");
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public ValidationResult(Element element) {
        this();

        // Load Values.
        Element valuesElem = element.getChild("Values");
        if (valuesElem != null) {
            for (Element valueElem : (List<Element>) valuesElem.getChildren()) {
                getValues().put(valueElem.getChild("Name").getValue(), valueElem.getChild("Value").getValue());
            }
        }

        // Load Errors.
        Element errorsElem = element.getChild("Errors");
        if (errorsElem != null) {
            for (Element errorElem : (List<Element>) errorsElem.getChildren()) {
                if ((errorElem.getChild("Field") != null) && (errorElem.getChild("Code") != null)) {
                    addError(
                            errorElem.getChild("Field").getValue(),
                            errorElem.getChild("Code").getValue(),
                            errorElem.getChild("Message").getValue(),
                            (errorElem.getChild("Value") != null) ? errorElem.getChild("Value").getValue() : "");
                }
            }
        }
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        // add values
        if (!getValues().isEmpty()) {
            JSONArray valuesArr = new JSONArray();
            for (String key : getValues().keySet()) {
                if (getValues().get(key) != null) {
                    valuesArr.put(
                            new JSONObject()
                                    .put("name", key)
                                    .put("value", getValues().get(key)));
                }
            }
            obj.put("values", valuesArr);
        }
        // add errors
        if (getErrors() != null) {
            JSONArray errorsArr = new JSONArray();
            for (Map<String, String> fieldAndCode : getErrors()) {
                JSONObject fieldObj = new JSONObject();
                fieldObj.put("field", fieldAndCode.get("field"));
                fieldObj.put("code", fieldAndCode.get("code"));
                fieldObj.put("message", fieldAndCode.get("message"));
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
                if (getValues().get(key) != null) {
                    valuesElem.addContent(
                            new Element("Value")
                                    .addContent(new Element("Name").setText(key))
                                    .addContent(new Element("Value").setText(getValues().get(key))));
                }
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
                errorElem.addContent(new Element("Message").setText(fieldAndCode.get("message")));
                if (fieldAndCode.containsKey("value")) {
                    errorElem.addContent(new Element("Value").setText(fieldAndCode.get("value")));
                }
                errorsElem.addContent(errorElem);
            }
            elem.addContent(errorsElem);
        }
        return elem;
    }

    public void addValue(String key, String value) {
        values.put(key, value);
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    public void addError(String field, String code) {
        addError(field, code, "", null);
    }

    public void addError(String field, String code, String message) {
        addError(field, code, message, null);
    }

    public void addError(String field, String code, String message, String value) {
        Map<String, String> fieldAndCode = new HashMap<String, String>();
        fieldAndCode.put("field", field);
        fieldAndCode.put("code", code);
        fieldAndCode.put("message", message);
        if (value != null) {
            fieldAndCode.put("value", value);
        }
        getErrors().add(fieldAndCode);
    }

    public List<Map<String, String>> getErrors() {
        return errors;
    }

    public void setErrors(Errors e) {
        for (Object error : e.getAllErrors()) {
            if (error.getClass().isAssignableFrom(FieldError.class)) {
                FieldError fieldError = (FieldError) error;
                if ((fieldError.getRejectedValue() != null) && (fieldError.getRejectedValue() instanceof String)) {
                    addError(fieldError.getField(), fieldError.getCode(), getMessage(fieldError.getCodes()), (String) fieldError.getRejectedValue());
                } else {
                    addError(fieldError.getField(), fieldError.getCode(), getMessage(fieldError.getCodes()));
                }
            }
        }
    }

    private String getMessage(String[] codes) {
        String message = "";
        for (String code : codes) {
            message = getMessage(code);
            if (!message.isEmpty()) {
                break;
            }
        }
        return message;
    }

    private String getMessage(String key) {
        if (messageSource != null) {
            String message = messageSource.getMessage(key, null, Locale.ENGLISH);
            return !message.equals(key) ? message : "";
        } else {
            return "";
        }
    }
}