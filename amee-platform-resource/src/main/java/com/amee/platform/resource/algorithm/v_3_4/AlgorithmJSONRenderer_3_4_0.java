package com.amee.platform.resource.algorithm.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import com.amee.platform.resource.algorithm.AlgorithmResource;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.4.0")
public class AlgorithmJSONRenderer_3_4_0 implements AlgorithmResource.Renderer {

    protected Algorithm algorithm;
    protected JSONObject rootObj;
    protected JSONObject algorithmObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        algorithmObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "algorithm", algorithmObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(algorithmObj, "uid", algorithm.getUid());
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(algorithmObj, "status", algorithm.getStatus().getName());
        ResponseHelper.put(algorithmObj, "created", DATE_FORMAT.print(algorithm.getCreated().getTime()));
        ResponseHelper.put(algorithmObj, "modified", DATE_FORMAT.print(algorithm.getModified().getTime()));
    }

    @Override
    public void addName() {
        ResponseHelper.put(algorithmObj, "name", algorithm.getName());
    }

    @Override
    public void addContent() {
        ResponseHelper.put(algorithmObj, "content", algorithm.getContent());
    }

    @Override
    public void addItemDefinition(ItemDefinition itemDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", itemDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", itemDefinition.getName());
        ResponseHelper.put(algorithmObj, "itemDefinition", itemDefinitionObj);
    }

    public String getMediaType() {
        return "application/json";
    }

    @Override
    public Object getObject() {
        return rootObj;
    }
}
