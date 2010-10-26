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

import com.amee.domain.AMEEEntityAdapter;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.IItemService;
import com.amee.domain.item.BaseItem;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.NuDataItem;
import com.amee.domain.item.profile.NuProfileItem;
import com.amee.domain.path.Pathable;
import com.amee.domain.profile.LegacyProfileItem;
import com.amee.domain.profile.ProfileItem;
import com.amee.platform.science.InternalValue;
import com.amee.platform.science.StartEndDate;
import org.joda.time.Duration;

import java.util.*;

public abstract class Item extends AMEEEntityAdapter implements Pathable {

    public final static int NAME_MAX_SIZE = LegacyItem.NAME_MAX_SIZE;

    public Item() {
        super();
    }

    public static Item getItem(LegacyItem item) {
        if (LegacyDataItem.class.isAssignableFrom(item.getClass())) {
            return DataItem.getDataItem((LegacyDataItem) item);
        } else if (LegacyProfileItem.class.isAssignableFrom(item.getClass())) {
            return ProfileItem.getProfileItem((LegacyProfileItem) item);
        } else {
            throw new RuntimeException("Class not supported: " + item.getClass().toString());
        }
    }

    public static Item getItem(BaseItem item) {
        if (NuDataItem.class.isAssignableFrom(item.getClass())) {
            return DataItem.getDataItem((NuDataItem) item);
        } else if (NuProfileItem.class.isAssignableFrom(item.getClass())) {
            return ProfileItem.getProfileItem((NuProfileItem) item);
        } else {
            throw new RuntimeException("Class not supported: " + item.getClass().toString());
        }
    }

    public void addItemValue(ItemValue itemValue) {
        if (isLegacy()) {
            getLegacyEntity().addItemValue(itemValue.getLegacyEntity());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Set<ItemValueDefinition> getItemValueDefinitions() {
        if (isLegacy()) {
            return getLegacyEntity().getItemValueDefinitions();
        } else {
            return getItemService().getItemValueDefinitionsInUse(getNuEntity());
        }
    }

    public List<IAMEEEntityReference> getHierarchy() {
        if (isLegacy()) {
            return getLegacyEntity().getHierarchy();
        } else {
            return getNuEntity().getHierarchy();
        }
    }

    public ItemDefinition getItemDefinition() {
        if (isLegacy()) {
            return getLegacyEntity().getItemDefinition();
        } else {
            return getNuEntity().getItemDefinition();
        }
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        if (isLegacy()) {
            getLegacyEntity().setItemDefinition(itemDefinition);
        } else {
            getNuEntity().setItemDefinition(itemDefinition);
        }
    }

    public DataCategory getDataCategory() {
        if (isLegacy()) {
            return getLegacyEntity().getDataCategory();
        } else {
            return getNuEntity().getDataCategory();
        }
    }

    public void setDataCategory(DataCategory dataCategory) {
        if (isLegacy()) {
            getLegacyEntity().setDataCategory(dataCategory);
        } else {
            getNuEntity().setDataCategory(dataCategory);
        }
    }

    public String getName() {
        if (isLegacy()) {
            return getLegacyEntity().getName();
        } else {
            return getNuEntity().getName();
        }
    }

    public String getDisplayName() {
        if (isLegacy()) {
            return getLegacyEntity().getDisplayName();
        } else {
            return getNuEntity().getDisplayName();
        }
    }

    public String getDisplayPath() {
        if (isLegacy()) {
            return getLegacyEntity().getDisplayPath();
        } else {
            return getNuEntity().getDisplayPath();
        }
    }

    public String getFullPath() {
        if (isLegacy()) {
            return getLegacyEntity().getFullPath();
        } else {
            return getNuEntity().getFullPath();
        }
    }

    public List<ItemValue> getItemValues() {
        if (isLegacy()) {
            List<ItemValue> itemValues = new ArrayList<ItemValue>();
            for (LegacyItemValue legacyItemValue : getLegacyEntity().getItemValues()) {
                itemValues.add(ItemValue.getItemValue(legacyItemValue));
            }
            return itemValues;
        } else {
            List<ItemValue> itemValues = new ArrayList<ItemValue>();
            for (BaseItemValue baseItemValue : getItemService().getItemValues(getNuEntity())) {
                itemValues.add(ItemValue.getItemValue(baseItemValue));
            }
            return itemValues;
        }
    }

    public List<ItemValue> getAllItemValues(String itemValuePath) {
        if (isLegacy()) {
            List<ItemValue> itemValues = new ArrayList<ItemValue>();
            for (LegacyItemValue legacyItemValue : getLegacyEntity().getAllItemValues(itemValuePath)) {
                itemValues.add(ItemValue.getItemValue(legacyItemValue));
            }
            return itemValues;
        } else {
            List<ItemValue> itemValues = new ArrayList<ItemValue>();
            for (BaseItemValue baseItemValue : getItemService().getAllItemValues(getNuEntity(), itemValuePath)) {
                itemValues.add(ItemValue.getItemValue(baseItemValue));
            }
            return itemValues;
        }
    }

    @Deprecated
    public ItemValueMap getItemValuesMap() {
        if (isLegacy()) {
            return ItemValueMap.getItemValueMap(getLegacyEntity().getItemValuesMap());
        } else {
            return ItemValueMap.getItemValueMap(getNuEntity().getItemValuesMap());
        }
    }

    public ItemValue getItemValue(String identifier, Date startDate) {
        if (isLegacy()) {
            return ItemValue.getItemValue(getLegacyEntity().getItemValue(identifier, startDate));
        } else {
            return ItemValue.getItemValue(getItemService().getItemValue(getNuEntity(), identifier, startDate));
        }
    }

    public ItemValue getItemValue(String identifier) {
        if (isLegacy()) {
            return ItemValue.getItemValue(getLegacyEntity().getItemValue(identifier));
        } else {
            return ItemValue.getItemValue(getItemService().getItemValue(getNuEntity(), identifier));
        }
    }

    public void appendInternalValues(Map<ItemValueDefinition, InternalValue> values) {
        if (isLegacy()) {
            getLegacyEntity().appendInternalValues(values);
        } else {
            getItemService().appendInternalValues(getNuEntity(), values);
        }
    }

    public void setName(String name) {
        if (isLegacy()) {
            getLegacyEntity().setName(name);
        } else {
            getNuEntity().setName(name);
        }
    }

    public abstract StartEndDate getStartDate();

    public abstract StartEndDate getEndDate();

    public boolean supportsCalculation() {
        if (isLegacy()) {
            return getLegacyEntity().supportsCalculation();
        } else {
            return getNuEntity().supportsCalculation();
        }
    }

    public boolean isWithinLifeTime(Date date) {
        if (isLegacy()) {
            return getLegacyEntity().isWithinLifeTime(date);
        } else {

            // Data or Profile?
            if (NuDataItem.class.isAssignableFrom(getNuEntity().getClass())) {
                // Data items don't have start dates
                return true;
            } else if (NuProfileItem.class.isAssignableFrom(getNuEntity().getClass())) {
                NuProfileItem profileItem = (NuProfileItem) getNuEntity();
                return profileItem.isWithinLifeTime(date);
            } else {
                throw new IllegalStateException("Item should be either a NuDataItem or NuProfileItem.");
            }
        }
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        if (isLegacy()) {
            getLegacyEntity().setEffectiveStartDate(effectiveStartDate);
        } else {
            getNuEntity().setEffectiveStartDate(effectiveStartDate);
        }
    }

    public Date getEffectiveStartDate() {
        if (isLegacy()) {
            return getLegacyEntity().getEffectiveStartDate();
        } else {
            return getNuEntity().getEffectiveStartDate();
        }
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        if (isLegacy()) {
            getLegacyEntity().setEffectiveEndDate(effectiveEndDate);
        } else {
            getNuEntity().setEffectiveEndDate(effectiveEndDate);
        }
    }

    public Date getEffectiveEndDate() {
        if (isLegacy()) {
            return getLegacyEntity().getEffectiveEndDate();
        } else {
            return getNuEntity().getEffectiveEndDate();
        }
    }

    public Duration getDuration() {
        if (isLegacy()) {
            return getLegacyEntity().getDuration();
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    public boolean isUnique(ItemValueDefinition itemValueDefinition, StartEndDate startDate) {
        if (isLegacy()) {
            return getLegacyEntity().isUnique(itemValueDefinition, startDate);
        } else {
            return getItemService().isUnique(getNuEntity(), itemValueDefinition, startDate);
        }
    }

    public abstract LegacyItem getLegacyEntity();

    public abstract BaseItem getNuEntity();

    public abstract IItemService getItemService();
}