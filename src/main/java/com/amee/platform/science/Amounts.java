package com.amee.platform.science;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A container class to hold multiple GHG values returned by a Algorithm.
 *
 */
public class Amounts {

    public static final int MAX_NOTE_LENGTH = 255;

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
     * @param value amount of CO2 in kg/year.
     */
    public void putCo2Amount(double value) {
        putAmount(CO2_DEFAULT_TYPE, CO2_DEFAULT_UNIT, CO2_DEFAULT_PER_UNIT, value);
    }

    /**
     * Get the CO2 Amount.
     *
     * @return the CO2 Amount
     * @throws IllegalStateException if Amounts is non-empty and does not contain a CO2 Amount.
     */
    public Amount getCo2Amount() {
        if (amounts.isEmpty()) {
            return CO2Amount.ZERO;
        } else if (amounts.containsKey(CO2_DEFAULT_TYPE)) {
            return amounts.get(CO2_DEFAULT_TYPE);
        } else {
            throw new IllegalStateException("There is no value of type " + CO2_DEFAULT_TYPE);
        }
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
        Amount amount = newAmount(unit, perUnit, value);
        amounts.put(type, amount);

        // TODO: Is it correct to make the first amount the default?
        // It guards against forgetting to set a default type.
        if (amounts.size() == 1) {
            setDefaultType(type);
        }
    }

    private Amount newAmount(String unit, String perUnit, double value) {
        AmountUnit amountUnit = AmountUnit.valueOf(unit);
        Amount amount;
        if (!perUnit.isEmpty()) {
            AmountPerUnit amountPerUnit = AmountPerUnit.valueOf(perUnit);
            amount = new Amount(value, AmountCompoundUnit.valueOf(amountUnit, amountPerUnit));
        } else {
            amount = new Amount(value, amountUnit);
        }
        return amount;
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
    public Amount getDefaultAmount() {
        if (amounts.isEmpty()) {
            return CO2Amount.ZERO;
        } else if (defaultType == null) {
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
     * @throws IllegalArgumentException if note length is greater than {@value #MAX_NOTE_LENGTH}
     */
    public void addNote(String note) {
        if (note.length() > MAX_NOTE_LENGTH) {
            throw new IllegalArgumentException("Note must be <= " + MAX_NOTE_LENGTH + " characters. Tried to add note of length " + note.length());
        }
        notes.add(note);
    }

    /**
     * Get the notes.
     *
     * @return the List of notes.
     */
    public List<String> getNotes() {
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
            append("amounts", amounts).
            append("defaultType", defaultType).
            append("notes", notes).
            toString();
    }

}
