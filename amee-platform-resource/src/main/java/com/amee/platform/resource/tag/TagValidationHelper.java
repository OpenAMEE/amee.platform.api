package com.amee.platform.resource.tag;

import com.amee.base.validation.BaseValidator;
import com.amee.domain.tag.Tag;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

@Service
@Scope("prototype")
public class TagValidationHelper extends BaseValidator {

    @Autowired
    private TagValidator tagValidator;

    private Tag tag;
    private Set<String> allowedFields;

    @Override
    public Object getObject() {
        return tag;
    }

    @Override
    protected Validator getValidator() {
        return tagValidator;
    }

    @Override
    public String getName() {
        return "tag";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("tag");
        }
        return allowedFields.toArray(new String[] {});
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return tagValidator.supports(clazz);
    }
}