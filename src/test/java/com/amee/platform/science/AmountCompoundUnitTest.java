package com.amee.platform.science;

import org.junit.Test;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import static junit.framework.Assert.*;

public class AmountCompoundUnitTest {

    private static final AmountUnit KILOGRAM = new AmountUnit(SI.KILOGRAM);
    private static final AmountPerUnit YEAR = new AmountPerUnit(NonSI.YEAR);
    private static final AmountPerUnit MONTH = new AmountPerUnit(NonSI.MONTH);

    @Test
    public void testEqualsAndHashCode() {
        AmountCompoundUnit acu1 = AmountCompoundUnit.valueOf(KILOGRAM, MONTH);
        AmountCompoundUnit acu2 = AmountCompoundUnit.valueOf(KILOGRAM, MONTH);
        assertEquals(acu1, acu2);
        assertEquals(acu1.hashCode(), acu2.hashCode());

        AmountCompoundUnit acu3 = AmountCompoundUnit.valueOf(KILOGRAM, YEAR);
        assertFalse(acu1.equals(acu3));
        assertFalse(acu1.hashCode() == acu3.hashCode());
    }
}
