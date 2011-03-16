package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.ValueUsageType;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import com.amee.platform.resource.itemvaluedefinition.ValueUsageTypeEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueUsageValidator_3_1_0 extends BaseValidator implements ItemValueDefinitionResource.ItemValueUsageValidator {

    private ItemValueUsage itemValueUsage;
    private Set<String> allowedFields = new HashSet<String>();

    public ItemValueUsageValidator_3_1_0() {
        super();
    }

    @Override
    public void initialise() {
        addName();
        addType();
    }

    private void addName() {
        allowedFields.add("name");
        add(new ValidationSpecification()
                .setName("name")
                .setMinSize(ItemValueUsage.NAME_MIN_SIZE)
                .setMaxSize(ItemValueUsage.NAME_MAX_SIZE)
        );
    }

    private void addType() {
        allowedFields.add("type");
        add(ValueUsageType.class, "type", new ValueUsageTypeEditor());
        add(new ValidationSpecification()
                .setName("type")
        );
    }

    @Override
    public String getName() {
        return "itemValueUsage";
    }

    @Override
    public boolean supports(Class clazz) {
        return ItemValueUsage.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        return allowedFields.toArray(new String[]{});
    }

    @Override
    public ItemValueUsage getObject() {
        return itemValueUsage;
    }

    @Override
    public void setObject(ItemValueUsage itemValueUsage) {
        this.itemValueUsage = itemValueUsage;
    }
}