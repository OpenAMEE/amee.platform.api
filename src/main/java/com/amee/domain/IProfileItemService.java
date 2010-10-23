package com.amee.domain;

import com.amee.domain.item.profile.NuProfileItem;

public interface IProfileItemService extends IItemService {

    @Override
    public NuProfileItem getItemByUid(String uid);

    public boolean hasNonZeroPerTimeValues(NuProfileItem profileItem);

    public boolean isSingleFlight(NuProfileItem profileItem);

    public void persist(NuProfileItem profileItem);

    // Representations.

//    public JSONObject getJSONObject(NuProfileItem dataItem, boolean detailed) throws JSONException;
//
//    public JSONObject getJSONObject(BaseProfileItemValue itemValue, boolean detailed) throws JSONException;
}
