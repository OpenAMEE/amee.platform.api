/*
 * This file is part of AMEE.
 *
 * Copyright (c) 2007, 2008, 2009 AMEE UK LIMITED (help@amee.com).
 *
 * AMEE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *                                                                      
 * AMEE is free software and is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by http://www.dgen.net.
 * Website http://www.amee.cc
 */
package com.amee.platform.science;

import org.junit.Test;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import static junit.framework.Assert.*;

public class AmountTest {
    private static final AmountUnit KILOGRAM = new AmountUnit(SI.KILOGRAM);
    private static final AmountPerUnit YEAR = new AmountPerUnit(NonSI.YEAR);
    private static final AmountPerUnit MONTH = new AmountPerUnit(NonSI.MONTH);

    @Test
    public void testEqualsAndHashCode() {
        Amount d1 = new Amount("123.45", AmountUnit.valueOf("kWh"));
        Amount d2 = new Amount(123.45, AmountUnit.valueOf("kWh"));
        assertEquals("Objects should be equal", d1, d2);
        assertEquals("HashCodes should be equal", d1.hashCode(), d2.hashCode());

        // Different units
        Amount d3 = new Amount("123.45", AmountUnit.valueOf("MWh"));
        assertFalse("Different units should not be equal: " + d3.getUnit() + ", " + d1.getUnit(), d1.equals(d3));
        assertFalse("HashCodes should not be equal", d1.hashCode() == d3.hashCode());

        // Different values
        Amount d4 = new Amount(54.321, AmountUnit.valueOf("MWh"));
        assertFalse("Different values should not be equal: " + d3.getValue() + ", " + d3.getValue(), d4.equals(d3));
        assertFalse("HashCodes should not be equal", d4.hashCode() == d3.hashCode());
    }

    @Test
    public void testValidOperations() {

        // Amounts have the same units.
        Amount d1 = new Amount("123.45", AmountUnit.valueOf("kWh"));
        Amount d2 = new Amount(123.45, AmountUnit.valueOf("kWh"));
        Amount d3 = new Amount(123.45);

        Amount expected = new Amount(123.45 + 123.45, AmountUnit.valueOf("kWh"));
        Amount actual = d1.add(d2);
        assertEquals(expected, actual);

        expected = new Amount(123.45 - 123.45, AmountUnit.valueOf("kWh"));
        actual = d1.subtract(d2);
        assertEquals(expected, actual);

        expected = new Amount(123.45 * 123.45, AmountUnit.valueOf("kWh"));
        actual = d1.multiply(d2);
        assertEquals(expected, actual);

        expected = new Amount(123.45 / 123.45, AmountUnit.valueOf("kWh"));
        actual = d1.divide(d2);
        assertEquals(expected, actual);

        expected = new Amount(123.45 + 123.45, AmountUnit.valueOf("kWh"));
        actual = d1.add(d3);
        assertEquals("Should be able to add kWh and dimensionless unit", expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidAdd() {
        Amount d1 = new Amount(54.321, AmountUnit.valueOf("MWh"));
        Amount d2 = new Amount(54.321, AmountUnit.valueOf("kWh"));
        d1.add(d2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSubtract() {
        Amount d1 = new Amount(54.321, AmountUnit.valueOf("MWh"));
        Amount d2 = new Amount(54.321, AmountUnit.valueOf("kWh"));
        d1.subtract(d2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidMultiply() {
        Amount d1 = new Amount(54.321, AmountUnit.valueOf("MWh"));
        Amount d2 = new Amount(54.321, AmountUnit.valueOf("kWh"));
        d1.multiply(d2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDivide() {
        Amount d1 = new Amount(54.321, AmountUnit.valueOf("MWh"));
        Amount d2 = new Amount(54.321, AmountUnit.valueOf("kWh"));
        d1.divide(d2);
    }

    @Test
    public void testConvert() {
        // kWh -> TJ (http://www.wolframalpha.com/input/?i=0.1+kwh+in+TJ)
        Amount kwh = new Amount(0.1, AmountUnit.valueOf("kWh"));
        Amount expected = new Amount("3.6e-7", AmountUnit.valueOf("TJ"));
        Amount actual = kwh.convert(AmountUnit.valueOf("TJ"));
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertPerUnit() {
        // YEAR -> MONTH (http://www.wolframalpha.com/input/?i=123.456+kg%2Fyear+in+kg%2Fmonth)
        Amount kgPerYear = new Amount(123.456, new AmountCompoundUnit(KILOGRAM, YEAR));
        Amount expected = new Amount(10.288, MONTH);
        Amount actual = kgPerYear.convert(MONTH);
        assertEquals(expected, actual);
    }

}
