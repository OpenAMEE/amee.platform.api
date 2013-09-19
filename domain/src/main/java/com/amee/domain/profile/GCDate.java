package com.amee.domain.profile;

import org.joda.time.DateTime;

public abstract class GCDate extends java.util.Date {

    protected String dateStr;
    
    public GCDate(long time) {
        setTime(time);
        setDefaultDateStr();
    }

    public GCDate(String dateStr) {
        super();
        if (dateStr != null) {
            setTime(parseStr(dateStr));
            this.dateStr = dateStr;
        } else {
            setTime(defaultDate());
            setDefaultDateStr();
        }
    }

    protected abstract long parseStr(String dateStr);

    protected abstract void setDefaultDateStr();

    protected abstract long defaultDate();
    
    @Override
    public String toString() {
        return dateStr;    
    }

    public DateTime toDateTime() {
        return new DateTime(this.getTime());
    }

}
