package com.amee.platform.science;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds a collection of {@link ReturnValue} objects added by Algorithms.
 * The first ReturnValue added is marked as the default but this may be changed by calling setDefaultType.
 * A list of Note objects may be added containing additional textual information about the ReturnValues.
 *
 * A new ReturnValues object is provided to each Algorithm. See: {@link AlgorithmRunner}.
 *
 * Note: if an algorithm is unable to calculate a result for a particular GHG null will be stored.
 *
 * Not thread-safe.
 *
 * TODO: Consider implementing Map interface.
 */
public class ReturnValues {

    /** The return values, indexed by GHG type. */
    private Map<String, ReturnValue> returnValues = new HashMap<String, ReturnValue>();

    /** The default GHG type. */
    private String defaultType;

    /** Optional text notes. */
    private List<Note> notes = new ArrayList<Note>();

    public Map<String, ReturnValue> getReturnValues() {
        return returnValues;
    }

    /**
     * Add an amount to the return values.
     *
     * This method is called by algorithms.
     *
     * @param type the GHG type to add, eg 'CO2'.
     * @param unit the unit, eg 'kg'.
     * @param perUnit the per unit, eg 'month'.
     * @param value the value of the amount.
     */
    public void putValue(String type, String unit, String perUnit, double value) {
        ReturnValue returnValue = new ReturnValue(type, unit, perUnit, value);
        returnValues.put(type, returnValue);

        // We make the first added amount the default.
        if (returnValues.size() == 1) {
            setDefaultType(type);
        }
    }

    /**
     * Add an empty amount to the return values.
     *
     * This method is called by algorithms when we cannot calculate a value for a certain type.
     *
     * @param type the GHG type to add, eg 'CH4'.
     */
    public void putEmptyValue(String type) {
        returnValues.put(type, null);

        // We still make the first added amount the default even if null.
        if (returnValues.size() == 1) {
            setDefaultType(type);
        }
    }

    /**
     * Mark the given GHG type as the default.
     *
     * @param type the type to be marked as default.
     * @throws IllegalArgumentException if there are no values of the given type.
     */
    public void setDefaultType(String type) {
        if (returnValues.containsKey(type)) {
            defaultType = type;
        } else {
            throw new IllegalArgumentException("There are no values of type " + type);
        }
    }

    /**
     * Get the default GHG type.
     *
     * @return the default type.
     * @throws IllegalStateException if there is no default type set.
     */
    public String getDefaultType() {
        if (defaultType == null) {
            throw new IllegalStateException("There is no default type");
        }
        return defaultType;
    }

    /**
     * Get the default value.
     *
     * @return the default ReturnValue. May return null.
     */
    public ReturnValue getDefaultValue() {
        if (defaultType == null) {
            throw new IllegalStateException("There is no default type");
        }
        if (!returnValues.containsKey(defaultType)) {
            throw new IllegalArgumentException("There are no values of type " + defaultType);
        } else {
            return returnValues.get(defaultType);
        }
    }

    /**
     * Get the default value as an Amount
     *
     * @return the default Amount or ZERO if there are no ReturnValues or the default value is null.
     * @throws IllegalStateException if there is no default type set.
     */
    public CO2Amount defaultValueAsAmount() {
        if (returnValues.isEmpty()) {
            return CO2Amount.ZERO;
        }
        if (defaultType == null) {
            throw new IllegalStateException("There is no default type");
        }
        if (returnValues.get(defaultType) == null) {
            return CO2Amount.ZERO;
        }
        ReturnValue defaultValue = returnValues.get(defaultType);
        return defaultValue.toAmount();
    }

    /**
     * Get the numeric value of the default ReturnValue.
     *
     * @return the value of the default Amount.
     */
    public double defaultValueAsDouble() {
        if (defaultType == null) {
            throw new IllegalStateException("There is no default type");
        }
        if (returnValues.get(defaultType) == null) {
            return 0.0;
        } else {
            return returnValues.get(defaultType).getValue();
        }
    }

    /**
     * Add a text note.
     *
     * @param type the type of note, eg 'comment'.
     * @param value the value of the note.
     */
    public void addNote(String type, String value) {
        notes.add(new Note(type, value));
    }

    /**
     * Get the notes.
     *
     * @return the List of notes.
     */
    public List<Note> getNotes() {
        return notes;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a String representation of this object.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
            append("returnValues", returnValues).
            append("defaultType", defaultType).
            append("notes", notes).
            toString();
    }

    public boolean hasReturnValues() {
        return !returnValues.isEmpty();
    }

    /**
     * Returns the number of ReturnValues in this collection.
     *
     * @return the number of ReturnValues in this collection.
     */
    public int size() {
        return returnValues.size();
    }
}
