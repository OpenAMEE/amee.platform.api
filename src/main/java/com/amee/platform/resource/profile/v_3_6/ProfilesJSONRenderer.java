package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.profile.ProfileResource;
import com.amee.platform.resource.profile.ProfilesResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfilesJSONRenderer implements ProfilesResource.Renderer {

    protected JSONObject rootObj;
    protected JSONArray profilesArr;

    @Override
    public void start() {
        rootObj = new JSONObject();
        profilesArr = new JSONArray();
        ResponseHelper.put(rootObj, "profiles", profilesArr);
    }

    @Override
    public void newProfile(ProfileResource.Renderer renderer) {
        try {
            profilesArr.put(((JSONObject) renderer.getObject()).getJSONObject("profile"));
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

    @Override
    public void setTruncated(boolean truncated) {
        // TODO
    }
}
