package com.amee.platform.science;

import org.junit.Test;
import static org.junit.Assert.*;

public class AmountsTest {

    @Test
    public void putAmount() {
        Amounts values = new Amounts();
        values.putAmount("CO2e", "kg", "month", 123.45);
        assertTrue("Failed to add amount", values.getAmounts().size() == 1);
    }

    @Test
    public void putCo2Amount() {
        Amounts values = new Amounts();
        values.putCo2Amount(1.234);
        assertEquals("Default type should be CO2", "CO2", values.getDefaultType());
        assertEquals(1.234, values.getAmounts().get("CO2").getValue(), 0.000001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setInvalidDefaultType() {
        Amounts values = new Amounts();
        values.setDefaultType("CO2e");
    }

    @Test
    public void setDefaultType() {
        Amounts values = new Amounts();
        values.putAmount("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        assertEquals("Default type should be CO2e", "CO2e", values.getDefaultType());
    }

    @Test
    public void getDefaultAmount() {
        Amounts values = new Amounts();
        values.putAmount("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        values.putAmount("CO2", "kg", "month", 54.321);

        Amount defaultAmount = values.getDefaultAmount();
        assertEquals("Default amount should be CO2e", 123.45, defaultAmount.getValue(), 0.000001);
    }

    @Test
    public void getDefaultAmountAsDouble() {
        Amounts values = new Amounts();
        values.putAmount("CO2e", "kg", "month", 123.45);
        values.setDefaultType("CO2e");
        values.putAmount("CO2", "kg", "month", 54.321);

        assertEquals(123.45, values.getDefaultAmountAsDouble(), 0.000001);
    }
}
