package com.amee.domain;

import com.amee.domain.item.profile.BaseProfileItemValue;
import com.amee.domain.item.profile.NuProfileItem;
import com.amee.platform.science.ReturnValues;
import org.json.JSONException;
import org.json.JSONObject;

public interface IProfileItemService extends IItemService {

    public NuProfileItem getItemByUid(String uid);

    public ReturnValues getAmounts(NuProfileItem profileItem, boolean recalculate);

    @Deprecated
    public double getAmount(NuProfileItem profileItem);

    public boolean hasNonZeroPerTimeValues(NuProfileItem profileItem);

    public boolean isSingleFlight(NuProfileItem profileItem);

    // Representations.

    public JSONObject getJSONObject(NuProfileItem dataItem, boolean detailed) throws JSONException;

    public JSONObject getJSONObject(BaseProfileItemValue itemValue, boolean detailed) throws JSONException;
}
