package com.amee.platform.service.v3.itemvaluedefinition;

import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemValueDefinitionJSONRenderer_3_1 implements ItemValueDefinitionRenderer {

    private ItemValueDefinition itemValueDefinition;
    private JSONObject rootObj;
    private JSONObject itemValueDefinitionObj;

    public ItemValueDefinitionJSONRenderer_3_1() {
        this(true);
    }

    public ItemValueDefinitionJSONRenderer_3_1(boolean start) {
        super();
        if (start) {
            start();
        }
    }

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        put(rootObj, "status", "OK");
    }

    @Override
    public void newItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        this.itemValueDefinition = itemValueDefinition;
        itemValueDefinitionObj = new JSONObject();
        if (rootObj != null) {
            put(rootObj, "itemValueDefinition", itemValueDefinitionObj);
        }
    }

    @Override
    public void addBasic() {
        put(itemValueDefinitionObj, "uid", itemValueDefinition.getUid());
        put(itemValueDefinitionObj, "type", itemValueDefinition.getObjectType().getName());
    }

    @Override
    public void addName() {
        put(itemValueDefinitionObj, "name", itemValueDefinition.getName());
    }

    @Override
    public void addPath() {
        put(itemValueDefinitionObj, "path", itemValueDefinition.getPath());
    }

    @Override
    public void addAudit() {
        put(itemValueDefinitionObj, "status", itemValueDefinition.getStatus().getName());
        put(itemValueDefinitionObj, "created", DATE_FORMAT.print(itemValueDefinition.getCreated().getTime()));
        put(itemValueDefinitionObj, "modified", DATE_FORMAT.print(itemValueDefinition.getModified().getTime()));
    }

    @Override
    public void addWikiDoc() {
        put(itemValueDefinitionObj, "wikiDoc", itemValueDefinition.getWikiDoc());
    }

    @Override
    public void addItemDefinition(ItemDefinition itemDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        put(itemDefinitionObj, "uid", itemDefinition.getUid());
        put(itemDefinitionObj, "name", itemDefinition.getName());
        put(itemValueDefinitionObj, "itemDefinition", itemDefinitionObj);
    }

    @Override
    public void addUsages() {
        JSONArray itemValueUsagesArr = new JSONArray();
        put(itemValueDefinitionObj, "usages", itemValueUsagesArr);
        for (ItemValueUsage itemValueUsage : itemValueDefinition.getItemValueUsages()) {
            JSONObject itemValueUsageObj = new JSONObject();
            put(itemValueUsageObj, "name", itemValueUsage.getName());
            put(itemValueUsageObj, "type", itemValueUsage.getType().toString());
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

    public JSONObject getItemValueDefinitionJSONObject() {
        return itemValueDefinitionObj;
    }

    @Override
    public Object getObject() {
        return rootObj;
    }
}
