package com.amee.platform.science;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnValues {
    // These are the default values used in CO2Amount
    private static final String CO2_DEFAULT_TYPE = "CO2";
    private static final String CO2_DEFAULT_UNIT = "kg";
    private static final String CO2_DEFAULT_PER_UNIT = "year";

    /** The return Amounts, indexed by GHG type. */
    private Map<String, ReturnValue> returnValues = new HashMap<String, ReturnValue>();

    /** The default GHG type. */
    private String defaultType;

    /** Optional text notes. */
    private List<Note> notes = new ArrayList<Note>();

    public Map<String, ReturnValue> getReturnValues() {
        return returnValues;
    }

//    /**
//     * Puts a CO2 Amount using default units (kg/year)
//     *
//     * @param value amount of CO2 in kg/year.
//     */
//    public void putCo2Amount(double value) {
//        putAmount(CO2_DEFAULT_TYPE, CO2_DEFAULT_UNIT, CO2_DEFAULT_PER_UNIT, value);
//    }
//
//    /**
//     * Get the CO2 Amount.
//     *
//     * @return the CO2 Amount
//     * @throws IllegalStateException if Amounts is non-empty and does not contain a CO2 Amount.
//     */
//    public Amount getCo2Amount() {
//        if (amounts.isEmpty()) {
//            return CO2Amount.ZERO;
//        } else if (amounts.containsKey(CO2_DEFAULT_TYPE)) {
//            return amounts.get(CO2_DEFAULT_TYPE);
//        } else {
//            throw new IllegalStateException("There is no value of type " + CO2_DEFAULT_TYPE);
//        }
//    }

    /**
     * Add an amount to the return values.
     *
     * @param type the GHG type to add, eg 'CO2'.
     * @param unit the unit, eg 'kg'.
     * @param perUnit the per unit, eg 'month'.
     * @param value the value of the amount.
     */
    public void putValue(String type, String unit, String perUnit, double value) {
        ReturnValue returnValue = new ReturnValue(type, unit, perUnit, value);
        returnValues.put(type, returnValue);

        // TODO: Is it correct to make the first amount the default?
        // It guards against forgetting to set a default type.
        if (returnValues.size() == 1) {
            setDefaultType(type);
        }
    }

    // TODO: check how we deal with no perUnit. PerUnit may be null?
    private CO2Amount newAmount(String unit, String perUnit, double value) {
        CO2AmountUnit amountUnit = new CO2AmountUnit(unit, perUnit);

        return new CO2Amount(value, amountUnit);
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
     * Get the default Amount
     *
     * @return the default Amount or ZERO if there are no Amounts.
     * @throws IllegalStateException if there is no default type set.
     */
    public CO2Amount defaultValueAsAmount() {
        if (returnValues.isEmpty()) {
            return CO2Amount.ZERO;
        } else if (defaultType == null) {
            throw new IllegalStateException("There is no default type");
        }
        ReturnValue defaultValue = returnValues.get(defaultType);
        return defaultValue.asAmount();
//        return newAmount(defaultValue.getUnit(), defaultValue.getPerUnit(), defaultValue.getValue());
    }

    /**
     * Get the numeric value of the default ReturnValue.
     *
     * @return the value of the default Amount.
     */
    public double defaultValueAsDouble() {
        return returnValues.get(defaultType).asDouble();
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
     * Returns a string representation of this object. The exact details of the representation are subject to change
     * but the following may be regarded as typical:
     * com.amee.platform.science.Amounts@5ae80842[amounts={CO2e=123.45, CO2=54.321},defaultType=CO2e,notes=[Note 1, Note 2]]
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
}
