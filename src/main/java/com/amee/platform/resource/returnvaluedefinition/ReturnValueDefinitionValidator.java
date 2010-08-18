package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.ReturnValueDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ReturnValueDefinitionValidator extends BaseValidator {

    // TODO: If defaultType is set to true then no other peer RVD should have defaultType set to true.

    // Alpha numerics & underscore.
    private final static String TYPE_PATTERN_STRING = "^[a-zA-Z0-9_]*$";

    public ReturnValueDefinitionValidator() {
        super();
        addType();
        addUnit();
        addPerUnit();
    }

    public boolean supports(Class clazz) {
        return ReturnValueDefinition.class.isAssignableFrom(clazz);
    }

    private void addType() {
        add(new ValidationSpecification()
                .setName("type")
                .setMinSize(ReturnValueDefinition.TYPE_MIN_SIZE)
                .setMaxSize(ReturnValueDefinition.TYPE_MAX_SIZE)
                .setFormat(TYPE_PATTERN_STRING)
        );
    }

    private void addUnit() {
        add(new ValidationSpecification()
                .setName("unit")
                .setAllowEmpty(true)
        );
    }

    private void addPerUnit() {
        add(new ValidationSpecification()
                .setName("perUnit")
                .setAllowEmpty(true)
        );
    }
}