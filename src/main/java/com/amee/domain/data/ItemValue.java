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
import com.amee.domain.item.NumberValue;
import com.amee.domain.item.data.DataItemNumberValue;
import com.amee.domain.item.data.DataItemTextValue;
import com.amee.domain.item.data.NuDataItem;
import com.amee.domain.item.profile.NuProfileItem;
import com.amee.domain.item.profile.ProfileItemNumberValue;
import com.amee.domain.item.profile.ProfileItemTextValue;
import com.amee.domain.path.Pathable;
import com.amee.platform.science.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.List;

@Configurable(autowire = Autowire.BY_TYPE)
public class ItemValue extends AMEEEntityAdapter implements Pathable, ExternalValue {

    private LegacyItemValue legacyEntity;
    private BaseItemValue nuEntity;

    public ItemValue() {
        super();
    }

    public ItemValue(ItemValueDefinition itemValueDefinition, Item item) {
        super();
        if (item.isLegacy()) {
            setLegacyEntity(new LegacyItemValue(itemValueDefinition, item.getLegacyEntity()));
            getLegacyEntity().setAdapter(this);
        } else {
            BaseItemValue itemValue;
            // Data or Profile?
            if (NuDataItem.class.isAssignableFrom(item.getNuEntity().getClass())) {
                // Work with DataItems.
                NuDataItem dataItem = (NuDataItem) item.getNuEntity();
                // Create a nu style value.
                if (itemValueDefinition.getValueDefinition().getValueType().equals(ValueType.INTEGER) ||
                        itemValueDefinition.getValueDefinition().getValueType().equals(ValueType.DOUBLE)) {
                    // TODO: Handle DIVH.
                    // Item is a number.
                    itemValue = new DataItemNumberValue(itemValueDefinition, dataItem);
                } else {
                    // TODO: Handle DIVH.
                    // Item is text.
                    itemValue = new DataItemTextValue(itemValueDefinition, dataItem);
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
        this(itemValueDefinition, item);
        setValue(value);
    }

    public ItemValue(LegacyItemValue itemValue) {
        super();
        setLegacyEntity(itemValue);
        getLegacyEntity().setAdapter(this);
    }

    public ItemValue(BaseItemValue itemValue) {
        super();
        setNuEntity(itemValue);
        getNuEntity().setAdapter(this);
    }

    public static ItemValue getItemValue(LegacyItemValue legacyItemValue) {
        if (legacyItemValue != null) {
            if (legacyItemValue.getAdapter() != null) {
                return legacyItemValue.getAdapter();
            } else {
                return new ItemValue(legacyItemValue);
            }
        } else {
            return null;
        }
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
            return ItemValue.getItemValue(getLegacyEntity().getCopy());
        } else {
            throw new UnsupportedOperationException();
            // return ItemValue.getItemValue(getNuEntity().getCopy());
        }
    }

    @Override
    public String getUsableValue() {
        if (isLegacy()) {
            return getLegacyEntity().getUsableValue();
        } else {
            return getNuEntity().getUsableValue();
        }
    }

    public boolean isUsableValue() {
        if (isLegacy()) {
            return getLegacyEntity().isUsableValue();
        } else {
            return getNuEntity().isUsableValue();
        }
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        if (isLegacy()) {
            return getLegacyEntity().getJSONObject(detailed);
        } else {
            return getNuEntity().getJSONObject(detailed);
        }
    }

    public JSONObject getJSONObject() throws JSONException {
        if (isLegacy()) {
            return getLegacyEntity().getJSONObject();
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        if (isLegacy()) {
            return getLegacyEntity().getIdentityJSONObject();
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    public Element getElement(Document document) {
        if (isLegacy()) {
            return getLegacyEntity().getElement(document);
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    public Element getElement(Document document, boolean detailed) {
        if (isLegacy()) {
            return getLegacyEntity().getElement(document, detailed);
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    public Element getIdentityElement(Document document) {
        if (isLegacy()) {
            return getLegacyEntity().getIdentityElement(document);
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    public List<IAMEEEntityReference> getHierarchy() {
        if (isLegacy()) {
            return getLegacyEntity().getHierarchy();
        } else {
            return getNuEntity().getHierarchy();
        }
    }

    @Override
    public String getName() {
        if (isLegacy()) {
            return getLegacyEntity().getName();
        } else {
            return getNuEntity().getName();
        }
    }

    @Override
    public String getDisplayName() {
        if (isLegacy()) {
            return getLegacyEntity().getDisplayName();
        } else {
            return getNuEntity().getDisplayName();
        }
    }

    @Override
    public String getPath() {
        if (isLegacy()) {
            return getLegacyEntity().getPath();
        } else {
            return getNuEntity().getPath();
        }
    }

    @Override
    public String getDisplayPath() {
        if (isLegacy()) {
            return getLegacyEntity().getDisplayPath();
        } else {
            return getNuEntity().getDisplayPath();
        }
    }

    @Override
    public String getFullPath() {
        if (isLegacy()) {
            return getLegacyEntity().getFullPath();
        } else {
            return getNuEntity().getFullPath();
        }
    }

    public ItemValueDefinition getItemValueDefinition() {
        if (isLegacy()) {
            return getLegacyEntity().getItemValueDefinition();
        } else {
            return getNuEntity().getItemValueDefinition();
        }
    }

    public void setItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        if (isLegacy()) {
            getLegacyEntity().setItemValueDefinition(itemValueDefinition);
        } else {
            getNuEntity().setItemValueDefinition(itemValueDefinition);
        }
    }

    public Item getItem() {
        if (isLegacy()) {
            return Item.getItem(getLegacyEntity().getItem());
        } else {
            return Item.getItem(getNuEntity().getItem());
        }
    }

    public void setItem(Item item) {
        if (isLegacy()) {
            getLegacyEntity().setItem(item.getLegacyEntity());
        } else {
            getNuEntity().setItem(item.getNuEntity());
        }
    }

    public String getValue() {
        if (isLegacy()) {
            return getLegacyEntity().getValue();
        } else {
            return getNuEntity().getValueAsString();
        }
    }

    public void setValue(String value) {
        if (isLegacy()) {
            getLegacyEntity().setValue(value);
        } else {
            getNuEntity().setValue(value);
        }
    }

    @Override
    public StartEndDate getStartDate() {
        return getLegacyEntity().getStartDate();
    }

    public void setStartDate(Date startDate) {
        if (isLegacy()) {
            getLegacyEntity().setStartDate(startDate);
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isDouble() {
        if (isLegacy()) {
            return getLegacyEntity().isDouble();
        } else {
            return getNuEntity().isDouble();
        }
    }

    @Override
    public AmountUnit getUnit() {
        if (isLegacy()) {
            return getLegacyEntity().getUnit();
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getUnit();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public AmountUnit getCanonicalUnit() {
        if (isLegacy()) {
            return getLegacyEntity().getCanonicalUnit();
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getCanonicalUnit();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public void setUnit(String unit) throws IllegalArgumentException {
        if (isLegacy()) {
            getLegacyEntity().setUnit(unit);
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                ((NumberValue) getNuEntity()).setUnit(unit);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public AmountPerUnit getPerUnit() {
        if (isLegacy()) {
            return getLegacyEntity().getPerUnit();
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getPerUnit();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public AmountPerUnit getCanonicalPerUnit() {
        if (isLegacy()) {
            return getLegacyEntity().getCanonicalPerUnit();
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getCanonicalPerUnit();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    public void setPerUnit(String perUnit) throws IllegalArgumentException {
        if (isLegacy()) {
            getLegacyEntity().setPerUnit(perUnit);
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                    ((NumberValue) getNuEntity()).setPerUnit(perUnit);
                } else {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public AmountCompoundUnit getCompoundUnit() {
        if (isLegacy()) {
            return getLegacyEntity().getCompoundUnit();
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getCompoundUnit();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public AmountCompoundUnit getCanonicalCompoundUnit() {
        if (isLegacy()) {
            return getLegacyEntity().getCanonicalCompoundUnit();
        } else {
            if (NumberValue.class.isAssignableFrom(getNuEntity().getClass())) {
                return ((NumberValue) getNuEntity()).getCanonicalCompoundUnit();
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }

    @Override
    public boolean hasUnit() {
        if (isLegacy()) {
            return getLegacyEntity().hasUnit();
        } else {
            return NumberValue.class.isAssignableFrom(getNuEntity().getClass()) &&
                    ((NumberValue) getNuEntity()).hasUnit();
        }
    }

    @Override
    public boolean hasPerUnit() {
        if (isLegacy()) {
            return getLegacyEntity().hasPerUnit();
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
            return getLegacyEntity().isNonZero();
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getLabel() {
        if (isLegacy()) {
            return getLegacyEntity().getLabel();
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
            return getLegacyEntity().isConvertible();
        } else {
            return getNuEntity().isConvertible();
        }
    }

    public void setBuilder(Builder builder) {
        if (isLegacy()) {
            getLegacyEntity().setBuilder(builder);
        } else {
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    public boolean isHistoryAvailable() {
        if (isLegacy()) {
            return getLegacyEntity().isHistoryAvailable();
        } else {
            return getNuEntity().isHistoryAvailable();
        }
    }

    public void setHistoryAvailable(boolean historyAvailable) {
        if (isLegacy()) {
            getLegacyEntity().setHistoryAvailable(historyAvailable);
        } else {
            getNuEntity().setHistoryAvailable(historyAvailable);
        }
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.IV;
    }

    public LegacyItemValue getLegacyEntity() {
        return legacyEntity;
    }

    public void setLegacyEntity(LegacyItemValue legacyEntity) {
        legacyEntity.setAdapter(this);
        this.legacyEntity = legacyEntity;
    }

    public BaseItemValue getNuEntity() {
        return nuEntity;
    }

    public void setNuEntity(BaseItemValue nuEntity) {
        this.nuEntity = nuEntity;
    }
}