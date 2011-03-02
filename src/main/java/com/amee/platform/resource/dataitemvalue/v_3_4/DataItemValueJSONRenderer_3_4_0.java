package com.amee.platform.resource.dataitemvalue.v_3_4;

import com.amee.base.domain.Since;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.IDataItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.HistoryValue;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.platform.resource.dataitemvalue.DataItemValueResource;
import com.amee.service.item.DataItemService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
@Since("3.4.0")
public class DataItemValueJSONRenderer_3_4_0 implements DataItemValueResource.Renderer {

    @Autowired
    protected DataItemService dataItemService;

    protected BaseDataItemValue dataItemValue;
    protected JSONObject rootObj;
    protected JSONObject dataItemValueObj;

    @Override
    public void start() {
        rootObj = new JSONObject();
    }

    @Override
    public void ok() {
        ResponseHelper.put(rootObj, "status", "OK");
    }

    @Override
    public void newDataItemValue(BaseDataItemValue dataItemValue) {
        this.dataItemValue = dataItemValue;
        dataItemValueObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "value", dataItemValueObj);
        }
    }

    @Override
    public void addBasic() {
        ResponseHelper.put(dataItemValueObj, "uid", dataItemValue.getUid());
        ResponseHelper.put(dataItemValueObj, "history", dataItemValue.isHistoryAvailable());
        ResponseHelper.put(dataItemValueObj, "value", dataItemValue.getValueAsString());
        if (NumberValue.class.isAssignableFrom(dataItemValue.getClass())) {
            NumberValue nv = (NumberValue) dataItemValue;
            if (nv.hasUnit()) {
                ResponseHelper.put(dataItemValueObj, "unit", nv.getUnit().toString());
                if (nv.hasPerUnit()) {
                    ResponseHelper.put(dataItemValueObj, "perUnit", nv.getPerUnit().toString());
                    ResponseHelper.put(dataItemValueObj, "compoundUnit", nv.getCompoundUnit().toString());
                }
            }
        }
        if (HistoryValue.class.isAssignableFrom(dataItemValue.getClass())) {
            HistoryValue hv = (HistoryValue) dataItemValue;
            ResponseHelper.put(dataItemValueObj, "startDate", DATE_FORMAT.print(hv.getStartDate().getTime()));
        } else {
            ResponseHelper.put(dataItemValueObj, "startDate", DATE_FORMAT.print(IDataItemService.EPOCH.getTime()));
        }
    }

    @Override
    public void addPath() {
        ResponseHelper.put(dataItemValueObj, "path", dataItemValue.getPath());
        ResponseHelper.put(dataItemValueObj, "fullPath", dataItemValue.getFullPath());
    }

    @Override
    public void addDataCategory() {
        JSONObject categoryObj = new JSONObject();
        ResponseHelper.put(categoryObj, "uid", dataItemValue.getDataItem().getDataCategory().getUid());
        ResponseHelper.put(categoryObj, "wikiName", dataItemValue.getDataItem().getDataCategory().getWikiName());
        ResponseHelper.put(dataItemValueObj, "category", categoryObj);
    }

    @Override
    public void addDataItem() {
        JSONObject itemObj = new JSONObject();
        ResponseHelper.put(itemObj, "uid", dataItemValue.getDataItem().getUid());
        ResponseHelper.put(dataItemValueObj, "item", itemObj);
    }

    @Override
    public void addAudit() {
        ResponseHelper.put(dataItemValueObj, "status", dataItemValue.getStatus().getName());
        ResponseHelper.put(dataItemValueObj, "created", DATE_FORMAT.print(dataItemValue.getCreated().getTime()));
        ResponseHelper.put(dataItemValueObj, "modified", DATE_FORMAT.print(dataItemValue.getModified().getTime()));
    }

    @Override
    public void addItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", itemValueDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", itemValueDefinition.getName());
        ResponseHelper.put(itemDefinitionObj, "path", itemValueDefinition.getPath());
        ResponseHelper.put(dataItemValueObj, "itemValueDefinition", itemDefinitionObj);
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
