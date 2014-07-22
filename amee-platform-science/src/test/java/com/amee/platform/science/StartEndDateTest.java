package com.amee.platform.science;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class StartEndDateTest {
    private static final TimeZone TIME_ZONE_NY = TimeZone.getTimeZone("America/New_York");
    private static final TimeZone TIME_ZONE_LONDON = TimeZone.getTimeZone("Europe/London");

    @Test
    public void testGetStartOfMonthDate() throws Exception {

        // First of this month in New York
        DateTime newYork = new DateTime(DateTimeZone.forTimeZone(TIME_ZONE_NY)).withDayOfMonth(1).withTime(0, 0, 0 ,0);
        DateTime utc = newYork.toDateTime(DateTimeZone.UTC);

        assertEquals(utc.toDate(), StartEndDate.getStartOfMonthDate(TIME_ZONE_NY).toDate());

        String expected = newYork.toString(ISODateTimeFormat.dateTimeNoMillis());
        assertEquals(expected, StartEndDate.getStartOfMonthDate(TIME_ZONE_NY).toString());
    }

    @Test
    public void testGetLocalStartEndDate() throws Exception {
        // 2010-01-01 12:00 UTC = 2010-01-01 07:00 America/New_York
        DateTime utcTime = new DateTime(2010, 1, 1, 12, 0, 0, 0, DateTimeZone.UTC);
        DateTime newYorkTime = new DateTime(2010, 1, 1, 7, 0, 0, 0, DateTimeZone.forTimeZone(TIME_ZONE_NY));

        assertEquals(newYorkTime.toDate(),
                StartEndDate.getLocalStartEndDate(utcTime.toDate(), TIME_ZONE_NY).toDate());
        assertEquals("2010-01-01T07:00:00-05:00",
                StartEndDate.getLocalStartEndDate(utcTime.toDate(), TIME_ZONE_NY).toString());

        // Summer time
        // 2010-06-01 12:00 UTC = 2010-01-01 08:00 America/New_York
        DateTime utcSummerTime = new DateTime(2010, 6, 1, 12, 0, 0, 0, DateTimeZone.UTC);
        DateTime newYorkSummerTime = new DateTime(2010, 6, 1, 8, 0, 0, 0, DateTimeZone.forTimeZone(TIME_ZONE_NY));

        assertEquals(newYorkSummerTime.toDate(),
                StartEndDate.getLocalStartEndDate(utcSummerTime.toDate(), TIME_ZONE_NY).toDate());
        assertEquals("2010-06-01T08:00:00-04:00",
                StartEndDate.getLocalStartEndDate(utcSummerTime.toDate(), TIME_ZONE_NY).toString());

        // Epoch
        // 1970-01-01 00:00 UTC = 1970-01-01 01:00 Europe/London (British Standard Time Act 1968)
        DateTime utcEpoch = new DateTime(0, DateTimeZone.UTC);
        DateTime londonEpoch = new DateTime(0, DateTimeZone.forTimeZone(TIME_ZONE_LONDON));

        assertEquals(londonEpoch.toDate(),
                StartEndDate.getLocalStartEndDate(utcEpoch.toDate(), TIME_ZONE_LONDON).toDate());
        assertEquals("1970-01-01T01:00:00+01:00",
                StartEndDate.getLocalStartEndDate(londonEpoch.toDate(), TIME_ZONE_LONDON).toString());
    }
}
