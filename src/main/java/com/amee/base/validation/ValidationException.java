package com.amee.base.validation;

import com.amee.base.resource.ResourceException;
import com.amee.base.resource.ValidationResult;
import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends ResourceException {

    private List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

    public ValidationException() {
        super();
    }

    public ValidationException(ValidationResult validationResult) {
        this();
        setValidationResult(validationResult);
    }

    public ValidationException(List<ValidationResult> validationResults) {
        this();
        setValidationResults(validationResults);
    }

    @Override
    public JSONObject getJSONObject() {
        try {
            JSONObject o = new JSONObject();
            if (validationResults.size() == 1) {
                o.put("validationResult", getValidationResult().getJSONObject());
            } else if (!validationResults.isEmpty()) {
                JSONArray arr = new JSONArray();
                for (ValidationResult validationResult : validationResults) {
                    arr.put(validationResult.getJSONObject());
                }
                o.put("validationResults", arr);
            }
            o.put("status", "INVALID");
            return o;
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @Override
    public Document getDocument() {
        Element rootElem = new Element("Representation");
        if (validationResults.size() == 1) {
            rootElem.addContent(getValidationResult().getElement());
        } else if (!validationResults.isEmpty()) {
            Element validationResultsElem = new Element("ValidationResults");
            for (ValidationResult validationResult : validationResults) {
                validationResultsElem.addContent(validationResult.getElement());
            }
            rootElem.addContent(validationResultsElem);
        }
        rootElem.addContent(new Element("Status").setText("INVALID"));
        return new Document(rootElem);
    }

    public ValidationResult getValidationResult() {
        return validationResults.isEmpty() ? null : validationResults.get(0);
    }

    public void setValidationResult(ValidationResult validationResult) {
        validationResults.clear();
        validationResults.add(validationResult);
    }

    public void setValidationResults(List<ValidationResult> validationResults) {
        validationResults.clear();
        validationResults.addAll(validationResults);
    }
}
