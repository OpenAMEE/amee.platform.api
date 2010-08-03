package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.validation.ValidationHelper;
import com.amee.domain.ValueUsageType;
import com.amee.domain.data.ItemValueUsage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
public class ItemValueUsageValidationHelper extends ValidationHelper {

    @Autowired
    private ItemValueUsageValidator itemValueUsageValidator;

    private ItemValueUsage itemValueUsage;
    private Set<String> allowedFields;

    @Override
    protected void registerCustomEditors(DataBinder dataBinder) {
        dataBinder.registerCustomEditor(ValueUsageType.class, "type", new ValueUsageTypeEditor());
    }

    @Override
    public Object getObject() {
        return itemValueUsage;
    }

    @Override
    protected Validator getValidator() {
        return itemValueUsageValidator;
    }

    @Override
    public String getName() {
        return "itemValueUsage";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("type");
        }
        return allowedFields.toArray(new String[]{});
    }

    public ItemValueUsage getItemValueUsage() {
        return itemValueUsage;
    }

    public void setItemValueUsage(ItemValueUsage itemValueUsage) {
        this.itemValueUsage = itemValueUsage;
    }
}