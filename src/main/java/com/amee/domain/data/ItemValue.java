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

import com.amee.domain.*;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.HistoryValue;
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.data.*;
import com.amee.domain.item.profile.BaseProfileItemValue;
import com.amee.domain.item.profile.NuProfileItem;
import com.amee.domain.item.profile.ProfileItemNumberValue;
import com.amee.domain.item.profile.ProfileItemTextValue;
import com.amee.domain.path.Pathable;
import com.amee.platform.science.*;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

public class ItemValue extends AMEEEntityAdapter implements Pathable, ExternalValue {

    private BaseItemValue nuEntity;

    /**
     * This value can be used to override the persisted value.
     */
    @Transient
    private transient Object valueOverride;

    public ItemValue() {
        super();
    }

    public ItemValue(ItemValueDefinition itemValueDefinition, Item item, boolean isHistory) {
        super();
        if (item.isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            BaseItemValue itemValue;
            // Data or Profile?
            if (NuDataItem.class.isAssignableFrom(item.getNuEntity().getClass())) {
                // Work with DataItems.
                NuDataItem dataItem = (NuDataItem) item.getNuEntity();
                // Create a nu style value.
                if (itemValueDefinition.getValueDefinition().getValueType().equals(ValueType.INTEGER) ||
                        itemValueDefinition.getValueDefinition().getValueType().equals(ValueType.DOUBLE)) {
                    // Item is a number.
                    if (isHistory) {
                        itemValue = new DataItemNumberValueHistory(itemValueDefinition, dataItem);
                    } else {
                        itemValue = new DataItemNumberValue(itemValueDefinition, dataItem);
                    }
                } else {
                    // Item is text.
                    if (isHistory) {
                        itemValue = new DataItemTextValueHistory(itemValueDefinition, dataItem);
                    } else {
                        itemValue = new DataItemTextValue(itemValueDefinition, dataItem);
                    }
                }
            } else if (NuProfileItem.class.isAssignableFrom(item.getNuEntity().getClass())) {
                // Work with ProfileItems.
                NuProfileItem profileItem = (NuProfileItem) item.getNuEntity();
                // Create a nu style value.
                if (itemValueDefinition.getValueDefinition().getValueType().equals(ValueType.INTEGER) ||
                        itemValueDefinition.getValueDefinition().getValueType().equals(ValueType.DOUBLE)) {
                    // Item is a number.
                    itemValue = new ProfileItemNumberValue(itemValueDefinition, profileItem);
                } else {
                    // Item is text.
                    itemValue = new ProfileItemTextValue(itemValueDefinition, profileItem);
                }
            } else {
                throw new IllegalStateException("Item should be either a NuDataItem or NuProfileItem.");
            }
            setNuEntity(itemValue);
            getNuEntity().setAdapter(this);
        }
    }

    public ItemValue(ItemValueDefinition itemValueDefinition, Item item, String value) {
        this(itemValueDefinition, item, false);
        setValue(value);
    }

    public ItemValue(ItemValueDefinition itemValueDefinition, Item item, String value, boolean isHistory) {
        this(itemValueDefinition, item, isHistory);
        setValue(value);
    }

    public ItemValue(BaseItemValue itemValue) {
        super();
        setNuEntity(itemValue);
        getNuEntity().setAdapter(this);
    }

    public static ItemValue getItemValue(BaseItemValue baseItemValue) {
        if (baseItemValue != null) {
            if (baseItemValue.getAdapter() != null) {
                return baseItemValue.getAdapter();
            } else {
                return new ItemValue(baseItemValue);
            }
        } else {
            return null;
        }
    }

    public ItemValue getCopy() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return ItemValue.getItemValue(getNuEntity().getCopy());
        }
    }

    @Override
    public String getUsableValue() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getUsableValue();
        }
    }

    public boolean isUsableValue() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().isUsableValue();
        }
    }

    public List<IAMEEEntityReference> getHierarchy() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getHierarchy();
        }
    }

    @Override
    public String getName() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getName();
        }
    }

    @Override
    public String getDisplayName() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getDisplayName();
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

    @Override
    public String getDisplayPath() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getDisplayPath();
        }
    }

    @Override
    public String getFullPath() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getFullPath();
        }
    }

    public ItemValueDefinition getItemValueDefinition() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getItemValueDefinition();
        }
    }

    public void setItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setItemValueDefinition(itemValueDefinition);
        }
    }

    public Item getItem() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return Item.getItem(getNuEntity().getItem());
        }
    }

    public void setItem(Item item) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setItem(item.getNuEntity());
        }
    }

    public String getValue() {
        if (valueOverride != null) {
            return valueOverride.toString();
        }

        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getValueAsString();
        }
    }

    public void setValue(String value) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setValue(value);
        }
    }

    @Override
    public StartEndDate getStartDate() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (BaseProfileItemValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((BaseProfileItemValue) getNuEntity()).getProfileItem().getStartDate();
            } else if (BaseDataItemValue.class.isAssignableFrom(getNuEntity().getClass())) {
                if (ExternalHistoryValue.class.isAssignableFrom(getNuEntity().getClass())) {
                    return ((ExternalHistoryValue) getNuEntity()).getStartDate();
                } else {
                    return new StartEndDate(IDataItemService.EPOCH);
                }
            } else {
                throw new IllegalStateException("A BaseProfileItemValue or BaseDataItemValue instance was expected.");
            }
        }
    }

    public void setStartDate(Date startDate) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (BaseDataItemValue.class.isAssignableFrom(getNuEntity().getClass())) {
                if (HistoryValue.class.isAssignableFrom(getNuEntity().getClass())) {
                    ((HistoryValue) getNuEntity()).setStartDate(startDate);
                }
            } else {
                throw new IllegalStateException("A BaseDataItemValue instance was expected.");
            }
        }
    }

    @Override
    public boolean isDouble() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().isDouble();
        }
    }

    @Override
    public AmountUnit getUnit() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getUnit();
            } else {
                return getItemValueDefinition().getUnit();
            }
        }
    }

    @Override
    public AmountUnit getCanonicalUnit() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getCanonicalUnit();
            } else {
                return getItemValueDefinition().getUnit();
            }
        }
    }

    public void setUnit(String unit) throws IllegalArgumentException {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                ((NumberValue) getNuEntity()).setUnit(unit);
            } else {
                throw new IllegalStateException("A NumberValue was expected.");
            }
        }
    }

    @Override
    public AmountPerUnit getPerUnit() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getPerUnit();
            } else {
                return getItemValueDefinition().getPerUnit();
            }
        }
    }

    @Override
    public AmountPerUnit getCanonicalPerUnit() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getCanonicalPerUnit();
            } else {
                return getItemValueDefinition().getPerUnit();
            }
        }
    }

    public void setPerUnit(String perUnit) throws IllegalArgumentException {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                ((NumberValue) getNuEntity()).setPerUnit(perUnit);
            } else {
                throw new IllegalStateException("A NumberValue was expected.");
            }
        }
    }

    @Override
    public AmountCompoundUnit getCompoundUnit() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getCompoundUnit();
            } else {
                return getUnit().with(getPerUnit());
            }
        }
    }

    @Override
    public AmountCompoundUnit getCanonicalCompoundUnit() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getCanonicalCompoundUnit();
            } else {
                return getItemValueDefinition().getCanonicalCompoundUnit();
            }
        }
    }

    @Override
    public boolean hasUnit() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return NumberValue.class.isAssignableFrom(getNuEntity().getClass()) &&
                    ((NumberValue) getNuEntity()).hasUnit();
        }
    }

    @Override
    public boolean hasPerUnit() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return NumberValue.class.isAssignableFrom(getNuEntity().getClass()) &&
                    ((NumberValue) getNuEntity()).hasPerUnit();
        }
    }

    public boolean hasPerTimeUnit() {
        return hasPerUnit() && getPerUnit().isTime();
    }

    public boolean isNonZero() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().isNonZero();
        }
    }

    @Override
    public String getLabel() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().getLabel();
        }
    }

    @Override
    public boolean isTrash() {
        return getAdaptedEntity().isTrash();
    }

    @Override
    public boolean isConvertible() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().isConvertible();
        }
    }

    public boolean isHistoryAvailable() {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            return getNuEntity().isHistoryAvailable();
        }
    }

    public void setHistoryAvailable(boolean historyAvailable) {
        if (isLegacy()) {
            throw new IllegalStateException("Legacy entities are no longer supported.");
        } else {
            getNuEntity().setHistoryAvailable(historyAvailable);
        }
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.IV;
    }

    public BaseItemValue getNuEntity() {
        return nuEntity;
    }

    public void setNuEntity(BaseItemValue nuEntity) {
        this.nuEntity = nuEntity;
    }

    public Object getValueOverride() {
        return valueOverride;
    }

    public void setValueOverride(Object valueOverride) {
        this.valueOverride = valueOverride;
    }
}