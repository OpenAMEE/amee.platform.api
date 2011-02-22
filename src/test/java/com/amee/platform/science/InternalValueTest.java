package com.amee.platform.science;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InternalValueTest {

    DateTime now;
    DateTime one;
    DateTime two;
    DateTime three;
    DateTime four;
    DateTime five;
    DateTime six;
    DateTime max;

    @Before
    public void init() {
        now = new DateTime();
        one = now.plusMinutes(1);
        two = now.plusMinutes(2);
        three = now.plusMinutes(3);
        four = now.plusMinutes(4);
        five = now.plusMinutes(5);
        six = now.plusMinutes(6);
        max = new DateTime(Long.MAX_VALUE);
    }

    @Test
    public void unfilteredTimeSeries() {

        // Create list of ItemValues.
        List<ExternalGenericValue> values = createItemValues();

        // Filter with start and end date outside of value range.
        InternalValue internal = new InternalValue(values, now.toDate(), max.toDate());

        // We expect all values to be present.
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();
        assertTrue("Should contain expected ItemValues", filteredDates.contains(one));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(two));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(three));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(four));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(five));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(six));
    }

    @Test
    public void filterTimeSeriesWithStartAndEndDatesA() {

        // Create list of ItemValues.
        List<ExternalGenericValue> values = createItemValues();

        // Create start date co-incidental with value three.
        Date start = now.plusMinutes(3).toDate();

        // Create end date co-incidental with value five.
        Date end = now.plusMinutes(5).toDate();

        // Do the filtering.
        InternalValue internal = new InternalValue(values, start, end);

        // We only expect values two and three to be present.
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(one));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(two));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(three));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(four));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(five));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(six));
    }

    @Test
    public void filterTimeSeriesWithStartAndEndDatesB() {

        // Create list of ItemValues.
        List<ExternalGenericValue> values = createItemValues();

        // Create start date between values two & three.
        Date start = now.plusMinutes(2).plusSeconds(30).toDate();

        // Create end date co-incidental with value four. 
        Date end = now.plusMinutes(4).toDate();

        // Do the filtering.
        InternalValue internal = new InternalValue(values, start, end);

        // We only expect values two and three to be present.
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(one));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(two));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(three));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(four));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(five));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(six));
    }

    @Test
    public void filterTimeSeriesWithStartAndEndDatesC() {

        // Create list of ItemValues.
        List<ExternalGenericValue> values = createItemValues();

        // Create start date between values two & three.
        Date start = now.plusMinutes(2).plusSeconds(30).toDate();

        // Create end date between values four & five.
        Date end = now.plusMinutes(4).plusSeconds(30).toDate();

        // Do the filtering.
        InternalValue internal = new InternalValue(values, start, end);

        // We only expect values two and three to be present.
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(one));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(two));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(three));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(four));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(five));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(six));
        assertTrue("Should have a window of two minutes, is:"+filteredValues.getSeriesTimeInMillis() +"s",
                filteredValues.getSeriesTimeInMillis().equals(new Amount((float)(120*1000))));
    }

    @Test
    public void filterTimeSeriesWithStartDate() {

        // Create list of ItemValues.
        List<ExternalGenericValue> values = createItemValues();

        InternalValue internal = new InternalValue(values, two.toDate(), max.toDate());
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();

        // We expect all values to be present.
        assertTrue("Should contain expected ItemValues", filteredDates.contains(one));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(two));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(three));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(four));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(five));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(six));
    }

    private List<ExternalGenericValue> createItemValues() {
        List<ExternalGenericValue> values = new ArrayList<ExternalGenericValue>();
        values.add(new MockExternalValue("1", one.toDate()));
        values.add(new MockExternalValue("2", two.toDate()));
        values.add(new MockExternalValue("3", three.toDate()));
        values.add(new MockExternalValue("4", four.toDate()));
        values.add(new MockExternalValue("5", five.toDate()));
        values.add(new MockExternalValue("6", six.toDate()));
        return values;
    }
}
