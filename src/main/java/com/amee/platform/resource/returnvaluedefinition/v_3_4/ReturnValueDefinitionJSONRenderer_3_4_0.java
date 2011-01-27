package com.amee.platform.resource.returnvaluedefinition.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.returnvaluedefinition.ReturnValueDefinitionResource;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class ReturnValueDefinitionJSONRenderer_3_4_0 implements ReturnValueDefinitionResource.Renderer {

    private ReturnValueDefinition returnValueDefinition;
    private JSONObject rootObj;
    private JSONObject returnValueDefinitionObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newReturnValueDefinition(ReturnValueDefinition returnValueDefinition) {
        this.returnValueDefinition = returnValueDefinition;
        returnValueDefinitionObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "returnValueDefinition", returnValueDefinitionObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(returnValueDefinitionObj, "uid", returnValueDefinition.getUid());
    }

    @Override
    public void addType() {
        ResponseHelper.put(returnValueDefinitionObj, "type", returnValueDefinition.getType());
    }

    @Override
    public void addItemDefinition(ItemDefinition itemDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", itemDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", itemDefinition.getName());
        ResponseHelper.put(returnValueDefinitionObj, "itemDefinition", itemDefinitionObj);
    }

    @Override
    public void addValueDefinition(ValueDefinition valueDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", valueDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", valueDefinition.getName());
        ResponseHelper.put(itemDefinitionObj, "valueType", valueDefinition.getValueType().getName());
        ResponseHelper.put(returnValueDefinitionObj, "valueDefinition", itemDefinitionObj);
    }

    @Override
    public void addUnits() {
        ResponseHelper.put(returnValueDefinitionObj, "unit", returnValueDefinition.getUnit().toString());
        ResponseHelper.put(returnValueDefinitionObj, "perUnit", returnValueDefinition.getPerUnit().toString());
    }

    @Override
    public void addFlags() {
        ResponseHelper.put(returnValueDefinitionObj, "default", Boolean.toString(returnValueDefinition.isDefaultType()));
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(returnValueDefinitionObj, "status", returnValueDefinition.getStatus().getName());
        ResponseHelper.put(returnValueDefinitionObj, "created", DATE_FORMAT.print(returnValueDefinition.getCreated().getTime()));
        ResponseHelper.put(returnValueDefinitionObj, "modified", DATE_FORMAT.print(returnValueDefinition.getModified().getTime()));
    }

    public String getMediaType() {
        return "application/json";
    }

    @Override
    public Object getObject() {
        return rootObj;
    }
}
