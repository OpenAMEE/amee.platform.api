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

import com.amee.domain.ObjectType;
import com.amee.platform.science.StartEndDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Configurable(autowire = Autowire.BY_TYPE)
public class DataItem extends Item {

    private LegacyDataItem legacyEntity;

    public DataItem() {
        super();
        setLegacyEntity(new LegacyDataItem());
        getLegacyEntity().setAdapter(this);
    }

    public DataItem(DataCategory dataCategory, ItemDefinition itemDefinition) {
        super();
        setLegacyEntity(new LegacyDataItem(dataCategory, itemDefinition));
        getLegacyEntity().setAdapter(this);
    }

    public DataItem(LegacyDataItem dataItem) {
        super();
        setLegacyEntity(dataItem);
        getLegacyEntity().setAdapter(this);
    }

    public static DataItem getDataItem(LegacyDataItem legacyDataItem) {
        if (legacyDataItem != null) {
            if (legacyDataItem.getAdapter() != null) {
                return legacyDataItem.getAdapter();
            } else {
                return new DataItem(legacyDataItem);
            }
        } else {
            return null;
        }
    }

    public String getLabel() {
        return getLegacyEntity().getLabel();
    }

    public JSONObject getJSONObject(boolean detailed, boolean showHistory) throws JSONException {
        return getLegacyEntity().getJSONObject(detailed, showHistory);
    }

    @Override
    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        return getLegacyEntity().getJSONObject(detailed);
    }

    public Element getElement(Document document, boolean detailed, boolean showHistory) {
        return getLegacyEntity().getElement(document, detailed, showHistory);
    }

    public Element getElement(Document document, boolean detailed) {
        return getLegacyEntity().getElement(document, detailed);
    }

    @Override
    public String getPath() {
        return getLegacyEntity().getPath();
    }

    public void setPath(String path) {
        getLegacyEntity().setPath(path);
    }

    public String getWikiDoc() {
        return getLegacyEntity().getWikiDoc();
    }

    public void setWikiDoc(String wikiDoc) {
        getLegacyEntity().setWikiDoc(wikiDoc);
    }

    public String getProvenance() {
        return getLegacyEntity().getProvenance();
    }

    public void setProvenance(String provenance) {
        getLegacyEntity().setProvenance(provenance);
    }

    public ObjectType getObjectType() {
        return ObjectType.DI;
    }

    @Override
    public boolean isTrash() {
        return getLegacyEntity().isTrash();
    }

    @Override
    public StartEndDate getStartDate() {
        return getLegacyEntity().getStartDate();
    }

    @Override
    public StartEndDate getEndDate() {
        return getLegacyEntity().getEndDate();
    }

    public LegacyDataItem getLegacyEntity() {
        return legacyEntity;
    }

    public void setLegacyEntity(LegacyDataItem legacyEntity) {
        this.legacyEntity = legacyEntity;
    }
}