package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionsResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionsJSONRenderer_3_1_0 implements ItemValueDefinitionsResource.Renderer {

    private JSONObject rootObj;
    private JSONArray itemValueDefinitionsArr;

    public void start() {
        rootObj = new JSONObject();
        itemValueDefinitionsArr = new JSONArray();
        put(rootObj, "itemValueDefinitions", itemValueDefinitionsArr);
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    public void newItemValueDefinition(ItemValueDefinitionResource.Renderer renderer) {
        try {
            itemValueDefinitionsArr.put(((JSONObject) renderer.getObject()).getJSONObject("itemValueDefinition"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    protected JSONObject put(JSONObject o, String key, Object value) {
        try {
            return o.put(key, value);
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
