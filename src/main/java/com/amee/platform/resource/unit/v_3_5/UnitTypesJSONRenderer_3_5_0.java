package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.unit.UnitTypeResource;
import com.amee.platform.resource.unit.UnitTypesResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitTypesJSONRenderer_3_5_0 implements UnitTypesResource.Renderer {

    protected JSONObject rootObj;
    protected JSONArray unitTypesArr;

    @Override
    public void start() {
        rootObj = new JSONObject();
        unitTypesArr = new JSONArray();
        ResponseHelper.put(rootObj, "unitTypes", unitTypesArr);
    }

    @Override
    public void newUnitType(UnitTypeResource.Renderer renderer) {
        try {
            unitTypesArr.put(((JSONObject) renderer.getObject()).getJSONObject("unitType"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public String getMediaType() {
        return "application/json";
    }

    @Override
    public JSONObject getObject() {
        return rootObj;
    }
}
