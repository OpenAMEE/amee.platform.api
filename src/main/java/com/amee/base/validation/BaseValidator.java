package com.amee.base.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for Validator implementations providing a mechanism to validate objects
 * with {@link ValidationSpecification}s.
 * <p/>
 * TODO: Merge this with {@link ValidationHelper}.
 */
public abstract class BaseValidator extends ValidationHelper implements Validator {

    private List<ValidationSpecification> specifications = new ArrayList<ValidationSpecification>();

    public BaseValidator() {
        super();
    }

    public void add(ValidationSpecification validationSpecification) {
        getSpecifications().add(validationSpecification);
    }

    @Override
    public void validate(Object o, Errors e) {
        for (ValidationSpecification validationSpecification : getSpecifications()) {
            if (validationSpecification.validate(o, e) == ValidationSpecification.STOP) {
                break;
            }
        }
    }

    public List<ValidationSpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<ValidationSpecification> specifications) {
        this.specifications.clear();
        if (specifications != null) {
            this.specifications.addAll(specifications);
        }
    }

    @Override
    protected Validator getValidator() {
        return this;
    }
}
