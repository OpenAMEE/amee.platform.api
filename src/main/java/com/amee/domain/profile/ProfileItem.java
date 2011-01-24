package com.amee.domain.profile;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.IProfileItemService;
import com.amee.domain.ObjectType;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.Item;
import com.amee.domain.item.profile.NuProfileItem;
import com.amee.platform.science.ReturnValues;
import com.amee.platform.science.StartEndDate;

import java.util.Date;

public class ProfileItem extends Item {

    public final static boolean USE_NU = true;

    private NuProfileItem nuEntity;

    public ProfileItem() {
        super();
        if (USE_NU) {
            setNuEntity(new NuProfileItem());
        } else {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        }
    }

    public ProfileItem(Profile profile, DataItem dataItem) {
        super();
        if (dataItem.isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            setNuEntity(new NuProfileItem(profile, dataItem.getNuEntity()));
        }
    }

    public ProfileItem(Profile profile, DataCategory dataCategory, DataItem dataItem) {
        super();
        if (dataItem.isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            setNuEntity(new NuProfileItem(profile, dataCategory, dataItem.getNuEntity()));
        }
    }

    public ProfileItem(NuProfileItem profileItem) {
        super();
        setNuEntity(profileItem);
    }

    public static ProfileItem getProfileItem(NuProfileItem profileItem) {
        if (profileItem != null) {
            if (profileItem.getAdapter() != null) {
                return profileItem.getAdapter();
            } else {
                return new ProfileItem(profileItem);
            }
        } else {
            return null;
        }
    }

    public ProfileItem getCopy() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return ProfileItem.getProfileItem(getNuEntity().getCopy());
        }
    }

    @Override
    public String getPath() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getPath();
        }
    }

    public Profile getProfile() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getProfile();
        }
    }

    public void setProfile(Profile profile) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setProfile(profile);
        }
    }

    public DataItem getDataItem() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return DataItem.getDataItem(getNuEntity().getDataItem());
        }
    }

    public void setDataItem(DataItem dataItem) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setDataItem(dataItem.getNuEntity());
        }
    }

    public StartEndDate getStartDate() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getStartDate();
        }
    }

    public void setStartDate(Date startDate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setStartDate(startDate);
        }
    }

    @Override
    public StartEndDate getEndDate() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getEndDate();
        }
    }

    public void setEndDate(Date endDate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setEndDate(endDate);
        }
    }

    public boolean isEnd() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().isEnd();
        }
    }

    public ReturnValues getAmounts(boolean recalculate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getAmounts(recalculate);
        }
    }

    public ReturnValues getAmounts() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getAmounts();
        }
    }

    @Deprecated
    public double getAmount() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getAmount();
        }
    }

    public void setAmounts(ReturnValues amounts) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setAmounts(amounts);
        }
    }

    public boolean hasNonZeroPerTimeValues() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getItemService().hasNonZeroPerTimeValues(getNuEntity());
        }
    }

    public boolean isSingleFlight() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getItemService().isSingleFlight(getNuEntity());
        }
    }

    @Override
    public boolean isTrash() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().isTrash();
        }
    }

    @Override
    public ObjectType getObjectType() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return ObjectType.NPI;
        }
    }

    @Override
    public NuProfileItem getNuEntity() {
        return nuEntity;
    }

    public void setNuEntity(NuProfileItem nuEntity) {
        nuEntity.setAdapter(this);
        this.nuEntity = nuEntity;
    }

    public IProfileItemService getItemService() {
        return ThreadBeanHolder.get(IProfileItemService.class);
    }
}
