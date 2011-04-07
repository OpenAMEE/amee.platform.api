package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.unit.AMEEUnit;
import com.amee.platform.resource.unit.UnitResource;
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
 * A Validator implementation for validating Units.
 */
@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitValidator_3_5_0 extends BaseValidator implements UnitResource.UnitValidator {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    protected UnitService unitService;

    protected AMEEUnit unit;
    protected Set<String> allowedFields = new HashSet<String>();

    public UnitValidator_3_5_0() {
        super();
    }

    @Override
    public void initialise() {
        addName();
        addInternalSymbol();
        addExternalSymbol();
    }

    protected void addName() {
        allowedFields.add("name");
        add(new ValidationSpecification()
                .setName("name")
                .setMaxSize(AMEEUnit.NAME_MAX_SIZE)
                .setAllowEmpty(false)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure Unit is unique on name.
                                AMEEUnit thisUnit = (AMEEUnit) object;
                                if (thisUnit != null) {
                                    if (!unitService.isUnitUniqueByName(thisUnit)) {
                                        errors.rejectValue("name", "duplicate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        })
        );
    }

    protected void addInternalSymbol() {
        allowedFields.add("internalSymbol");
        add(new ValidationSpecification()
                .setName("internalSymbol")
                .setMaxSize(AMEEUnit.SYMBOL_MAX_SIZE)
                .setAllowEmpty(false)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure Unit is unique on symbol.
                                AMEEUnit thisUnit = (AMEEUnit) object;
                                if (thisUnit != null) {
                                    if (!unitService.isUnitUniqueBySymbol(thisUnit)) {
                                        errors.rejectValue("internalSymbol", "duplicate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        })
        );
    }

    protected void addExternalSymbol() {
        allowedFields.add("externalSymbol");
        add(new ValidationSpecification()
                .setName("externalSymbol")
                .setMaxSize(AMEEUnit.SYMBOL_MAX_SIZE)
                .setAllowEmpty(true)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure Unit is unique on symbol.
                                AMEEUnit thisUnit = (AMEEUnit) object;
                                if (thisUnit != null) {
                                    if (!unitService.isUnitUniqueBySymbol(thisUnit)) {
                                        errors.rejectValue("externalSymbol", "duplicate");
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        })
        );
    }

    @Override
    public String getName() {
        return "unit";
    }

    @Override
    public boolean supports(Class clazz) {
        return AMEEUnit.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public AMEEUnit getObject() {
        return unit;
    }

    @Override
    public void setObject(AMEEUnit unit) {
        this.unit = unit;
    }
}