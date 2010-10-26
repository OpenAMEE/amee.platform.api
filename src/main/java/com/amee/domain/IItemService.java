package com.amee.domain;

import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.NuItemValueMap;
import com.amee.domain.item.BaseItem;
import com.amee.domain.item.BaseItemValue;
import com.amee.platform.science.StartEndDate;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IItemService {

    public BaseItem getItemByUid(String uid);

    public Set<ItemValueDefinition> getItemValueDefinitionsInUse(BaseItem item);

    public List<BaseItemValue> getItemValues(BaseItem item);

    public List<BaseItemValue> getAllItemValues(BaseItem item, String itemValuePath);

    public Set<BaseItemValue> getActiveItemValues(BaseItem item);

    public Set<BaseItemValue> getAllItemValues(BaseItem item);

    public BaseItemValue getItemValue(BaseItem item, String identifier, Date startDate);

    public BaseItemValue getItemValue(BaseItem item, String identifier);

    public NuItemValueMap getItemValuesMap(BaseItem item);

    public boolean isUnique(BaseItem item, ItemValueDefinition itemValueDefinition, StartEndDate startDate);

    public void loadItemValuesForItems(Collection<BaseItem> items);
}
