package com.amee.platform.resource.profile;

import com.amee.base.validation.ValidationHelper;
import com.amee.platform.search.ProfilesFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
public class ProfilesFilterValidationHelper extends ValidationHelper {

    @Autowired
    private ProfilesFilterValidator validator;

    private ProfilesFilter profilesFilter;
    private Set<String> allowedFields;

    @Override
    public Object getObject() {
        return profilesFilter;
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    @Override
    public String getName() {
        return "profileFilter";
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

    public ProfilesFilter getProfilesFilter() {
        return profilesFilter;
    }

    public void setProfilesFilter(ProfilesFilter profilesFilter) {
        this.profilesFilter = profilesFilter;
    }
}
