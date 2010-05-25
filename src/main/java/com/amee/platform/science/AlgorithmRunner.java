package com.amee.platform.science;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.org.mozilla.javascript.internal.JavaScriptException;
import sun.org.mozilla.javascript.internal.NativeJavaObject;
import sun.org.mozilla.javascript.internal.Scriptable;
import sun.org.mozilla.javascript.internal.ScriptableObject;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;

public class AlgorithmRunner {

    private final Log scienceLog = LogFactory.getLog("science");

    // The ScriptEngine for the JavaScript context.
    private final ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
    private static final String DEFAULT_TYPE = "CO2";
    private static final String DEFAULT_UNIT = "kg";
    private static final String DEFAULT_PER_UNIT = "month";

    /**
     * Evaluate an Algorithm.
     *
     * @param algorithm - the Algorithm to evaluate
     * @param values    - map of key/value input pairs
     * @return the values calculated by the Algorithm.
     * @throws ScriptException - re-throws exception generated by script execution
     * @throws AlgorithmException if the algorithm returns no value.
     */
    public ReturnValues evaluate(Algorithm algorithm, Map<String, Object> values) throws ScriptException {
        Bindings bindings = getBindings();
        bindings.putAll(values);
        bindings.put("logger", scienceLog);

        ReturnValues returnValues = new ReturnValues();
        bindings.put("returnValues", returnValues);

        Object result = algorithm.getCompiledScript(getEngine()).eval(bindings);

        // First check for multiple return values
        if (!returnValues.getAmounts().isEmpty()) {
            return returnValues;
        }

        // If it is an old-style single return value algorithm, wrap it in a ReturnValues object.
        if (result != null) {
            returnValues.addAmount(DEFAULT_TYPE, DEFAULT_UNIT, DEFAULT_PER_UNIT, Double.parseDouble(result.toString()));
            returnValues.setDefaultType(DEFAULT_TYPE);
            return returnValues;
        }

        // The algorithm returned no value.       
        throw new AlgorithmException("Algorithm result is null (" + algorithm.getLabel() + ").");
    }

    /**
     * Get the Bindings instances from the ScriptEngine.
     *
     * @return the Bindings instance
     */
    protected Bindings getBindings() {
        return getEngine().createBindings();
    }

    /**
     * Get the ScriptEngine.
     *
     * @return the ScriptEngine instance
     */
    protected ScriptEngine getEngine() {
        return engine;
    }

    /**
     * Returns an IllegalArgumentException that is wrapped in a ScriptException.
     *
     * @param e ScriptException to look within
     * @return an IllegalArgumentException instance or null
     */
    public static IllegalArgumentException getIllegalArgumentException(ScriptException e) {

        // Must have a cause.
        if (e.getCause() == null) {
            return null;
        }

        // If cause is an IllegalArgumentException return that.
        if (e.getCause() instanceof IllegalArgumentException) {
            return (IllegalArgumentException) e.getCause();
        }

        // Is there a wrapped JavaScriptException?
        if (!(e.getCause() instanceof JavaScriptException)) {
            return null;
        }

        // Now switch to working with wrapped JavaScriptException.
        JavaScriptException jse = (JavaScriptException) e.getCause();

        // JavaScriptException must have a value.
        if (jse.getValue() == null) {
            return null;
        }

        // Get value in which the IllegalArgumentException may be wrapped.
        Object value = jse.getValue();

        // Unwrap NativeError to NativeJavaObject first, if possible.
        if (value instanceof Scriptable) {
            Object njo = ScriptableObject.getProperty(((Scriptable) value), "rhinoException");
            if (njo instanceof NativeJavaObject) {
                value = njo;
            } else {
                njo = ScriptableObject.getProperty(((Scriptable) value), "javaException");
                if (njo instanceof NativeJavaObject) {
                    value = njo;
                }
            }
        }

        // If value is a NativeJavaObject, unwrap to the Java object.
        if (value instanceof NativeJavaObject) {
            value = ((NativeJavaObject) value).unwrap();
        }

        // Did we find an IllegalArgumentException?
        if (value instanceof IllegalArgumentException) {
            return (IllegalArgumentException) value;
        } else {
            return null;
        }
    }
}