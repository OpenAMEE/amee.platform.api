package com.amee.platform.resource;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import com.amee.base.validation.BaseValidator;
import com.amee.domain.AMEEStatus;

@Service
@Scope("prototype")
public class EntityFilterValidationHelper extends BaseValidator {

    @Autowired
    private EntityFilterValidator entityFilterValidator;

    private EntityFilter entityFilter;
    private Set<String> allowedFields;

    @Override
    protected void registerCustomEditors(DataBinder dataBinder) {
        dataBinder.registerCustomEditor(AMEEStatus.class, "status", new StatusEditor());
    }

    @Override
    public Object getObject() {
        return entityFilter;
    }

    @Override
    protected Validator getValidator() {
        return entityFilterValidator;
    }

    @Override
    public String getName() {
        return "entityFilter";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("status");
        }
        return allowedFields.toArray(new String[]{});
    }

    public EntityFilter getEntityFilter() {
        return entityFilter;
    }

    public void setEntityFilter(EntityFilter entityFilter) {
        this.entityFilter = entityFilter;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return entityFilterValidator.supports(clazz);
    }
}
