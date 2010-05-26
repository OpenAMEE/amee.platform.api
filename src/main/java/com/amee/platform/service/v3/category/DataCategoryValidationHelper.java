package com.amee.platform.service.v3.category;

import com.amee.base.validation.ValidationHelper;
import com.amee.domain.data.DataCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
public class DataCategoryValidationHelper extends ValidationHelper {

    @Autowired
    private DataCategoryValidator dataCategoryValidator;

    private DataCategory dataCategory;
    private Set<String> allowedFields;

    @Override
    public Object getObject() {
        return dataCategory;
    }

    @Override
    protected Validator getValidator() {
        return dataCategoryValidator;
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
        }
        return allowedFields.toArray(new String[]{});
    }

    public DataCategory getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(DataCategory dataCategory) {
        this.dataCategory = dataCategory;
    }
}
