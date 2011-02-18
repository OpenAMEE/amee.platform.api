package com.amee.platform.resource.dataitem.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.service.item.DataItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataItemValidator_3_0_0 extends BaseValidator implements DataItemResource.DataItemValidator {

    // Alpha numerics & underscore.
    private final static String PATH_PATTERN_STRING = "^[a-zA-Z0-9_]*$";

    @Autowired
    private DataItemService dataItemService;

    private DataItem dataItem;
    private Set<String> allowedFields;

    public DataItemValidator_3_0_0() {
        super();
        addName();
        addPath();
        addWikiDoc();
        addProvenance();
    }

    private void addName() {
        add(new ValidationSpecification()
                .setName("name")
                .setMaxSize(DataItem.NAME_MAX_SIZE)
                .setAllowEmpty(true));
    }

    private void addPath() {
        add(new ValidationSpecification()
                .setName("path")
                .setMaxSize(DataItem.PATH_MAX_SIZE)
                .setFormat(PATH_PATTERN_STRING)
                .setAllowEmpty(true)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure DataItem is unique on path.
                                DataItem thisDI = (DataItem) object;
                                if ((thisDI != null) && (thisDI.getDataCategory() != null)) {
                                    if (!dataItemService.isDataItemUniqueByPath(thisDI)) {
                                        errors.rejectValue("path", "duplicate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        }));

    }

    private void addWikiDoc() {
        add(new ValidationSpecification()
                .setName("wikiDoc")
                .setMaxSize(DataItem.WIKI_DOC_MAX_SIZE)
                .setAllowEmpty(true));
    }

    private void addProvenance() {
        add(new ValidationSpecification()
                .setName("provenance")
                .setMaxSize(DataItem.PROVENANCE_MAX_SIZE)
                .setAllowEmpty(true));
    }

    @Override
    public String getName() {
        return "dataItem";
    }

    @Override
    public boolean supports(Class clazz) {
        return DataItem.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("path");
            allowedFields.add("wikiDoc");
            allowedFields.add("provenance");
        }
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public DataItem getObject() {
        return dataItem;
    }

    @Override
    public void setObject(DataItem dataItem) {
        this.dataItem = dataItem;
    }
}