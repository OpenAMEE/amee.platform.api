package com.amee.base.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for Validator implementations providing a mechanism to validate objects
 * with {@link ValidationSpecification}s.
 * <p/>
 * TODO: Merge {@link ValidationHelper} into this.
 */
public abstract class BaseValidator extends ValidationHelper implements Validator {

    /**
     * A {@link List} of {@link ValidationSpecification}s for validating an object against.
     */
    private List<ValidationSpecification> specifications = new ArrayList<ValidationSpecification>();

    /**
     * A constructor.
     */
    public BaseValidator() {
        super();
    }

    /**
     * Add a {@link ValidationSpecification} to the {@link List} or specifications.
     *
     * @param validationSpecification a {@link ValidationSpecification} to add the list
     */
    public void add(ValidationSpecification validationSpecification) {
        getSpecifications().add(validationSpecification);
    }

    /**
     * Validate the supplied object and update the supplied Errors object with any failures. This iterates
     * over all the current {@link ValidationSpecification}s and validates the object against each.
     *
     * @param object to validate
     * @param errors to store validation errors
     */
    @Override
    public void validate(Object object, Errors errors) {
        for (ValidationSpecification validationSpecification : getSpecifications()) {
            if (validationSpecification.validate(object, errors) == ValidationSpecification.STOP) {
                break;
            }
        }
    }

    /**
     * Get the {@link ValidationSpecification}s.
     *
     * @return the {@link ValidationSpecification}s
     */
    public List<ValidationSpecification> getSpecifications() {
        return specifications;
    }

    /**
     * Set the {@link ValidationSpecification}s.
     *
     * @param specifications the {@link ValidationSpecification}s
     */
    public void setSpecifications(List<ValidationSpecification> specifications) {
        this.specifications.clear();
        if (specifications != null) {
            this.specifications.addAll(specifications);
        }
    }

    /**
     * Get the current {@link Validator} implementation.
     *
     * @return the current {@link Validator} implementation
     */
    @Override
    protected Validator getValidator() {
        return this;
    }
}
