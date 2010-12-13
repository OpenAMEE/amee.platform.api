package com.amee.platform.resource.search;

import com.amee.base.validation.ValidationSpecification;
import com.amee.platform.search.SearchFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
@Scope("prototype")
public class SearchFilterValidator implements Validator {

    private ValidationSpecification qSpec;

    public SearchFilterValidator() {
        super();
        qSpec = new ValidationSpecification();
        qSpec.setName("q");
    }

    @Override
    public boolean supports(Class clazz) {
        return SearchFilter.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors e) {
        qSpec.validate(o, e);
    }
}