package com.amee.domain;

import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.domain.item.data.NuDataItem;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IDataItemService extends IItemService {

    // The UNIX time epoch, which is 1970-01-01 00:00:00. See: http://en.wikipedia.org/wiki/Unix_epoch
    public final static Date EPOCH = new Date(0);

    public List<NuDataItem> getDataItems(DataCategory dataCategory);

    public List<NuDataItem> getDataItems(Set<Long> dataItemIds);

    public NuDataItem getItemByUid(String uid);

    public String getLabel(NuDataItem dataItem);

    public void remove(DataItem dataItem);

    /*
     * TODO: The following methods should live in a Renderer but are being
     * TODO: added here for convenience.
     */
    public JSONObject getJSONObject(NuDataItem dataItem, boolean detailed, boolean showHistory) throws JSONException;

    public JSONObject getJSONObject(NuDataItem dataItem, boolean detailed) throws JSONException;

    public Element getElement(NuDataItem dataItem, Document document, boolean detailed, boolean showHistory);

    public Element getElement(NuDataItem dataItem, Document document, boolean detailed);
}
