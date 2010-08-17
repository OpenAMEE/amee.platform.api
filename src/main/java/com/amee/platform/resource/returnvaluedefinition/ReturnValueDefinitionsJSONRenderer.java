package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.domain.Since;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionRenderer;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionsRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ReturnValueDefinitionsJSONRenderer implements ReturnValueDefinitionsRenderer {

    private JSONObject rootObj;
    private JSONArray returnValueDefinitionsArr;

    public void start() {
        rootObj = new JSONObject();
        returnValueDefinitionsArr = new JSONArray();
        put(rootObj, "returnValueDefinitions", returnValueDefinitionsArr);
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    public void newReturnValueDefinition(ReturnValueDefinitionRenderer returnValueDefinitionRenderer) {
        try {
            returnValueDefinitionsArr.put(((JSONObject) returnValueDefinitionRenderer.getObject()).getJSONObject("returnValueDefinition"));
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
