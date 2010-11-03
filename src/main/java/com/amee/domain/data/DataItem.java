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

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.IDataItemService;
import com.amee.domain.ObjectType;
import com.amee.domain.item.data.NuDataItem;
import com.amee.platform.science.StartEndDate;

public class DataItem extends Item {

    public final static int PATH_MAX_SIZE = LegacyDataItem.PATH_MAX_SIZE;
    public final static int WIKI_DOC_MAX_SIZE = LegacyDataItem.WIKI_DOC_MAX_SIZE;
    public final static int PROVENANCE_MAX_SIZE = LegacyDataItem.PROVENANCE_MAX_SIZE;

    public final static boolean USE_NU = true;

    private LegacyDataItem legacyEntity;
    private NuDataItem nuEntity;

    public DataItem() {
        super();
        if (USE_NU) {
            setNuEntity(new NuDataItem());
        } else {
            setLegacyEntity(new LegacyDataItem());
        }
    }

    public DataItem(DataCategory dataCategory, ItemDefinition itemDefinition) {
        super();
        if (USE_NU) {
            setNuEntity(new NuDataItem(dataCategory, itemDefinition));
        } else {
            setLegacyEntity(new LegacyDataItem(dataCategory, itemDefinition));
        }
    }

    public DataItem(LegacyDataItem dataItem) {
        super();
        setLegacyEntity(dataItem);
    }

    public DataItem(NuDataItem dataItem) {
        super();
        setNuEntity(dataItem);
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
            return getItemService().getLabel(getNuEntity());
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
            return new StartEndDate(IDataItemService.EPOCH);
        }
    }

    @Override
    public StartEndDate getEndDate() {
        if (isLegacy()) {
            return getLegacyEntity().getEndDate();
        } else {
            return null;
        }
    }

    @Override
    public ObjectType getObjectType() {
        if (isLegacy()) {
            return ObjectType.DI;
        } else {
            return ObjectType.NDI;
        }
    }

    @Override
    public LegacyDataItem getLegacyEntity() {
        return legacyEntity;
    }

    public void setLegacyEntity(LegacyDataItem legacyEntity) {
        legacyEntity.setAdapter(this);
        this.legacyEntity = legacyEntity;
    }

    @Override
    public NuDataItem getNuEntity() {
        return nuEntity;
    }

    public void setNuEntity(NuDataItem nuEntity) {
        nuEntity.setAdapter(this);
        this.nuEntity = nuEntity;
    }

    public IDataItemService getItemService() {
        return (IDataItemService) ThreadBeanHolder.get("dataItemService");
    }
}