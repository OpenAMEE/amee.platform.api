package com.amee.domain.item.profile;

import com.amee.domain.AMEEStatus;
import com.amee.domain.ObjectType;
import com.amee.domain.data.DataCategory;
import com.amee.domain.item.BaseItem;
import com.amee.domain.item.data.NuDataItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.science.StartEndDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "PROFILE_ITEM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NuProfileItem extends BaseItem {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "PROFILE_ID")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "DATA_ITEM_ID")
    private NuDataItem dataItem;

    @Column(name = "START_DATE")
    @Index(name = "START_DATE_IND")
    protected Date startDate = new Date();

    @Column(name = "END_DATE")
    @Index(name = "END_DATE_IND")
    protected Date endDate;

    public NuProfileItem() {
        super();
    }

    public NuProfileItem(Profile profile, NuDataItem dataItem) {
        super(dataItem.getDataCategory(), dataItem.getItemDefinition());
        setProfile(profile);
        setDataItem(dataItem);
    }

    public NuProfileItem(Profile profile, DataCategory dataCategory, NuDataItem dataItem) {
        super(dataCategory, dataItem.getItemDefinition());
        setProfile(profile);
        setDataItem(dataItem);
    }

    public NuProfileItem getCopy() {
        log.debug("getCopy()");
        NuProfileItem profileItem = new NuProfileItem();
        copyTo(profileItem);
        return profileItem;
    }

    protected void copyTo(NuProfileItem o) {
        super.copyTo(o);
        o.profile = profile;
        o.dataItem = dataItem;
        o.startDate = (startDate != null) ? (Date) startDate.clone() : null;
        o.endDate = (endDate != null) ? (Date) endDate.clone() : null;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || getDataItem().isTrash() || getProfile().isTrash();
    }

    /**
     * @return returns true if this Item supports CO2 amounts, otherwise false.
     */
    @Override
    public boolean supportsCalculation() {
        return !getItemDefinition().getAlgorithms().isEmpty();
    }

    @Override
    public String getPath() {
        return getUid();
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public NuDataItem getDataItem() {
        return dataItem;
    }

    public void setDataItem(NuDataItem dataItem) {
        if (dataItem != null) {
            this.dataItem = dataItem;
        }
    }

    public StartEndDate getStartDate() {
        return new StartEndDate(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public StartEndDate getEndDate() {
        if (endDate != null) {
            return new StartEndDate(endDate);
        } else {
            return null;
        }
    }

    public void setEndDate(Date endDate) {
        // May be null.
        this.endDate = endDate;
    }

    public boolean isEnd() {
        return (endDate != null) && (startDate.compareTo(endDate) == 0);
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.PI;
    }
}
