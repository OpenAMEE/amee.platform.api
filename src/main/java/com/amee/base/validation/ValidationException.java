package com.amee.base.validation;

import com.amee.base.resource.ResourceException;
import com.amee.base.resource.ValidationResult;
import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONException;
import org.json.JSONObject;

public class ValidationException extends ResourceException {

    private ValidationResult validationResult;

    public ValidationException() {
        super();
    }

    public ValidationException(ValidationResult validationResult) {
        this();
        setValidationResult(validationResult);
    }

    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            if (getValidationResult() != null) {
                o.put("validationResult", getValidationResult().getJSONObject());
            }
            o.put("status", "INVALID");
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public Document getDocument() {
        Element rootElem = new Element("Representation");
        if (getValidationResult() != null) {
            rootElem.addContent(getValidationResult().getElement());
        }
        rootElem.addContent(new Element("Status").setText("INVALID"));
        return new Document(rootElem);
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }
}
