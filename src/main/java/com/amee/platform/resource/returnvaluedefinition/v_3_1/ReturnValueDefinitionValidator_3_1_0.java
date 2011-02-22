package com.amee.platform.resource.returnvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.PerUnitEditor;
import com.amee.platform.resource.UnitEditor;
import com.amee.platform.resource.ValueDefinitionEditor;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionResource;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * A Validator implementation for validating ReturnValueDefinition.
 */
@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionValidator_3_1_0 extends BaseValidator implements ReturnValueDefinitionResource.ReturnValueDefinitionValidator {

    // Alpha numerics & underscore.
    private final static String TYPE_PATTERN_STRING = "^[a-zA-Z0-9_]*$";

    @Autowired
    private ValueDefinitionEditor valueDefinitionEditor;

    private ReturnValueDefinition returnValueDefinition;
    protected Set<String> allowedFields = new HashSet<String>();

    public ReturnValueDefinitionValidator_3_1_0() {
        super();
    }

    @Override
    public void initialise() {
        addType();
        addValueDefinition();
        addUnit();
        addPerUnit();
        addDefaultType();
    }

    /**
     * Configure the validator for the type property of the DataItem.
     * <p/>
     * TODO: Validate for uniqueness.
     */
    private void addType() {
        allowedFields.add("type");
        add(new ValidationSpecification()
                .setName("type")
                .setMinSize(ReturnValueDefinition.TYPE_MIN_SIZE)
                .setMaxSize(ReturnValueDefinition.TYPE_MAX_SIZE)
                .setFormat(TYPE_PATTERN_STRING)
        );
    }

    /**
     * Configure the validator for the unit property of the DataItem.
     */
    private void addUnit() {
        allowedFields.add("unit");
        add(AmountUnit.class, "unit", new UnitEditor());
        add(new ValidationSpecification()
                .setName("unit")
                .setAllowEmpty(true)
                .setMaxSize(ReturnValueDefinition.UNIT_MAX_SIZE)
        );
    }

    /**
     * Configure the validator for the perUnit property of the DataItem.
     */
    private void addPerUnit() {
        allowedFields.add("perUnit");
        add(AmountPerUnit.class, "perUnit", new PerUnitEditor());
        add(new ValidationSpecification()
                .setName("perUnit")
                .setAllowEmpty(true)
                .setMaxSize(ReturnValueDefinition.PER_UNIT_MAX_SIZE)
        );
    }

    /**
     * Configure the validator for the defaultType property of the DataItem.
     * <p/>
     * TODO: If defaultType is set to true then no other peer RVD should have defaultType set to true.
     */
    private void addDefaultType() {
        allowedFields.add("defaultType");
    }

    /**
     * Configure the validator for the valueDefinition property of the DataItem.
     */
    private void addValueDefinition() {
        allowedFields.add("valueDefinition");
        add(ValueDefinition.class, "valueDefinition", valueDefinitionEditor);
    }

    @Override
    public String getName() {
        return "returnValueDefinition";
    }

    @Override
    public boolean supports(Class clazz) {
        return ReturnValueDefinition.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public ReturnValueDefinition getObject() {
        return returnValueDefinition;
    }

    @Override
    public void setObject(ReturnValueDefinition returnValueDefinition) {
        this.returnValueDefinition = returnValueDefinition;
    }
}