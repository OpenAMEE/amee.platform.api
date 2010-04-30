package com.amee.platform.science;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataSeriesTest {

    DateTime now;
    DataSeries lhs;
    DataSeries rhs;
    DataPoint rhp;
    float rhf;

    private static final double DELTA = 0.000001;

    @Before
    public void init() {

        now = new DateTime();

        // Test adding two series
        List<DataPoint> a = new ArrayList<DataPoint>();
        a.add(new DataPoint(now.plusDays(1), new Amount("1")));
        a.add(new DataPoint(now.plusDays(2), new Amount("2")));
        a.add(new DataPoint(now.plusDays(3), new Amount("3")));
        lhs = new DataSeries(a);

        List<DataPoint> b = new ArrayList<DataPoint>();
        b.add(new DataPoint(now.plusDays(1), new Amount("2")));
        b.add(new DataPoint(now.plusDays(2), new Amount("3")));
        b.add(new DataPoint(now.plusDays(3), new Amount("4")));
        rhs = new DataSeries(b);

        lhs.setSeriesStartDate(now.plusHours(36));
        rhs.setSeriesStartDate(now.plusHours(37));
        lhs.setSeriesEndDate(now.plusDays(5));
        rhs.setSeriesEndDate(now.plusDays(5));
        rhp = new DataPoint(now.plusDays(1), new Amount("4"));

        rhf = 4.0f;
    }

    @Test
    public void add() {

        DataSeries test;
        DataSeries actual;

        // Add two data series
        List<DataPoint> sum = new ArrayList<DataPoint>();
        sum.add(new DataPoint(now.plusDays(1), new Amount("3")));
        sum.add(new DataPoint(now.plusDays(2), new Amount("5")));
        sum.add(new DataPoint(now.plusDays(3), new Amount("7")));
        actual = new DataSeries(sum);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        test = lhs.plus(rhs);
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());
        assertEquals("Combined series should have start date", now.plusHours(36), test.getSeriesStartDate());
        assertEquals("Combined series should have end date", now.plusDays(5), test.getSeriesEndDate());

        // Add a series and a single point
        sum = new ArrayList<DataPoint>();
        sum.add(new DataPoint(now.plusDays(1), new Amount("5")));
        sum.add(new DataPoint(now.plusDays(2), new Amount("6")));
        sum.add(new DataPoint(now.plusDays(3), new Amount("7")));
        actual = new DataSeries(sum);
        test = lhs.plus(rhp);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        assertEquals("Combined series should have start date, is " + test.getSeriesStartDate() + " cf " + now.plusHours(36),
                now.plusHours(36), test.getSeriesStartDate());
        assertEquals("Combined series should have end date", now.plusDays(5), test.getSeriesEndDate());
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());

        // Add a series and a primitive
        test = lhs.plus(rhf);
        assertEquals("Combined series should have start date, is " + test.getSeriesStartDate() + " cf " + now.plusHours(36),
                now.plusHours(36), test.getSeriesStartDate());
        assertEquals("Combined series should have end date", now.plusDays(5), test.getSeriesEndDate());
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());
    }



    @Test
    public void subtract() {

        DataSeries test;
        DataSeries actual;

        List<DataPoint> diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("-1")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("-1")));
        diff.add(new DataPoint(now.plusDays(3), new Amount("-1")));
        actual = new DataSeries(diff);
        test = lhs.subtract(rhs);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());


        diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("-3")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("-2")));
        diff.add(new DataPoint(now.plusDays(3), new Amount("-1")));
        actual = new DataSeries(diff);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        test = lhs.subtract(rhp);
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());

        // Subtract a series and a primitive
        test = lhs.subtract(rhf);
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());
    }

    @Test
    public void divide() {

        DataSeries test;
        DataSeries actual;

        List<DataPoint> diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("0.5")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("2").divide(new Amount("3"))));
        diff.add(new DataPoint(now.plusDays(3), new Amount("0.75")));
        actual = new DataSeries(diff);
        test = lhs.divide(rhs);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());

        diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("0.25")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("0.5")));
        diff.add(new DataPoint(now.plusDays(3), new Amount("0.75")));
        actual = new DataSeries(diff);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        test = lhs.divide(rhp);
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());

        // Divide a series and a primitive
        test = lhs.divide(rhf);

        //print(test, actual);
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());
    }

    @Test
    public void multiply() {

        DataSeries test;
        DataSeries actual;

        List<DataPoint> diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("2")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("6")));
        diff.add(new DataPoint(now.plusDays(3), new Amount("12")));
        actual = new DataSeries(diff);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        test = lhs.multiply(rhs);
        assertEquals("Integrate should produce the correct value.", test.integrate(), actual.integrate());


        diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("4")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("8")));
        diff.add(new DataPoint(now.plusDays(3), new Amount("12")));
        actual = new DataSeries(diff);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        test = lhs.multiply(rhp);
        assertEquals("Combined series should have start date, is " + test.getSeriesStartDate() + " cf " + now.plusHours(36),
            now.plusHours(36), test.getSeriesStartDate());
        assertEquals("Combined series should have end date", now.plusDays(5), test.getSeriesEndDate());
        assertEquals("Integrate should produce the correct value, is " + test.integrate() + " cf " + actual.integrate(),
            test.integrate(), actual.integrate());

        // Divide a series and a primitive
        test = lhs.multiply(rhf);
        assertEquals("Integrate should produce the correct value, is " + test.integrate() + " cf " + actual.integrate(),
            test.integrate(), actual.integrate());
        }

    @Test
    public void queryWithNarrowerStartAndEndDate() {
        List<DataPoint> points = new ArrayList<DataPoint>();
        points.add(new DataPoint(now.plusDays(1), new Amount("2")));
        points.add(new DataPoint(now.plusDays(2), new Amount("6")));
        points.add(new DataPoint(now.plusDays(3), new Amount("12")));
        points.add(new DataPoint(now.plusDays(4), new Amount("12")));
        DataSeries series = new DataSeries(points);
        series.setSeriesStartDate(now.plusDays(1));
        series.setSeriesEndDate(now.plusDays(3));
        Amount window = new Amount(now.plusDays(3).getMillis() - now.plusDays(1).getMillis());

        assertEquals("Should have correct time window.", window, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double target = (2 + 6) / 2.0;
        assertEquals("Integrate should produce the correct value (" + target + "): " + series.integrate().getValue(),
            target, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithWiderStartAndEndDate() {
        List<DataPoint> points = new ArrayList<DataPoint>();
        points.add(new DataPoint(now.plusDays(2), new Amount("6")));
        points.add(new DataPoint(now.plusDays(3), new Amount("12")));
        points.add(new DataPoint(now.plusDays(4), new Amount("15")));
        DataSeries series = new DataSeries(points);
        series.setSeriesStartDate(now.plusDays(1));
        series.setSeriesEndDate(now.plusDays(5));
        Amount window = new Amount(now.plusDays(5).getMillis() - now.plusDays(2).getMillis());
        assertEquals("Should have correct time window.", window, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double target = (6 + 12 + 15) / 3.0;
        assertEquals("Integrate should produce the correct value (" + target + "): " + series.integrate().getValue(),
            target, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithStartDate() {
        List<DataPoint> points = new ArrayList<DataPoint>();
        points.add(new DataPoint(now.plusDays(1), new Amount("2")));
        points.add(new DataPoint(now.plusDays(2), new Amount("6")));
        points.add(new DataPoint(now.plusDays(3), new Amount("12")));
        points.add(new DataPoint(now.plusDays(4), new Amount("12")));
        DataSeries series = new DataSeries(points);
        series.setSeriesStartDate(now.plusDays(2));
        Amount window = new Amount(now.plusDays(4).getMillis() - now.plusDays(2).getMillis());
        assertEquals("Should have correct time window.", window, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double target = (6 + 12) / 2.0;
        assertEquals("Integrate should produce the correct value (" + target + "): " + series.integrate().getValue(),
            target, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithEndDate() {
        List<DataPoint> points = new ArrayList<DataPoint>();
        points.add(new DataPoint(now.plusDays(1), new Amount("2")));
        points.add(new DataPoint(now.plusDays(2), new Amount("6")));
        points.add(new DataPoint(now.plusDays(3), new Amount("12")));
        points.add(new DataPoint(now.plusDays(4), new Amount("12")));
        DataSeries series = new DataSeries(points);
        series.setSeriesEndDate(now.plusDays(2));
        Amount window = new Amount(now.plusDays(2).getMillis() - now.plusDays(1).getMillis());
        assertEquals("Should have correct time window.", window, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double target = 2.0;
        assertEquals("Integrate should produce the correct value ( " + target + "): " + series.integrate().getValue(),
            target, series.integrate().getValue(), DELTA);
    }

    @Test
    public void queryWithoutStartOrEndDate() {
        DateTime start = now.plusDays(1);
        DateTime end = now.plusDays(4);
        List<DataPoint> points = new ArrayList<DataPoint>();
        points.add(new DataPoint(start, new Amount("2")));
        points.add(new DataPoint(now.plusDays(2), new Amount("6")));
        points.add(new DataPoint(now.plusDays(3), new Amount("12")));
        points.add(new DataPoint(end, new Amount("15")));
        DataSeries series = new DataSeries(points);
        Amount window = new Amount(end.getMillis() - start.getMillis());
        assertEquals("Should have correct time window.", window, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double target = (2+6+12)/3.0;
        assertEquals("Integrate should produce the correct value (" + target + "): " + series.integrate().getValue(),
            target, series.integrate().getValue(), DELTA);

    }

    @Test
    public void queryWithInsideStartAndEndDate() {
        int start = 15;
        int end = 35;
        List<DataPoint> points = new ArrayList<DataPoint>();
        points.add(new DataPoint(now.plusDays(10), new Amount("2")));
        points.add(new DataPoint(now.plusDays(20), new Amount("3")));
        points.add(new DataPoint(now.plusDays(30), new Amount("5")));
        points.add(new DataPoint(now.plusDays(40), new Amount("7")));
        DataSeries series = new DataSeries(points);
        series.setSeriesStartDate(now.plusDays(start));
        series.setSeriesEndDate(now.plusDays(end));
        Amount window = new Amount(now.plusDays(end).getMillis() - now.plusDays(start).getMillis());
        assertEquals("Should have correct time window.", window, series.getSeriesTimeInMillis());
        assertNotNull("Should be able to integrate.", series.integrate());
        double target = (2 * 5 + 3 * 10 + 5 * 5) / 20.0;
        assertEquals("Integrate should produce the correct value (" + target + "): " + series.integrate().getValue(),
            target, series.integrate().getValue(), DELTA);
    }

    private void print(DataSeries test, DataSeries actual) {

        for (DateTime dt : test.getDateTimePoints()) {
            DataPoint dp = test.getDataPoint(dt);
            System.out.println("test: " + dt.toString("yyyy-dd-MM") + " => " + dp.getValue());
        }
        System.out.println("test: " + test.integrate());

        for (DateTime dt : actual.getDateTimePoints()) {
            DataPoint dp = actual.getDataPoint(dt);
            System.out.println("actual: " + dt.toString("yyyy-dd-MM") + " => " + dp.getValue());
        }
        System.out.println("actual: " + actual.integrate());
    }
}
