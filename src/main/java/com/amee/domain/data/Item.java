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
import com.amee.domain.path.Pathable;
import com.amee.platform.science.InternalValue;
import com.amee.platform.science.StartEndDate;
import org.joda.time.Duration;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

public abstract class Item extends AMEEEntityAdapter implements Pathable {

    public final static int NAME_MAX_SIZE = LegacyItem.NAME_MAX_SIZE;

    public Item() {
        super();
    }

    public void addItemValue(ItemValue itemValue) {
        getLegacyEntity().addItemValue(itemValue.getLegacyEntity());
    }

    public Set<ItemValueDefinition> getItemValueDefinitions() {
        return getLegacyEntity().getItemValueDefinitions();
    }

    public JSONObject getIdentityJSONObject() throws JSONException {
        return getLegacyEntity().getIdentityJSONObject();
    }

    public abstract JSONObject getJSONObject(boolean detailed) throws JSONException;

    public Element getIdentityElement(Document document) {
        return getLegacyEntity().getIdentityElement(document);
    }

    public List<IAMEEEntityReference> getHierarchy() {
        return getLegacyEntity().getHierarchy();
    }

    public ItemDefinition getItemDefinition() {
        return getLegacyEntity().getItemDefinition();
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        getLegacyEntity().setItemDefinition(itemDefinition);
    }

    public DataCategory getDataCategory() {
        return getLegacyEntity().getDataCategory();
    }

    public void setDataCategory(DataCategory dataCategory) {
        getLegacyEntity().setDataCategory(dataCategory);
    }

    public String getName() {
        return getLegacyEntity().getName();
    }

    public String getDisplayName() {
        return getLegacyEntity().getDisplayName();
    }

    public String getDisplayPath() {
        return getLegacyEntity().getDisplayPath();
    }

    public String getFullPath() {
        return getLegacyEntity().getFullPath();
    }

    public List<ItemValue> getItemValues() {
        List<ItemValue> itemValues = new ArrayList<ItemValue>();
        for (LegacyItemValue legacyItemValue : getLegacyEntity().getItemValues()) {
            itemValues.add(ItemValue.getItemValue(legacyItemValue));
        }
        return itemValues;
    }

    public List<ItemValue> getAllItemValues(String itemValuePath) {
        List<ItemValue> itemValues = new ArrayList<ItemValue>();
        for (LegacyItemValue legacyItemValue : getLegacyEntity().getAllItemValues(itemValuePath)) {
            itemValues.add(ItemValue.getItemValue(legacyItemValue));
        }
        return itemValues;
    }

    @Deprecated
    public ItemValueMap getItemValuesMap() {
        throw new UnsupportedOperationException();
    }

    public ItemValue getItemValue(String identifier, Date startDate) {
        return ItemValue.getItemValue(getLegacyEntity().getItemValue(identifier, startDate));
    }

    public ItemValue getItemValue(String identifier) {
        return ItemValue.getItemValue(getLegacyEntity().getItemValue(identifier));
    }

    public void appendInternalValues(Map<ItemValueDefinition, InternalValue> values) {
        getLegacyEntity().appendInternalValues(values);
    }

    public void setName(String name) {
        getLegacyEntity().setName(name);
    }

    public abstract StartEndDate getStartDate();

    public abstract StartEndDate getEndDate();

    public boolean supportsCalculation() {
        return getLegacyEntity().supportsCalculation();
    }

    public boolean isWithinLifeTime(Date date) {
        return getLegacyEntity().isWithinLifeTime(date);
    }

    public void setEffectiveStartDate(Date effectiveStartDate) {
        getLegacyEntity().setEffectiveStartDate(effectiveStartDate);
    }

    public Date getEffectiveStartDate() {
        return getLegacyEntity().getEffectiveStartDate();
    }

    public void setEffectiveEndDate(Date effectiveEndDate) {
        getLegacyEntity().setEffectiveEndDate(effectiveEndDate);
    }

    public Date getEffectiveEndDate() {
        return getLegacyEntity().getEffectiveEndDate();
    }

    public Duration getDuration() {
        return getLegacyEntity().getDuration();
    }

    public boolean isUnique(ItemValueDefinition itemValueDefinition, StartEndDate startDate) {
        return getLegacyEntity().isUnique(itemValueDefinition, startDate);
    }

    public abstract LegacyItem getLegacyEntity();
}