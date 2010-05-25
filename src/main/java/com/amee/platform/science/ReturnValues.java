package com.amee.platform.science;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An object to hold multiple GHG values to be returned by a Algorithm.
 *
 */
public class ReturnValues {

    /** The return Amounts, indexed by GHG type. */
    private Map<String, Amount> amounts = new HashMap<String, Amount>();

    /** The default GHG type. */
    private String defaultType;

    /** Optional comments to be returned. */
    private List<String> notes = new ArrayList<String>();

    public Map<String, Amount> getAmounts() {
            return amounts;
        }

    /**
     * Add an amount to the return values.
     *
     * @param type the GHG type to add, eg 'CO2'.
     * @param unit the unit, eg 'kg'.
     * @param perUnit the per unit, eg 'month'.
     * @param value the value of the amount.
     */
    public void addAmount(String type, String unit, String perUnit, double value) {
        AmountUnit amountUnit = AmountUnit.valueOf(unit);
        AmountPerUnit amountPerUnit = AmountPerUnit.valueOf(perUnit);
        Amount amount = new Amount(value, AmountCompoundUnit.valueOf(amountUnit, amountPerUnit));
        amounts.put(type, amount);
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
     * @return the value of the default Amount.
     */
    public double getDefaultAmountAsDouble() {
        return getDefaultAmount().getValue();
    }

    public void addNote(String note) {
        notes.add(note);
    }

    public List<String> getNotes() {
        return notes;
    }
}
