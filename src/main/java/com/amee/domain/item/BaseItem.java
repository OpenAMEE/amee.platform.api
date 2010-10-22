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
package com.amee.domain.item;

import com.amee.domain.AMEEEntity;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.data.*;
import com.amee.domain.path.Pathable;
import com.amee.platform.science.ExternalValue;
import com.amee.platform.science.InternalValue;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import javax.persistence.*;
import java.util.*;

@MappedSuperclass
public abstract class BaseItem extends AMEEEntity implements Pathable {

    public final static int NAME_MAX_SIZE = 255;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ITEM_DEFINITION_ID")
    private ItemDefinition itemDefinition;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "DATA_CATEGORY_ID")
    private DataCategory dataCategory;

    @Column(name = "NAME", length = NAME_MAX_SIZE, nullable = false)
    private String name = "";

    @Transient
    private transient String fullPath;

    @Transient
    private NuItemValueMap itemValueMap;

    @Transient
    private Set<BaseItemValue> activeItemValues;

    public BaseItem() {
        super();
    }

    public BaseItem(DataCategory dataCategory, ItemDefinition itemDefinition) {
        this();
        setDataCategory(dataCategory);
        setItemDefinition(itemDefinition);
    }

    /**
     * Copy values from this instance to the supplied instance.
     * <p/>
     * Does not copy ItemValues.
     *
     * @param o Object to copy values to
     */
    protected void copyTo(BaseItem o) {
        super.copyTo(o);
        o.itemDefinition = itemDefinition;
        o.dataCategory = dataCategory;
        o.name = name;
    }

    /**
     * Get the full path of this Item.
     *
     * @return the full path
     */
    @Override
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        if (!getName().isEmpty()) {
            return getName();
        } else {
            return getDisplayPath();
        }
    }

    @Override
    public String getDisplayPath() {
        if (!getPath().isEmpty()) {
            return getPath();
        } else {
            return getUid();
        }
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    /**
     * @return returns true if this Item supports CO2 amounts, otherwise false.
     */
    public boolean supportsCalculation() {
        return !getItemDefinition().getAlgorithms().isEmpty();
    }

    public abstract Item getAdapter();

    // TODO
    public void appendInternalValues(Map<ItemValueDefinition, InternalValue> values) {
        NuItemValueMap itemValueMap = getItemValueMap();
        for (Object path : itemValueMap.keySet()) {

            // Get all BaseItemValues with this ItemValueDefinition path.
            List<BaseItemValue> itemValues = getAdapter().getItemService().getAllItemValues(this, (String) path);
            if (itemValues.size() > 1 || itemValues.get(0).getItemValueDefinition().isForceTimeSeries()) {
                appendTimeSeriesItemValue(values, itemValues);
            } else if (itemValues.size() == 1) {
                appendSingleValuedItemValue(values, itemValues.get(0));
            }
        }
    }

    public NuItemValueMap getItemValueMap() {
        if (itemValueMap == null) {
            itemValueMap = new NuItemValueMap();
            for (BaseItemValue itemValue : getActiveItemValues()) {
                itemValueMap.put(itemValue.getDisplayPath(), itemValue);
            }
        }
        return itemValueMap;
    }

    private Set<BaseItemValue> getActiveItemValues() {
        return Collections.unmodifiableSet(getAdapter().getItemService().getActiveItemValues(this));
    }

    @SuppressWarnings("unchecked")
    private void appendTimeSeriesItemValue(Map<ItemValueDefinition, InternalValue> values, List<BaseItemValue> itemValues) {
        ItemValueDefinition ivd = itemValues.get(0).getItemValueDefinition();

        // Add all LegacyItemValues with usable values
        List<ExternalValue> usableSet = (List<ExternalValue>) CollectionUtils.select(itemValues, new UsableValuePredicate());

        if (!usableSet.isEmpty()) {
            values.put(ivd, new InternalValue(usableSet, getAdapter().getEffectiveStartDate(), getAdapter().getEffectiveEndDate()));
            log.debug("appendTimeSeriesItemValue() - added timeseries value " + ivd.getPath());
        }
    }

    private void appendSingleValuedItemValue(Map<ItemValueDefinition, InternalValue> values, BaseItemValue itemValue) {
        if (itemValue.isUsableValue()) {
            values.put(itemValue.getItemValueDefinition(), new InternalValue(itemValue.getValueAsString()));
            log.debug("appendSingleValuedItemValue() - added single value " + itemValue.getPath());
        }
    }

    public static class UsableValuePredicate implements Predicate {
        public boolean evaluate(Object o) {
            return ((BaseItemValue) o).isUsableValue();
        }
    }

}