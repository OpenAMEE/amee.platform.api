package com.amee.platform.resource.profileitem.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.profileitem.ProfileItemResource;
import com.amee.platform.resource.profileitem.ProfileItemsResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemsJSONRenderer_3_6_0 implements ProfileItemsResource.Renderer {

    private JSONObject rootObj;
    private JSONArray itemsArr;

    @Override
    public void start() {
        rootObj = new JSONObject();
        itemsArr = new JSONArray();
        ResponseHelper.put(rootObj, "items", itemsArr);
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newProfileItem(ProfileItemResource.Renderer renderer) {
        try {
            itemsArr.put(((JSONObject) renderer.getObject()).getJSONObject("item"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @Override
    public void setTruncated(boolean truncated) {
        ResponseHelper.put(rootObj, "resultsTruncated", truncated);
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
