package com.amee.platform.science;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A ReturnValue represents a particular type of GHG emission.
 * It must have a type and value. It may also have a unit and perUnit.
 *
 * This class is immutable.
 */
public class ReturnValue {
    private final String type;
    private final String unit;
    private final String perUnit;
    private final double value;

    /**
     * Constructs a ReturnValue.
     *
     * @param type the GHG type.
     * @param unit optional unit. Passing null will store empty string.
     * @param perUnit optional perUnit. Passing null will store empty string.
     * @param value the value.
     * @throws NullPointerException if type is null.
     */
    public ReturnValue(String type, String unit, String perUnit, double value) {
        if (type == null) {
            throw new NullPointerException("ReturnValue type cannot be null.");
        }
        this.type = type;

        if (unit == null) {
            this.unit = "";
        } else {
            this.unit = unit;
        }

        if (perUnit == null) {
            this.perUnit = "";
        } else {
            this.perUnit = perUnit;
        }

        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getUnit() {
        return unit;
    }
    
    public String getCompoundUnit() {
        if (unit.isEmpty()) {
            return "";
        }
        if (perUnit.isEmpty()) {
            return unit;
        }

        AmountUnit unit = AmountUnit.valueOf(getUnit());
        AmountPerUnit perUnit = AmountPerUnit.valueOf(getPerUnit());
        return AmountCompoundUnit.valueOf(unit, perUnit).toString();
    }

    public String getPerUnit() {
        return perUnit;
    }

    public double getValue() {
        return value;
    }

    /**
     * Converts this ReturnValue into a CO2Amount.
     *
     * @return a CO2Amount with this ReturnValue's values.
     */
    public CO2Amount toAmount() {
        if (value == 0.0) {
            return CO2Amount.ZERO;
        }
        return newAmount(unit, perUnit, value);
    }

    private CO2Amount newAmount(String unit, String perUnit, Double value) {
        CO2AmountUnit amountUnit = new CO2AmountUnit(unit, perUnit);
        return new CO2Amount(value, amountUnit);
    }

    @Override
    public String toString() {
        NumberFormat f = NumberFormat.getInstance();
        if (f instanceof DecimalFormat) {
            ((DecimalFormat) f).applyPattern("0.0################");
        }
        return new ToStringBuilder(this).
            append("type", type).
            append("unit", unit).
            append("perUnit", perUnit).
            append("value", f.format(value)).
            toString();
    }
}
