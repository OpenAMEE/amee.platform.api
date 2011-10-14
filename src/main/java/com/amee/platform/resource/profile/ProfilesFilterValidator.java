package com.amee.platform.resource.profile;

import com.amee.base.validation.BaseValidator;
import com.amee.platform.search.ProfilesFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ProfilesFilterValidator extends BaseValidator {

    @Override
    public boolean supports(Class clazz) {
        return ProfilesFilter.class.isAssignableFrom(clazz);
    }
}
