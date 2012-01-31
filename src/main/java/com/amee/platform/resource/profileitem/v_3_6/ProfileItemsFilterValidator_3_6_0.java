package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.DataItemService;
import com.amee.domain.ProfileItemsFilter;
import com.amee.domain.TimeZoneHolder;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.platform.resource.StartEndDateEditor;
import com.amee.platform.resource.profileitem.ProfileItemsResource;
import com.amee.platform.science.StartEndDate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsFilterValidator_3_6_0 extends BaseValidator implements ProfileItemsResource.FilterValidator {

    protected ProfileItemsFilter object;
    protected Set<String> allowedFields = new HashSet<String>();
    private StartEndDate defaultStartDate;

    public ProfileItemsFilterValidator_3_6_0() {
        super();
    }

    @Override
    public void initialise() {
        allowedFields.add("resultStart");
        allowedFields.add("resultLimit");

        addStartDate();
        addEndDate();
        addSelectBy();
    }

    /**
     * Configure the validator for the startDate property.
     */
    protected void addStartDate() {
        allowedFields.add("startDate");
        add(StartEndDate.class, "startDate", new StartEndDateEditor(defaultStartDate));
        add(new ValidationSpecification()
            .setName("startDate")
            .setAllowEmpty(true));
    }

    /**
     * Configure the validator for the endDate property.
     */
    protected void addEndDate() {
        allowedFields.add("endDate");
        add(StartEndDate.class, "endDate", new StartEndDateEditor(DataItemService.Y2038));
        add(new ValidationSpecification()
            .setName("endDate")
            .setAllowEmpty(true));
    }

    /**
     * Configure the validator for the selectBy property.
     */
    protected void addSelectBy() {
        allowedFields.add("selectBy");
        add(new ValidationSpecification()
            .setName("selectBy")
            .setAllowEmpty(true)
            .setCustomValidation(new ValidationSpecification.CustomValidation() {
                @Override
                public int validate(Object object, Object value, Errors errors) {

                    ProfileItemsFilter thisProfileItemsFilter = (ProfileItemsFilter) object;
                    if (thisProfileItemsFilter != null && thisProfileItemsFilter.getSelectBy() != null) {

                        // Valid values are "start" or "end"
                        if (!("start".equalsIgnoreCase(thisProfileItemsFilter.getSelectBy()) ||
                            "end".equalsIgnoreCase(thisProfileItemsFilter.getSelectBy()))) {
                            errors.rejectValue("selectBy", "format", "The value must be either 'start' or 'end'");
                        }
                    }
                    return ValidationSpecification.CONTINUE;
                }
            })
        );
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
