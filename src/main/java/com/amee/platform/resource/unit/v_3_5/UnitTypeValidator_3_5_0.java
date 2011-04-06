package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.unit.AMEEUnitType;
import com.amee.platform.resource.unit.UnitTypeResource;
import com.amee.service.unit.UnitService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.HashSet;
import java.util.Set;

/**
 * A Validator implementation for validating UnitTypes.
 */
@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitTypeValidator_3_5_0 extends BaseValidator implements UnitTypeResource.UnitTypeValidator {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    protected UnitService unitService;

    protected AMEEUnitType unitType;
    protected Set<String> allowedFields = new HashSet<String>();

    public UnitTypeValidator_3_5_0() {
        super();
    }

    @Override
    public void initialise() {
        addName();
    }

    protected void addName() {
        allowedFields.add("name");
        add(new ValidationSpecification()
                .setName("name")
                .setMaxSize(AMEEUnitType.NAME_MAX_SIZE)
                .setAllowEmpty(false)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure UnitType is unique on name.
                                AMEEUnitType thisUnitType = (AMEEUnitType) object;
                                if (thisUnitType != null) {
                                    if (!unitService.isUnitTypeUniqueByName(thisUnitType)) {
                                        errors.rejectValue("name", "duplicate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        })
        );
    }

    @Override
    public String getName() {
        return "unitType";
    }

    @Override
    public boolean supports(Class clazz) {
        return AMEEUnitType.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public AMEEUnitType getObject() {
        return unitType;
    }

    @Override
    public void setObject(AMEEUnitType unitType) {
        this.unitType = unitType;
    }
}