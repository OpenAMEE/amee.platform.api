package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.validation.ValidationHelper;
import com.amee.platform.resource.CSVListEditor;
import com.amee.platform.resource.tag.TagsFilterValidator;
import com.amee.platform.resource.tag.TagsResource;
import com.amee.service.tag.TagsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagsFilterValidationHelper_3_2_0 extends ValidationHelper implements TagsResource.TagsFilterValidationHelper {

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
    public Object getObject() {
        return tagsFilter;
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
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public TagsFilter getTagsFilter() {
        return tagsFilter;
    }

    @Override
    public void setTagsFilter(TagsFilter tagsFilter) {
        this.tagsFilter = tagsFilter;
    }
}
