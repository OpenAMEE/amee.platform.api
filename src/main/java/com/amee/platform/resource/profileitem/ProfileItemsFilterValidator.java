package com.amee.platform.resource.profileitem;

import com.amee.base.validation.BaseValidator;
import com.amee.domain.ProfileItemsFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@Scope("prototype")
public class ProfileItemsFilterValidator extends BaseValidator {

    @Override
    public boolean supports(Class clazz) {
        return ProfileItemsFilter.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object o, Errors e) {
        // Do nothing. The Editors do the validation.
    }
}
