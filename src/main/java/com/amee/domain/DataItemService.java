package com.amee.domain;

import com.amee.base.domain.ResultsWrapper;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueMap;
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
    final static Date EPOCH = new Date(0);

    // Almost (less 7 seconds) the last unix time, which is 2038-01-19 03:14:07. See: http://en.wikipedia.org/wiki/Year_2038_problem
    // This time is seven seconds less than the last unix because StartEndDate is not sensitive to seconds.
    final static Date Y2038 = new DateTime(2038, 1, 19, 3, 14, 0, 0).toDate();

    @Override
    DataItem getItemByUid(String uid);

    long getDataItemCount(IDataCategoryReference dataCategory);

    List<DataItem> getDataItems(IDataCategoryReference dataCategory);

    List<DataItem> getDataItems(IDataCategoryReference dataCategory, boolean checkDataItems);

    List<DataItem> getDataItems(Set<Long> dataItemIds);

    DataItem getDataItemByIdentifier(DataCategory parent, String path);

    Map<String, DataItem> getDataItemMap(Set<Long> dataItemIds, boolean loadValues);

    ItemValueMap getDrillDownValuesMap(DataItem dataItem);

    boolean equivalentDataItemExists(DataItem dataItem);

    DataItem getDataItemByUid(DataCategory parent, String uid);

    DataItem getDataItemByPath(DataCategory parent, String path);

    String getLabel(DataItem dataItem);

    Choices getUserValueChoices(DataItem dataItem, APIVersion apiVersion);

    void checkDataItem(DataItem dataItem);

    Date getDataItemsModified(DataCategory dataCategory);

    boolean isDataItemUniqueByPath(DataItem dataItem);

    boolean isDataItemValueUniqueByStartDate(BaseDataItemValue itemValue);

    ResultsWrapper<BaseDataItemValue> getAllItemValues(DataItemValuesFilter filter);

    void persist(DataItem dataItem);

    void persist(DataItem dataItem, boolean checkDataItem);

    void remove(DataItem dataItem);

    void persist(BaseItemValue itemValue);

    void remove(BaseItemValue itemValue);

    StartEndDate getStartDate(DataItem dataItem);

    StartEndDate getEndDate(DataItem dataItem);

    void updateDataItemValues(DataItem dataitem);
}
