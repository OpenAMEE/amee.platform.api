package com.amee.domain;

import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.DataItem;

import java.util.Date;

public class DataItemValuesFilter extends LimitFilter {

    private static final long serialVersionUID = 4120820672227897639L;
    private Date startDate = DataItemService.MYSQL_MIN_DATETIME;
    private Date endDate = DataItemService.MYSQL_MAX_DATETIME;
    private DataItem dataItem;
    private ItemValueDefinition itemValueDefinition;

    public DataItemValuesFilter() {
        super();
    }

    @Override
    public int getResultLimitDefault() {
        return 50;
    }

    @Override
    public int getResultLimitMax() {
        return 100;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (startDate == null) {
            startDate = DataItemService.MYSQL_MIN_DATETIME;
        }
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        if (endDate == null) {
            endDate = DataItemService.MYSQL_MAX_DATETIME;
        }
        this.endDate = endDate;
    }

    public DataItem getDataItem() {
        return dataItem;
    }

    public void setDataItem(DataItem dataItem) {
        this.dataItem = dataItem;
    }

    public ItemValueDefinition getItemValueDefinition() {
        return itemValueDefinition;
    }

    public void setItemValueDefinition(ItemValueDefinition itemValueDefinition) {
        this.itemValueDefinition = itemValueDefinition;
    }
}