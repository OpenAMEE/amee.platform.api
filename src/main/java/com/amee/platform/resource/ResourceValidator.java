package com.amee.platform.resource;

import com.amee.base.resource.ValidationResult;

import java.util.Map;

import org.springframework.validation.Validator;

/**
 * Defines a generic validator for validating requests for arbitrary resources.
 * 
 * @param <T> the type of resource to validate
 */
public interface ResourceValidator<T> extends Validator {

    public boolean isValid(Map<String, String> queryParameters);

    public T getObject();

    public void setObject(T object);

    public ValidationResult getValidationResult();

}
