package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.platform.resource.CSVListEditor;
import com.amee.platform.resource.tag.TagsFilterValidator;
import com.amee.platform.resource.tag.TagsResource;
import com.amee.service.tag.TagsFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagsFilterValidationHelper_3_2_0 extends BaseValidator implements TagsResource.TagsFilterValidationHelper {

    @Autowired
    private TagsFilterValidator validator;

    private TagsFilter tagsFilter;
    private Set<String> allowedFields;

    @Override
    protected void registerCustomEditors(DataBinder dataBinder) {
        dataBinder.registerCustomEditor(List.class, "incTags", new CSVListEditor());
        dataBinder.registerCustomEditor(List.class, "excTags", new CSVListEditor());
    }

    @Override
    public TagsFilter getObject() {
        return tagsFilter;
    }

    @Override
    public void setObject(TagsFilter tagsFilter) {
        this.tagsFilter = tagsFilter;
    }

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Override
    public String getName() {
        return "tagsFilter";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("incTags");
            allowedFields.add("excTags");
        }
        return allowedFields.toArray(new String[] {});
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return validator.supports(clazz);
    }
}
