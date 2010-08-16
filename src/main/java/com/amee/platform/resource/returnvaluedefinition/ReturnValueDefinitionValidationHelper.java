package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.validation.ValidationHelper;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.PerUnitEditor;
import com.amee.platform.resource.UnitEditor;
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
public class ReturnValueDefinitionValidationHelper extends ValidationHelper {

    @Autowired
    private ReturnValueDefinitionValidator returnValueDefinitionValidator;

    private ReturnValueDefinition returnValueDefinition;
    private Set<String> allowedFields;

    @Override
    protected void registerCustomEditors(DataBinder dataBinder) {
        dataBinder.registerCustomEditor(AmountUnit.class, "unit", new UnitEditor());
        dataBinder.registerCustomEditor(AmountPerUnit.class, "perUnit", new PerUnitEditor());
    }

    @Override
    public Object getObject() {
        return returnValueDefinition;
    }

    @Override
    protected Validator getValidator() {
        return returnValueDefinitionValidator;
    }

    @Override
    public String getName() {
        return "returnValueDefinition";
    }

    @Override
    public String[] getAllowedFields() {
        if (allowedFields == null) {
            allowedFields = new HashSet<String>();
            allowedFields.add("type");
            allowedFields.add("unit");
            allowedFields.add("perUnit");
            allowedFields.add("defaultType");
        }
        return allowedFields.toArray(new String[]{});
    }

    public ReturnValueDefinition getReturnValueDefinition() {
        return returnValueDefinition;
    }

    public void setReturnValueDefinition(ReturnValueDefinition returnValueDefinition) {
        this.returnValueDefinition = returnValueDefinition;
    }
}