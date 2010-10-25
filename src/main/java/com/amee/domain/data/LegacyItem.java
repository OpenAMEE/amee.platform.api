/**
 * This file is part of AMEE.
 *
 * AMEE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * AMEE is free software and is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by http://www.dgen.net.
 * Website http://www.amee.cc
 */
package com.amee.domain.data;

import com.amee.domain.AMEEEntity;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.path.Pathable;
import com.amee.platform.science.ExternalValue;
import com.amee.platform.science.InternalValue;
import com.amee.platform.science.StartEndDate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import java.util.*;

@Entity
@Inheritance
@Table(name = "ITEM")
@DiscriminatorColumn(name = "TYPE", length = 3)
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Configurable(autowire = Autowire.BY_TYPE)
public abstract class LegacyItem extends AMEEEntity implements Pathable {

    public final static int NAME_MAX_SIZE = 255;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ITEM_DEFINITION_ID")
    private ItemDefinition itemDefinition;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "DATA_CATEGORY_ID")
    private DataCategory dataCategory;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<LegacyItemValue> itemValues = new ArrayList<LegacyItemValue>();

    @Column(name = "NAME", length = NAME_MAX_SIZE, nullable = false)
    private String name = "";

    @Transient
    private LegacyItemValueMap itemValuesMap;

    @Transient
    private Set<LegacyItemValue> activeItemValues;

    @Transient
    private Date effectiveStartDate;

    @Transient
    private Date effectiveEndDate;

    @Transient
    private transient String fullPath;

    public LegacyItem() {
        super();
    }

    public LegacyItem(DataCategory dataCategory, ItemDefinition itemDefinition) {
        this();
        setDataCategory(dataCategory);
        setItemDefinition(itemDefinition);
    }

    /**
     * Copy values from this instance to the supplied instance.
     * <p/>
     * Does not copy LegacyItemValues.
     *
     * @param o Object to copy values to
     */
    protected void copyTo(LegacyItem o) {
        super.copyTo(o);
        o.itemDefinition = itemDefinition;
        o.dataCategory = dataCategory;
        o.name = name;
        o.effectiveStartDate = (effectiveStartDate != null) ? (Date) effectiveStartDate.clone() : null;
        o.effectiveEndDate = (effectiveEndDate != null) ? (Date) effectiveEndDate.clone() : null;
    }

    public void addItemValue(LegacyItemValue itemValue) {
        itemValues.add(itemValue);
        resetItemValueCollections();
    }

    public Set<ItemValueDefinition> getItemValueDefinitions() {
        Set<ItemValueDefinition> itemValueDefinitions = new HashSet<ItemValueDefinition>();
        for (LegacyItemValue itemValue : getActiveItemValues()) {
            itemValueDefinitions.add(itemValue.getItemValueDefinition());
        }
        return itemValueDefinitions;
    }

//    public JSONObject getIdentityJSONObject() throws JSONException {
//        return XMLUtils.getIdentityJSONObject(this);
//    }

//    public abstract JSONObject getJSONObject(boolean detailed) throws JSONException;
//
//    public abstract Element getIdentityElement(Document document);

    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = new ArrayList<IAMEEEntityReference>();
        entities.add(this);
        DataCategory dc = getDataCategory();
        while (dc != null) {
            entities.add(dc);
            dc = dc.getDataCategory();
        }
        Collections.reverse(entities);
        return entities;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        if (itemDefinition != null) {
            this.itemDefinition = itemDefinition;
        }
    }

    public DataCategory getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(DataCategory dataCategory) {
        if (dataCategory != null) {
            this.dataCategory = dataCategory;
        }
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        if (getName().length() > 0) {
            return getName();
        } else {
            return getDisplayPath();
        }
    }

    public String getDisplayPath() {
        if (!getPath().isEmpty()) {
            return getPath();
        } else {
            return getUid();
        }
    }

    /**
     * Get the full path of this Item.
     *
     * @return the full path
     */
    public String getFullPath() {
        // Need to build the fullPath?
        if (fullPath == null) {
            // Is there a parent.
            if (getDataCategory() != null) {
                // There is a parent.
                fullPath = getDataCategory().getFullPath() + "/" + getDisplayPath();
            } else {
                // There must be a parent.
                throw new RuntimeException("Item has no parent.");
            }
        }
        return fullPath;
    }

    /**
     * Get an unmodifiable List of {@link com.amee.domain.data.LegacyItemValue}s owned by this Item.
     * <p/>
     * For an historical sequence of {@link com.amee.domain.data.LegacyItemValue}s, only the active entry for each {@link com.amee.domain.data.ItemValueDefinition}
     * for the prevailing datetime context is returned.
     *
     * @return - the List of {@link com.amee.domain.data.LegacyItemValue}
     */
    public List<LegacyItemValue> getItemValues() {
        return Collections.unmodifiableList(getItemValuesMap().getAll(getEffectiveStartDate()));
    }

    /**
     * Get an unmodifiable List of ALL {@link com.amee.domain.data.LegacyItemValue}s owned by this Item for a particular {@link com.amee.domain.data.ItemValueDefinition}
     *
     * @param itemValuePath - the {@link com.amee.domain.data.ItemValueDefinition} path
     * @return - the List of {@link com.amee.domain.data.LegacyItemValue}
     */
    public List<LegacyItemValue> getAllItemValues(String itemValuePath) {
        return Collections.unmodifiableList(getItemValuesMap().getAll(itemValuePath));
    }

    /**
     * Get an unmodifiable List of all active (not deleted) {@link com.amee.domain.data.LegacyItemValue}s owned by this Item.
     *
     * @return - the Set of {@link com.amee.domain.data.LegacyItemValue}
     */
    private Set<LegacyItemValue> getActiveItemValues() {
        if (activeItemValues == null) {
            activeItemValues = new HashSet<LegacyItemValue>();
            for (LegacyItemValue iv : itemValues) {
                if (!iv.isTrash()) {
                    activeItemValues.add(iv);
                }
            }
        }
        return Collections.unmodifiableSet(activeItemValues);
    }

    /**
     * Return an {@link com.amee.domain.data.ItemValueMap} of {@link com.amee.domain.data.LegacyItemValue}s belonging to this Item.
     * The key is the value returned by {@link LegacyItemValue#getDisplayPath()}.
     *
     * @return {@link com.amee.domain.data.ItemValueMap}
     */
    public LegacyItemValueMap getItemValuesMap() {
        if (itemValuesMap == null) {
            itemValuesMap = new LegacyItemValueMap();
            for (LegacyItemValue itemValue : getActiveItemValues()) {
                itemValuesMap.put(itemValue.getDisplayPath(), itemValue);
            }
        }
        return itemValuesMap;
    }

    /**
     * Attempt to match an {@link com.amee.domain.data.LegacyItemValue} belonging to this Item using some identifier. The identifier may be a path
     * or UID.
     *
     * @param identifier - a value to be compared to the path and then the uid of the {@link com.amee.domain.data.LegacyItemValue}s belonging
     *                   to this Item.
     * @param startDate  - the startDate to use in the {@link com.amee.domain.data.LegacyItemValue} lookup
     * @return the matched {@link com.amee.domain.data.LegacyItemValue} or NULL if no match is found.
     */
    public LegacyItemValue getItemValue(String identifier, Date startDate) {
        LegacyItemValue iv = getItemValuesMap().get(identifier, startDate);
        if (iv == null) {
            iv = getByUid(identifier);
        }
        return iv;
    }

    /**
     * Get an {@link com.amee.domain.data.LegacyItemValue} belonging to this Item using some identifier and prevailing datetime context.
     *
     * @param identifier - a value to be compared to the path and then the uid of the {@link com.amee.domain.data.LegacyItemValue}s belonging
     *                   to this Item.
     * @return the matched {@link com.amee.domain.data.LegacyItemValue} or NULL if no match is found.
     */
    public LegacyItemValue getItemValue(String identifier) {
        return getItemValue(identifier, getEffectiveStartDate());
    }

    /**
     * Get an {@link com.amee.domain.data.LegacyItemValue} by UID
     *
     * @param uid - the {@link com.amee.domain.data.LegacyItemValue} UID
     * @return the {@link com.amee.domain.data.LegacyItemValue} if found or NULL
     */
    private LegacyItemValue getByUid(final String uid) {
        return (LegacyItemValue) CollectionUtils.find(getActiveItemValues(), new Predicate() {
            public boolean evaluate(Object o) {
                LegacyItemValue iv = (LegacyItemValue) o;
                return iv.getUid().equals(uid);
            }
        });
    }

    /**
     * Add the Item's {@link com.amee.domain.data.LegacyItemValue} collection to the passed {@link com.amee.platform.science.InternalValue} collection.
     *
     * @param values - the {@link com.amee.platform.science.InternalValue} collection
     */
    @SuppressWarnings("unchecked")
    public void appendInternalValues(Map<ItemValueDefinition, InternalValue> values) {
        LegacyItemValueMap itemValueMap = getItemValuesMap();
        for (Object path : itemValueMap.keySet()) {
            // Get all LegacyItemValues with this ItemValueDefinition path.
            List<LegacyItemValue> itemValues = getAllItemValues((String) path);
            if (itemValues.size() > 1 || itemValues.get(0).getItemValueDefinition().isForceTimeSeries()) {
                appendTimeSeriesItemValue(values, itemValues);
            } else if (itemValues.size() == 1) {
                appendSingleValuedItemValue(values, itemValues.get(0));
            }
        }
    }

    // Add an LegacyItemValue timeseries to the InternalValue collection.

    @SuppressWarnings("unchecked")
    private void appendTimeSeriesItemValue(Map<ItemValueDefinition, InternalValue> values, List<LegacyItemValue> itemValues) {
        ItemValueDefinition ivd = itemValues.get(0).getItemValueDefinition();

        // Add all LegacyItemValues with usable values
        List<ExternalValue> usableSet = (List<ExternalValue>) CollectionUtils.select(itemValues, new LegacyUsableValuePredicate());

        if (!usableSet.isEmpty()) {
            values.put(ivd, new InternalValue(usableSet, getEffectiveStartDate(), getEffectiveEndDate()));
            log.debug("appendTimeSeriesItemValue() - added timeseries value " + ivd.getPath());
        }
    }

    // Add a single-valued LegacyItemValue to the InternalValue collection.

    private void appendSingleValuedItemValue(Map<ItemValueDefinition, InternalValue> values, LegacyItemValue itemValue) {
        if (itemValue.isUsableValue()) {
            values.put(itemValue.getItemValueDefinition(), new InternalValue(itemValue));
            log.debug("appendSingleValuedItemValue() - added single value " + itemValue.getPath());
        }
    }

    private void resetItemValueCollections() {
        itemValuesMap = null;
        activeItemValues = null;
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    /**
     * An Item must have a startDate.
     *
     * @return a StartEndDate
     */
    public abstract StartEndDate getStartDate();

    /**
     * An Item can have an endDate.
     *
     * @return a StartEndDate
     */
    public abstract StartEndDate getEndDate();

    /**
     * @return returns true if this Item supports CO2 amounts, otherwise false.
     */
    public boolean supportsCalculation() {
        return !getItemDefinition().getAlgorithms().isEmpty();
    }

    /**
     * Check whether a date is within the lifetime of an Item.
     *
     * @param date - the {@link java.util.Date} to check if within the lifetime as this Item.
     * @return true if the the passed date is greater or equal to the start date of this Item
     *         and less than the end date of this Item, otherwise false.
     */
    public boolean isWithinLifeTime(Date date) {
        return (date.equals(getStartDate()) || date.after(getStartDate())) &&
                (getEndDate() == null || date.before(getEndDate()));
    }

    /**
     * Set the effective start date for {@link com.amee.domain.data.LegacyItemValue} look-ups.
     *
     * @param effectiveStartDate - the effective start date for {@link com.amee.domain.data.LegacyItemValue} look-ups. If NULL or
     *                           before {@link com.amee.domain.data.LegacyItem#getStartDate()} this value is ignored.
     */
    public void setEffectiveStartDate(Date effectiveStartDate) {
        if ((effectiveStartDate != null) && effectiveStartDate.before(getStartDate())) {
            this.effectiveStartDate = null;
        } else {
            this.effectiveStartDate = effectiveStartDate;
        }
    }

    /**
     * Get the effective start date for {@link com.amee.domain.data.LegacyItemValue} look-ups.
     *
     * @return - the effective start date. If no date has been explicitly specified,
     *         then the Item startDate is returned.
     */
    public Date getEffectiveStartDate() {
        if (effectiveStartDate != null) {
            return effectiveStartDate;
        } else {
            return getStartDate();
        }
    }

    /**
     * Set the effective end date for {@link com.amee.domain.data.LegacyItemValue} look-ups.
     *
     * @param effectiveEndDate - the effective end date for {@link com.amee.domain.data.LegacyItemValue} look-ups. If NULL or
     *                         after {@link com.amee.domain.data.LegacyItem#getEndDate()} (if set) this value is ignored.
     */
    public void setEffectiveEndDate(Date effectiveEndDate) {
        if ((getEndDate() != null) && (effectiveEndDate != null) && effectiveEndDate.after(getEndDate())) {
            this.effectiveEndDate = null;
        } else {
            this.effectiveEndDate = effectiveEndDate;
        }
    }

    /**
     * Get the effective end date for {@link com.amee.domain.data.LegacyItemValue} look-ups.
     *
     * @return - the effective end date. If no date has been explicitly specified,
     *         then the Item endDate is returned.
     */
    public Date getEffectiveEndDate() {
        if (effectiveEndDate != null) {
            return effectiveEndDate;
        } else {
            return getEndDate();
        }
    }

    /**
     * Returns a Duration for the Item which is based on the startDate and endDate values. If there is no
     * endDate then null is returned.
     *
     * @return the Duration or null
     */
    public Duration getDuration() {
        if (getEndDate() != null) {
            return new Duration(getStartDate().getTime(), getEndDate().getTime());
        } else {
            return null;
        }
    }

    /**
     * Check if there exists amongst the current set of LegacyItemValues, an entry with the given
     * itemValueDefinition and startDate.
     *
     * @param itemValueDefinition - an {@link com.amee.domain.data.ItemValueDefinition}
     * @param startDate           - an {@link com.amee.domain.data.LegacyItemValue} startDate
     * @return - true if the newItemValue is unique, otherwise false
     */
    public boolean isUnique(ItemValueDefinition itemValueDefinition, StartEndDate startDate) {
        String uniqueId = itemValueDefinition.getUid() + startDate.getTime();
        for (LegacyItemValue iv : getActiveItemValues()) {
            String checkId = iv.getItemValueDefinition().getUid() + iv.getStartDate().getTime();
            if (uniqueId.equals(checkId)) {
                return false;
            }
        }
        return true;
    }

    public abstract Item getAdapter();
}