package com.amee.platform.resource.itemdefinition;

import com.amee.platform.search.ItemDefinitionsFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
@Scope("prototype")
public class ItemDefinitionsFilterValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return ItemDefinitionsFilter.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors e) {
        // Do nothing. The Editors do the validation.
    }
}
