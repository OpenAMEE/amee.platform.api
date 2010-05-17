package com.amee.base.validation;

import com.amee.base.resource.ResourceException;
import com.amee.base.resource.ValidationResult;
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

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            o.put("validationResult", getValidationResult().getJSONObject());
            o.put("status", "INVALID");
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }
}
