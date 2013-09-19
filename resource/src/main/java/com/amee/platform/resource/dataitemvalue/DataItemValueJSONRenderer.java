package com.amee.platform.resource.dataitemvalue;

import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ResponseHelper;
import com.amee.domain.DataItemService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.HistoryValue;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.data.BaseDataItemValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract Superclass for JSON renderers of DataItemValues and DataItemValueHistories which share almost all code.
 * 
 * @see
 * {@link com.amee.platform.resource.dataitemvalue.v_3_4.DataItemValueJSONRenderer_3_4_0 DataItemValueJSONRenderer_3_4_0},
 * {@link com.amee.platform.resource.dataitemvalue.v_3_6.DataItemValueHistoryJSONRenderer_3_6_0 DataItemValueHistoryJSONRenderer_3_6_0}
 */
public abstract class DataItemValueJSONRenderer implements ResourceRenderer {

    private static final Logger LOG = LoggerFactory.getLogger(DataItemValueJSONRenderer.class);
    
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
    public String getMediaType() {
        return "application/json";
    }

    @Override
    public Object getObject() {
        return rootObj;
    }

    public void newDataItemValue(BaseDataItemValue dataItemValue) {
        this.dataItemValue = dataItemValue;
        dataItemValueObj = new JSONObject();
        if (rootObj != null) {
            ResponseHelper.put(rootObj, "value", dataItemValueObj);
        }
    }

    public void addBasic() {
        ResponseHelper.put(dataItemValueObj, "uid", dataItemValue.getUid());
        if (!isHistoryItem()) {
            ResponseHelper.put(dataItemValueObj, "history", dataItemValue.isHistoryAvailable());
        }

        String valueString = dataItemValue.getValueAsString();
        if (NumberValue.class.isAssignableFrom(dataItemValue.getClass())) {

            // If the value is a number, format the JSON appropriately, otherwise use a String
            try {
                Double doubleValue = Double.valueOf(valueString);
                if (doubleValue == null) {
                    // This shouldn't be possible
                    ResponseHelper.put(dataItemValueObj, "value", JSONObject.NULL);
                } else if (Double.isInfinite(doubleValue)) {
                    ResponseHelper.put(dataItemValueObj, "value", "Infinity");
                } else if (Double.isNaN(doubleValue)) {
                    ResponseHelper.put(dataItemValueObj, "value", "NaN");
                } else {
                    ResponseHelper.put(dataItemValueObj, "value", doubleValue);
                }
            } catch (NumberFormatException e) {
                // Cannot handle this numeric value, use the String representation instead
                LOG.warn("Cannot parse "+valueString+" as a double, defaulting to String representation", e);
                ResponseHelper.put(dataItemValueObj, "value", valueString);
            }

            NumberValue nv = (NumberValue) dataItemValue;
            if (nv.hasUnit()) {
                ResponseHelper.put(dataItemValueObj, "unit", nv.getCompoundUnit().toString());
            }
            
        } else if ("true".equalsIgnoreCase(valueString) || "false".equalsIgnoreCase(valueString)) {
            // Value represents a boolean
            ResponseHelper.put(dataItemValueObj, "value", Boolean.valueOf(dataItemValue.getValueAsString()));
            
        } else {
            // Not a numeric/boolean value, use a String
            ResponseHelper.put(dataItemValueObj, "value", valueString);
        }

        if (HistoryValue.class.isAssignableFrom(dataItemValue.getClass())) {
            HistoryValue hv = (HistoryValue) dataItemValue;
            ResponseHelper.put(dataItemValueObj, "startDate", DATE_FORMAT.print(hv.getStartDate().getTime()));
        } else {
            ResponseHelper.put(dataItemValueObj, "startDate", DATE_FORMAT.print(DataItemService.EPOCH.getTime()));
        }
    }
    
    /**
     * Indicates whether or not this DataItemValue forms part of a request for a history or not
     * @return  <code>true</code> if this item is part of a history, <code>false</code> otherwise
     */
    public abstract boolean isHistoryItem();

    public void addPath() {
        ResponseHelper.put(dataItemValueObj, "path", dataItemValue.getPath());
        ResponseHelper.put(dataItemValueObj, "fullPath", dataItemValue.getFullPath());
    }

    public void addDataCategory() {
        JSONObject categoryObj = new JSONObject();
        ResponseHelper.put(categoryObj, "uid", dataItemValue.getDataItem().getDataCategory().getUid());
        ResponseHelper.put(categoryObj, "wikiName", dataItemValue.getDataItem().getDataCategory().getWikiName());
        ResponseHelper.put(dataItemValueObj, "category", categoryObj);
    }

    public void addDataItem() {
        JSONObject itemObj = new JSONObject();
        ResponseHelper.put(itemObj, "uid", dataItemValue.getDataItem().getUid());
        ResponseHelper.put(dataItemValueObj, "item", itemObj);
    }

    public void addAudit() {
        ResponseHelper.put(dataItemValueObj, "status", dataItemValue.getStatus().getName());
        ResponseHelper.put(dataItemValueObj, "created", DATE_FORMAT.print(dataItemValue.getCreated().getTime()));
        ResponseHelper.put(dataItemValueObj, "modified", DATE_FORMAT.print(dataItemValue.getModified().getTime()));
    }

    public void addItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        JSONObject itemDefinitionObj = new JSONObject();
        ResponseHelper.put(itemDefinitionObj, "uid", itemValueDefinition.getUid());
        ResponseHelper.put(itemDefinitionObj, "name", itemValueDefinition.getName());
        ResponseHelper.put(itemDefinitionObj, "path", itemValueDefinition.getPath());
        ResponseHelper.put(dataItemValueObj, "itemValueDefinition", itemDefinitionObj);
    }
}
