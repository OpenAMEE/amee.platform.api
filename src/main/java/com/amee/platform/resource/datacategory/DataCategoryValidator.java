package com.amee.platform.resource.datacategory;

import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.DataCategory;
import com.amee.service.auth.AuthenticationService;
import com.amee.service.auth.ResourceAuthorizationService;
import com.amee.service.data.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@Scope("prototype")
public class DataCategoryValidator extends BaseValidator {

    // Alpha numerics & underscore.
    private final static String PATH_PATTERN_STRING = "^[a-zA-Z0-9_\\-]*$";
    private final static String WIKI_NAME_PATTERN_STRING = PATH_PATTERN_STRING;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private DataService dataService;

    @Autowired
    private ResourceAuthorizationService resourceAuthorizationService;

    public DataCategoryValidator() {
        super();
        addName();
        addPath();
        addWikiName();
        addWikiDoc();
        addProvenance();
        addAuthority();
        addHistory();
        addDataCategory();
    }

    public boolean supports(Class clazz) {
        return DataCategory.class.isAssignableFrom(clazz);
    }

    private void addName() {
        add(new ValidationSpecification()
                .setName("name")
                .setMinSize(DataCategory.NAME_MIN_SIZE)
                .setMaxSize(DataCategory.NAME_MAX_SIZE)
        );
    }

    private void addPath() {
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

    private void addWikiName() {
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

    private void addWikiDoc() {
        add(new ValidationSpecification()
                .setName("wikiDoc")
                .setMaxSize(DataCategory.WIKI_DOC_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    private void addProvenance() {
        add(new ValidationSpecification()
                .setName("provenance")
                .setMaxSize(DataCategory.PROVENANCE_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    private void addAuthority() {
        add(new ValidationSpecification()
                .setName("authority")
                .setMaxSize(DataCategory.AUTHORITY_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    private void addHistory() {
        add(new ValidationSpecification()
                .setName("history")
                .setMaxSize(DataCategory.HISTORY_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    private void addDataCategory() {
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

    /**
     * Setter used by unit tests.
     *
     * @param dataService a DataService (probably mocked)
     */
    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
}