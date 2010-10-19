package com.amee.domain;

import com.amee.domain.item.profile.NuProfileItem;
import com.amee.platform.science.ReturnValues;

public interface IProfileItemService extends IItemService {

    public NuProfileItem getItemByUid(String uid);

    public ReturnValues getAmounts(NuProfileItem profileItem, boolean recalculate);

    @Deprecated
    public double getAmount(NuProfileItem profileItem);

    public boolean hasNonZeroPerTimeValues(NuProfileItem profileItem);

    public boolean isSingleFlight(NuProfileItem profileItem);
}
