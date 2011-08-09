package com.amee.platform.resource.dataitem.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.validation.ValidationSpecification;
import com.amee.platform.resource.dataitem.DataItemResource;
import com.amee.platform.resource.dataitem.v_3_6.DataItemValidator_3_6_0;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

/**
 * DataItem validation. Extends DataItemValidator_3_6_0 but does not check for duplicate DataItems.
 */
@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValidator_3_4_0 extends DataItemValidator_3_6_0 implements DataItemResource.DataItemValidator {

    public DataItemValidator_3_4_0() {
        super();
    }

    /**
     * Duplicate checking is not implemented in 3.4 so we override the 3.6 implementation with the base implementation again.
     * 
     * @param object to validate
     * @param errors to store validation errors
     */
    @Override
    public void validate(Object object, Errors errors) {
        for (ValidationSpecification validationSpecification : getSpecifications()) {
            if (validationSpecification.validate(object, errors) == ValidationSpecification.STOP) {
                break;
            }
        }
    }
}