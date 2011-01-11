package com.amee.platform.resource.drill.v_3_3;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.sheet.Choice;
import com.amee.platform.resource.drill.DrillResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.3.0")
public class DrillJSONRenderer_3_3_0 implements DrillResource.Renderer {

    private JSONObject rootObj;
    private JSONObject drillObj;
    private JSONArray selectionsArr;
    private JSONObject choicesObj;
    private JSONArray choicesArr;

    public void start() {
        rootObj = new JSONObject();
        drillObj = new JSONObject();
        ResponseHelper.put(rootObj, "drill", drillObj);
    }

    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    public void startSelections() {
        selectionsArr = new JSONArray();
        ResponseHelper.put(drillObj, "selections", selectionsArr);
    }

    public void newSelection(Choice selection) {
        JSONObject obj = new JSONObject();
        if (!selection.getValue().equals(selection.getName())) {
            ResponseHelper.put(obj, "name", selection.getName());
        }
        ResponseHelper.put(obj, "value", selection.getValue());
        selectionsArr.put(obj);
    }

    public void startChoices(String name) {
        choicesObj = new JSONObject();
        ResponseHelper.put(drillObj, "choices", choicesObj);
        ResponseHelper.put(choicesObj, "name", name);
        choicesArr = new JSONArray();
        ResponseHelper.put(choicesObj, "choices", choicesArr);
    }

    public void newChoice(Choice choice) {
        JSONObject obj = new JSONObject();
        if (!choice.getValue().equals(choice.getName())) {
            ResponseHelper.put(obj, "name", choice.getName());
        }
        ResponseHelper.put(obj, "value", choice.getValue());
        choicesArr.put(obj);
    }

    public String getMediaType() {
        return "application/json";
    }

    public JSONObject getObject() {
        return rootObj;
    }
}
