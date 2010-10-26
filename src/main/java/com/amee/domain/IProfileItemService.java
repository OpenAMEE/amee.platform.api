package com.amee.domain;

import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.profile.NuProfileItem;

public interface IProfileItemService extends IItemService {

    @Override
    public NuProfileItem getItemByUid(String uid);

    public boolean hasNonZeroPerTimeValues(NuProfileItem profileItem);

    public boolean isSingleFlight(NuProfileItem profileItem);

    public void persist(NuProfileItem profileItem);

    public void persist(BaseItemValue itemValue);
}
