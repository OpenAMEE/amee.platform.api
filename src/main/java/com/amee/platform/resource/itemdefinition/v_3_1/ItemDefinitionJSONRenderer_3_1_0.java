package com.amee.platform.resource.itemdefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemdefinition.ItemDefinitionResource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Scope("prototype")
@Since("3.1.0")
public class ItemDefinitionJSONRenderer_3_1_0 implements ItemDefinitionResource.Renderer {

    private ItemDefinition itemDefinition;
    private JSONObject rootObj;
    private JSONObject itemDefinitionObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        put(rootObj, "status", "OK");
    }

    @Override
    public void newItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
        itemDefinitionObj = new JSONObject();
        if (rootObj != null) {
            put(rootObj, "itemDefinition", itemDefinitionObj);
        }
    }

    @Override
    public void addBasic() {
        put(itemDefinitionObj, "uid", itemDefinition.getUid());
    }

    @Override
    public void addAudit() {
        put(itemDefinitionObj, "status", itemDefinition.getStatus().getName());
        put(itemDefinitionObj, "created", DATE_FORMAT.print(itemDefinition.getCreated().getTime()));
        put(itemDefinitionObj, "modified", DATE_FORMAT.print(itemDefinition.getModified().getTime()));
    }

    @Override
    public void addName() {
        put(itemDefinitionObj, "name", itemDefinition.getName());
    }

    @Override
    public void addDrillDown() {
        put(itemDefinitionObj, "drillDown", itemDefinition.getDrillDown());
    }

    @Override
    public void addUsages() {
        Set<ItemValueUsage> allItemValueUsages = itemDefinition.getAllItemValueUsages();
        JSONArray itemValueUsagesArr = new JSONArray();
        put(itemDefinitionObj, "usages", itemValueUsagesArr);
        for (ItemValueUsage itemValueUsage : itemDefinition.getItemValueUsages()) {
            JSONObject itemValueUsageObj = new JSONObject();
            put(itemValueUsageObj, "name", itemValueUsage.getName());
            put(itemValueUsageObj, "present", Boolean.toString(allItemValueUsages.contains(itemValueUsage)));
            itemValueUsagesArr.put(itemValueUsageObj);
        }
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

    @Override
    public Object getObject() {
        return rootObj;
    }
}
