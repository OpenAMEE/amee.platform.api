package com.amee.domain;

import com.amee.base.domain.ResultsWrapper;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.item.profile.ProfileItemNumberValue;
import com.amee.domain.profile.Profile;
import com.amee.platform.science.StartEndDate;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface ProfileItemService extends ItemService {

    @Override
    ProfileItem getItemByUid(String uid);

    boolean hasNonZeroPerTimeValues(ProfileItem profileItem);

    boolean isNonZeroPerTimeValue(ProfileItemNumberValue value);

    boolean isSingleFlight(ProfileItem profileItem);

    int getProfileItemCount(Profile profile, DataCategory dataCategory);

    ResultsWrapper<ProfileItem> getProfileItems(Profile profile, ProfileItemsFilter filter);

    List<ProfileItem> getProfileItems(Profile profile, IDataCategoryReference dataCategory, Date profileDate);

    List<ProfileItem> getProfileItems(Profile profile, IDataCategoryReference dataCategory, StartEndDate startDate, StartEndDate endDate);

    boolean isUnique(ProfileItem pi);

    Collection<Long> getProfileDataCategoryIds(Profile profile);

    void persist(ProfileItem profileItem);

    void remove(ProfileItem profileItem);

    void persist(BaseItemValue itemValue);

    void remove(BaseItemValue itemValue);
    
    void updateProfileItemValues(ProfileItem profileItem);
}
