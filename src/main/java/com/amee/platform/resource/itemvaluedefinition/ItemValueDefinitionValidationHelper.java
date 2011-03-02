package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.validation.ValidationHelper;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.ApiVersionSetEditor;
import com.amee.platform.resource.PerUnitEditor;
import com.amee.platform.resource.UnitEditor;
import com.amee.platform.resource.ValueDefinitionEditor;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.HashSet;
import java.util.Set;

@Service
@Scope("prototype")
public class ItemValueDefinitionValidationHelper extends ValidationHelper {

    @Autowired
    private ItemValueDefinitionValidator itemValueDefinitionValidator;

    private ItemValueDefinition itemValueDefinition;
    private Set<String> allowedFields;

    @Autowired
    private ValueDefinitionEditor valueDefinitionEditor;

    @Autowired
    private ApiVersionSetEditor apiVersionSetEditor;

    @Override
    public Object getObject() {
        return itemValueDefinition;
    }

    @Override
    protected Validator getValidator() {
        return itemValueDefinitionValidator;
    }

    @Override
    public String getName() {
        return "itemValueDefinition";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("name");
            allowedFields.add("path");
            allowedFields.add("wikiDoc");
            allowedFields.add("value");
            allowedFields.add("choices");
            allowedFields.add("fromProfile");
            allowedFields.add("fromData");
            allowedFields.add("allowedRoles");
            allowedFields.add("unit");
            allowedFields.add("perUnit");
            allowedFields.add("valueDefinition");
            allowedFields.add("apiVersions");
        }
        return allowedFields.toArray(new String[]{});
    }

    public ItemValueDefinition getItemValueDefinition() {
        return itemValueDefinition;
    }

    public void setItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        this.itemValueDefinition = itemValueDefinition;
    }

    @Override
    protected void registerCustomEditors(DataBinder dataBinder) {
        dataBinder.registerCustomEditor(ValueDefinition.class, "valueDefinition", valueDefinitionEditor);
        dataBinder.registerCustomEditor(Set.class, "apiVersions", apiVersionSetEditor);
        dataBinder.registerCustomEditor(AmountUnit.class, "unit", new UnitEditor());
        dataBinder.registerCustomEditor(AmountPerUnit.class, "perUnit", new PerUnitEditor());
    }
}