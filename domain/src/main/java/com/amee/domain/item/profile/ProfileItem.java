package com.amee.domain.item.profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.Duration;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.AMEEStatus;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;
import com.amee.domain.ProfileItemService;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueMap;
import com.amee.domain.item.BaseItem;
import com.amee.domain.item.data.DataItem;
import com.amee.domain.profile.CO2CalculationService;
import com.amee.domain.profile.Profile;
import com.amee.platform.science.ReturnValues;
import com.amee.platform.science.StartEndDate;

@Entity
@Table(name = "profile_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProfileItem extends BaseItem {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "data_item_id")
    private DataItem dataItem;

    @Column(name = "start_date")
    protected Date startDate = new Date();

    @Column(name = "end_date")
    protected Date endDate;

    @Transient
    private ReturnValues amounts = new ReturnValues();

    /**
     * A temporary and transient variable used in validation to set the endDate.
     */
    @Transient
    private String duration;

    /**
     * A temporary and transient object for use in validation. See the getValues() method below.
     */
    @Transient
    private Object values;

    /**
     * A temporary and transient object for use in validation. See the getUnits() method below.
     */
    @Transient
    private Object units;

    /**
     * A temporary and transient object for use in validation. See the getPerUnits() method below.
     * This bean has the same structure as the units bean.
     */
    @Transient
    private Object perUnits;

    public ProfileItem() {
        super();
    }

    public ProfileItem(Profile profile, DataItem dataItem) {
        super(dataItem.getDataCategory(), dataItem.getItemDefinition());
        setProfile(profile);
        setDataItem(dataItem);
    }

    public ProfileItem(Profile profile, DataCategory dataCategory, DataItem dataItem) {
        super(dataCategory, dataItem.getItemDefinition());
        setProfile(profile);
        setDataItem(dataItem);
    }

    public ProfileItem getCopy() {
        log.debug("getCopy()");
        ProfileItem profileItem = new ProfileItem();
        copyTo(profileItem);
        return profileItem;
    }

    protected void copyTo(ProfileItem o) {
        super.copyTo(o);
        o.profile = profile;
        o.dataItem = dataItem;
        o.startDate = (startDate != null) ? (Date) startDate.clone() : null;
        o.endDate = (endDate != null) ? (Date) endDate.clone() : null;
        o.amounts = amounts;
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

    public DataItem getDataItem() {
        return dataItem;
    }

    public void setDataItem(DataItem dataItem) {
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

    // Required for data binding. The parameter type of the setter must match the return type of the getter.
    public void setStartDate(StartEndDate startDate) {
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

    // Required for data binding. The parameter type of the setter must match the return type of the getter.
    public void setEndDate(StartEndDate endDate) {
        // May be null.
        this.endDate = endDate;
    }

    public boolean isEnd() {
        return (endDate != null) && (startDate.compareTo(endDate) == 0);
    }

    public boolean isWithinLifeTime(Date date) {
        return (date.equals(getStartDate()) || date.after(getStartDate())) &&
                (getEndDate() == null || date.before(getEndDate()));
    }

    /**
     * Get the GHG {@link com.amee.platform.science.ReturnValues ReturnValues} for this ProfileItem.
     * <p/>
     * If the ProfileItem does not support calculations (i.e. metadata) an empty ReturnValues object is returned.
     *
     * @param recalculate force recalculation of the amounts. If false, only calculate amounts if amounts is empty.
     * @return - the {@link com.amee.platform.science.ReturnValues ReturnValues} for this ProfileItem
     */
    public ReturnValues getAmounts(boolean recalculate) {
        if (amounts.getReturnValues().isEmpty() || recalculate) {
            log.debug("getAmounts() - calculating amounts");
            getCalculationService().calculate(this);
        }
        return amounts;
    }

    /**
     * Get the GHG {@link com.amee.platform.science.ReturnValues ReturnValues} for this ProfileItem.
     * <p/>
     * If the ProfileItem does not support calculations (i.e. metadata) an empty ReturnValues object is returned.
     * <p/>
     * Note: this method only calculates the amounts if amounts is empty, ie, has not already been calculated.
     *
     * @return - the {@link com.amee.platform.science.ReturnValues ReturnValues} for this ProfileItem
     */
    public ReturnValues getAmounts() {
        return getAmounts(false);
    }

    /**
     * Returns the default GHG amount for this ProfileItem as a double.
     * This method is only included to provide backwards compatibility for existing Algorithms.
     *
     * The following algorithms call this method:
     *  - 599C0F18A362 (Computers Generic)
     *  - 7A613C522477 (Entertainment Generic)
     *  
     * @return the double value of the default GHG amount.
     */
    @Deprecated
    public double getAmount() {
        return getAmounts().defaultValueAsDouble();
    }

    public void setAmounts(ReturnValues amounts) {
        this.amounts = amounts;
    }

    /**
     * Set the effective start date for {@link com.amee.domain.item.profile.BaseProfileItemValue} look-ups.
     *
     * @param effectiveStartDate - the effective start date for {@link com.amee.domain.item.profile.BaseProfileItemValue} look-ups. If NULL or
     *                           before the start date this value is ignored.
     */
    public void setEffectiveStartDate(Date effectiveStartDate) {
        if ((effectiveStartDate != null) && effectiveStartDate.before(getStartDate())) {
            super.setEffectiveStartDate(null);
        } else {
            super.setEffectiveStartDate(effectiveStartDate);
        }
    }

    /**
     * Get the effective start date for {@link com.amee.domain.item.profile.BaseProfileItemValue} look-ups.
     *
     * @return - the effective start date. If no date has been explicitly specified,
     *         then the Item startDate is returned.
     */
    public Date getEffectiveStartDate() {
        if (super.getEffectiveStartDate() != null) {
            return super.getEffectiveStartDate();
        } else {
            return getStartDate();
        }
    }

    /**
     * Set the effective end date for {@link com.amee.domain.item.profile.BaseProfileItemValue} look-ups.
     *
     * @param effectiveEndDate - the effective end date for {@link com.amee.domain.item.profile.BaseProfileItemValue} look-ups. If NULL or
     *                         after the end date (if set) this value is ignored.
     */
    public void setEffectiveEndDate(Date effectiveEndDate) {
        if ((getEndDate() != null) && (effectiveEndDate != null) && effectiveEndDate.after(getEndDate())) {
            super.setEffectiveEndDate(null);
        } else {
            super.setEffectiveEndDate(effectiveEndDate);
        }
    }

    /**
     * Get the effective end date for {@link com.amee.domain.item.profile.BaseProfileItemValue} look-ups.
     *
     * @return - the effective end date. If no date has been explicitly specified,
     *         then the Item endDate is returned.
     */
    public Date getEffectiveEndDate() {
        if (super.getEffectiveEndDate() != null) {
            return super.getEffectiveEndDate();
        } else {
            return getEndDate();
        }
    }

    /**
     * Returns a Duration for the Item which is based on the startDate and endDate values. If there is no
     * endDate then null is returned.
     *
     * Strange method name so it doesn't conflict with the duration getter/setter required for bean binding in validation.
     *
     * @return the Duration or null
     */
    public Duration getDurationInternal() {
        if (getEndDate() != null) {
            return new Duration(getStartDate().getTime(), getEndDate().getTime());
        } else {
            return null;
        }
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.PI;
    }

    @Transient
    protected CO2CalculationService getCalculationService() {
        return ThreadBeanHolder.get(CO2CalculationService.class);
    }

    /**
     * Simulates the legacy Item.getItemValuesMap method. Usage of this is discouraged.
     * <p/>
     * This is used in algorithms.
     *
     * @return an ItemValueMap of {@link com.amee.domain.item.BaseItemValue} instances.
     */
    @Deprecated
    @Transient
    public ItemValueMap getItemValuesMap() {
        return getProfileItemService().getItemValuesMap(this);
    }

    /**
     * Gets the ProfileItemService bound to the current thread. Usage of this is discouraged.
     *
     * @return the current {@link com.amee.domain.ProfileItemService}
     */
    @Deprecated
    @Transient
    private ProfileItemService getProfileItemService() {
        return ThreadBeanHolder.get(ProfileItemService.class);
    }

    /**
     * Returns a temporary and transient JavaBean related to the Item Values associated with this
     * ProfileItem. The bean is intended as a target for property binding during input validation within
     * PUT and POST requests. See {@link com.amee.domain.data.ItemDefinition#getProfileItemValuesBean()} for more details
     * on how this bean is created.
     *
     * @return
     */
    public Object getValues() {
        if (values == null) {
            values = getItemDefinition().getProfileItemValuesBean();
        }
        return values;
    }

    /**
     * Returns a temporary and transient JavaBean related to the units associated with this
     * ProfileItem. The bean is intended as a target for property binding during input validation within
     * PUT and POST requests. See {@link com.amee.domain.data.ItemDefinition#getProfileItemUnitsBean()} for more details
     * on how this bean is created.
     *
     * @return
     */
    public Object getUnits() {
        if (units == null) {
            units = getItemDefinition().getProfileItemUnitsBean();
        }
        return units;
    }

    /**
     * Returns a temporary and transient JavaBean related to the perUnits associated with this
     * ProfileItem. The bean is intended as a target for property binding during input validation within
     * PUT and POST requests. See {@link com.amee.domain.data.ItemDefinition#getProfileItemUnitsBean()} for more details
     * on how this bean is created.
     *
     * @return
     */
    public Object getPerUnits() {
        if (perUnits == null) {
            perUnits = getItemDefinition().getProfileItemUnitsBean();
        }
        return perUnits;
    }

    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = new ArrayList<IAMEEEntityReference>();
        entities.add(getProfile());
        entities.add(this);
        return entities;
    }

    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getNote(){
    	return getMetadataValue("note");
    }
    
    public void setNote(String note){
    	getOrCreateMetadata("note").setValue(note);
        onModify();
    }
}