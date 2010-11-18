package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.2.0")
public class TagJSONRenderer_3_2_0 implements TagResource.Renderer {

    private Tag tag;
    private JSONObject rootObj;
    private JSONObject tagObj;

    public void start() {
        rootObj = new JSONObject();
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    public void newTag(Tag tag) {
        this.tag = tag;
        tagObj = new JSONObject();
        if (rootObj != null) {
            put(rootObj, "tag", tagObj);
        }
    }

    public void addBasic() {
        put(tagObj, "uid", tag.getUid());
        put(tagObj, "name", tag.getTag());
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

