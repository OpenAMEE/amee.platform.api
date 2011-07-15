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
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Implements various Algorithm related test cases.
 * <p/>
 * Some test cases are described here: https://docs.google.com/a/amee.cc/Doc?docid=0AVPTOpeCYkq1ZGZxOXE1Y3JfMTFkZDk5eHdjZA&hl=en_GB
 */
@RunWith(MockitoJUnitRunner.class)
public class AlgorithmServiceTest {

    private AlgorithmRunner algorithmService;
    private DataSeries seriesA;
    private DataSeries seriesB;
    private DataSeries seriesC;
    
    @Mock private Algorithm mockAlgorithm;

    @Before
    public void init() throws ScriptException {
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
        final String algorithmContent = "1";
        stubGetCompiledScript(algorithmContent);

        // The algorithm input values.
        Map<String, Object> values = new HashMap<String, Object>();
        assertNotNull("Really simple algorithm should be OK.", algorithmService.evaluate(mockAlgorithm, values));
    }

    @Test
    public void emptyAlgorithmNotOK() throws ScriptException {
        final String algorithmContent = "";
        stubGetCompiledScript(algorithmContent);
        Map<String, Object> values = new HashMap<String, Object>();
        try {
            algorithmService.evaluate(mockAlgorithm, values);
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
        final String algorithmContent = "throw new java.lang.IllegalArgumentException('Bang!');";
        stubGetCompiledScript(algorithmContent);
        
        Map<String, Object> values = new HashMap<String, Object>();
        try {
            algorithmService.evaluate(mockAlgorithm, values);
            fail("Algorithm should throw IllegalArgumentException.");
        } catch (ScriptException e) {
            IllegalArgumentException iae = AlgorithmRunner.getIllegalArgumentException(e);
            if ((iae == null) || !iae.getMessage().equals("Bang!")) {
                fail("Algorithm should throw IllegalArgumentException with correct message.");
            }
        }
    }

    /**
     * Use a DataSeries in an Algorithm without a startDate and endDate.
     */
    @Test
    public void shouldUseDataSeries() throws ScriptException {
        final String algorithmContent = "series.integrate();";
        stubGetCompiledScript(algorithmContent);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("series", seriesA.copy());
        try {
            ReturnValues result = algorithmService.evaluate(mockAlgorithm, values);
            assertEquals("Should be able to use DataSeries.integrate() without a startDate and endDate.", 0.6666666666666666, result.defaultValueAsDouble());
        } catch (ScriptException e) {
            fail("Caught ScriptException: " + e.getMessage());
        }
    }

    /**
     * Use a DataSeries in an Algorithm with a startDate and endDate.
     */
    @Test
    public void shouldUseDataSeriesWithDateRange() throws ScriptException {
        final String algorithmContent = "series.integrate();";
        stubGetCompiledScript(algorithmContent);

        DataSeries series = seriesA.copy();
        series.setSeriesStartDate(new DateTime(2010, 1, 2, 0, 0, 0, 0));
        series.setSeriesEndDate(new DateTime(2010, 1, 5, 0, 0, 0, 0));

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("series", series);
        try {
            ReturnValues result = algorithmService.evaluate(mockAlgorithm, values);
            assertEquals("Should be able to use DataSeries.integrate() with a startDate and endDate.", 0.5, result.defaultValueAsDouble());
        } catch (ScriptException e) {
            fail("Caught ScriptException: " + e.getMessage());
        }
    }

    /**
     * Use multiple DataSeries in an Algorithm with a startDate and endDate.
     */
    @Test
    public void shouldUseMultipleDataSeriesWithDateRange() throws ScriptException {
        final String algorithmContent =
                "series = seriesA.plus(seriesB).plus(seriesC);\n" +
                        "series.setSeriesStartDate(startDate);\n" +
                        "series.setSeriesEndDate(endDate);\n" +
                        "series.integrate()";
        stubGetCompiledScript(algorithmContent);

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("startDate", new DateTime(2010, 1, 2, 0, 0, 0, 0));
        values.put("endDate", new DateTime(2010, 1, 5, 0, 0, 0, 0));
        values.put("seriesA", seriesA.copy());
        values.put("seriesB", seriesB.copy());
        values.put("seriesC", seriesC.copy());
        try {
            ReturnValues result = algorithmService.evaluate(mockAlgorithm, values);
            System.out.println(result);
            assertEquals("Should be able to use DataSeries.integrate() with a startDate and endDate.", 3.1666666666666665, result.defaultValueAsDouble());
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
        StringBuilder content = new StringBuilder();
        content.append("returnValues.putValue('CO2', 'kg', 'month', 5.43);");
        content.append("returnValues.putValue('CO2e', 'kg', 'month', 1.23);");
        content.append("returnValues.setDefaultType('CO2');");
        content.append("returnValues.addNote('comment', 'Note 1');");
        final String algorithmContent = content.toString();
        stubGetCompiledScript(algorithmContent);

        Map<String, Object> values = new HashMap<String, Object>();

        ReturnValues result = algorithmService.evaluate(mockAlgorithm, values);
        assertEquals("Should have 2 amounts in result", 2, result.getReturnValues().size());
        assertEquals("Incorrect default amount", 5.43, result.defaultValueAsDouble(), 0.000001);
        assertEquals("Should have 1 note", 1, result.getNotes().size());
        assertEquals("Incorrect note value", "Note 1", result.getNotes().get(0).getValue());
    }

    /**
     * Old style algorithm. Single return value.
     *
     * @throws Exception
     */
    @Test
    public void testSingleReturnValue() throws Exception {
        final String algorithmContent = "1.23";
        stubGetCompiledScript(algorithmContent);
        
        Map<String, Object> values = new HashMap<String, Object>();

        ReturnValues result = algorithmService.evaluate(mockAlgorithm, values);
        assertEquals("Should have 1 result", 1, result.getReturnValues().size());
        assertEquals("Incorrect return value", 1.23, result.defaultValueAsDouble(), 0.000001);
        assertTrue("Notes should be empty", result.getNotes().isEmpty());
    }

    @Test(expected = AlgorithmException.class)
    public void testNullReturnValue() throws Exception {
        final String algorithmContent = "null";
        stubGetCompiledScript(algorithmContent);
        
        Map<String, Object> values = new HashMap<String, Object>();
        algorithmService.evaluate(mockAlgorithm, values);
    }

    private void stubGetCompiledScript(final String AlgorithmContent) throws ScriptException {
        when(mockAlgorithm.getCompiledScript(any(ScriptEngine.class))).thenAnswer(new Answer<CompiledScript>() {
            @Override
            public CompiledScript answer(InvocationOnMock invocation) throws ScriptException {
                Object[] args = invocation.getArguments();
                return ((Compilable) args[0]).compile(AlgorithmContent);
            }
        });
    }
}