package com.amee.platform.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.domain.AMEEStatus;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.auth.User;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.domain.tag.Tag;
import com.amee.domain.unit.AMEEUnit;
import com.amee.domain.unit.AMEEUnitType;

/**
 * A service to retrieve resources based on the request.
 * These will usually be retrieved from the database using request attributes.
 */
public interface ResourceService {

    DataCategory getDataCategory(RequestWrapper requestWrapper);

    DataCategory getDataCategory(RequestWrapper requestWrapper, AMEEStatus status);

    DataCategory getDataCategoryWhichHasItemDefinition(RequestWrapper requestWrapper);

    DataItem getDataItem(RequestWrapper requestWrapper, DataCategory dataCategory);

    BaseDataItemValue getDataItemValue(RequestWrapper requestWrapper, DataItem dataItem, ItemValueDefinition itemValueDefinition);

    ItemDefinition getItemDefinition(RequestWrapper requestWrapper);

    ItemValueDefinition getItemValueDefinition(RequestWrapper requestWrapper, ItemDefinition itemDefinition);

    ItemValueDefinition getItemValueDefinition(RequestWrapper requestWrapper, DataItem dataItem);

    ReturnValueDefinition getReturnValueDefinition(RequestWrapper requestWrapper, ItemDefinition itemDefinition);

    Algorithm getAlgorithm(RequestWrapper requestWrapper, ItemDefinition itemDefinition);

    Tag getTag(RequestWrapper requestWrapper);

    AMEEUnitType getUnitType(RequestWrapper requestWrapper);

    AMEEUnitType getUnitType(RequestWrapper requestWrapper, boolean allowMissingUnitType);

    AMEEUnit getUnit(RequestWrapper requestWrapper, AMEEUnitType unitType);
    
    Profile getProfile(RequestWrapper requestWrapper);

    ProfileItem getProfileItem(RequestWrapper requestWrapper, Profile profile);

    User getCurrentUser(RequestWrapper requestWrapper);
}
