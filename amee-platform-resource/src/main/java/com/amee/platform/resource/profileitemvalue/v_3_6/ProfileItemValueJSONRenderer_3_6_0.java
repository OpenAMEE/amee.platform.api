package com.amee.platform.resource.profileitemvalue.v_3_6;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.ProfileItemService;
import com.amee.domain.ValueType;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.profile.BaseProfileItemValue;
import com.amee.platform.resource.profileitemvalue.ProfileItemValueResource;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.6.0")
public class ProfileItemValueJSONRenderer_3_6_0 implements ProfileItemValueResource.Renderer {

    @Autowired
    protected ProfileItemService profileItemService;
    
    protected BaseProfileItemValue profileItemValue;
    protected JSONObject rootObj;
    protected JSONObject profileItemValueObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }
    
    @Override
    public void newProfileItemValue(BaseProfileItemValue profileItemValue) {
        this.profileItemValue = profileItemValue;
        profileItemValueObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "value", profileItemValueObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(profileItemValueObj, "uid", profileItemValue.getUid());
        if (NumberValue.class.isAssignableFrom(profileItemValue.getClass())) {
            NumberValue nv = (NumberValue) profileItemValue;
            ResponseHelper.put(profileItemValueObj, "value", nv.getValueAsDouble());
            if (nv.hasUnit()) {
                ResponseHelper.put(profileItemValueObj, "unit", nv.getCompoundUnit().toString());
            }
        } else {
            ResponseHelper.put(profileItemValueObj, "value", profileItemValue.getValueAsString());
        }
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(profileItemValueObj, "status", profileItemValue.getStatus().getName());
        ResponseHelper.put(profileItemValueObj, "created", DATE_FORMAT.print(profileItemValue.getCreated().getTime()));
        ResponseHelper.put(profileItemValueObj, "modified", DATE_FORMAT.print(profileItemValue.getModified().getTime()));
    }

    @Override
    public void addItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", itemValueDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", itemValueDefinition.getName());
        ResponseHelper.put(itemDefinitionObj, "path", itemValueDefinition.getPath());
        ResponseHelper.put(profileItemValueObj, "itemValueDefinition", itemDefinitionObj);
    }

    @Override
    public void addProfileItem() {
        JSONObject itemObj = new JSONObject();
        ResponseHelper.put(itemObj, "uid", profileItemValue.getProfileItem().getUid());
        ResponseHelper.put(profileItemValueObj, "item", itemObj);
    }

    @Override
    public void addDataCategory() {
        JSONObject categoryObj = new JSONObject();
        ResponseHelper.put(categoryObj, "uid", profileItemValue.getProfileItem().getDataCategory().getUid());
        ResponseHelper.put(categoryObj, "wikiName", profileItemValue.getProfileItem().getDataCategory().getWikiName());
        ResponseHelper.put(profileItemValueObj, "category", categoryObj);
    }

    @Override
    public String getMediaType() {
        return "application/json";
    }

    @Override
    public Object getObject() {
        return rootObj;
    }
}
