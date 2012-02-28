package com.amee.platform.resource.dataitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.DataItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.BaseDataItemTextValue;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.itemvaluedefinition.ItemValueEditor;
import com.amee.platform.science.AmountUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.HashSet;
import java.util.Set;

/**
 * A Validator implementation for validating DataItems.
 */
@Service
@Scope("prototype")
@Since("3.6.0")
public class DataItemValidator_3_6_0 extends BaseValidator implements DataItemResource.DataItemValidator {

    private final Log log = LogFactory.getLog(getClass());

    // Alpha numerics & underscore.
    private final static String PATH_PATTERN_STRING = "^[a-zA-Z0-9_]*$";

    @Autowired
    protected DataItemService dataItemService;

    protected DataItem dataItem;
    protected Set<String> allowedFields = new HashSet<String>();

    public DataItemValidator_3_6_0() {
        super();
    }

    @Override
    public void initialise() {
        addName();
        addPath();
        addWikiDoc();
        addProvenance();
        addValues();
        addUnits();
        addPerUnits();
    }

    /**
     * Configure the validator for the name property of the DataItem.
     */
    protected void addName() {
        allowedFields.add("name");
        add(new ValidationSpecification()
                .setName("name")
                .setMaxSize(DataItem.NAME_MAX_SIZE)
                .setAllowEmpty(true));
    }

    /**
     * Configure the validator for the path property of the DataItem.
     */
    protected void addPath() {
        allowedFields.add("path");
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

    /**
     * Configure the validator for the wikiDoc property of the DataItem.
     */
    protected void addWikiDoc() {
        allowedFields.add("wikiDoc");
        add(new ValidationSpecification()
                .setName("wikiDoc")
                .setMaxSize(DataItem.WIKI_DOC_MAX_SIZE)
                .setAllowEmpty(true));
    }

    /**
     * Configure the validator for the provenance property of the DataItem.
     */
    protected void addProvenance() {
        allowedFields.add("provenance");
        add(new ValidationSpecification()
                .setName("provenance")
                .setMaxSize(DataItem.PROVENANCE_MAX_SIZE)
                .setAllowEmpty(true));
    }

    /**
     * Configure the validator for Data Item Values for the current DataItem.
     */
    protected void addValues() {
        for (ItemValueDefinition ivd : dataItem.getItemDefinition().getActiveItemValueDefinitions()) {
            if (ivd.isFromData()) {
                String paramName = "values." + ivd.getPath();

                // Allow parameter for this ItemValueDefinition.
                allowedFields.add(paramName);
                if (ivd.isDouble()) {
                    // Double values.
                    // Add ValidationSpecification.
                    add(new ValidationSpecification()
                            .setName(paramName)
                            .setDoubleNumber(true)
                            .setAllowEmpty(true));
                    // Add the editor.
                    add(Double.class, paramName, new ItemValueEditor(ivd));
                } else if (ivd.isInteger()) {
                    // Integer values.
                    // Add ValidationSpecification.
                    add(new ValidationSpecification()
                            .setName(paramName)
                            .setIntegerNumber(true)
                            .setAllowEmpty(true));
                    // Add the editor.
                    add(Integer.class, paramName, new ItemValueEditor(ivd));
                } else {
                    // String values.
                    // Add ValidationSpecification.
                    add(new ValidationSpecification()
                            .setName(paramName)
                            .setMaxSize(BaseDataItemTextValue.VALUE_SIZE)
                            .setAllowEmpty(true));
                    // Add the editor.
                    add(String.class, paramName, new ItemValueEditor(ivd));
                }
            }
        }
    }

    protected void addUnits() {
        for (ItemValueDefinition ivd : dataItem.getItemDefinition().getActiveItemValueDefinitions()) {
            if (ivd.isFromData()) {
                final String unitName = "units." + ivd.getPath();

                // Allow this field.
                allowedFields.add(unitName);
                add(new ValidationSpecification()
                    .setName(unitName)
                    .setAllowEmpty(true)
                    .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {

                                String unit = (String) value;

                                // Ensure unit is valid
                                if (unit != null) {
                                    try {
                                        AmountUnit.valueOf(unit);
                                    } catch (IllegalArgumentException e) {
                                        errors.rejectValue(unitName, "format");
                                    }
                                }

                                return ValidationSpecification.CONTINUE;
                            }
                        }
                    )
                );
            }
        }
    }
    
    protected void addPerUnits() {
        for (ItemValueDefinition ivd : dataItem.getItemDefinition().getActiveItemValueDefinitions()) {
            if (ivd.isFromProfile()) {
                final String perUnitName = "perUnits." + ivd.getPath();

                // Allow this field
                allowedFields.add(perUnitName);
                add(new ValidationSpecification()
                    .setName(perUnitName)
                    .setAllowEmpty(true)
                    .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {

                                String perUnit = (String) value;

                                // Ensure perUnit is valid
                                if (perUnit != null) {
                                    try {
                                        AmountUnit.valueOf(perUnit);
                                    } catch (IllegalArgumentException e) {
                                        errors.rejectValue(perUnitName, "format");
                                    }
                                }

                                return ValidationSpecification.CONTINUE;
                            }
                        }
                    )
                );
            }
        }
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
        return allowedFields.toArray(new String[allowedFields.size()]);
    }

    @Override
    public DataItem getObject() {
        return dataItem;
    }

    @Override
    public void setObject(DataItem dataItem) {
        this.dataItem = dataItem;
    }

    /**
     * Override validate to perform global object validation.
     * 
     * @param object to validate
     * @param errors to store validation errors
     */
    @Override
    public void validate(Object object, Errors errors) {
        super.validate(object, errors);

        // Check for duplicates (as long as we don't have errors already)
        if (!errors.hasErrors() && !dataItemService.isUnique(dataItem)) {
            errors.reject("duplicate");
        }
    }

    /**
     * Setter used by unit tests.
     *
     * @param dataItemService a DataItemService (probably mocked)
     */
    @Override
    public void setDataItemService(DataItemService dataItemService) {
        this.dataItemService = dataItemService;
    }
}