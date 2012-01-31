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

    // TODO: Handle mode (prorata)

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
}
