package com.amee.platform.resource.itemvaluedefinition.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.APIVersion;
import com.amee.domain.ValueDefinition;
import com.amee.domain.ValueType;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueUsage;
import com.amee.platform.resource.itemvaluedefinition.ItemValueDefinitionResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Scope("prototype")
@Since("3.4.0")
public class ItemValueDefinitionJSONRenderer_3_4_0 implements ItemValueDefinitionResource.Renderer {

    protected ItemValueDefinition itemValueDefinition;
    protected JSONObject rootObj;
    protected JSONObject itemValueDefinitionObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        this.itemValueDefinition = itemValueDefinition;
        itemValueDefinitionObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "itemValueDefinition", itemValueDefinitionObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(itemValueDefinitionObj, "uid", itemValueDefinition.getUid());
    }

    @Override
    public void addName() {
        ResponseHelper.put(itemValueDefinitionObj, "name", itemValueDefinition.getName());
    }

    @Override
    public void addPath() {
        ResponseHelper.put(itemValueDefinitionObj, "path", itemValueDefinition.getPath());
    }

    @Override
    public void addValue() {
        ResponseHelper.put(itemValueDefinitionObj, "value", itemValueDefinition.getValue());
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(itemValueDefinitionObj, "status", itemValueDefinition.getStatus().getName());
        ResponseHelper.put(itemValueDefinitionObj, "created", DATE_FORMAT.print(itemValueDefinition.getCreated().getTime()));
        ResponseHelper.put(itemValueDefinitionObj, "modified", DATE_FORMAT.print(itemValueDefinition.getModified().getTime()));
    }

    @Override
    public void addWikiDoc() {
        ResponseHelper.put(itemValueDefinitionObj, "wikiDoc", itemValueDefinition.getWikiDoc());
    }

    @Override
    public void addItemDefinition(ItemDefinition itemDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", itemDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", itemDefinition.getName());
        ResponseHelper.put(itemValueDefinitionObj, "itemDefinition", itemDefinitionObj);
    }

    @Override
    public void addValueDefinition(ValueDefinition valueDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", valueDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", valueDefinition.getName());
        ResponseHelper.put(itemDefinitionObj, "valueType",
                valueDefinition.getValueType().equals(ValueType.DOUBLE) ? "DOUBLE" : valueDefinition.getValueType().getName());
        ResponseHelper.put(itemValueDefinitionObj, "valueDefinition", itemDefinitionObj);
    }

    @Override
    public void addUsages() {
        Collection<ItemValueUsage> itemDefinitionUsages = itemValueDefinition.getItemDefinition().getItemValueUsages();
        JSONArray itemValueUsagesArr = new JSONArray();
        ResponseHelper.put(itemValueDefinitionObj, "usages", itemValueUsagesArr);
        for (ItemValueUsage itemValueUsage : itemValueDefinition.getItemValueUsages()) {
            JSONObject itemValueUsageObj = new JSONObject();
            ResponseHelper.put(itemValueUsageObj, "name", itemValueUsage.getName());
            ResponseHelper.put(itemValueUsageObj, "type", itemValueUsage.getType().toString());
            ResponseHelper.put(itemValueUsageObj, "active", Boolean.toString(itemDefinitionUsages.contains(itemValueUsage)));
            itemValueUsagesArr.put(itemValueUsageObj);
        }
    }

    @Override
    public void addChoices() {
        ResponseHelper.put(itemValueDefinitionObj, "choices", itemValueDefinition.getChoices());
    }

    @Override
    public void addUnits() {
        if (itemValueDefinition.hasUnit()) {
            ResponseHelper.put(itemValueDefinitionObj, "unit", itemValueDefinition.getUnitAsAmountUnit().toString());
        }
        if (itemValueDefinition.hasPerUnit()) {
            ResponseHelper.put(itemValueDefinitionObj, "perUnit", itemValueDefinition.getPerUnitAsAmountPerUnit().toString());
        }
    }

    @Override
    public void addFlags() {
        ResponseHelper.put(itemValueDefinitionObj, "drillDown", itemValueDefinition.isDrillDown());
        ResponseHelper.put(itemValueDefinitionObj, "fromData", itemValueDefinition.isFromData());
        ResponseHelper.put(itemValueDefinitionObj, "fromProfile", itemValueDefinition.isFromProfile());
    }

    @Override
    public void addVersions() {
        JSONArray versionsArr = new JSONArray();
        ResponseHelper.put(itemValueDefinitionObj, "versions", versionsArr);
        for (APIVersion apiVersion : itemValueDefinition.getAPIVersions()) {
            JSONObject versionObj = new JSONObject();
            ResponseHelper.put(versionObj, "version", apiVersion.getVersion());
            versionsArr.put(versionObj);
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
