package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.DataItemService;
import com.amee.domain.DataItemValuesFilter;
import com.amee.platform.resource.StartEndDateEditor;
import com.amee.platform.resource.dataitemvalue.DataItemValuesResource;
import com.amee.platform.science.StartEndDate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValuesFilterValidator_3_4_0 extends BaseValidator implements DataItemValuesResource.FilterValidator {

    protected DataItemValuesFilter object;
    protected Set<String> allowedFields = new HashSet<String>();
    private Date defaultStartDate = new Date();

    public DataItemValuesFilterValidator_3_4_0() {
        super();
    }

    @Override
    public void initialise() {
        addStartDate();
        addEndDate();
        allowedFields.add("resultStart");
        allowedFields.add("resultLimit");
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

    @Override
    public String getName() {
        return "dataItemValuesFilter";
    }

    @Override
    public boolean supports(Class clazz) {
        return DataItemValuesFilter.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[allowedFields.size()]);
    }

    @Override
    public DataItemValuesFilter getObject() {
        return object;
    }

    @Override
    public void setObject(DataItemValuesFilter object) {
        this.object = object;
    }

    @Override
    public Date getDefaultStartDate() {
        return defaultStartDate;
    }

    @Override
    public void setDefaultStartDate(Date defaultStartDate) {
        this.defaultStartDate = defaultStartDate;
    }
}