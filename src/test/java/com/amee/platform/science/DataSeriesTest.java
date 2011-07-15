package com.amee.platform.science;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DataSeriesTest {

//    private DataSeries seriesA;
//    private DataSeries seriesB;
//    private DataSeries seriesC;

    private DateTime now;
    private DataSeries lhSeries;
    private DataSeries rhSeries;
    private DataPoint rhPoint;
    private double rhDouble;

    /**
     * The original DELTA value for test accuracy.
     */
    private static final double DELTA = 0.000001;

    @Before
    public void init() {

        // Force time zone to UTC.
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        TimeZone.setDefault(timeZone);
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(timeZone));

        now = new DateTime();

        lhSeries = new DataSeries();
        lhSeries.addDataPoint(new DataPoint(now.plusDays(1), new Amount("1")));
        lhSeries.addDataPoint(new DataPoint(now.plusDays(2), new Amount("2")));
        lhSeries.addDataPoint(new DataPoint(now.plusDays(3), new Amount("3")));
        lhSeries.setSeriesStartDate(now.plusHours(36));
        lhSeries.setSeriesEndDate(now.plusDays(5));

        rhSeries = new DataSeries();
        rhSeries.addDataPoint(new DataPoint(now.plusDays(1), new Amount("2")));
        rhSeries.addDataPoint(new DataPoint(now.plusDays(2), new Amount("3")));
        rhSeries.addDataPoint(new DataPoint(now.plusDays(3), new Amount("4")));
        rhSeries.setSeriesStartDate(now.plusHours(37));
        rhSeries.setSeriesEndDate(now.plusDays(5));

        rhPoint = new DataPoint(now.plusDays(1), new Amount("4"));

        rhDouble = 4.0;
    }

    @Test
    public void add() {

        // Add a series to a series
        DataSeries expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("3")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("5")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("7")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));

        DataSeries actual = lhSeries.plus(rhSeries);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());
        assertEquals("Combined series should have start date of: " + expected.getSeriesStartDate(),
            now.plusHours(36), actual.getSeriesStartDate());
        assertEquals("Combined series should have end date of: " + expected.getSeriesEndDate(),
            now.plusDays(5), actual.getSeriesEndDate());

        // Add a point to a series
        expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("5")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("6")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("7")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));

        actual = lhSeries.plus(rhPoint);
        assertEquals("Combined series should have start date of: " + expected.getSeriesStartDate() + " was: " + actual.getSeriesStartDate(),
            expected.getSeriesStartDate(), actual.getSeriesStartDate());
        assertEquals("Combined series should have end date of: " + expected.getSeriesEndDate(),
            expected.getSeriesEndDate(), actual.getSeriesEndDate());
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());

        // Add a double to a series
        actual = lhSeries.plus(rhDouble);
        assertEquals("Combined series should have start date of: " + now.plusHours(36) + " was: " + actual.getSeriesStartDate(),
            now.plusHours(36), actual.getSeriesStartDate());
        assertEquals("Combined series should have end date of: " + now.plusDays(5) + " was: " + actual.getSeriesEndDate(),
            now.plusDays(5), actual.getSeriesEndDate());
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());
    }

    @Test
    public void subtract() {

        // Subtract a series from a series
        DataSeries expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("-1")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("-1")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("-1")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));

        DataSeries actual = lhSeries.subtract(rhSeries);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());

        // Subtract a point from a series
        expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("-3")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("-2")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("-1")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));

        actual = lhSeries.subtract(rhPoint);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());

        // Subtract a double from a series
        actual = lhSeries.subtract(rhDouble);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());

        // Subtract a series from a double
        expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("3")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("2")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("1")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));
        
        actual = lhSeries.subtract(rhDouble, true);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());
    }

    @Test
    public void divide() {

        // Divide a series by a series
        DataSeries expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("0.5")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount(2.0 / 3.0)));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("0.75")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));

        DataSeries actual = lhSeries.divide(rhSeries);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());

        // Divide a series by a point
        expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("0.25")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("0.5")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("0.75")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));

        actual = lhSeries.divide(rhPoint);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());

        // Divide a series by a double
        actual = lhSeries.divide(rhDouble);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());

        // Divide a double by a series
        expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("4")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("2")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount(4.0 / 3.0)));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));
        
        actual = lhSeries.divide(rhDouble, true);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());
    }

    @Test
    public void multiply() {

        // Multiply a series by a series
        DataSeries expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("2")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("6")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("12")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));

        DataSeries actual = lhSeries.multiply(rhSeries);
        assertEquals("Integrate should produce the correct value.", expected.integrate(), actual.integrate());

        // Multiply a series by a point
        expected = new DataSeries();
        expected.addDataPoint(new DataPoint(now.plusDays(1), new Amount("4")));
        expected.addDataPoint(new DataPoint(now.plusDays(2), new Amount("8")));
        expected.addDataPoint(new DataPoint(now.plusDays(3), new Amount("12")));
        expected.setSeriesStartDate(now.plusHours(36));
        expected.setSeriesEndDate(now.plusDays(5));

        actual = lhSeries.multiply(rhPoint);
        assertEquals("Combined series should have start date of: " + expected.getSeriesStartDate() + ". Was: " + actual.getSeriesStartDate(),
            expected.getSeriesStartDate(), actual.getSeriesStartDate());
        assertEquals("Combined series should have end date of: " + expected.getSeriesEndDate() + ". Was: " + actual.getSeriesEndDate(),
            expected.getSeriesEndDate(), actual.getSeriesEndDate());
        assertEquals("Integrate should produce the correct value of " + expected.integrate() + ". Was: " + actual.integrate(),
            expected.integrate(), actual.integrate());

        // Multiply a series by a double
        actual = lhSeries.multiply(rhDouble);
        assertEquals("Integrate should produce the correct value of " + expected.integrate() + ". Was: " + actual.integrate(),
            expected.integrate(), actual.integrate());
    }

    @Test
    public void queryWithNarrowerStartAndEndDate() {
        DataSeries series = new DataSeries();
        series.addDataPoint(new DataPoint(now.plusDays(1), new Amount("2")));
        series.addDataPoint(new DataPoint(now.plusDays(2), new Amount("6")));
        series.addDataPoint(new DataPoint(now.plusDays(3), new Amount("12")));
        series.addDataPoint(new DataPoint(now.plusDays(4), new Amount("12")));
        series.setSeriesStartDate(now.plusDays(1));
        series.setSeriesEndDate(now.plusDays(3));
        Long expectedWindow = now.plusDays(3).getMillis() - now.plusDays(1).getMillis();

        assertEquals("Should have correct time window.", expectedWindow, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double expectedValue = (2 + 6) / 2.0;
        assertEquals("Integrate should produce the correct value (" + expectedValue + ")",
            expectedValue, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithWiderStartAndEndDate() {
        DataSeries series = new DataSeries();
        series.addDataPoint(new DataPoint(now.plusDays(2), new Amount("6")));
        series.addDataPoint(new DataPoint(now.plusDays(3), new Amount("12")));
        series.addDataPoint(new DataPoint(now.plusDays(4), new Amount("15")));
        series.setSeriesStartDate(now.plusDays(1));
        series.setSeriesEndDate(now.plusDays(5));
        Long expectedWindow = now.plusDays(5).getMillis() - now.plusDays(2).getMillis();

        assertEquals("Should have correct time window.", expectedWindow, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double expectedValue = (6 + 12 + 15) / 3.0;
        assertEquals("Integrate should produce the correct value (" + expectedValue + ")",
                expectedValue, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithStartDate() {
        DataSeries series = new DataSeries();
        series.addDataPoint(new DataPoint(now.plusDays(1), new Amount("2")));
        series.addDataPoint(new DataPoint(now.plusDays(2), new Amount("6")));
        series.addDataPoint(new DataPoint(now.plusDays(3), new Amount("12")));
        series.addDataPoint(new DataPoint(now.plusDays(4), new Amount("12")));
        series.setSeriesStartDate(now.plusDays(2));
        Long expectedWindow = now.plusDays(4).getMillis() - now.plusDays(2).getMillis();

        assertEquals("Should have correct time window.", expectedWindow, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double expectedValue = (6 + 12) / 2.0;
        assertEquals("Integrate should produce the correct value (" + expectedValue + ")",
            expectedValue, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithEndDate() {
        DataSeries series = new DataSeries();
        series.addDataPoint(new DataPoint(now.plusDays(1), new Amount("2")));
        series.addDataPoint(new DataPoint(now.plusDays(2), new Amount("6")));
        series.addDataPoint(new DataPoint(now.plusDays(3), new Amount("12")));
        series.addDataPoint(new DataPoint(now.plusDays(4), new Amount("12")));
        series.setSeriesEndDate(now.plusDays(2));
        Long expectedWindow = now.plusDays(2).getMillis() - now.plusDays(1).getMillis();

        assertEquals("Should have correct time window.", expectedWindow, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double expectedValue = 2.0;
        assertEquals("Integrate should produce the correct value ( " + expectedValue + ")",
            expectedValue, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithoutStartOrEndDate() {
        DateTime start = now.plusDays(1);
        DateTime end = now.plusDays(4);

        DataSeries series = new DataSeries();
        series.addDataPoint(new DataPoint(start, new Amount("2")));
        series.addDataPoint(new DataPoint(now.plusDays(2), new Amount("6")));
        series.addDataPoint(new DataPoint(now.plusDays(3), new Amount("12")));
        series.addDataPoint(new DataPoint(end, new Amount("15")));
        Long expectedWindow = end.getMillis() - start.getMillis();
        assertEquals("Should have correct time window.", expectedWindow, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double expectedValue = (2 + 6 + 12) / 3.0;
        assertEquals("Integrate should produce the correct value (" + expectedValue + ")",
            expectedValue, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithInsideStartAndEndDate() {
        DataSeries series = new DataSeries();
        series.addDataPoint(new DataPoint(now.plusDays(10), new Amount("2")));
        series.addDataPoint(new DataPoint(now.plusDays(20), new Amount("3")));
        series.addDataPoint(new DataPoint(now.plusDays(30), new Amount("5")));
        series.addDataPoint(new DataPoint(now.plusDays(40), new Amount("7")));

        int start = 15;
        int end = 35;
        series.setSeriesStartDate(now.plusHours(start * 24));
        series.setSeriesEndDate(now.plusHours(end * 24));
        Long expectedWindow = now.plusHours(end * 24).getMillis() - now.plusHours(start * 24).getMillis();
        assertEquals("Should have correct time window.", expectedWindow, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double expectedValue = (2 * 5 + 3 * 10 + 5 * 5) / 20.0;
        assertEquals("Integrate should produce the correct value (" + expectedValue + ")",
            expectedValue, series.integrate().getValue(), DELTA);
    }

    private void print(DataSeries expected, DataSeries actual) {

        for (DateTime dt : expected.getDateTimePoints()) {
            DataPoint dp = expected.getDataPoint(dt);
            System.out.println("expected: " + dt.toString("yyyy-dd-MM") + " => " + dp.getValue());
        }
        System.out.println("expected: " + expected.integrate());

        for (DateTime dt : actual.getDateTimePoints()) {
            DataPoint dp = actual.getDataPoint(dt);
            System.out.println("actual: " + dt.toString("yyyy-dd-MM") + " => " + dp.getValue());
        }
        System.out.println("actual: " + actual.integrate());
    }
}
