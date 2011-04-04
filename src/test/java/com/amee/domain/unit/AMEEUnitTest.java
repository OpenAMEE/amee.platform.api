package com.amee.domain.unit;

import com.amee.platform.science.AmountUnit;
import org.junit.Test;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AMEEUnitTest {

    @Test
    public void canGetCorrectUnit() {
        AMEEUnit u = new AMEEUnit("kilogram", "kg");
        assertTrue(SI.KILOGRAM.equals(u.getInternalUnit()));
    }

    @Test
    public void canGetCorrectAmountUnit() {
        AMEEUnit u = new AMEEUnit("kilogram", "kg");
        AmountUnit au = AmountUnit.valueOf("kg");
        assertTrue(au.equals(u.getAmountUnit()));
    }

    @Test
    public void canUseNonASCIISymbols() {
        AMEEUnit u1 = new AMEEUnit("Ångström", "\u00C5");
        AMEEUnit u2 = new AMEEUnit("Ångström", "\u00C5", "ang");
        assertTrue(NonSI.ANGSTROM.equals(u1.getInternalUnit()));
        assertTrue(NonSI.ANGSTROM.equals(u2.getInternalUnit()));
        assertEquals(u1.getSymbol(), "\u00C5");
        assertEquals(u2.getSymbol(), "ang");
    }
}
