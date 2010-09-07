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

import com.amee.domain.IDataItemService;
import com.amee.domain.IItemService;
import com.amee.domain.ObjectType;
import com.amee.domain.item.data.NuDataItem;
import com.amee.platform.science.StartEndDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Configurable(autowire = Autowire.BY_TYPE)
public class DataItem extends Item {

    public final static int PATH_MAX_SIZE = LegacyDataItem.PATH_MAX_SIZE;
    public final static int WIKI_DOC_MAX_SIZE = LegacyDataItem.WIKI_DOC_MAX_SIZE;
    public final static int PROVENANCE_MAX_SIZE = LegacyDataItem.PROVENANCE_MAX_SIZE;

    @Autowired
    private IDataItemService dataItemService;

    private LegacyDataItem legacyEntity;
    private NuDataItem nuEntity;

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

    public DataItem(NuDataItem dataItem) {
        super();
        setNuEntity(dataItem);
        getNuEntity().setAdapter(this);
    }

    public static DataItem getDataItem(LegacyDataItem dataItem) {
        if (dataItem != null) {
            if (dataItem.getAdapter() != null) {
                return dataItem.getAdapter();
            } else {
                return new DataItem(dataItem);
            }
        } else {
            return null;
        }
    }

    public static DataItem getDataItem(NuDataItem dataItem) {
        if (dataItem != null) {
            if (dataItem.getAdapter() != null) {
                return dataItem.getAdapter();
            } else {
                return new DataItem(dataItem);
            }
        } else {
            return null;
        }
    }

    public String getLabel() {
        if (isLegacy()) {
            return getLegacyEntity().getLabel();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public JSONObject getJSONObject(boolean detailed, boolean showHistory) throws JSONException {
        if (isLegacy()) {
            return getLegacyEntity().getJSONObject(detailed, showHistory);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public JSONObject getJSONObject(boolean detailed) throws JSONException {
        if (isLegacy()) {
            return getLegacyEntity().getJSONObject(detailed);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Element getElement(Document document, boolean detailed, boolean showHistory) {
        if (isLegacy()) {
            return getLegacyEntity().getElement(document, detailed, showHistory);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Element getElement(Document document, boolean detailed) {
        if (isLegacy()) {
            return getLegacyEntity().getElement(document, detailed);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String getPath() {
        if (isLegacy()) {
            return getLegacyEntity().getPath();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void setPath(String path) {
        if (isLegacy()) {
            getLegacyEntity().setPath(path);
        } else {
            getNuEntity().setPath(path);
        }
    }

    public String getWikiDoc() {
        if (isLegacy()) {
            return getLegacyEntity().getWikiDoc();
        } else {
            return getNuEntity().getWikiDoc();
        }
    }

    public void setWikiDoc(String wikiDoc) {
        if (isLegacy()) {
            getLegacyEntity().setWikiDoc(wikiDoc);
        } else {
            getNuEntity().setWikiDoc(wikiDoc);
        }
    }

    public String getProvenance() {
        if (isLegacy()) {
            return getLegacyEntity().getProvenance();
        } else {
            return getNuEntity().getProvenance();
        }
    }

    public void setProvenance(String provenance) {
        if (isLegacy()) {
            getLegacyEntity().setProvenance(provenance);
        } else {
            getNuEntity().setProvenance(provenance);
        }
    }

    @Override
    public boolean isTrash() {
        if (isLegacy()) {
            return getLegacyEntity().isTrash();
        } else {
            return getNuEntity().isTrash();
        }
    }

    @Override
    public StartEndDate getStartDate() {
        if (isLegacy()) {
            return getLegacyEntity().getStartDate();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public StartEndDate getEndDate() {
        if (isLegacy()) {
            return getLegacyEntity().getEndDate();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.DI;
    }

    @Override
    public LegacyDataItem getLegacyEntity() {
        return legacyEntity;
    }

    public void setLegacyEntity(LegacyDataItem legacyEntity) {
        this.legacyEntity = legacyEntity;
    }

    @Override
    public NuDataItem getNuEntity() {
        return nuEntity;
    }

    public void setNuEntity(NuDataItem nuEntity) {
        this.nuEntity = nuEntity;
    }

    public IItemService getItemService() {
        return dataItemService;
    }
}