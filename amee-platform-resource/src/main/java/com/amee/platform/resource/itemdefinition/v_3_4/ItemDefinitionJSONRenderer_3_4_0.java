package com.amee.platform.resource.itemdefinition.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Scope("prototype")
@Since("3.4.0")
public class ItemDefinitionJSONRenderer_3_4_0 implements ItemDefinitionResource.Renderer {

    private ItemDefinition itemDefinition;
    private JSONObject rootObj;
    private JSONObject itemDefinitionObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
        itemDefinitionObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "itemDefinition", itemDefinitionObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(itemDefinitionObj, "uid", itemDefinition.getUid());
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(itemDefinitionObj, "status", itemDefinition.getStatus().getName());
        ResponseHelper.put(itemDefinitionObj, "created", DATE_FORMAT.print(itemDefinition.getCreated().getTime()));
        ResponseHelper.put(itemDefinitionObj, "modified", DATE_FORMAT.print(itemDefinition.getModified().getTime()));
    }

    @Override
    public void addName() {
        ResponseHelper.put(itemDefinitionObj, "name", itemDefinition.getName());
    }

    @Override
    public void addDrillDown() {
        ResponseHelper.put(itemDefinitionObj, "drillDown", itemDefinition.getDrillDown());
    }

    @Override
    public void addUsages() {
        Set<ItemValueUsage> allItemValueUsages = itemDefinition.getAllItemValueUsages();
        JSONArray itemValueUsagesArr = new JSONArray();
        ResponseHelper.put(itemDefinitionObj, "usages", itemValueUsagesArr);
        for (ItemValueUsage itemValueUsage : itemDefinition.getItemValueUsages()) {
            JSONObject itemValueUsageObj = new JSONObject();
            ResponseHelper.put(itemValueUsageObj, "name", itemValueUsage.getName());
            ResponseHelper.put(itemValueUsageObj, "present", Boolean.toString(allItemValueUsages.contains(itemValueUsage)));
            itemValueUsagesArr.put(itemValueUsageObj);
        }
    }

    @Override
    public void addAlgorithms() {
        JSONArray algorithmsArr = new JSONArray();
        ResponseHelper.put(itemDefinitionObj, "algorithms", algorithmsArr);
        for (Algorithm algorithm : itemDefinition.getAlgorithms()) {
            JSONObject algorithmObj = new JSONObject();
            ResponseHelper.put(algorithmObj, "name", algorithm.getName());
            ResponseHelper.put(algorithmObj, "uid", algorithm.getUid());
            algorithmsArr.put(algorithmObj);
        }
    }

    public String getMediaType() {
        return "application/json";
    }

    @Override
    public Object getObject() {
        return rootObj;
    }
}
