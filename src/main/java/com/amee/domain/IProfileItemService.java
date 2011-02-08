package com.amee.domain;

import com.amee.domain.data.DataCategory;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.science.StartEndDate;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface IProfileItemService extends IItemService {

    @Override
    public ProfileItem getItemByUid(String uid);

    public boolean hasNonZeroPerTimeValues(ProfileItem profileItem);

    public boolean isSingleFlight(ProfileItem profileItem);

    public int getProfileItemCount(Profile profile, DataCategory dataCategory);

    public List<ProfileItem> getProfileItems(Profile profile, IDataCategoryReference dataCategory, Date profileDate);

    public List<ProfileItem> getProfileItems(Profile profile, IDataCategoryReference dataCategory, StartEndDate startDate, StartEndDate endDate);

    public boolean isUnique(ProfileItem pi);

    public boolean equivalentProfileItemExists(ProfileItem profileItem);

    public Collection<Long> getProfileDataCategoryIds(Profile profile);

    public void persist(ProfileItem profileItem);

    public void persist(BaseItemValue itemValue);
}
