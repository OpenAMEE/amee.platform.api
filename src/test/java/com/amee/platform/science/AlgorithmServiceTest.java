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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;

/**
 * Implements various Algorithm related test cases.
 * <p/>
 * Some test cases are described here: https://docs.google.com/a/amee.cc/Doc?docid=0AVPTOpeCYkq1ZGZxOXE1Y3JfMTFkZDk5eHdjZA&hl=en_GB
 */
public class AlgorithmServiceTest {

    private AlgorithmRunner algorithmService;
    private DataSeries seriesA;
    private DataSeries seriesB;
    private DataSeries seriesC;

    @Before
    public void init() {
        // Create the AlgorithmRunner.
        algorithmService = new AlgorithmRunner();
        // Create DataSeries A.
        seriesA = new DataSeries();
        seriesA.addDataPoint(new DataPoint(new DateTime(2010, 1, 1, 0, 0, 0, 0), new Amount("1")));
        seriesA.addDataPoint(new DataPoint(new DateTime(2010, 1, 3, 0, 0, 0, 0), new Amount("0")));
        seriesA.addDataPoint(new DataPoint(new DateTime(2010, 1, 4, 0, 0, 0, 0), new Amount("0.5")));
        // Create DataSeries B.
        seriesB = new DataSeries();
        seriesB.addDataPoint(new DataPoint(new DateTime(2010, 1, 1, 0, 0, 0, 0), new Amount("0")));
        seriesB.addDataPoint(new DataPoint(new DateTime(2010, 1, 3, 0, 0, 0, 0), new Amount("1")));
        seriesB.addDataPoint(new DataPoint(new DateTime(2010, 1, 4, 0, 0, 0, 0), new Amount("2")));
        // Create DataSeries C.
        seriesC = new DataSeries();
        seriesC.addDataPoint(new DataPoint(new DateTime(2010, 1, 1, 0, 0, 0, 0), new Amount("0")));
        seriesC.addDataPoint(new DataPoint(new DateTime(2010, 1, 2, 0, 0, 0, 0), new Amount("1")));
        seriesC.addDataPoint(new DataPoint(new DateTime(2010, 1, 4, 0, 0, 0, 0), new Amount("3")));
    }

    @Test
    public void reallySimpleAlgorithmOK() throws ScriptException {
        MockAlgorithm algorithm = new MockAlgorithm();
        algorithm.setContent("1");
        Map<String, Object> values = new HashMap<String, Object>();
        assertNotNull("Really simple algorithm should be OK.", algorithmService.evaluate(algorithm, values));
    }

    @Test
    public void emptyAlgorithmNotOK() throws ScriptException {
        MockAlgorithm algorithm = new MockAlgorithm();
        algorithm.setContent("");
        Map<String, Object> values = new HashMap<String, Object>();
        try {
            algorithmService.evaluate(algorithm, values);
            fail("Empty algorithm should NOT be OK.");
        } catch (Throwable t) {
            // swallow
        }
    }

//    @Test
//    public void algorithmCanHandleMissingObject() throws ScriptException {
//        Algorithm algorithm = new Algorithm();
//        algorithm.setContent("if (testObj) { 1; } else { 0; }");
//        Map<String, Object> values = new HashMap<String, Object>();
//        BigDecimal result = new BigDecimal(algorithmService.evaluate(algorithm, values));
//        assertTrue("Algorithm should detect object is missing.", result.equals(new BigDecimal("0")));
//    }
//
//    @Test
//    public void algorithmCanHandlePresentObject() throws ScriptException {
//        Algorithm algorithm = new Algorithm();
//        algorithm.setContent("if (testObj) { 1; } else { 0; }");
//        Map<String, Object> values = new HashMap<String, Object>();
//        values.put("testObj", new Boolean(true));
//        BigDecimal result = new BigDecimal(algorithmService.evaluate(algorithm, values));
//        assertTrue("Algorithm should detect object is present.", result.equals(new BigDecimal("1")));
//    }

    @Test
    public void algorithmCanThrowIllegalArgumentException() throws ScriptException {
        MockAlgorithm algorithm = new MockAlgorithm();
        algorithm.setContent("throw new java.lang.IllegalArgumentException('Bang!');");
        Map<String, Object> values = new HashMap<String, Object>();
        try {
            algorithmService.evaluate(algorithm, values);
            fail("Algorithm should throw IllegalArgumentException.");
        } catch (ScriptException e) {
            IllegalArgumentException iae = AlgorithmRunner.getIllegalArgumentException(e);
            if ((iae == null) || !iae.getMessage().equals("Bang!")) {
                fail("Algorithm should throw IllegalArgumentException with correct message.");
            }
        }
    }

    /**
     * Multiply A by B.
     */
    @Test
    public void shouldMultiplyDataSeriesAAndB() {
        String expectedSeriesAMultipliedByB = "{\"seriesStartDate\":\"2010-01-01T00:00:00.000Z\",\"seriesEndDate\":\"2010-01-04T00:00:00.000Z\",\"dataPoints\":[[\"2010-01-01T00:00:00.000Z\",\"0.0\"],[\"2010-01-03T00:00:00.000Z\",\"0.0\"],[\"2010-01-04T00:00:00.000Z\",\"1.0\"]]}";
        DataSeries seriesAMultipliedByB = seriesA.multiply(seriesB);
        System.out.println(expectedSeriesAMultipliedByB);
        System.out.println(seriesAMultipliedByB.toString());
        assertEquals("Should be able to multiply two DataSeries objects.", seriesAMultipliedByB.toString(), expectedSeriesAMultipliedByB);
    }

    /**
     * Add B to A.
     */
    @Test
    public void shouldAddDataSeriesBToA() {
        String expectedSeriesAPlusB = "{\"seriesStartDate\":\"2010-01-01T00:00:00.000Z\",\"seriesEndDate\":\"2010-01-04T00:00:00.000Z\",\"dataPoints\":[[\"2010-01-01T00:00:00.000Z\",\"1.0\"],[\"2010-01-03T00:00:00.000Z\",\"1.0\"],[\"2010-01-04T00:00:00.000Z\",\"2.5\"]]}";
        DataSeries seriesAPlusB = seriesA.plus(seriesB);
        assertEquals("Should be able to add together two DataSeries objects.", seriesAPlusB.toString(), expectedSeriesAPlusB);
    }

    /**
     * Multiply B by C.
     */
    @Test
    public void shouldMultiplyDataSeriesBAndC() {
        String expectedSeriesBMultipliedByC = "{\"seriesStartDate\":\"2010-01-01T00:00:00.000Z\",\"seriesEndDate\":\"2010-01-04T00:00:00.000Z\",\"dataPoints\":[[\"2010-01-01T00:00:00.000Z\",\"0.0\"],[\"2010-01-02T00:00:00.000Z\",\"0.0\"],[\"2010-01-03T00:00:00.000Z\",\"1.0\"],[\"2010-01-04T00:00:00.000Z\",\"6.0\"]]}";
        DataSeries seriesBMultipliedByC = seriesB.multiply(seriesC);
        System.out.println(expectedSeriesBMultipliedByC);
        System.out.println(seriesBMultipliedByC.toString());
        assertEquals("Should be able to multiply two DataSeries objects.", seriesBMultipliedByC.toString(), expectedSeriesBMultipliedByC);
    }

    /**
     * Add C to B.
     */
    @Test
    public void shouldAddDataSeriesCToB() {
        String expectedSeriesBPlusC = "{\"seriesStartDate\":\"2010-01-01T00:00:00.000Z\",\"seriesEndDate\":\"2010-01-04T00:00:00.000Z\",\"dataPoints\":[[\"2010-01-01T00:00:00.000Z\",\"0.0\"],[\"2010-01-02T00:00:00.000Z\",\"1.0\"],[\"2010-01-03T00:00:00.000Z\",\"2.0\"],[\"2010-01-04T00:00:00.000Z\",\"5.0\"]]}";
        DataSeries seriesBPlusC = seriesB.plus(seriesC);
        assertEquals("Should be able to add together two DataSeries objects.", seriesBPlusC.toString(), expectedSeriesBPlusC);
    }

    /**
     * Use a DataSeries in an Algorithm without a startDate and endDate.
     */
    @Test
    public void shouldUseDataSeries() {
        MockAlgorithm algorithm = new MockAlgorithm();
        algorithm.setContent("series.integrate();");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("series", seriesA.copy());
        try {
            Amounts result = algorithmService.evaluate(algorithm, values);
            assertEquals("Should be able to use DataSeries.integrate() without a startDate and endDate.", 0.6666666666666666, result.getDefaultAmountAsDouble());
        } catch (ScriptException e) {
            fail("Caught ScriptException: " + e.getMessage());
        }
    }

    /**
     * Use a DataSeries in an Algorithm with a startDate and endDate.
     */
    @Test
    public void shouldUseDataSeriesWithDateRange() {
        MockAlgorithm algorithm = new MockAlgorithm();
        algorithm.setContent("series.integrate();");
        DataSeries series = seriesA.copy();
        series.setSeriesStartDate(new DateTime(2010, 1, 2, 0, 0, 0, 0));
        series.setSeriesEndDate(new DateTime(2010, 1, 5, 0, 0, 0, 0));
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("series", series);
        try {
            Amounts result = algorithmService.evaluate(algorithm, values);
            assertEquals("Should be able to use DataSeries.integrate() with a startDate and endDate.", 0.5, result.getDefaultAmountAsDouble());
        } catch (ScriptException e) {
            fail("Caught ScriptException: " + e.getMessage());
        }
    }

    /**
     * Use multiple DataSeries in an Algorithm with a startDate and endDate.
     */
    @Test
    public void shouldUseMultipleDataSeriesWithDateRange() {
        MockAlgorithm algorithm = new MockAlgorithm();
        algorithm.setContent(
                "series = seriesA.plus(seriesB).plus(seriesC);\n" +
                        "series.setSeriesStartDate(startDate);\n" +
                        "series.setSeriesEndDate(endDate);\n" +
                        "series.integrate()");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("startDate", new DateTime(2010, 1, 2, 0, 0, 0, 0));
        values.put("endDate", new DateTime(2010, 1, 5, 0, 0, 0, 0));
        values.put("seriesA", seriesA.copy());
        values.put("seriesB", seriesB.copy());
        values.put("seriesC", seriesC.copy());
        try {
            Amounts result = algorithmService.evaluate(algorithm, values);
            System.out.println(result);
            assertEquals("Should be able to use DataSeries.integrate() with a startDate and endDate.", 3.1666666666666665, result.getDefaultAmountAsDouble());
        } catch (ScriptException e) {
            fail("Caught ScriptException: " + e.getMessage());
        }
    }

    /**
     * New style algorithm. Multiple return values.
     *
     * @throws Exception
     */
    @Test
    public void multipleReturnValues() throws Exception {
        MockAlgorithm mockAlgorithm = new MockAlgorithm();
        StringBuilder content = new StringBuilder();
        content.append("returnValues.putAmount('CO2', 'kg', 'month', 5.43);");
        content.append("returnValues.putAmount('CO2e', 'kg', 'month', 1.23);");
        content.append("returnValues.setDefaultType('CO2');");
        content.append("returnValues.addNote('Note 1');");
        mockAlgorithm.setContent(content.toString());

        Map<String, Object> values = new HashMap<String, Object>();

        Amounts result = algorithmService.evaluate(mockAlgorithm, values);
        assertEquals("Should have 2 amounts in result", 2, result.getAmounts().size());
        assertEquals("Incorrect default amount", 5.43, result.getDefaultAmountAsDouble(), 0.000001);
        assertEquals("Should have 1 note", 1, result.getNotes().size());
        assertEquals("Incorrect note value", "Note 1", result.getNotes().get(0));
    }

    /**
     * Old style algorithm. Single return value.
     *
     * @throws Exception
     */
    @Test
    public void testSingleReturnValue() throws Exception {
        MockAlgorithm mockAlgorithm = new MockAlgorithm();
        mockAlgorithm.setContent("1.23");
        Map<String, Object> values = new HashMap<String, Object>();

        Amounts result = algorithmService.evaluate(mockAlgorithm, values);
        assertEquals("Should have 1 result", 1, result.getAmounts().size());
        assertEquals("Incorrect return value", 1.23, result.getDefaultAmountAsDouble(), 0.000001);
        assertTrue("Notes should be empty", result.getNotes().isEmpty());
    }

    @Test(expected = AlgorithmException.class)
    public void testNullReturnValue() throws Exception {
        MockAlgorithm mockAlgorithm = new MockAlgorithm();
        mockAlgorithm.setContent(null);
        Map<String, Object> values = new HashMap<String, Object>();

        algorithmService.evaluate(mockAlgorithm, values);
    }
}