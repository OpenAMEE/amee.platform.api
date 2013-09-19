package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import com.amee.platform.resource.tag.TagsResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagsJSONRenderer_3_2_0 implements TagsResource.Renderer {

    protected JSONObject rootObj;
    protected JSONArray tagsArr;

    @Override
    public void start() {
        rootObj = new JSONObject();
        tagsArr = new JSONArray();
        ResponseHelper.put(rootObj, "tags", tagsArr);
    }

    @Override
    public void newTag(TagResource.Renderer renderer) {
        try {
            tagsArr.put(((JSONObject) renderer.getObject()).getJSONObject("tag"));
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    @Deprecated
    @Override
    public void newTag(Tag tag) {
        throw new UnsupportedOperationException("This method is deprecated since 3.2.0.");
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
