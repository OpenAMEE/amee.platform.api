package com.amee.domain;

import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ItemValueMap;
import com.amee.domain.item.BaseItem;
import com.amee.domain.item.BaseItemValue;
import com.amee.platform.science.StartEndDate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ItemService {

    BaseItem getItemByUid(String uid);

    Set<ItemValueDefinition> getItemValueDefinitionsInUse(BaseItem item);

    List<BaseItemValue> getItemValues(BaseItem item);

    List<BaseItemValue> getAllItemValues(BaseItem item, String itemValuePath);

    Set<BaseItemValue> getActiveItemValues(BaseItem item);

    Set<BaseItemValue> getAllItemValues(BaseItem item);

    BaseItemValue getItemValue(BaseItem item, String identifier, Date startDate);

    BaseItemValue getByUid(BaseItem item, final String uid);

    BaseItemValue getItemValue(BaseItem item, String identifier);

    ItemValueMap getItemValuesMap(BaseItem item);

    boolean isItemValueUnique(BaseItem item, ItemValueDefinition itemValueDefinition, StartEndDate startDate);

    void loadItemValuesForItems(Collection<BaseItem> items);

    void addItemValue(BaseItemValue itemValue);

    void clearItemValues();

    StartEndDate getStartDate(BaseItemValue itemValue);
}
