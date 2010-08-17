package com.amee.platform.resource.datacategory;

import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.path.PathItem;
import com.amee.domain.tag.Tag;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class DataCategoryJSONRenderer implements DataCategoryRenderer {

    private DataCategory dataCategory;
    private JSONObject rootObj;
    private JSONObject dataCategoryObj;
    private JSONArray tagsArr;

    public void start() {
        rootObj = new JSONObject();
    }

    public void ok() {
        put(rootObj, "status", "OK");
    }

    public void newDataCategory(DataCategory dataCategory) {
        this.dataCategory = dataCategory;
        dataCategoryObj = new JSONObject();
        if (rootObj != null) {
            put(rootObj, "category", dataCategoryObj);
        }
    }

    public void addBasic() {
        put(dataCategoryObj, "uid", dataCategory.getUid());
        put(dataCategoryObj, "name", dataCategory.getName());
        put(dataCategoryObj, "wikiName", dataCategory.getWikiName());
    }

    public void addPath(PathItem pathItem) {
        put(dataCategoryObj, "path", dataCategory.getPath());
        if (pathItem != null) {
            put(dataCategoryObj, "fullPath", pathItem.getFullPath() + "/" + dataCategory.getDisplayPath());
        }
    }

    public void addParent() {
        if (dataCategory.getDataCategory() != null) {
            put(dataCategoryObj, "parentUid", dataCategory.getDataCategory().getUid());
            put(dataCategoryObj, "parentWikiName", dataCategory.getDataCategory().getWikiName());
        }
    }

    public void addAudit() {
        put(dataCategoryObj, "status", dataCategory.getStatus().getName());
        put(dataCategoryObj, "created", DATE_FORMAT.print(dataCategory.getCreated().getTime()));
        put(dataCategoryObj, "modified", DATE_FORMAT.print(dataCategory.getModified().getTime()));
    }

    public void addAuthority() {
        put(dataCategoryObj, "authority", dataCategory.getAuthority());
    }

    public void addWikiDoc() {
        put(dataCategoryObj, "wikiDoc", dataCategory.getWikiDoc());
    }

    public void addProvenance() {
        put(dataCategoryObj, "provenance", dataCategory.getProvenance());
    }

    public void addItemDefinition(ItemDefinition itemDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        put(itemDefinitionObj, "uid", itemDefinition.getUid());
        put(itemDefinitionObj, "name", itemDefinition.getName());
        put(dataCategoryObj, "itemDefinition", itemDefinitionObj);
    }

    public void startTags() {
        tagsArr = new JSONArray();
        put(dataCategoryObj, "tags", tagsArr);
    }

    public void newTag(Tag tag) {
        JSONObject tagObj = new JSONObject();
        put(tagObj, "tag", tag.getTag());
        tagsArr.put(tagObj);
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
