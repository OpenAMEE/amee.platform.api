package com.amee.platform.resource.returnvaluedefinition;

import com.amee.base.domain.Since;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ReturnValueDefinitionJSONRenderer implements ReturnValueDefinitionRenderer {

    // TODO: Add audit (created, modified).

    private ReturnValueDefinition returnValueDefinition;
    private JSONObject rootObj;
    private JSONObject returnValueDefinitionObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        put(rootObj, "status", "OK");
    }

    @Override
    public void newReturnValueDefinition(ReturnValueDefinition returnValueDefinition) {
        this.returnValueDefinition = returnValueDefinition;
        returnValueDefinitionObj = new JSONObject();
        if (rootObj != null) {
            put(rootObj, "returnValueDefinition", returnValueDefinitionObj);
        }
    }

    @Override
    public void addBasic() {
        put(returnValueDefinitionObj, "uid", returnValueDefinition.getUid());
    }

    @Override
    public void addType() {
        put(returnValueDefinitionObj, "type", returnValueDefinition.getType());
    }

    @Override
    public void addItemDefinition(ItemDefinition itemDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        put(itemDefinitionObj, "uid", itemDefinition.getUid());
        put(itemDefinitionObj, "name", itemDefinition.getName());
        put(returnValueDefinitionObj, "itemDefinition", itemDefinitionObj);
    }

    @Override
    public void addUnits() {
        put(returnValueDefinitionObj, "unit", returnValueDefinition.getUnit().toString());
        put(returnValueDefinitionObj, "perUnit", returnValueDefinition.getPerUnit().toString());
    }

    @Override
    public void addFlags() {
        put(returnValueDefinitionObj, "default", Boolean.toString(returnValueDefinition.isDefaultType()));
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

    @Override
    public Object getObject() {
        return rootObj;
    }
}
