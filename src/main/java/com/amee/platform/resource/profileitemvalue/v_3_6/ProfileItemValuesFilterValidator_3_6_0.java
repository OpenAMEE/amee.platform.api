package com.amee.platform.resource.profileitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.domain.ProfileItemValuesFilter;
import com.amee.platform.resource.profileitemvalue.ProfileItemValuesResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemValuesFilterValidator_3_6_0 extends BaseValidator implements ProfileItemValuesResource.ProfileItemValuesFilterValidator {

    protected ProfileItemValuesFilter object;
    protected Set<String> allowedFields = new HashSet<String>();

    public ProfileItemValuesFilterValidator_3_6_0() {
        super();
    }

    @Override
    public void initialise() {
        allowedFields.add("resultStart");
        allowedFields.add("resultLimit");
    }

    @Override
    public String getName() {
        return "profileItemValuesFilter";
    }

    @Override
    public void setObject(ProfileItemValuesFilter object) {
        this.object = object;
    }

    @Override
    public ProfileItemValuesFilter getObject() {
        return object;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ProfileItemValuesFilter.class.isAssignableFrom(clazz);
    }
}
