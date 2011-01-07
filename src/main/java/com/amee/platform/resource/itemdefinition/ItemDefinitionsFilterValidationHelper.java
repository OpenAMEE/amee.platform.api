package com.amee.platform.resource.itemdefinition;

import com.amee.base.validation.ValidationHelper;
import com.amee.platform.search.ItemDefinitionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
public class ItemDefinitionsFilterValidationHelper extends ValidationHelper {

    @Autowired
    private ItemDefinitionsFilterValidator validator;

    private ItemDefinitionsFilter itemDefinitionsFilter;
    private Set<String> allowedFields;

    @Override
    public Object getObject() {
        return itemDefinitionsFilter;
    }

    @Override
    protected Validator getValidator() {
        return validator;
    }

    @Override
    public String getName() {
        return "itemDefinitionFilter";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("resultStart");
            allowedFields.add("resultLimit");
        }
        return allowedFields.toArray(new String[]{});
    }

    public ItemDefinitionsFilter getItemDefinitionFilter() {
        return itemDefinitionsFilter;
    }

    public void setItemDefinitionFilter(ItemDefinitionsFilter itemDefinitionsFilter) {
        this.itemDefinitionsFilter = itemDefinitionsFilter;
    }
}
