package com.amee.platform.resource.profileitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.profileitemvalue.ProfileItemValueResource;
import com.amee.platform.resource.profileitemvalue.ProfileItemValuesResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemValuesJSONRenderer_3_6_0 implements ProfileItemValuesResource.Renderer {

    private JSONObject rootObj;
    private JSONArray valuesArr;

    @Override
    public void start() {
        rootObj = new JSONObject();
        valuesArr = new JSONArray();
        ResponseHelper.put(rootObj, "values", valuesArr);
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newProfileItemValue(ProfileItemValueResource.Renderer renderer) {
        try {
            valuesArr.put(((JSONObject) renderer.getObject()).getJSONObject("value"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @Override
    public String getMediaType() {
        return "application/json";
    }

    @Override
    public Object getObject() {
        return rootObj;
    }
}
