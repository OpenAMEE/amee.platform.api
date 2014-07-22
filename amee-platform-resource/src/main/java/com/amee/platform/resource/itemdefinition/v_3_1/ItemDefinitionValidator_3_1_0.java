package com.amee.platform.resource.itemdefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.validation.BaseValidator;
import com.amee.base.validation.ValidationSpecification;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemDefinitionValidator_3_1_0 extends BaseValidator implements ItemDefinitionResource.ItemDefinitionValidator {

    private ItemDefinition object;
    private Set<String> allowedFields;

    public ItemDefinitionValidator_3_1_0() {
        super();
        addName();
        addDrillDown();
        addUsages();
    }

    private void addName() {
        add(new ValidationSpecification()
                .setName("name")
                .setMinSize(ItemDefinition.NAME_MIN_SIZE)
                .setMaxSize(ItemDefinition.NAME_MAX_SIZE)
        );
    }

    private void addDrillDown() {
        add(new ValidationSpecification()
                .setName("drillDown")
                .setMinSize(ItemDefinition.DRILL_DOWN_MIN_SIZE)
                .setMaxSize(ItemDefinition.DRILL_DOWN_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    private void addUsages() {
        add(new ValidationSpecification()
                .setName("usagesString")
                .setMinSize(ItemDefinition.USAGES_MIN_SIZE)
                .setMaxSize(ItemDefinition.USAGES_MAX_SIZE)
                .setAllowEmpty(true)
        );
    }

    @Override
    public String getName() {
        return "itemDefinition";
    }

    public boolean supports(Class clazz) {
        return ItemDefinition.class.isAssignableFrom(clazz);
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("drillDown");
            allowedFields.add("usagesString");
        }
        return allowedFields.toArray(new String[]{});
    }

    @Override
    protected void beforeBind(Map<String, String> values) {
        this.renameValue(values, "usages", "usagesString");
    }

    @Override
    public ItemDefinition getObject() {
        return object;
    }

    @Override
    public void setObject(ItemDefinition object) {
        this.object = object;
    }
}