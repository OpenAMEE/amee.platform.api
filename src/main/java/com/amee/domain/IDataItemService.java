package com.amee.domain;

import com.amee.domain.data.DataCategory;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.NuDataItem;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IDataItemService extends IItemService {

    // The UNIX time epoch, which is 1970-01-01 00:00:00. See: http://en.wikipedia.org/wiki/Unix_epoch
    public final static Date EPOCH = new Date(0);

    public List<NuDataItem> getDataItems(IDataCategoryReference dataCategory);

    public List<NuDataItem> getDataItems(IDataCategoryReference dataCategory, boolean checkDataItems);

    public List<NuDataItem> getDataItems(Set<Long> dataItemIds);

    public NuDataItem getDataItemByIdentifier(DataCategory parent, String path);

    public Map<String, NuDataItem> getDataItemMap(Set<Long> dataItemIds, boolean loadValues);

    public NuDataItem getDataItemByUid(DataCategory parent, String uid);

    public NuDataItem getItemByUid(String uid);

    public NuDataItem getDataItemByPath(DataCategory parent, String path);

    public String getLabel(NuDataItem dataItem);

    public void checkDataItem(NuDataItem dataItem);

    public void persist(NuDataItem dataItem);

    public void persist(NuDataItem dataItem, boolean checkDataItem);

    public void remove(NuDataItem dataItem);

    public void persist(BaseItemValue itemValue);

    public void remove(BaseItemValue itemValue);
}
