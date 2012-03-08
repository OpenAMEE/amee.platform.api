package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.DataItemService;
import com.amee.domain.ProfileItemsFilter;
import com.amee.platform.resource.StartEndDateEditor;
import com.amee.platform.resource.profileitem.ProfileItemsResource;
import com.amee.platform.science.StartEndDate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsFilterValidator_3_6_0 extends BaseValidator implements ProfileItemsResource.FilterValidator {

    protected ProfileItemsFilter object;
    protected Set<String> allowedFields = new HashSet<String>();
    private StartEndDate defaultStartDate;
    private static final String SELECT_BY_PATTERN_STRING = "(start)|(end)";
    private static final String MODE_PATTERN_STRING = "prorata";

    // IS0 8601 duration. (https://secure.wikimedia.org/wikipedia/en/wiki/ISO_8601#Durations)
    private static final String DURATION_PATTERN_STRING = "^P\\d*Y?\\d*M?\\d*D?T?\\d*H?\\d*M?\\d*S?";

    public ProfileItemsFilterValidator_3_6_0() {
        super();
    }

    @Override
    public void initialise() {
        allowedFields.add("resultStart");
        allowedFields.add("resultLimit");

        addStartDate();
        addEndDate();
        addDuration();
        addSelectBy();
        addMode();
    }

    /**
     * Configure the validator for the startDate property.
     */
    protected void addStartDate() {
        allowedFields.add("startDate");
        addCustomEditor(StartEndDate.class, "startDate", new StartEndDateEditor(defaultStartDate));
        add(new ValidationSpecification()
            .setName("startDate")
            .setAllowEmpty(true));
    }

    /**
     * Configure the validator for the endDate property.
     */
    protected void addEndDate() {
        allowedFields.add("endDate");
        addCustomEditor(StartEndDate.class, "endDate", new StartEndDateEditor(DataItemService.Y2038));
        add(new ValidationSpecification()
            .setName("endDate")
            .setAllowEmpty(true));
    }

    protected void addDuration() {
        allowedFields.add("duration");
        add(new ValidationSpecification()
            .setName("duration")
            .setAllowEmpty(true)
            .setFormat(DURATION_PATTERN_STRING)
            .setCustomValidation(
                new ValidationSpecification.CustomValidation() {
                    @Override
                    public int validate(Object object, Object value, Errors errors) {
                        ProfileItemsFilter thisProfileItemsFilter = (ProfileItemsFilter) object;
                        if (thisProfileItemsFilter != null && thisProfileItemsFilter.getDuration() != null) {
                            StartEndDate endDate = thisProfileItemsFilter.getStartDate().plus((String) value);

                            // End date must be after start date
                            if (endDate.before(thisProfileItemsFilter.getStartDate())) {
                                errors.rejectValue("duration", "end_before_start.endDate");
                            }

                            // Date must be in allowed range.
                            if (endDate.compareTo(DataItemService.Y2038) >= 0) {
                                errors.rejectValue("duration", "end_of_epoch.endDate");
                            }

                            thisProfileItemsFilter.setEndDate(endDate);
                        }
                        return ValidationSpecification.CONTINUE;
                    }
                }
            )
        );
    }

    /**
     * Configure the validator for the selectBy property.
     */
    protected void addSelectBy() {
        allowedFields.add("selectBy");
        add(new ValidationSpecification()
            .setName("selectBy")
            .setAllowEmpty(true)
            .setFormat(SELECT_BY_PATTERN_STRING));
    }

    /**
     * Configure the validator for the mode property.
     */
    protected void addMode() {
        allowedFields.add("mode");
        add(new ValidationSpecification()
            .setName("mode")
            .setAllowEmpty(true)
            .setFormat(MODE_PATTERN_STRING));
    }

    @Override
    public String getName() {
        return "profileItemsFilter";
    }

    @Override
    public boolean supports(Class clazz) {
        return ProfileItemsFilter.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[allowedFields.size()]);
    }

    @Override
    public ProfileItemsFilter getObject() {
        return object;
    }

    @Override
    public void setObject(ProfileItemsFilter object) {
        this.object = object;
    }

    @Override
    public StartEndDate getDefaultStartDate() {
        return defaultStartDate;
    }

    @Override
    public void setDefaultStartDate(StartEndDate defaultStartDate) {
        this.defaultStartDate = defaultStartDate;
    }
}
