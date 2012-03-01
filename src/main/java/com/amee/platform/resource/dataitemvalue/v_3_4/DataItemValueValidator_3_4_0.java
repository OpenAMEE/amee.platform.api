package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.DataItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.HistoryValue;
import com.amee.domain.item.data.BaseDataItemTextValue;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.platform.resource.StartEndDateEditor;
import com.amee.platform.resource.itemvaluedefinition.ItemValueEditor;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;
import com.amee.platform.science.StartEndDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * A Validator implementation for validating DataItems.
 */
@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueValidator_3_4_0 extends BaseValidator implements DataItemValueResource.DataItemValueValidator {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    protected DataItemService dataItemService;

    protected BaseDataItemValue dataItemValue;
    protected Set<String> allowedFields = new HashSet<String>();

    public DataItemValueValidator_3_4_0() {
        super();
    }

    @Override
    public void initialise() {
        addValue();
        addStartDate();
    }

    /**
     * Configure the validator for Data Item Values for the current DataItem.
     */
    protected void addValue() {
        ItemValueDefinition ivd = dataItemValue.getItemValueDefinition();
        if (ivd.isDouble()) {
            // Double values.
            // Allow parameter for this ItemValueDefinition.
            allowedFields.add("value");
            // Add ValidationSpecification.
            add(new ValidationSpecification()
                    .setName("value")
                    .setDoubleNumber(true));
            // Add the editor.
            addCustomEditor(Double.class, "value", new ItemValueEditor(ivd));
        } else if (ivd.isInteger()) {
            // Integer values.
            // Allow parameter for this ItemValueDefinition.
            allowedFields.add("value");
            // Add ValidationSpecification.
            add(new ValidationSpecification()
                    .setName("value")
                    .setIntegerNumber(true));
            // Add the editor.
            addCustomEditor(Integer.class, "value", new ItemValueEditor(ivd));
        } else {
            // String values.
            // Allow parameter for this ItemValueDefinition.
            allowedFields.add("value");
            // Add ValidationSpecification.
            add(new ValidationSpecification()
                    .setName("value")
                    .setMaxSize(BaseDataItemTextValue.VALUE_SIZE)
                    .setAllowEmpty(true));
            // Add the editor.
            addCustomEditor(String.class, "value", new ItemValueEditor(ivd));
        }
    }

    /**
     * Configure the validator for the startDate property. Only validate startDate if value is a {@link HistoryValue}.
     */
    protected void addStartDate() {
        if (HistoryValue.class.isAssignableFrom(dataItemValue.getClass())) {
            allowedFields.add("startDate");
            addCustomEditor(StartEndDate.class, "startDate", new StartEndDateEditor(new Date()));
            add(new ValidationSpecification()
                    .setName("startDate")
                    .setAllowEmpty(true)
                    .setCustomValidation(
                            new ValidationSpecification.CustomValidation() {
                                @Override
                                public int validate(Object object, Object value, Errors errors) {
                                    // Ensure historical Data Item Value is unique on startDate.
                                    BaseDataItemValue thisDIV = (BaseDataItemValue) object;
                                    if (thisDIV != null) {
                                        if (HistoryValue.class.isAssignableFrom(thisDIV.getClass())) {
                                            HistoryValue hv = (HistoryValue) thisDIV;
                                            if (hv.getStartDate().compareTo(DataItemService.EPOCH) <= 0) {
                                                errors.rejectValue("startDate", "epoch");
                                            } else if (hv.getStartDate().compareTo(DataItemService.Y2038) >= 0) {
                                                errors.rejectValue("startDate", "end_of_epoch");
                                            } else if (!dataItemService.isDataItemValueUniqueByStartDate(thisDIV)) {
                                                errors.rejectValue("startDate", "duplicate");
                                            }
                                        } else {
                                            throw new IllegalStateException("Should not be checking a non-historical DataItemValue.");
                                        }
                                    }
                                    return ValidationSpecification.CONTINUE;
                                }
                            }));
        }
    }

    @Override
    public String getName() {
        return "dataItemValue";
    }

    @Override
    public boolean supports(Class clazz) {
        return BaseDataItemValue.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public BaseDataItemValue getObject() {
        return dataItemValue;
    }

    @Override
    public void setObject(BaseDataItemValue dataItemValue) {
        this.dataItemValue = dataItemValue;
    }
}