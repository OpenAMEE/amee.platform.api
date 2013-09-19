package com.amee.platform.science;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final static Logger log = LoggerFactory.getLogger(StartEndDate.class);

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
        return DateTime.now().secondOfMinute().withMinimumValue().getMillis();
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

    /**
     * Get the start of the month in the given time zone.
     * @param timeZone the time zone to use when calculating the start of the month.
     * @return a StartEndDate set to the start of the month in the given time zone.
     */
    public static StartEndDate getStartOfMonthDate(TimeZone timeZone) {
        DateMidnight startOfMonth = new DateMidnight(DateTimeZone.forTimeZone(timeZone)).withDayOfMonth(1);
        return new StartEndDate(startOfMonth.toString(ISODateTimeFormat.dateTimeNoMillis()));
    }

    /**
     * Create a new StartEndDate using the given Date and TimeZone
     * @param theDate the Date to set the StartEndDate to. (UTC is implied).
     * @param theTimeZone the timeZone to convert the given timestamp to.
     * @return a StartEndDate with the dateStr correctly set to the given time zone.
     */
    public static StartEndDate getLocalStartEndDate(Date theDate, TimeZone theTimeZone) {
        DateTime localDate = new DateTime(theDate, DateTimeZone.forTimeZone(theTimeZone));
        return new StartEndDate(localDate.toString(ISODateTimeFormat.dateTimeNoMillis()));
    }
}
