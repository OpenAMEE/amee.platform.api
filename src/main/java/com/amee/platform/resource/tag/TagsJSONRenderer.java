package com.amee.platform.resource.tag;

import com.amee.domain.tag.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class TagsJSONRenderer implements TagsRenderer {

    private JSONObject rootObj;
    private JSONArray tagsArr;

    public TagsJSONRenderer() {
        super();
        start();
    }

    public void start() {
        rootObj = new JSONObject();
        tagsArr = new JSONArray();
        put(rootObj, "tags", tagsArr);
    }

    public void newTag(Tag tag) {
        JSONObject tagObj = new JSONObject();
        put(tagObj, "tag", tag.getTag());
        put(tagObj, "count", tag.getCount());
        tagsArr.put(tagObj);
    }

    public void ok() {
        put(rootObj, "status", "OK");
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
