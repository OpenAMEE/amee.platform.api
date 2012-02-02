package com.amee.domain;

import java.util.Date;

public class ProfileItemsFilter extends LimitFilter {

    private Date startDate = new Date();
    private Date endDate = null;

    /**
     * Setting this to 'start' will only include items which start during the query window.
     * Setting 'end' will include only items which end during the window.
     * The default behaviour is to include any item that intersects the query window.
     */
    private String selectBy;

    /**
     * Set the calculation mode used. By default, emission values for items are for the whole item,
     * not just the part of the item that intersects the query window. To get just the emissions that took place
     * during the query window, set this parameter to 'prorata'.
     */
    private String mode;

    @Override
    public int getResultLimitDefault() {
        return 50;
    }

    @Override
    public int getResultLimitMax() {
        return 100;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        if (startDate != null) {
            this.startDate = startDate;
        }
    }

    public String getSelectBy() {
        return selectBy;
    }

    public void setSelectBy(String selectBy) {
        this.selectBy = selectBy;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
