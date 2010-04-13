package com.amee.platform.science;

import org.joda.time.DateTime;

import java.util.Date;

public abstract class BaseDate extends java.util.Date {

    protected String dateStr;

    public BaseDate(long time) {
        setTime(time);
        setDefaultDateStr();
    }

    public BaseDate(String dateStr) {
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

    public Date toDate() {
        return new Date(this.getTime());
    }
}
