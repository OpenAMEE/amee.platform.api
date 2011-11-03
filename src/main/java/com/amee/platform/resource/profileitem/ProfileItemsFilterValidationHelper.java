package com.amee.platform.resource.profileitem;

import com.amee.base.validation.ValidationHelper;
import com.amee.domain.ProfileItemsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
public class ProfileItemsFilterValidationHelper extends ValidationHelper {

    @Autowired
    private ProfileItemsFilterValidator validator;

    private ProfileItemsFilter profileItemsFilter;
    private Set<String> allowedFields;

    @Override
    public Object getObject() {
        return profileItemsFilter;
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Override
    public String getName() {
        return "profileItemsFilter";
    }
    
    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("resultStart");
            allowedFields.add("resultLimit");
        }
        return allowedFields.toArray(new String[]{});
    }

    public ProfileItemsFilter getProfileItemsFilter() {
        return profileItemsFilter;
    }

    public void setProfileItemsFilter(ProfileItemsFilter profileItemsFilter) {
        this.profileItemsFilter = profileItemsFilter;
    }
}
