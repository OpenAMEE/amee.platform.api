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

import static junit.framework.Assert.*;

public class DecimalTest {

    @Test
    public void testEqualsAndHashCode() {
        Amount d1 = new Amount("123.45", AmountUnit.valueOf("kWh"));
        Amount d2 = new Amount(123.45, AmountUnit.valueOf("kWh"));
        assertEquals("Objects should be equal", d1, d2);
        assertEquals("HashCodes should be equal", d1.hashCode(), d2.hashCode());

        Amount d3 = new Amount("123.45", AmountUnit.valueOf("MWh"));
        assertFalse("Objects should not be equal", d1.equals(d3));
        assertFalse("HashCodes should not be equal", d1.hashCode() == d3.hashCode());

        Amount d4 = new Amount(54.321, AmountUnit.valueOf("MWh"));
        assertFalse(d4.equals(d3));
        assertFalse("HashCodes should not be equal", d4.hashCode() == d3.hashCode());
    }

    @Test
    public void testOperations() {
        Amount d1 = new Amount("123.45", AmountUnit.valueOf("kWh"));
        Amount d2 = new Amount(123.45, AmountUnit.valueOf("kWh"));

        // TODO: These operations discard the unit. Is this correct?
        Amount expected = new Amount(123.45 + 123.45);
        Amount actual = d1.add(d2);
        assertEquals(expected, actual);

        expected = new Amount(123.45 - 123.45);
        actual = d1.subtract(d2);
        assertEquals(expected, actual);

        expected = new Amount(123.45 * 123.45);
        actual = d1.multiply(d2);
        assertEquals(expected, actual);

        expected = new Amount(123.45 / 123.45);
        actual = d1.divide(d2);
        assertEquals(expected, actual);
    }

}
