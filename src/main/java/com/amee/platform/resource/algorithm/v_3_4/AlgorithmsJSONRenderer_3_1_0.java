package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.platform.resource.algorithm.AlgorithmResource;
import com.amee.platform.resource.algorithm.AlgorithmsResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.1.0")
public class AlgorithmsJSONRenderer_3_1_0 implements AlgorithmsResource.Renderer {

    private JSONObject rootObj;
    private JSONArray algorithmsArr;

    public void start() {
        rootObj = new JSONObject();
        algorithmsArr = new JSONArray();
        ResponseHelper.put(rootObj, "algorithms", algorithmsArr);
    }

    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    public void newAlgorithm(AlgorithmResource.Renderer renderer) {
        try {
            algorithmsArr.put(((JSONObject) renderer.getObject()).getJSONObject("algorithm"));
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
