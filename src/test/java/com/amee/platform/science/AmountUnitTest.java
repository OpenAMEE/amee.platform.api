package com.amee.platform.science;

import org.junit.Test;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import static junit.framework.Assert.*;

public class AmountUnitTest {

    private static final AmountUnit KILOGRAM = new AmountUnit(SI.KILOGRAM);
    private static final AmountPerUnit YEAR = new AmountPerUnit(NonSI.YEAR);

    @Test
    public void testEqualsAndHashCode() {
        AmountUnit au1 = AmountUnit.valueOf("kWh");
        AmountUnit au2 = AmountUnit.valueOf("kWh");
        assertEquals(au1, au2);
        assertEquals(au1.hashCode(), au2.hashCode());

        AmountUnit au3 = AmountUnit.valueOf("MWh");
        assertFalse(au1.equals(au3));
        assertFalse(au1.hashCode() == au3.hashCode());

        AmountUnit apu1 = AmountPerUnit.valueOf("month");
        assertFalse(au1.equals(apu1));
        assertFalse(au1.hashCode() == apu1.hashCode());

        AmountUnit acu1 = AmountCompoundUnit.valueOf(KILOGRAM, YEAR);
        assertFalse(au1.equals(acu1));
        assertFalse(au1.hashCode() == acu1.hashCode());
    }
}
