package com.amee.platform.resource.tag.v_3_2;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagResource;
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

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newTag(Tag tag) {
        this.tag = tag;
        tagObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "tag", tagObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(tagObj, "uid", tag.getUid());
        ResponseHelper.put(tagObj, "tag", tag.getTag());
        if (tag.hasCount()) {
            ResponseHelper.put(tagObj, "count", tag.getCount());
        }
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

