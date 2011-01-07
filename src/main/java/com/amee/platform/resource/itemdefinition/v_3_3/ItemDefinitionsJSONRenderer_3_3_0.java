package com.amee.platform.resource.itemdefinition.v_3_3;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
import com.amee.platform.resource.itemdefinition.ItemDefinitionsResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.3.0")
public class ItemDefinitionsJSONRenderer_3_3_0 implements ItemDefinitionsResource.Renderer {

    private JSONObject rootObj;
    private JSONArray itemDefinitionsArr;

    public void start() {
        rootObj = new JSONObject();
        itemDefinitionsArr = new JSONArray();
        ResponseHelper.put(rootObj, "itemDefinitions", itemDefinitionsArr);
    }

    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    public void newItemDefinition(ItemDefinitionResource.Renderer renderer) {
        try {
            itemDefinitionsArr.put(((JSONObject) renderer.getObject()).getJSONObject("itemDefinition"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public String getMediaType() {
        return "application/json";
    }

    public JSONObject getObject() {
        return rootObj;
    }
}
