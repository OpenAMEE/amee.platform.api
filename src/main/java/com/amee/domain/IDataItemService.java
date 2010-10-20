package com.amee.domain;

import com.amee.domain.item.data.NuDataItem;
import org.json.JSONException;
import org.json.JSONObject;

public interface IDataItemService extends IItemService {

    public NuDataItem getItemByUid(String uid);

    public String getLabel(NuDataItem dataItem);

    /*
     * TODO: The following methods should live in a Renderer but are being
     * added here for convenience
     */
    public JSONObject getJSONObject(NuDataItem dataItem, boolean detailed, boolean showHistory) throws JSONException;
}
