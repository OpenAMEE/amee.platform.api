package com.amee.domain;

import com.amee.base.domain.ResultsWrapper;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.sheet.Choices;
import com.amee.platform.science.StartEndDate;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DataItemService extends ItemService {

    // The UNIX time epoch, which is 1970-01-01 00:00:00. See: http://en.wikipedia.org/wiki/Unix_epoch
    public final static Date EPOCH = new Date(0);

    // Almost (less 7 seconds) the last unix time, which is 2038-01-19 03:14:07. See: http://en.wikipedia.org/wiki/Year_2038_problem
    // This time is seven seconds less than the last unix because StartEndDate is not sensitive to seconds.
    public final static Date Y2038 = new DateTime(2038, 1, 19, 3, 14, 0, 0).toDate();

    public long getDataItemCount(IDataCategoryReference dataCategory);

    public List<DataItem> getDataItems(IDataCategoryReference dataCategory);

    public List<DataItem> getDataItems(IDataCategoryReference dataCategory, boolean checkDataItems);

    public List<DataItem> getDataItems(Set<Long> dataItemIds);

    public DataItem getDataItemByIdentifier(DataCategory parent, String path);

    public Map<String, DataItem> getDataItemMap(Set<Long> dataItemIds, boolean loadValues);

    public DataItem getDataItemByUid(DataCategory parent, String uid);

    public DataItem getItemByUid(String uid);

    public DataItem getDataItemByPath(DataCategory parent, String path);

    public String getLabel(DataItem dataItem);

    public Choices getUserValueChoices(DataItem dataItem, APIVersion apiVersion);

    public void checkDataItem(DataItem dataItem);

    public Date getDataItemsModified(DataCategory dataCategory);

    public boolean isDataItemUniqueByPath(DataItem dataItem);

    public boolean isDataItemValueUniqueByStartDate(BaseDataItemValue itemValue);

    public ResultsWrapper<BaseDataItemValue> getAllItemValues(DataItemValuesFilter filter);

    public void persist(DataItem dataItem);

    public void persist(DataItem dataItem, boolean checkDataItem);

    public void remove(DataItem dataItem);

    public void persist(BaseItemValue itemValue);

    public void remove(BaseItemValue itemValue);

    public StartEndDate getStartDate(DataItem dataItem);

    public StartEndDate getEndDate(DataItem dataItem);

    public void updateDataItemValues(DataItem dataitem);
}
