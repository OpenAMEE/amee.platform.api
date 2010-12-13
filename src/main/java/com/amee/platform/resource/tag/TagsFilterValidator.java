package com.amee.platform.resource.tag;

import com.amee.service.tag.TagsFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
@Scope("prototype")
public class TagsFilterValidator implements Validator {

    @Override
    public void validate(Object o, Errors e) {
    }

    @Override
    public boolean supports(Class clazz) {
        return TagsFilter.class.isAssignableFrom(clazz);
    }
}
