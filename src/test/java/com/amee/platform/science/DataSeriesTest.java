package com.amee.platform.science;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

public class DataSeriesTest {

    DateTime now;
    DataSeries lhs;
    DataSeries rhs;
    DataPoint rhp;
    float rhf;

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
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));
        assertTrue("Combined series should have start date",test.getSeriesStartDate().equals(
                now.plusHours(36)
        ));
        assertTrue("Combined series should have end date",test.getSeriesEndDate().equals(
                now.plusDays(5)
        ));
        // Add a series and a single point
        sum = new ArrayList<DataPoint>();
        sum.add(new DataPoint(now.plusDays(1), new Amount("5")));
        sum.add(new DataPoint(now.plusDays(2), new Amount("6")));
        sum.add(new DataPoint(now.plusDays(3), new Amount("7")));
        actual = new DataSeries(sum);
        test = lhs.plus(rhp);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        assertTrue("Combined series should have start date, is "+test.getSeriesStartDate() +" cf "+now.plusHours(36),test.getSeriesStartDate().equals(
                now.plusHours(36)
        ));
        assertTrue("Combined series should have end date",test.getSeriesEndDate().equals(
                now.plusDays(5)
        ));
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));

        // Add a series and a primitive
        test = lhs.plus(rhf);
        assertTrue("Combined series should have start date, is "+test.getSeriesStartDate() +" cf "+now.plusHours(36),test.getSeriesStartDate().equals(
                now.plusHours(36)
        ));
        assertTrue("Combined series should have end date",test.getSeriesEndDate().equals(
                now.plusDays(5)
        ));
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));

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
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));


        diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("-3")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("-2")));
        diff.add(new DataPoint(now.plusDays(3), new Amount("-1")));
        actual = new DataSeries(diff);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        test = lhs.subtract(rhp);
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));

        // Subtract a series and a primitive
        test = lhs.subtract(rhf);
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));
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
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));

        diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("0.25")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("0.5")));
        diff.add(new DataPoint(now.plusDays(3), new Amount("0.75")));
        actual = new DataSeries(diff);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        test = lhs.divide(rhp);
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));

        // Divide a series and a primitive
        test = lhs.divide(rhf);

        //print(test, actual);
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));
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
        assertTrue("Integrate should produce the correct value.", test.integrate().equals(actual.integrate()));


        diff = new ArrayList<DataPoint>();
        diff.add(new DataPoint(now.plusDays(1), new Amount("4")));
        diff.add(new DataPoint(now.plusDays(2), new Amount("8")));
        diff.add(new DataPoint(now.plusDays(3), new Amount("12")));
        actual = new DataSeries(diff);
        actual.setSeriesStartDate(now.plusHours(36));
        actual.setSeriesEndDate( now.plusDays(5));
        test = lhs.multiply(rhp);
        assertTrue("Combined series should have start date, is "+test.getSeriesStartDate() +" cf "+
                now.plusHours(36),test.getSeriesStartDate().equals(
                now.plusHours(36)
        ));
        assertTrue("Combined series should have end date",test.getSeriesEndDate().equals(
                now.plusDays(5)
        ));
        assertTrue("Integrate should produce the correct value, is "+
                test.integrate()+" cf "+
                actual.integrate(), test.integrate().equals(actual.integrate()));

        // Divide a series and a primitive
        test = lhs.multiply(rhf);
        assertTrue("Integrate should produce the correct value, is "+
                        test.integrate()+" cf "+
                        actual.integrate(), test.integrate().equals(actual.integrate()));
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

        assertTrue("Should have correct time window.", series.getSeriesTimeInMillis().equals(window));
        assertTrue("Should be able to integrate.", series.integrate() != null);
        Amount target=new Amount( new Float((2+6)/2.0).toString() );
        assertTrue("Integrate should produce the correct value ("+target+"):"+series.integrate(),
                series.integrate().equals(target));
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
        assertTrue("Should have correct time window.", series.getSeriesTimeInMillis().equals(window));
        assertTrue("Should be able to integrate.", series.integrate() != null);
        Amount target=new Amount( new Float((6+12+15)/3.0).toString() );
        assertTrue("Integrate should produce the correct value ("+target+"):"+series.integrate(),
                series.integrate().equals(target));
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
        assertTrue("Should have correct time window.", series.getSeriesTimeInMillis().equals(window));
        assertTrue("Should be able to integrate.", series.integrate() != null);
        Amount target=new Amount( new Float((6+12)/2.0).toString() );
        assertTrue("Integrate should produce the correct value ("+target+"):"+series.integrate(),
                series.integrate().equals(target));
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
        assertTrue("Should have correct time window.", series.getSeriesTimeInMillis().equals(window));
        assertTrue("Should be able to integrate.", series.integrate() != null);
        Amount target=new Amount( new Float(2).toString() );
        assertTrue("Integrate should produce the correct value ("+target+"):"+series.integrate(),
                series.integrate().equals(target));
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
        assertTrue("Should have correct time window.", series.getSeriesTimeInMillis().equals(window));
        assertTrue("Should be able to integrate.", series.integrate() != null);
        Amount target=new Amount( new Float((2+6+12)/3.0).toString() );
        assertTrue("Integrate should produce the correct value ("+target+"):"+series.integrate(),
                series.integrate().equals(target));
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
        assertTrue("Should have correct time window.", series.getSeriesTimeInMillis().equals(window));
        assertTrue("Should be able to integrate.", series.integrate() != null);
        Amount target=new Amount( new Float((2*5+3*10+5*5)/20.0).toString() );
        assertTrue("Integrate should produce the correct value ("+target+"):"+series.integrate(),
                series.integrate().equals(target));
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
