package com.amee.base.validation;

import com.amee.base.resource.ResourceException;
import com.amee.base.resource.ValidationResult;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A {@link ResourceException} that can be thrown by resources to represent validation errors, typically from
 * HTTP GET, POST or PUT calls which updating a bean or an entity.
 */
public class ValidationException extends ResourceException {

    /**
     * A {@link List} of {@link ValidationResult}s.
     */
    private List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

    /**
     * Constructor.
     */
    public ValidationException() {
        super();
    }

    /**
     * Constructor that accepts a single {@link ValidationResult}.
     *
     * @param validationResult to include in the exception
     */
    public ValidationException(ValidationResult validationResult) {
        this();
        setValidationResult(validationResult);
    }

    /**
     * Constructor that accepts a {@link List} of {@link ValidationResult}s.
     *
     * @param validationResults to include in the exception
     */
    public ValidationException(List<ValidationResult> validationResults) {
        this();
        setValidationResults(validationResults);
    }

    /**
     * Constructor that accepts an error message.
     * 
     * @param message the message to include along with the exception.
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Create a {@link JSONObject} response representation which includes a single {@link ValidationResult} or
     * a list of {@link ValidationResult}s.
     *
     * @return {@link JSONObject} response representation
     */
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

    /**
     * Create a {@link Document} response representation which includes a single {@link ValidationResult} or
     * a list of {@link ValidationResult}s.
     *
     * @return {@link Document} response representation
     */
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

    /**
     * Get the first {@link ValidationResult} in the list.
     *
     * @return the {@link ValidationResult}
     */
    public ValidationResult getValidationResult() {
        return validationResults.isEmpty() ? null : validationResults.get(0);
    }

    /**
     * Set the first {@link ValidationResult} in the list.
     *
     * @param validationResult the {@link ValidationResult} to set
     */
    public void setValidationResult(ValidationResult validationResult) {
        validationResults.clear();
        validationResults.add(validationResult);
    }

    /**
     * Sets the {@link ValidationResult}s list.
     *
     * @param validationResults the {@link ValidationResult}s to set
     */
    public void setValidationResults(List<ValidationResult> validationResults) {
        validationResults.clear();
        validationResults.addAll(validationResults);
    }
}
