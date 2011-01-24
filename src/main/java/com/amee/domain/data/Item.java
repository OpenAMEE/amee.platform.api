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
import com.amee.domain.profile.ProfileItem;
import com.amee.platform.science.StartEndDate;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class Item extends AMEEEntityAdapter implements Pathable {

    public final static int NAME_MAX_SIZE = NuDataItem.NAME_MAX_SIZE;

    public Item() {
        super();
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
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getItemService().addItemValue(itemValue.getNuEntity());
        }
    }

    public Set<ItemValueDefinition> getItemValueDefinitions() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getItemService().getItemValueDefinitionsInUse(getNuEntity());
        }
    }

    public List<IAMEEEntityReference> getHierarchy() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getHierarchy();
        }
    }

    public ItemDefinition getItemDefinition() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getItemDefinition();
        }
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setItemDefinition(itemDefinition);
        }
    }

    public DataCategory getDataCategory() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getDataCategory();
        }
    }

    public void setDataCategory(DataCategory dataCategory) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setDataCategory(dataCategory);
        }
    }

    public String getName() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getName();
        }
    }

    public String getDisplayName() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getDisplayName();
        }
    }

    public String getDisplayPath() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getDisplayPath();
        }
    }

    public String getFullPath() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getFullPath();
        }
    }

    public List<ItemValue> getItemValues() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
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
            throw new IllegalStateException("Legacy entities are no longer supported.");
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
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return ItemValueMap.getItemValueMap(getItemService().getItemValuesMap(getNuEntity()));
        }
    }

    public ItemValue getItemValue(String identifier, Date startDate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return ItemValue.getItemValue(getItemService().getItemValue(getNuEntity(), identifier, startDate));
        }
    }

    public ItemValue getItemValue(String identifier) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return ItemValue.getItemValue(getItemService().getItemValue(getNuEntity(), identifier));
        }
    }

    public void setName(String name) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setName(name);
        }
    }

    public abstract StartEndDate getStartDate();

    public abstract StartEndDate getEndDate();

    public boolean supportsCalculation() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().supportsCalculation();
        }
    }

    public boolean isWithinLifeTime(Date date) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
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
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setEffectiveStartDate(effectiveStartDate);
        }
    }

    public Date getEffectiveStartDate() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getEffectiveStartDate();
        }
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setEffectiveEndDate(effectiveEndDate);
        }
    }

    public Date getEffectiveEndDate() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getEffectiveEndDate();
        }
    }

    public Duration getDuration() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            // Data or Profile?
            if (NuDataItem.class.isAssignableFrom(getNuEntity().getClass())) {
                // Data items don't have start or end dates.
                return null;
            } else if (NuProfileItem.class.isAssignableFrom(getNuEntity().getClass())) {
                NuProfileItem profileItem = (NuProfileItem) getNuEntity();
                return profileItem.getDuration();
            } else {
                throw new IllegalStateException("Item should be either a NuDataItem or NuProfileItem.");
            }
        }
    }

    public boolean isUnique(ItemValueDefinition itemValueDefinition, StartEndDate startDate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getItemService().isUnique(getNuEntity(), itemValueDefinition, startDate);
        }
    }

    public abstract BaseItem getNuEntity();

    public abstract IItemService getItemService();
}