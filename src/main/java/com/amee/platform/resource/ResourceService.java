package com.amee.platform.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;

/**
 * Created by IntelliJ IDEA.
 * User: dig
 * Date: 28/02/2011
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
public interface ResourceService {
    DataCategory getDataCategory(RequestWrapper requestWrapper);

    DataCategory getDataCategoryWhichHasItemDefinition(RequestWrapper requestWrapper);

    DataItem getDataItem(RequestWrapper requestWrapper, DataCategory dataCategory);

    BaseDataItemValue getDataItemValue(RequestWrapper requestWrapper, DataItem dataItem);
}
