package com.amee.platform.resource.datacategory.v_3_0;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.tag.Tag;
import com.amee.platform.resource.datacategory.DataCategoryResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.0.0")
public class DataCategoryJSONRenderer_3_0_0 implements DataCategoryResource.Renderer {

    private DataCategory dataCategory;
    private JSONObject rootObj;
    private JSONObject dataCategoryObj;
    private JSONArray tagsArr;

    public void start() {
        rootObj = new JSONObject();
    }

    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    public void newDataCategory(DataCategory dataCategory) {
        this.dataCategory = dataCategory;
        dataCategoryObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "category", dataCategoryObj);
        }
    }

    public void addBasic() {
        ResponseHelper.put(dataCategoryObj, "uid", dataCategory.getUid());
        ResponseHelper.put(dataCategoryObj, "name", dataCategory.getName());
        ResponseHelper.put(dataCategoryObj, "wikiName", dataCategory.getWikiName());
    }

    public void addPath() {
        ResponseHelper.put(dataCategoryObj, "path", dataCategory.getPath());
        ResponseHelper.put(dataCategoryObj, "fullPath", dataCategory.getFullPath());
    }

    public void addParent() {
        if (dataCategory.getDataCategory() != null) {
            ResponseHelper.put(dataCategoryObj, "parentUid", dataCategory.getDataCategory().getUid());
            ResponseHelper.put(dataCategoryObj, "parentWikiName", dataCategory.getDataCategory().getWikiName());
        }
    }

    public void addAudit() {
        ResponseHelper.put(dataCategoryObj, "status", dataCategory.getStatus().getName());
        ResponseHelper.put(dataCategoryObj, "created", DATE_FORMAT.print(dataCategory.getCreated().getTime()));
        ResponseHelper.put(dataCategoryObj, "modified", DATE_FORMAT.print(dataCategory.getModified().getTime()));
    }

    public void addAuthority() {
        ResponseHelper.put(dataCategoryObj, "authority", dataCategory.getAuthority());
    }

    public void addHistory() {
        ResponseHelper.put(dataCategoryObj, "history", dataCategory.getHistory());
    }

    public void addWikiDoc() {
        ResponseHelper.put(dataCategoryObj, "wikiDoc", dataCategory.getWikiDoc());
    }

    public void addProvenance() {
        ResponseHelper.put(dataCategoryObj, "provenance", dataCategory.getProvenance());
    }

    public void addItemDefinition(ItemDefinition itemDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", itemDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", itemDefinition.getName());
        ResponseHelper.put(dataCategoryObj, "itemDefinition", itemDefinitionObj);
    }

    public void startTags() {
        tagsArr = new JSONArray();
        ResponseHelper.put(dataCategoryObj, "tags", tagsArr);
    }

    public void newTag(Tag tag) {
        JSONObject tagObj = new JSONObject();
        ResponseHelper.put(tagObj, "tag", tag.getTag());
        tagsArr.put(tagObj);
    }

    public String getMediaType() {
        return "application/json";
    }

    public JSONObject getObject() {
        return rootObj;
    }
}
