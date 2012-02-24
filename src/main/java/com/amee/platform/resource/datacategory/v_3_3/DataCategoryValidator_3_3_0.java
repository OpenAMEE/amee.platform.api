package com.amee.platform.resource.datacategory.v_3_3;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.DataCategoryEditor;
import com.amee.platform.resource.ItemDefinitionEditor;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import com.amee.service.data.DataService;

@Service
@Scope("prototype")
@Since("3.3.0")
public class DataCategoryValidator_3_3_0 extends BaseValidator implements DataCategoryResource.DataCategoryValidator {

    // Alpha numerics & underscore.
    private final static String PATH_PATTERN_STRING = "^[a-zA-Z0-9_\\-]*$";
    private final static String WIKI_NAME_PATTERN_STRING = PATH_PATTERN_STRING;

    @Autowired
    private DataService dataService;

    @Autowired
    protected DataCategoryEditor dataCategoryEditor;

    @Autowired
    protected ItemDefinitionEditor itemDefinitionEditor;

    private DataCategory dataCategory;
    private Set<String> allowedFields = new HashSet<String>();

    public DataCategoryValidator_3_3_0() {
        super();
    }

    public void initialise() {
        addName();
        addPath();
        addWikiName();
        addWikiDoc();
        addProvenance();
        addAuthority();
        addHistory();
        addDataCategory();
        addItemDefinition();
    }

    protected void addName() {
        allowedFields.add("name");
        add(new ValidationSpecification()
                .setName("name")
                .setMinSize(DataCategory.NAME_MIN_SIZE)
                .setMaxSize(DataCategory.NAME_MAX_SIZE)
        );
    }

    protected void addPath() {
        allowedFields.add("path");
        add(new ValidationSpecification()
                .setName("path")
                .setMinSize(DataCategory.PATH_MIN_SIZE)
                .setMaxSize(DataCategory.PATH_MAX_SIZE)
                .setFormat(PATH_PATTERN_STRING)
                .setAllowEmpty(true)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure DataCategory is unique on path.
                                DataCategory thisDC = (DataCategory) object;
                                if ((thisDC != null) && (thisDC.getDataCategory() != null)) {
                                    if (!dataService.isDataCategoryUniqueByPath(thisDC)) {
                                        errors.rejectValue("path", "duplicate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        })
        );
    }

    protected void addWikiName() {
        allowedFields.add("wikiName");
        add(new ValidationSpecification()
                .setName("wikiName")
                .setMinSize(DataCategory.WIKI_NAME_MIN_SIZE)
                .setMaxSize(DataCategory.WIKI_NAME_MAX_SIZE)
                .setFormat(WIKI_NAME_PATTERN_STRING)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure DataCategory is unique on wikiName.
                                DataCategory thisDC = (DataCategory) object;
                                if (thisDC != null) {
                                    if (!dataService.isDataCategoryUniqueByWikiName(thisDC)) {
                                        errors.rejectValue("wikiName", "duplicate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        })
        );
    }

    protected void addWikiDoc() {
        allowedFields.add("wikiDoc");
        add(new ValidationSpecification()
                .setName("wikiDoc")
                .setMaxSize(DataCategory.WIKI_DOC_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    protected void addProvenance() {
        allowedFields.add("provenance");
        add(new ValidationSpecification()
                .setName("provenance")
                .setMaxSize(DataCategory.PROVENANCE_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    protected void addAuthority() {
        allowedFields.add("authority");
        add(new ValidationSpecification()
                .setName("authority")
                .setMaxSize(DataCategory.AUTHORITY_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    protected void addHistory() {
        allowedFields.add("history");
        add(new ValidationSpecification()
                .setName("history")
                .setMaxSize(DataCategory.HISTORY_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    protected void addDataCategory() {
        allowedFields.add("dataCategory");
        add(DataCategory.class, "dataCategory", dataCategoryEditor);
        add(new ValidationSpecification()
                .setName("dataCategory")
                .setAllowEmpty(false)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                DataCategory thisDC = (DataCategory) object;
                                DataCategory newParentDC = (DataCategory) value;
                                // New parent DataCategory cannot be this DataCategory.
                                if (thisDC.equals(newParentDC)) {
                                    errors.rejectValue("dataCategory", "same");
                                } else {
                                    // Parent DataCategories cannot contain children of this DataCategory.
                                    // Loop over parents until we go past the root or reach this DataCategory.
                                    DataCategory parentDC = thisDC.getDataCategory();
                                    while (parentDC != null) {
                                        if (parentDC.equals(thisDC)) {
                                            errors.rejectValue("dataCategory", "child");
                                            break;
                                        }
                                        parentDC = parentDC.getDataCategory();
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        })
        );
    }

    protected void addItemDefinition() {
        allowedFields.add("itemDefinition");
        add(ItemDefinition.class, "itemDefinition", itemDefinitionEditor);
        add(new ValidationSpecification()
                .setName("itemDefinition")
                .setAllowEmpty(true)
        );
    }

    @Override
    public String getName() {
        return "dataCategory";
    }

    @Override
    public boolean supports(Class clazz) {
        return DataCategory.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public DataCategory getObject() {
        return dataCategory;
    }

    @Override
    public void setObject(DataCategory dataCategory) {
        this.dataCategory = dataCategory;
    }

    /**
     * Setter used by unit tests.
     *
     * @param dataService a DataService (probably mocked)
     */
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}