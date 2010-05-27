package com.amee.platform.science;

import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReturnValuesTest {

    private ReturnValues values;

    @Before
    public void setUp() {
        values = new ReturnValues();
    }

    @Test
    public void putAmount() {
        values.putValue("CO2e", "kg", "month", 123.45);
        assertTrue("Failed to add amount", values.getReturnValues().size() == 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidDefaultType() {
        values.setDefaultType("CO2e");
    }

    @Test
    public void setDefaultType() {
        values.putValue("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        assertEquals("Default type should be CO2e", "CO2e", values.getDefaultType());
    }

    @Test
    public void getDefaultAmount() {
        values.putValue("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        values.putValue("CO2", "kg", "month", 54.321);

        Amount defaultAmount = values.defaultValueAsAmount();
        assertEquals("Default amount should be CO2e", 123.45, defaultAmount.getValue(), 0.000001);
    }

    @Test
    public void getDefaultAmountAsDouble() {
        values.putValue("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        values.putValue("CO2", "kg", "month", 54.321);

        assertEquals(123.45, values.defaultValueAsDouble(), 0.000001);
    }

    @Test
    public void getDefaultAmountWhenEmpty() {
        assertEquals("Amount should be ZERO", CO2Amount.ZERO, values.defaultValueAsAmount());
    }


    @Test
    public void addNote() {
        values.addNote("comment", "Note 1");
        values.addNote("comment", "Note 2");
        assertEquals("Should be 2 notes", 2, values.getNotes().size());
    }
}
