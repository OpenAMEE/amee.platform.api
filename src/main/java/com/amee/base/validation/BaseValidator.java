package com.amee.base.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseValidator implements Validator {

    private List<ValidationSpecification> specifications = new ArrayList<ValidationSpecification>();

    public BaseValidator() {
        super();
    }

    public void add(ValidationSpecification validationSpecification) {
        getSpecifications().add(validationSpecification);
    }

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
}
