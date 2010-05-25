package com.amee.platform.science;

import org.junit.Test;
import static org.junit.Assert.*;

public class ReturnValuesTest {

    @Test
    public void testAddAmount() {
        ReturnValues values = new ReturnValues();
        values.addAmount("CO2e", "kg", "month", 123.45);
        assertTrue("Failed to add amount", values.getAmounts().size() == 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidDefaultType() {
        ReturnValues values = new ReturnValues();
        values.setDefaultType("CO2e");
    }

    @Test
    public void testSetDefaultType() {
        ReturnValues values = new ReturnValues();
        values.addAmount("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        assertEquals("Default type should be CO2e", "CO2e", values.getDefaultType());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetDefaultAmountNoneSet() {
        ReturnValues values = new ReturnValues();
        values.addAmount("CO2e", "kg", "month", 123.45);
        assertEquals("Default type should be CO2e", "CO2e", values.getDefaultType());
    }

    @Test
    public void testGetDefaultAmount() {
        ReturnValues values = new ReturnValues();
        values.addAmount("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        values.addAmount("CO2", "kg", "month", 54.321);

        Amount defaultAmount = values.getDefaultAmount();
        assertEquals("Default amount should be CO2e", 123.45, defaultAmount.getValue(), 0.000001);
    }

    @Test
    public void testGetDefaultAmountAsDouble() {
        ReturnValues values = new ReturnValues();
        values.addAmount("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        values.addAmount("CO2", "kg", "month", 54.321);

        assertEquals(123.45, values.getDefaultAmountAsDouble(), 0.000001);
    }
}
