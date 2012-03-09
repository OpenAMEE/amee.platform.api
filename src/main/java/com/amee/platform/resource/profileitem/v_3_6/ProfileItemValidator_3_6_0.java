package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.DataItemService;
import com.amee.domain.Metadata;
import com.amee.domain.ProfileItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.item.profile.ProfileItemTextValue;
import com.amee.domain.sheet.Choice;
import com.amee.platform.resource.StartEndDateEditor;
import com.amee.platform.resource.itemvaluedefinition.ItemValueEditor;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.platform.science.AmountUnit;
import com.amee.platform.science.StartEndDate;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemValidator_3_6_0 extends BaseValidator implements ProfileItemResource.ProfileItemValidator {

    @Autowired
    ProfileItemService profileItemService;

    protected ProfileItem profileItem;
    protected Set<String> allowedFields = new HashSet<String>();

    // IS0 8601 duration.
    // (https://secure.wikimedia.org/wikipedia/en/wiki/ISO_8601#Durations)
    private static final String DURATION_PATTERN_STRING = "^P\\d*Y?\\d*M?\\d*D?T?\\d*H?\\d*M?\\d*S?";

    @Override
    public void initialise() {
        addName();
        addDates();
        addValues();
        addNote();
        addUnits();
        addPerUnits();
    }

    @Override
    public String getName() {
        return "profileItem";
    }

    @Override
    public ProfileItem getObject() {
        return profileItem;
    }

    @Override
    public void setObject(ProfileItem profileItem) {
        this.profileItem = profileItem;
    }

    /**
     * Setter used by unit tests.
     * 
     * @param profileItemService
     *            a (probably mocked) ProfileItemService.
     */
    @Override
    public void setProfileItemService(ProfileItemService profileItemService) {
        this.profileItemService = profileItemService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ProfileItem.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[allowedFields.size()]);
    }

    /**
     * Override validate to perform global object validation.
     * 
     * @param object
     *            to validate
     * @param errors
     *            to store validation errors
     */
    @Override
    public void validate(Object object, Errors errors) {
        super.validate(object, errors);

        // Check for duplicates (as long as we don't have errors already)
        if (!errors.hasErrors() && !profileItemService.isUnique(profileItem)) {
            errors.reject("duplicate");
        }
    }

    protected void addName() {
        allowedFields.add("name");
        add(new ValidationSpecification()
                .setName("name")
                .setMaxSize(ProfileItem.NAME_MAX_SIZE)
                .setAllowEmpty(true));
    }

    protected void addDates() {

        // Start date
        allowedFields.add("startDate");
        addCustomEditor(Date.class, "startDate", new StartEndDateEditor(new Date()));
        add(new ValidationSpecification()
                .setName("startDate")
                .setAllowEmpty(true)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                ProfileItem thisProfileItem = (ProfileItem) object;
                                if (thisProfileItem != null) {

                                    // Date must be in allowed range.
                                    if (thisProfileItem.getStartDate().compareTo(DataItemService.MYSQL_MIN_DATETIME) <= 0) {
                                        errors.rejectValue("startDate", "start_before_min.startDate");
                                    }
                                    if (thisProfileItem.getStartDate().compareTo(DataItemService.MYSQL_MAX_DATETIME) >= 0) {
                                        errors.rejectValue("startDate", "end_after_max.startDate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        }
                ));

        // End date
        allowedFields.add("endDate");
        addCustomEditor(Date.class, "endDate", new StartEndDateEditor(null));
        add(new ValidationSpecification()
                .setName("endDate")
                .setAllowEmpty(true)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                ProfileItem thisProfileItem = (ProfileItem) object;
                                if (thisProfileItem != null && thisProfileItem.getEndDate() != null) {

                                    // End date must be after start date
                                    if (thisProfileItem.getEndDate().before(thisProfileItem.getStartDate())) {
                                        errors.rejectValue("endDate", "end_before_start.endDate");
                                    }

                                    // Date must be in allowed range (don't need
                                    // to check < max as previous test will
                                    // catch)
                                    if (thisProfileItem.getEndDate().compareTo(DataItemService.MYSQL_MAX_DATETIME) >= 0) {
                                        errors.rejectValue("endDate", "end_after_max.endDate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        }
                ));

        // Duration (period)
        allowedFields.add("duration");
        add(new ValidationSpecification()
                .setName("duration")
                .setAllowEmpty(true)
                .setFormat(DURATION_PATTERN_STRING)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                ProfileItem thisProfileItem = (ProfileItem) object;
                                if (thisProfileItem != null && thisProfileItem.getDuration() != null) {
                                    StartEndDate endDate = thisProfileItem.getStartDate().plus((String) value);

                                    // End date must be after start date
                                    if (endDate.before(thisProfileItem.getStartDate())) {
                                        errors.rejectValue("duration", "end_before_start.endDate");
                                    }

                                    // Date must be in allowed range.
                                    if (endDate.compareTo(DataItemService.MYSQL_MAX_DATETIME) >= 0) {
                                        errors.rejectValue("duration", "end_after_max.endDate");
                                    }

                                    thisProfileItem.setEndDate(endDate);
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        }
                ));
    }

    protected void addValues() {
        for (ItemValueDefinition ivd : profileItem.getItemDefinition().getActiveItemValueDefinitions()) {
            if (ivd.isFromProfile()) {
                String paramName = "values." + ivd.getPath();

                // Allow this field.
                allowedFields.add(paramName);

                if (ivd.isDouble()) {

                    // Validation spec.
                    add(new ValidationSpecification()
                            .setName(paramName)
                            .setDoubleNumber(true)
                            .setAllowEmpty(true));

                    // Property editor.
                    addCustomEditor(Double.class, paramName, new ItemValueEditor(ivd));
                } else if (ivd.isInteger()) {
                    add(new ValidationSpecification()
                            .setName(paramName)
                            .setIntegerNumber(true)
                            .setAllowEmpty(true));

                    addCustomEditor(Integer.class, paramName, new ItemValueEditor(ivd));
                } else {
                    add(new ValidationSpecification()
                            .setName(paramName)
                            .setMaxSize(ProfileItemTextValue.VALUE_SIZE)
                            .setAllowEmpty(true));

                    addCustomEditor(String.class, paramName, new ItemValueEditor(ivd));
                }

                // If the field has choices, make sure a valid one is specified
                if (ivd.isChoicesAvailable()) {
                    List<Choice> choices = ivd.getChoiceList();

                    // Build a regular expression based on the list of valid choices
                    StringBuilder regEx = new StringBuilder();
                    regEx.append("^");
                    for(int i=0; i<choices.size(); i++){
                        if(i != 0){
                            regEx.append("|");
                        }
                        regEx.append(Pattern.quote(choices.get(i).getValue()));
                    }
                    regEx.append("$");

                    add(new ValidationSpecification()
                            .setName(paramName)
                            .setAllowEmpty(true)
                            .setFormat(regEx.toString()));
                }
            }
        }
    }

    protected void addNote() {
        allowedFields.add("note");
        add(new ValidationSpecification()
                .setName("note")
                .setMaxSize(Metadata.VALUE_MAX_SIZE)
                .setAllowEmpty(true));
    }

    protected void addUnits() {
        for (ItemValueDefinition ivd : profileItem.getItemDefinition().getActiveItemValueDefinitions()) {
            if (ivd.isFromProfile()) {
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
                        ));
            }
        }
    }

    protected void addPerUnits() {
        for (ItemValueDefinition ivd : profileItem.getItemDefinition().getActiveItemValueDefinitions()) {
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
                        ));
            }
        }
    }
}
