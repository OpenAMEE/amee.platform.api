package com.amee.platform.resource.datacategory.v_3_3;

import com.amee.base.domain.Since;
import com.amee.base.validation.ValidationHelper;
import com.amee.domain.data.DataCategory;
import com.amee.platform.resource.DataCategoryEditor;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.3.0")
public class DataCategoryValidationHelper_3_3_0 extends ValidationHelper implements DataCategoryResource.DataCategoryValidationHelper {

    @Autowired
    protected DataCategoryEditor dataCategoryEditor;

    protected DataCategory dataCategory;
    protected Set<String> allowedFields;
    protected DataCategoryResource.DataCategoryValidator validator;

    @Override
    protected void registerCustomEditors(DataBinder dataBinder) {
        dataBinder.registerCustomEditor(DataCategory.class, "dataCategory", dataCategoryEditor);
    }

    @Override
    public Object getObject() {
        return dataCategory;
    }

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Override
    public void setValidator(DataCategoryResource.DataCategoryValidator validator) {
        this.validator = validator;
    }

    @Override
    public String getName() {
        return "dataCategory";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("path");
            allowedFields.add("wikiName");
            allowedFields.add("wikiDoc");
            allowedFields.add("provenance");
            allowedFields.add("authority");
            allowedFields.add("history");
            allowedFields.add("dataCategory");
        }
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public DataCategory getDataCategory() {
        return dataCategory;
    }

    @Override
    public void setDataCategory(DataCategory dataCategory) {
        this.dataCategory = dataCategory;
    }
}
