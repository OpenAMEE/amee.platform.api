package com.amee.platform.resource.itemvaluedefinition.v_3_1;

import com.amee.base.domain.Since;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service("itemValueDefinitionJSONRenderer_3_1_0")
@Scope("prototype")
@Since("3.1.0")
public class ItemValueDefinitionJSONRenderer implements ItemValueDefinitionRenderer {

    private ItemValueDefinition itemValueDefinition;
    private JSONObject rootObj;
    private JSONObject itemValueDefinitionObj;

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
    public void addValue() {
        put(itemValueDefinitionObj, "value", itemValueDefinition.getValue());
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
        Collection<ItemValueUsage> itemDefinitionUsages = itemValueDefinition.getItemDefinition().getItemValueUsages();
        JSONArray itemValueUsagesArr = new JSONArray();
        put(itemValueDefinitionObj, "usages", itemValueUsagesArr);
        for (ItemValueUsage itemValueUsage : itemValueDefinition.getItemValueUsages()) {
            JSONObject itemValueUsageObj = new JSONObject();
            put(itemValueUsageObj, "name", itemValueUsage.getName());
            put(itemValueUsageObj, "type", itemValueUsage.getType().toString());
            put(itemValueUsageObj, "active", Boolean.toString(itemDefinitionUsages.contains(itemValueUsage)));
            itemValueUsagesArr.put(itemValueUsageObj);
        }
    }

    @Override
    public void addChoices() {
        put(itemValueDefinitionObj, "choices", itemValueDefinition.getChoices());
    }

    @Override
    public void addUnits() {
        if (itemValueDefinition.hasUnit()) {
            put(itemValueDefinitionObj, "unit", itemValueDefinition.getUnit().toString());
        }
        if (itemValueDefinition.hasPerUnit()) {
            put(itemValueDefinitionObj, "perUnit", itemValueDefinition.getPerUnit().toString());
        }
    }

    @Override
    public void addFlags() {
        put(itemValueDefinitionObj, "drillDown", itemValueDefinition.isDrillDown());
        put(itemValueDefinitionObj, "fromData", itemValueDefinition.isFromData());
        put(itemValueDefinitionObj, "fromProfile", itemValueDefinition.isFromProfile());
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
