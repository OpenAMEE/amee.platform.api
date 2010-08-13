package com.amee.platform.resource.dataitem.v_3_1;

import com.amee.base.domain.Since;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValue;
import com.amee.domain.path.PathItem;
import com.amee.platform.resource.dataitem.DataItemRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("dataItemJSONRenderer_3_1_0")
@Scope("prototype")
@Since("3.1.0")
public class DataItemJSONRenderer implements DataItemRenderer {

    protected DataItem dataItem;
    protected JSONObject rootObj;
    protected JSONObject dataItemObj;
    protected JSONArray valuesArr;

    public void start() {
        rootObj = new JSONObject();
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    public void newDataItem(DataItem dataItem) {
        this.dataItem = dataItem;
        dataItemObj = new JSONObject();
        if (rootObj != null) {
            put(rootObj, "item", dataItemObj);
        }
    }

    public void addBasic() {
        put(dataItemObj, "uid", dataItem.getUid());
        put(dataItemObj, "type", dataItem.getObjectType().getName());
    }

    public void addName() {
        put(dataItemObj, "name", dataItem.getName());
    }

    public void addPath(PathItem pathItem) {
        put(dataItemObj, "path", dataItem.getPath());
        if (pathItem != null) {
            put(dataItemObj, "fullPath", pathItem.getFullPath() + "/" + dataItem.getDisplayPath());
        }
    }

    public void addParent() {
        put(dataItemObj, "categoryUid", dataItem.getDataCategory().getUid());
        put(dataItemObj, "categoryWikiName", dataItem.getDataCategory().getWikiName());
    }

    public void addAudit() {
        put(dataItemObj, "status", dataItem.getStatus().getName());
        put(dataItemObj, "created", DATE_FORMAT.print(dataItem.getCreated().getTime()));
        put(dataItemObj, "modified", DATE_FORMAT.print(dataItem.getModified().getTime()));
    }

    public void addWikiDoc() {
        put(dataItemObj, "wikiDoc", dataItem.getWikiDoc());
    }

    public void addProvenance() {
        put(dataItemObj, "provenance", dataItem.getProvenance());
    }

    public void addItemDefinition(ItemDefinition itemDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        put(itemDefinitionObj, "uid", itemDefinition.getUid());
        put(itemDefinitionObj, "name", itemDefinition.getName());
        put(dataItemObj, "itemDefinition", itemDefinitionObj);
    }

    public void startValues() {
        valuesArr = new JSONArray();
        put(dataItemObj, "values", valuesArr);
    }

    public void newValue(ItemValue itemValue) {
        JSONObject valueObj = new JSONObject();
        put(valueObj, "path", itemValue.getPath());
        put(valueObj, "value", itemValue.getValue());
        if (itemValue.hasUnit()) {
            put(valueObj, "unit", itemValue.getUnit().toString());
        }
        if (itemValue.hasPerUnit()) {
            put(valueObj, "perUnit", itemValue.getPerUnit().toString());
            put(valueObj, "compoundUnit", itemValue.getCompoundUnit().toString());
        }
        put(valueObj, "history", itemValue.isHistoryAvailable());
        valuesArr.put(valueObj);
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

    public Object getObject() {
        return rootObj;
    }
}
