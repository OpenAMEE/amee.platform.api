package com.amee.platform.science;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ReturnValue {
    private String type;
    private String unit;
    private String perUnit;
    private double value;

    public ReturnValue(String type, String unit, String perUnit, double value) {
        this.type = type;
        this.unit = unit;
        this.perUnit = perUnit;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPerUnit() {
        return perUnit;
    }

    public void setPerUnit(String perUnit) {
        this.perUnit = perUnit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Converts this ReturnValue to a double.
     *
     * @return the double value of this ReturnValue.
     */
    public double toDouble() {
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

    private CO2Amount newAmount(String unit, String perUnit, double value) {
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
