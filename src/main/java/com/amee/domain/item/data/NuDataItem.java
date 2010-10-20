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
package com.amee.domain.item.data;

import com.amee.domain.AMEEStatus;
import com.amee.domain.Metadata;
import com.amee.domain.ObjectType;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.DataItem;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.BaseItem;
import com.amee.platform.science.StartEndDate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Table(name = "DATA_ITEM")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class NuDataItem extends BaseItem {

    // The UNIX time epoch, which is 1970-01-01 00:00:00. See: http://en.wikipedia.org/wiki/Unix_epoch
    // Copied from LegacyDataItem
    public final static Date EPOCH = new Date(0);

    public final static int NAME_MAX_SIZE = 255;
    public final static int PATH_MAX_SIZE = 255;
    public final static int WIKI_DOC_MAX_SIZE = Metadata.VALUE_MAX_SIZE;
    public final static int PROVENANCE_MAX_SIZE = 255;

    // The earliest possible DataItemValue date
    private static final Date AD1000 = new DateTime(1000, 1, 1, 0, 0, 0, 0).toDate();

    @Column(name = "PATH", length = PATH_MAX_SIZE, nullable = false)
    private String path = "";

    @Transient
    private transient DataItem adapter;

    public NuDataItem() {
        super();
    }

    public NuDataItem(DataCategory dataCategory, ItemDefinition itemDefinition) {
        super(dataCategory, itemDefinition);
    }

    protected void copyTo(NuDataItem o) {
        super.copyTo(o);
        o.path = path;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || getDataCategory().isTrash() || getItemDefinition().isTrash();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        if (path == null) {
            path = "";
        }
        this.path = path;
    }

    public String getWikiDoc() {
        return getMetadataValue("wikiDoc");
    }

    public void setWikiDoc(String wikiDoc) {
        getOrCreateMetadata("wikiDoc").setValue(wikiDoc);
        onModify();
    }

    public String getProvenance() {
        return getMetadataValue("provenance");
    }

    public void setProvenance(String provenance) {
        getOrCreateMetadata("provenance").setValue(provenance);
        onModify();
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.NDI;
    }

    public DataItem getAdapter() {
        return adapter;
    }

    public void setAdapter(DataItem adapter) {
        this.adapter = adapter;
    }

    /**
     * A DataItem always has the epoch as the startDate.
     * Copied from LegacyDataItem
     *
     * @return EPOCH
     */
    public StartEndDate getStartDate() {
        return new StartEndDate(EPOCH);
    }

    /**
     * A DataItem never has an endDate.
     * Copied from LegacyDataItem
     *
     * @return null
     */
    public StartEndDate getEndDate() {
        return null;
    }

}