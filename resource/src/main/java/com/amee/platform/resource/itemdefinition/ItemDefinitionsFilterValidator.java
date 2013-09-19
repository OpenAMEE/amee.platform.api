package com.amee.platform.resource.itemdefinition;

import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.search.ItemDefinitionsFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ItemDefinitionsFilterValidator extends BaseValidator {

    public ItemDefinitionsFilterValidator() {
        super();
        addName();
    }

    @Override
    public boolean supports(Class clazz) {
        return ItemDefinitionsFilter.class.isAssignableFrom(clazz);
    }

    private void addName() {
        add(new ValidationSpecification()
                .setName("name")
                .setMinSize(ItemDefinition.NAME_MIN_SIZE)
                .setMaxSize(ItemDefinition.NAME_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }
}
