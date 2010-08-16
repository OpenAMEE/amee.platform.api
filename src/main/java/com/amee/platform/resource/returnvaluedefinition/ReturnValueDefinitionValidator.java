package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.ReturnValueDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ReturnValueDefinitionValidator extends BaseValidator {

    // Alpha numerics & underscore.
    private final static String TYPE_PATTERN_STRING = "^[a-zA-Z0-9_]*$";
    private final static String UNIT_PATTERN_STRING = TYPE_PATTERN_STRING;

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
                .setMinSize(ReturnValueDefinition.UNIT_MIN_SIZE)
                .setMaxSize(ReturnValueDefinition.UNIT_MAX_SIZE)
                .setFormat(UNIT_PATTERN_STRING)
                .setAllowEmpty(true)
        );
    }

    private void addPerUnit() {
        add(new ValidationSpecification()
                .setName("perUnit")
                .setMinSize(ReturnValueDefinition.PER_UNIT_MIN_SIZE)
                .setMaxSize(ReturnValueDefinition.PER_UNIT_MAX_SIZE)
                .setFormat(UNIT_PATTERN_STRING)
                .setAllowEmpty(true)
        );
    }
}