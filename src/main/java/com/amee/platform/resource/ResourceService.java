package com.amee.platform.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;

public interface ResourceService {

    public DataCategory getDataCategory(RequestWrapper requestWrapper);

    public DataCategory getDataCategoryWhichHasItemDefinition(RequestWrapper requestWrapper);

    public DataItem getDataItem(RequestWrapper requestWrapper, DataCategory dataCategory);

    public BaseDataItemValue getDataItemValue(RequestWrapper requestWrapper, DataItem dataItem, ItemValueDefinition itemValueDefinition);

    public ItemDefinition getItemDefinition(RequestWrapper requestWrapper);

    public ItemValueDefinition getItemValueDefinition(RequestWrapper requestWrapper, ItemDefinition itemDefinition);

    public ItemValueDefinition getItemValueDefinition(RequestWrapper requestWrapper, DataItem dataItem);

    public ReturnValueDefinition getReturnValueDefinition(RequestWrapper requestWrapper, ItemDefinition itemDefinition);

    public Algorithm getAlgorithm(RequestWrapper requestWrapper, ItemDefinition itemDefinition);
}
