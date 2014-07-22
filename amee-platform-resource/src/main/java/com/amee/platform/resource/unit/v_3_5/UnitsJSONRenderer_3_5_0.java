package com.amee.platform.resource.unit.v_3_5;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.unit.UnitResource;
import com.amee.platform.resource.unit.UnitsResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.5.0")
public class UnitsJSONRenderer_3_5_0 implements UnitsResource.Renderer {

    protected JSONObject rootObj;
    protected JSONArray unitsArr;

    @Override
    public void start() {
        rootObj = new JSONObject();
        unitsArr = new JSONArray();
        ResponseHelper.put(rootObj, "units", unitsArr);
    }

    @Override
    public void newUnit(UnitResource.Renderer renderer) {
        try {
            unitsArr.put(((JSONObject) renderer.getObject()).getJSONObject("unit"));
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
