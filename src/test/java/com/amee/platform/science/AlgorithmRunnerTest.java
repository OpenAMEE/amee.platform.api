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
public class AlgorithmRunnerTest {

    private AlgorithmRunner algorithmRunner;
    private DataSeries seriesA;
    private DataSeries seriesB;
    private DataSeries seriesC;
    
    @Mock private Algorithm mockAlgorithm;

    @Before
    public void init() throws ScriptException {

        // Create the AlgorithmRunner.
        algorithmRunner = new AlgorithmRunner();

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
        assertNotNull("Really simple algorithm should be OK.", algorithmRunner.evaluate(mockAlgorithm, values));
    }

    @Test
    public void emptyAlgorithmNotOK() throws ScriptException {
        final String algorithmContent = "";
        stubGetCompiledScript(algorithmContent);
        Map<String, Object> values = new HashMap<String, Object>();
        try {
            algorithmRunner.evaluate(mockAlgorithm, values);
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
            algorithmRunner.evaluate(mockAlgorithm, values);
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
        ReturnValues result = algorithmRunner.evaluate(mockAlgorithm, values);
        assertEquals("Should be able to use DataSeries.integrate() without a startDate and endDate.", 0.6666666666666666, result.defaultValueAsDouble());
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
        ReturnValues result = algorithmRunner.evaluate(mockAlgorithm, values);
        assertEquals("Should be able to use DataSeries.integrate() with a startDate and endDate.", 0.5, result.defaultValueAsDouble());
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

        ReturnValues result = algorithmRunner.evaluate(mockAlgorithm, values);
//        System.out.println(result);
        assertEquals("Should be able to use DataSeries.integrate() with a startDate and endDate.", 3.1666666666666665, result.defaultValueAsDouble());
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

        ReturnValues result = algorithmRunner.evaluate(mockAlgorithm, values);
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

        ReturnValues result = algorithmRunner.evaluate(mockAlgorithm, values);
        assertEquals("Should have 1 result", 1, result.getReturnValues().size());
        assertEquals("Incorrect return value", 1.23, result.defaultValueAsDouble(), 0.000001);
        assertTrue("Notes should be empty", result.getNotes().isEmpty());
    }

    @Test(expected = AlgorithmException.class)
    public void testNullReturnValue() throws Exception {
        final String algorithmContent = "null";
        stubGetCompiledScript(algorithmContent);
        
        Map<String, Object> values = new HashMap<String, Object>();
        algorithmRunner.evaluate(mockAlgorithm, values);
    }

    /**
     * One return value could not be calculated.
     */
    @Test
    public void emptyReturnValues() throws Exception {
        StringBuilder content = new StringBuilder();
        content.append("returnValues.putValue('CO2', 'kg', 'month', 5.43);");
        content.append("returnValues.putEmptyValue('CH4');");
        content.append("returnValues.setDefaultType('CO2');");
        content.append("returnValues.addNote('error', 'CH4 value could not be calculated.');");
        final String algorithmContent = content.toString();
        stubGetCompiledScript(algorithmContent);

        Map<String, Object> values = new HashMap<String, Object>();

        ReturnValues result = algorithmRunner.evaluate(mockAlgorithm, values);
        assertEquals("Should have 2 return values", 2, result.getReturnValues().size());
        assertEquals("Should have 1 error note", 1, result.getNotes().size());
        assertNull("Should have an empty value for CH4", result.getReturnValues().get("CH4").getValue());
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