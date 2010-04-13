package com.amee.platform.science;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;

import java.util.Date;
import java.util.TimeZone;

public class StartEndDate extends BaseDate {

    private final static Log log = LogFactory.getLog(StartEndDate.class);

    private static final DateTimeFormatter FMT = ISODateTimeFormat.dateTimeNoMillis();

    public StartEndDate() {
        this(new Date());
    }

    public StartEndDate(String dateStr) {
        super(dateStr);
    }

    public StartEndDate(Date date) {
        super(date.getTime());
    }

    protected long parseStr(String dateStr) {
        try {
            // Seconds are ignored and are not significant in this version of AMEE.
            return FMT.parseDateTime(dateStr).secondOfMinute().withMinimumValue().getMillis();
        } catch (IllegalArgumentException e) {
            log.warn("parseStr() Caught IllegalArgumentException: " + e.getMessage());
            throw e;
        }
    }

    protected void setDefaultDateStr() {
        this.dateStr = FMT.print(getTime());
    }

    protected long defaultDate() {
        return new DateTime().secondOfMinute().withMinimumValue().getMillis();
    }

    public StartEndDate plus(String duration) {
        Period period = ISOPeriodFormat.standard().parsePeriod(duration);
        DateTime thisPlusPeriod = new DateTime(getTime()).plus(period);
        return new StartEndDate(thisPlusPeriod.toDate());
    }

    public StartEndDate minus(String duration) {
        Period period = ISOPeriodFormat.standard().parsePeriod(duration);
        DateTime thisPlusPeriod = new DateTime(getTime()).minus(period);
        return new StartEndDate(thisPlusPeriod.toDate());
    }

    public static boolean validate(String dateStr) {
        try {
            FMT.parseDateTime(dateStr);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }

    public static StartEndDate getStartOfMonthDate(TimeZone timeZone) {
        DateMidnight startOfMonth = new DateMidnight(DateTimeZone.forTimeZone(timeZone)).withDayOfMonth(1);
        return new StartEndDate(startOfMonth.toDate());
    }
}
