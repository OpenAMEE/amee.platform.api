package com.amee.platform.resource.profile.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.data.DataCategory;
import com.amee.domain.profile.Profile;
import com.amee.platform.resource.profile.ProfileResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.xml.ws.Response;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileJSONRenderer_3_6_0 implements ProfileResource.Renderer {

    protected Profile profile;
    protected JSONObject rootObj;
    protected JSONObject profileObj;
    protected JSONArray categoriesArr;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newProfile(Profile profile) {
        this.profile = profile;
        profileObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "profile", profileObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(profileObj, "uid", profile.getUid());
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(profileObj, "status", profile.getStatus().getName());
        ResponseHelper.put(profileObj, "created", DATE_FORMAT.print(profile.getCreated().getTime()));
        ResponseHelper.put(profileObj, "modified", DATE_FORMAT.print(profile.getModified().getTime()));
    }

    @Override
    public void startCategories() {
        if (profileObj != null) {
            categoriesArr = new JSONArray();
            ResponseHelper.put(profileObj, "categories", categoriesArr);
        }
    }

    @Override
    public void newCategory(DataCategory category) {
        if (categoriesArr != null) {
            JSONObject categoryObj = new JSONObject();
            ResponseHelper.put(categoryObj, "uid", category.getUid());
            ResponseHelper.put(categoryObj, "name", category.getName());
            ResponseHelper.put(categoryObj, "wikiName", category.getWikiName());
            categoriesArr.put(categoryObj);
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
