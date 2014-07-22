package com.amee.platform.resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
@Scope("prototype")
public class EntityFilterValidator implements Validator {

    public boolean supports(Class clazz) {
        return EntityFilter.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors e) {
        // Do nothing. The Editors do the validation.
    }
}