package com.amee.platform.science;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A container class to hold multiple GHG values returned by a Algorithm.
 *
 */
public class Amounts {

    // These are the default values used in CO2Amount
    private static final String CO2_DEFAULT_TYPE = "CO2";
    private static final String CO2_DEFAULT_UNIT = "kg";
    private static final String CO2_DEFAULT_PER_UNIT = "year";

    /** The return Amounts, indexed by GHG type. */
    private Map<String, Amount> amounts = new HashMap<String, Amount>();

    /** The default GHG type. */
    private String defaultType;

    /** Optional text notes. */
    private List<String> notes = new ArrayList<String>();

    public Map<String, Amount> getAmounts() {
        return amounts;
    }

    /**
     * Puts a CO2 Amount using default units (kg/year)
     *
     * @param value amount of CO2 in kg/y.
     */
    public void putCo2Amount(double value) {
        putAmount(CO2_DEFAULT_TYPE, CO2_DEFAULT_UNIT, CO2_DEFAULT_PER_UNIT, value);
    }

    public Amount getCo2Amount() {
        if (!amounts.containsKey(CO2_DEFAULT_TYPE)) {
            throw new IllegalStateException("There is no value of type " + CO2_DEFAULT_TYPE);
        }
        return amounts.get(CO2_DEFAULT_TYPE);
    }

    /**
     * Add an amount to the return values.
     *
     * @param type the GHG type to add, eg 'CO2'.
     * @param unit the unit, eg 'kg'.
     * @param perUnit the per unit, eg 'month'.
     * @param value the value of the amount.
     */
    public void putAmount(String type, String unit, String perUnit, double value) {
        AmountUnit amountUnit = AmountUnit.valueOf(unit);
        AmountPerUnit amountPerUnit = AmountPerUnit.valueOf(perUnit);
        Amount amount = new Amount(value, AmountCompoundUnit.valueOf(amountUnit, amountPerUnit));
        amounts.put(type, amount);

        // TODO: Should we make the first amount the default?
        if (amounts.size() == 1) {
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
        if (amounts.containsKey(type)) {
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
            // TODO: How can we enforce the state?
            throw new IllegalStateException("There is no default type");
        }
        return defaultType;
    }

    /**
     * Get the default Amount
     *
     * @return the default Amount.
     * @throws IllegalStateException if there is no default type set.
     */
    public Amount getDefaultAmount() {
        if (defaultType == null) {
            throw new IllegalStateException("There is no default type");
        }
        return amounts.get(defaultType);
    }

    /**
     * Get the numeric value of the default Amount.
     *
     * @return the value of the default Amount.
     */
    public double getDefaultAmountAsDouble() {
        return getDefaultAmount().getValue();
    }

    /**
     * Add a text note.
     *
     * @param note String to add.
     */
    public void addNote(String note) {
        notes.add(note);
    }

    /**
     * Get the notes.
     *
     * @return List of notes.
     */
    public List<String> getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        //TODO: Implement
        return "Implement this!";
    }
}
