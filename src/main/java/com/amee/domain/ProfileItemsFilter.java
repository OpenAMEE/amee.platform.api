package com.amee.domain;

import com.amee.platform.science.StartEndDate;

public class ProfileItemsFilter extends LimitFilter {

    private StartEndDate startDate = new StartEndDate();
    private StartEndDate endDate = null;
    private String duration;

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

    public StartEndDate getEndDate() {
        if (endDate != null) {
            return new StartEndDate(endDate);
        } else {
            return null;
        }
    }

    public void setEndDate(StartEndDate endDate) {
        this.endDate = endDate;
    }

    public StartEndDate getStartDate() {
        return new StartEndDate(startDate);
    }

    public void setStartDate(StartEndDate startDate) {
        if (startDate != null) {
            this.startDate = startDate;
        }
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
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
