package com.amee.platform.resource.returnvaluedefinition.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.ValueDefinition;
import com.amee.platform.resource.returnvaluedefinition.v_3_4.ReturnValueDefinitionJSONRenderer_3_4_0;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class ReturnValueDefinitionJSONRenderer_3_2_0 extends ReturnValueDefinitionJSONRenderer_3_4_0 {

    @Override
    public void addValueDefinition(ValueDefinition valueDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", valueDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", valueDefinition.getName());
        ResponseHelper.put(itemDefinitionObj, "valueType", valueDefinition.getValueType().getName());
        ResponseHelper.put(returnValueDefinitionObj, "valueDefinition", itemDefinitionObj);
    }
}
