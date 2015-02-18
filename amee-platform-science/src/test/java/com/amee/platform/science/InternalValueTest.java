package com.amee.platform.science;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InternalValueTest {

    private DateTime now;
    private DateTime dateOne;
    private DateTime dateTwo;
    private DateTime dateThree;
    private DateTime dateFour;
    private DateTime dateFive;
    private DateTime dateSix;
    private DateTime dateMax;

    @Mock private ExternalValue mockValueOne;
    @Mock private ExternalValue mockValueTwo;
    @Mock private ExternalValue mockValueThree;
    @Mock private ExternalValue mockValueFour;
    @Mock private ExternalValue mockValueFive;
    @Mock private ExternalValue mockValueSix;

    private List<ExternalGenericValue> values = new ArrayList<ExternalGenericValue>();

    @Before
    public void init() {
        now = DateTime.now();
        dateOne = now.plusMinutes(1);
        dateTwo = now.plusMinutes(2);
        dateThree = now.plusMinutes(3);
        dateFour = now.plusMinutes(4);
        dateFive = now.plusMinutes(5);
        dateSix = now.plusMinutes(6);
        dateMax = new DateTime(new Date(Long.MAX_VALUE));

        when(mockValueOne.getStartDate()).thenReturn(new StartEndDate(dateOne.toDate()));
        values.add(mockValueOne);

        when(mockValueTwo.getStartDate()).thenReturn(new StartEndDate(dateTwo.toDate()));
        values.add(mockValueTwo);

        when(mockValueThree.getStartDate()).thenReturn(new StartEndDate(dateThree.toDate()));
        values.add(mockValueThree);

        when(mockValueFour.getStartDate()).thenReturn(new StartEndDate(dateFour.toDate()));
        values.add(mockValueFour);

        when(mockValueFive.getStartDate()).thenReturn(new StartEndDate(dateFive.toDate()));
        values.add(mockValueFive);

        when(mockValueSix.getStartDate()).thenReturn(new StartEndDate(dateSix.toDate()));
        values.add(mockValueSix);
    }

    @Test
    public void unfilteredTimeSeries() {

        // Filter with start and end date outside of value range.
        InternalValue internal = new InternalValue(values, now.toDate(), dateMax.toDate());

        // We expect all values to be present.
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateOne));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateTwo));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateThree));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateFour));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateFive));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateSix));
    }

    @Test
    public void filterTimeSeriesWithStartAndEndDatesA() {

        // Create start date co-incidental with value three.
        Date start = now.plusMinutes(3).toDate();

        // Create end date co-incidental with value five.
        Date end = now.plusMinutes(5).toDate();

        // Do the filtering.
        InternalValue internal = new InternalValue(values, start, end);

        // We only expect values two and three to be present.
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateOne));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateTwo));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateThree));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateFour));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateFive));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateSix));
    }

    @Test
    public void filterTimeSeriesWithStartAndEndDatesB() {

        // Create start date between values two & three.
        Date start = now.plusMinutes(2).plusSeconds(30).toDate();

        // Create end date co-incidental with value four. 
        Date end = now.plusMinutes(4).toDate();

        // Do the filtering.
        InternalValue internal = new InternalValue(values, start, end);

        // We only expect values two and three to be present.
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateOne));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateTwo));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateThree));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateFour));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateFive));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateSix));
    }

    @Test
    public void filterTimeSeriesWithStartAndEndDatesC() {

        // Create start date between values two & three.
        Date start = now.plusMinutes(2).plusSeconds(30).toDate();

        // Create end date between values four & five.
        Date end = now.plusMinutes(4).plusSeconds(30).toDate();

        // Do the filtering.
        InternalValue internal = new InternalValue(values, start, end);

        // We only expect values two and three to be present.
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateOne));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateTwo));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateThree));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateFour));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateFive));
        assertFalse("Should not contain filtered ItemValues", filteredDates.contains(dateSix));
        assertEquals("Should have a window of two minutes, is: " + filteredValues.getSeriesTimeInMillis() + "s",
            new Long(120 * 1000), filteredValues.getSeriesTimeInMillis());
    }

    @Test
    public void filterTimeSeriesWithStartDate() {

        InternalValue internal = new InternalValue(values, dateTwo.toDate(), dateMax.toDate());
        DataSeries filteredValues = (DataSeries) internal.getValue();
        Collection<DateTime> filteredDates = filteredValues.getDateTimePoints();

        // We expect all values to be present.
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateOne));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateTwo));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateThree));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateFour));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateFive));
        assertTrue("Should contain expected ItemValues", filteredDates.contains(dateSix));
    }
}
