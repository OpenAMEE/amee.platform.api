package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.ValueDefinition;
import com.amee.platform.resource.itemvaluedefinition.v_3_4.ItemValueDefinitionJSONRenderer_3_4_0;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionJSONRenderer_3_1_0 extends ItemValueDefinitionJSONRenderer_3_4_0 {

    @Override
    public void addValueDefinition(ValueDefinition valueDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", valueDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", valueDefinition.getName());
        ResponseHelper.put(itemDefinitionObj, "valueType", valueDefinition.getValueType().getName());
        ResponseHelper.put(itemValueDefinitionObj, "valueDefinition", itemDefinitionObj);
    }
}
