package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.ItemValueUsage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ItemValueUsageValidator extends BaseValidator {

    public ItemValueUsageValidator() {
        super();
        addName();
        addType();
    }

    public boolean supports(Class clazz) {
        return ItemValueUsage.class.isAssignableFrom(clazz);
    }

    private void addName() {
        add(new ValidationSpecification()
                .setName("name")
                .setMinSize(ItemValueUsage.NAME_MIN_SIZE)
                .setMaxSize(ItemValueUsage.NAME_MAX_SIZE)
        );
    }

    private void addType() {
        add(new ValidationSpecification()
                .setName("type")
        );
    }
}