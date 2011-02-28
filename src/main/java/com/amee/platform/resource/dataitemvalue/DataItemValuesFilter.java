package com.amee.platform.resource.dataitemvalue;

import com.amee.platform.search.LimitFilter;

import java.util.Date;

public class DataItemValuesFilter extends LimitFilter {

    private Date startDate;

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
        this.startDate = startDate;
    }
}