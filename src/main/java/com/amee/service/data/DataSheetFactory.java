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
package com.amee.service.data;

import com.amee.domain.LocaleHolder;
import com.amee.domain.ValueType;
import com.amee.domain.cache.CacheableFactory;
import com.amee.domain.data.*;
import com.amee.domain.sheet.Cell;
import com.amee.domain.sheet.Column;
import com.amee.domain.sheet.Row;
import com.amee.domain.sheet.Sheet;
import com.amee.platform.science.StartEndDate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class DataSheetFactory implements CacheableFactory {

    private final Log log = LogFactory.getLog(getClass());

    private DataService dataService;
    private DataBrowser dataBrowser;
    private String cacheName;

    private DataSheetFactory() {
        super();
    }

    public DataSheetFactory(DataService dataService, DataBrowser dataBrowser, String cacheName) {
        this();
        this.dataService = dataService;
        this.dataBrowser = dataBrowser;
        this.cacheName = cacheName;
    }

    public Object create() {

        log.debug("create()");

        List<Column> columns;
        Row row;
        ItemValue itemValue;
        Sheet sheet = null;
        ItemDefinition itemDefinition;
        DataCategory dataCategory = dataBrowser.getDataCategory();

        // must have an ItemDefinition
        itemDefinition = dataCategory.getItemDefinition();
        if (itemDefinition != null) {

            log.debug("create() - Create Sheet and Columns.");

            // create sheet and columns
            sheet = new Sheet();
            sheet.setKey(getKey());
            sheet.setLabel("DataItems");
            for (ItemValueDefinition itemValueDefinition : itemDefinition.getItemValueDefinitions()) {
                if (itemValueDefinition.isFromData()) {
                    new Column(sheet, itemValueDefinition.getPath(), itemValueDefinition.getName());
                }
            }
            new Column(sheet, "label");
            new Column(sheet, "path");
            new Column(sheet, "uid", true);
            new Column(sheet, "created", true);
            new Column(sheet, "modified", true);
            new Column(sheet, "startDate");
            new Column(sheet, "endDate");

            log.debug("create() - Create Rows and Cells.");

            // create rows and cells
            columns = sheet.getColumns();
            StartEndDate startDate = dataBrowser.getQueryStartDate();
            for (DataItem dataItem : dataService.getDataItems(dataCategory)) {
                row = new Row(sheet, dataItem.getUid());
                row.setLabel("DataItem");
                for (Column column : columns) {
                    itemValue = dataItem.getItemValue(column.getName(), startDate);
                    if (itemValue != null) {
                        new Cell(column, row, itemValue.getValue(), itemValue.getUid(), itemValue.getItemValueDefinition().getValueDefinition().getValueType());
                    } else if ("label".equalsIgnoreCase(column.getName())) {
                        new Cell(column, row, dataItem.getLabel(), ValueType.TEXT);
                    } else if ("path".equalsIgnoreCase(column.getName())) {
                        new Cell(column, row, dataItem.getDisplayPath(), ValueType.TEXT);
                    } else if ("uid".equalsIgnoreCase(column.getName())) {
                        new Cell(column, row, dataItem.getUid(), ValueType.TEXT);
                    } else if ("created".equalsIgnoreCase(column.getName())) {
                        new Cell(column, row, dataItem.getCreated(), ValueType.DATE);
                    } else if ("modified".equalsIgnoreCase(column.getName())) {
                        new Cell(column, row, dataItem.getModified(), ValueType.DATE);
                    } else if ("startDate".equalsIgnoreCase(column.getName())) {
                        new Cell(column, row, dataItem.getStartDate(), ValueType.DATE);
                    } else if ("endDate".equalsIgnoreCase(column.getName())) {
                        new Cell(column, row, dataItem.getEndDate(), ValueType.DATE);
                    } else {
                        // add empty cell
                        new Cell(column, row);
                    }
                }
            }

            log.debug("create() - Do sorts.");

            // sort columns and rows in sheet
            sheet.setDisplayBy(itemDefinition.getDrillDown());
            sheet.sortColumns();
            sheet.setSortBy(itemDefinition.getDrillDown());
            sheet.sortRows();
        }

        log.debug("create() - Done.");

        return sheet;
    }

    public String getKey() {
        return "DataSheet_" + dataBrowser.getDataCategory().getUid() +
                "_" +
                dataBrowser.getQueryStartDate() +
                "_" +
                ((dataBrowser.getQueryEndDate() != null) ? dataBrowser.getQueryEndDate() : "") +
                "_" +
                LocaleHolder.getLocale();
    }

    public String getCacheName() {
        return cacheName;
    }
}