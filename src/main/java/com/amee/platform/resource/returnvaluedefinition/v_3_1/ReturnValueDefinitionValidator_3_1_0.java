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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.HashSet;
import java.util.Set;

/**
 * A Validator implementation for validating ReturnValueDefinition.
 */
@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionValidator_3_1_0 extends BaseValidator implements ReturnValueDefinitionResource.ReturnValueDefinitionValidator {

    private final Log log = LogFactory.getLog(getClass());

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
        addName();
        addUnit();
        addPerUnit();
        addDefaultType();
    }

    /**
     * Configure the validator for the type property of the DataItem. Will reject the type value if there is
     * another ReturnValueDefinition with this type in the ItemDefinition.
     */
    private void addType() {
        allowedFields.add("type");
        add(new ValidationSpecification()
                .setName("type")
                .setMinSize(ReturnValueDefinition.TYPE_MIN_SIZE)
                .setMaxSize(ReturnValueDefinition.TYPE_MAX_SIZE)
                .setFormat(TYPE_PATTERN_STRING)
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                // Ensure ReturnValueDefinition is unique on type.
                                ReturnValueDefinition thisRVD = (ReturnValueDefinition) object;
                                if ((thisRVD != null) && (thisRVD.getItemDefinition() != null)) {
                                    for (ReturnValueDefinition rvd : thisRVD.getItemDefinition().getActiveReturnValueDefinitions()) {
                                        if (!thisRVD.equals(rvd) && thisRVD.getType().equals(rvd.getType())) {
                                            errors.rejectValue("type", "duplicate");
                                        }
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        }));
    }

	private void addName() {
		allowedFields.add("name");
		add(new ValidationSpecification()
				.setName("name")
				.setMinSize(ReturnValueDefinition.NAME_MIN_SIZE)
				.setMaxSize(ReturnValueDefinition.NAME_MAX_SIZE));
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
     */
    private void addDefaultType() {
        allowedFields.add("defaultType");
        add(new ValidationSpecification()
                .setName("defaultType")
                .setCustomValidation(
                        new ValidationSpecification.CustomValidation() {
                            @Override
                            public int validate(Object object, Object value, Errors errors) {
                                boolean foundDefault = false;
                                // Ensure there is only one ReturnValueDefinition with defaultType set to true.
                                ReturnValueDefinition thisRVD = (ReturnValueDefinition) object;
                                if ((thisRVD != null) && (thisRVD.getItemDefinition() != null)) {
                                    // Iterate over all ReturnValueDefinitions for the active ItemDefinition.
                                    for (ReturnValueDefinition rvd : thisRVD.getItemDefinition().getActiveReturnValueDefinitions()) {
                                        // Set foundDefault to true if we discover a default.
                                        if (rvd.isDefaultType()) {
                                            foundDefault = true;
                                        }
                                    }
                                    // Is there now now default type for the ItemDefinition?
                                    if (!foundDefault) {
                                        // Did we cause there to be no default type?
                                        if (thisRVD.hasDefaultTypeChanged() && !thisRVD.isDefaultType()) {
                                            // Rejected as this has caused the ItemDefinition to not
                                            // have a default type.
                                            errors.rejectValue("defaultType", "no_default_type");
                                        } else {
                                            // The ItemDefinition does not have a default type.
                                            log.warn("addDefaultType() ItemDefinition does not " +
                                                    "have a ReturnValueDefinition with a default type: " +
                                                    thisRVD.getItemDefinition().getUid());
                                        }
                                    }
                                }
                                return ValidationSpecification.CONTINUE;
                            }
                        }));
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