package com.amee.platform.search;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
@Scope("prototype")
public class DataItemsFilterValidator implements Validator {

    public boolean supports(Class clazz) {
        return QueryFilter.class.isAssignableFrom(clazz);
    }

    public void validate(Object o, Errors e) {
        // Do nothing. The Editors do the validation.
    }
}