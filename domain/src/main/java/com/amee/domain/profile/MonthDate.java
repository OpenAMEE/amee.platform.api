package com.amee.domain.profile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class MonthDate extends GCDate {

    private static final Logger log = LoggerFactory.getLogger(MonthDate.class);

    /**
     * V1 API does not support resolution greater than 1 month so time zones are not relevant.
     * This custom DateTimeFormatter accepts dates in the following format:
     *  - yyyyMMdd
     *  - yyyyMM
     * In either case, the date is always set to the first of the month and the time zone is set to UTC.
     */
    private static DateTimeFormatter FMT = new DateTimeFormatterBuilder()
        .appendYear(4, 4)
        .appendMonthOfYear(2)
        .appendOptional(DateTimeFormat.forPattern("dd").getParser())
        .toFormatter().withZone(DateTimeZone.UTC);

    public MonthDate() {
        super(System.currentTimeMillis());
    }

    public MonthDate(String validFrom) {
        super(validFrom);
    }

    protected long parseStr(String dateStr) {
        try {
            DateTime date = FMT.parseDateTime(dateStr);
            // v1 dates are always rounded down to the start of the month.
            return date.dayOfMonth().withMinimumValue().toDateMidnight().getMillis();
        } catch (IllegalArgumentException e) {
            log.warn("parseStr() Caught IllegalArgumentException: " + e.getMessage());
            return defaultDate();
        }
    }

    protected void setDefaultDateStr() {
        this.dateStr = FMT.print(this.getTime());
    }

    @Override
    protected long defaultDate() {
        // Beginning of current month in UTC.
        DateMidnight startOfMonth = new DateMidnight(DateTimeZone.UTC).withDayOfMonth(1);
        return startOfMonth.getMillis();
    }

    public static boolean validate(String dateStr) {
        try {
            FMT.parseDateTime(dateStr);
        } catch (IllegalArgumentException ex) {
            return false;
        }
        return true;
    }
}
