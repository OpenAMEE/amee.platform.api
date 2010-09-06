package com.amee.domain.profile;

import com.amee.domain.Builder;
import com.amee.domain.ObjectType;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.Item;
import com.amee.platform.science.ReturnValues;
import com.amee.platform.science.StartEndDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

@Configurable(autowire = Autowire.BY_TYPE)
public class ProfileItem extends Item {

    private DataItem dataItem;
    private LegacyProfileItem legacyEntity;

    public ProfileItem() {
        super();
        setLegacyEntity(new LegacyProfileItem());
    }

    public ProfileItem(Profile profile, DataItem dataItem) {
        super();
        setLegacyEntity(new LegacyProfileItem(profile, dataItem.getLegacyEntity()));
    }

    public ProfileItem(Profile profile, DataCategory dataCategory, DataItem dataItem) {
        super();
        setLegacyEntity(new LegacyProfileItem(profile, dataCategory, dataItem.getLegacyEntity()));
    }

    public ProfileItem(LegacyProfileItem profileItem) {
        super();
        setLegacyEntity(profileItem);
        getLegacyEntity().setAdapter(this);
    }

    public static ProfileItem getProfileItem(LegacyProfileItem legacyProfileItem) {
        if (legacyProfileItem != null) {
            if (legacyProfileItem.getAdapter() != null) {
                return legacyProfileItem.getAdapter();
            } else {
                return new ProfileItem(legacyProfileItem);
            }
        } else {
            return null;
        }
    }

    public void setBuilder(Builder builder) {
        getLegacyEntity().setBuilder(builder);
    }

    public ProfileItem getCopy() {
        return ProfileItem.getProfileItem(getLegacyEntity().getCopy());
    }

    public String getPath() {
        return getLegacyEntity().getPath();
    }

    public Profile getProfile() {
        return getLegacyEntity().getProfile();
    }

    public void setProfile(Profile profile) {
        getLegacyEntity().setProfile(profile);
    }

    public DataItem getDataItem() {
        return dataItem;
    }

    public void setDataItem(DataItem dataItem) {
        getLegacyEntity().setDataItem(dataItem.getLegacyEntity());
        this.dataItem = dataItem;
    }

    public StartEndDate getStartDate() {
        return getLegacyEntity().getStartDate();
    }

    public void setStartDate(Date startDate) {
        getLegacyEntity().setStartDate(startDate);
    }

    public StartEndDate getEndDate() {
        return getLegacyEntity().getEndDate();
    }

    public void setEndDate(Date endDate) {
        getLegacyEntity().setEndDate(endDate);
    }

    public boolean isEnd() {
        return getLegacyEntity().isEnd();
    }

    public ReturnValues getAmounts(boolean recalculate) {
        return getLegacyEntity().getAmounts(recalculate);
    }

    public ReturnValues getAmounts() {
        return getLegacyEntity().getAmounts();
    }

    @Deprecated
    public double getAmount() {
        return getLegacyEntity().getAmount();
    }

    public void setAmounts(ReturnValues amounts) {
        getLegacyEntity().setAmounts(amounts);
    }

    @Override
    public JSONObject getJSONObject(boolean b) throws JSONException {
        return getLegacyEntity().getJSONObject(b);
    }

    public Element getElement(Document document, boolean b) {
        return getLegacyEntity().getElement(document, b);
    }

    public boolean hasNonZeroPerTimeValues() {
        return getLegacyEntity().hasNonZeroPerTimeValues();
    }

    public boolean isSingleFlight() {
        return getLegacyEntity().isSingleFlight();
    }

    @Override
    public boolean isTrash() {
        return getLegacyEntity().isTrash();
    }

    public ObjectType getObjectType() {
        return ObjectType.PI;
    }

    public LegacyProfileItem getLegacyEntity() {
        return legacyEntity;
    }

    public void setLegacyEntity(LegacyProfileItem legacyEntity) {
        this.legacyEntity = legacyEntity;
        this.dataItem = DataItem.getDataItem(legacyEntity.getDataItem());
    }
}
