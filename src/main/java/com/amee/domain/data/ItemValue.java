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
import com.amee.domain.Builder;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;
import com.amee.domain.item.BaseItemValue;
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
        setLegacyEntity(new LegacyItemValue());
        getLegacyEntity().setAdapter(this);
    }

    public ItemValue(ItemValueDefinition itemValueDefinition, Item item, String value) {
        super();
        setLegacyEntity(new LegacyItemValue(itemValueDefinition, item.getLegacyEntity(), value));
        getLegacyEntity().setAdapter(this);
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
        return ItemValue.getItemValue(getLegacyEntity().getCopy());
    }

    @Override
    public String getUsableValue() {
        return getLegacyEntity().getUsableValue();
    }

    public boolean isUsableValue() {
        return getLegacyEntity().isUsableValue();
    }

    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        return getLegacyEntity().getJSONObject(detailed);
    }

    public JSONObject getJSONObject() throws JSONException {
        return getLegacyEntity().getJSONObject();
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        return getLegacyEntity().getIdentityJSONObject();
    }

    public Element getElement(Document document) {
        return getLegacyEntity().getElement(document);
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
        return getLegacyEntity().getIdentityElement(document);
    }

    public List<IAMEEEntityReference> getHierarchy() {
        return getLegacyEntity().getHierarchy();
    }

    @Override
    public String getName() {
        return getLegacyEntity().getName();
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
        return getLegacyEntity().getDisplayPath();
    }

    @Override
    public String getFullPath() {
        return getLegacyEntity().getFullPath();
    }

    public ItemValueDefinition getItemValueDefinition() {
        return getLegacyEntity().getItemValueDefinition();
    }

    public void setItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        getLegacyEntity().setItemValueDefinition(itemValueDefinition);
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
            // TODO
            throw new UnsupportedOperationException();
        }
    }

    public String getValue() {
        return getLegacyEntity().getValue();
    }

    public void setValue(String value) {
        getLegacyEntity().setValue(value);
    }

    @Override
    public StartEndDate getStartDate() {
        return getLegacyEntity().getStartDate();
    }

    public void setStartDate(Date startDate) {
        getLegacyEntity().setStartDate(startDate);
    }

    @Override
    public boolean isDouble() {
        return getLegacyEntity().isDouble();
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.IV;
    }

    @Override
    public AmountUnit getUnit() {
        return getLegacyEntity().getUnit();
    }

    @Override
    public AmountUnit getCanonicalUnit() {
        return getLegacyEntity().getCanonicalUnit();
    }

    public void setUnit(String unit) throws IllegalArgumentException {
        getLegacyEntity().setUnit(unit);
    }

    @Override
    public AmountPerUnit getPerUnit() {
        return getLegacyEntity().getPerUnit();
    }

    @Override
    public AmountPerUnit getCanonicalPerUnit() {
        return getLegacyEntity().getCanonicalPerUnit();
    }

    public void setPerUnit(String perUnit) throws IllegalArgumentException {
        getLegacyEntity().setPerUnit(perUnit);
    }

    @Override
    public AmountCompoundUnit getCompoundUnit() {
        return getLegacyEntity().getCompoundUnit();
    }

    @Override
    public AmountCompoundUnit getCanonicalCompoundUnit() {
        return getLegacyEntity().getCanonicalCompoundUnit();
    }

    @Override
    public boolean hasUnit() {
        return getLegacyEntity().hasUnit();
    }

    @Override
    public boolean hasPerUnit() {
        return getLegacyEntity().hasPerUnit();
    }

    public boolean hasPerTimeUnit() {
        return getLegacyEntity().hasPerTimeUnit();
    }

    public boolean isNonZero() {
        return getLegacyEntity().isNonZero();
    }

    @Override
    public String getLabel() {
        return getLegacyEntity().getLabel();
    }

    @Override
    public boolean isTrash() {
        return getLegacyEntity().isTrash();
    }

    @Override
    public boolean isConvertible() {
        return getLegacyEntity().isConvertible();
    }

    public void setBuilder(Builder builder) {
        getLegacyEntity().setBuilder(builder);
    }

    public boolean isHistoryAvailable() {
        return getLegacyEntity().isHistoryAvailable();
    }

    public void setHistoryAvailable(boolean historyAvailable) {
        getLegacyEntity().setHistoryAvailable(historyAvailable);
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