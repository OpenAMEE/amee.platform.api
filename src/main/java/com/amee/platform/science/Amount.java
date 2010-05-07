package com.amee.platform.science;

import javax.measure.Measure;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * An AMEE abstraction of an amount. An Amount has a value and a unit.
 * This class provides unit conversion.
 */
public class Amount {

    public final static Amount ZERO = new Amount(0.0);

    /** The value */
    private double value = 0.0;

    /** The unit (default to the dimensionless unit, ONE) */
    private AmountUnit unit = AmountUnit.ONE;

    /**
     * A Amount representing the supplied value and unit.
     *
     * @param value - the String representation of the value value
     * @param unit    - the unit of the value value
     */
    public Amount(String value, AmountUnit unit) {
        this(value);
        this.unit = unit;
    }

    /**
     * An Amount representing the supplied unit-less value.
     *
     * @param value - the String representation of the value.
     */
    public Amount(String value) {

        if (value == null) {
            throw new IllegalArgumentException("The String value must be non-null");
        }

        // Many value DataItem values in the DB have values of "-" so we need to handle this here.
        if (value.isEmpty() || value.equals("-")) {
            this.value = 0.0;
        } else {
            this.value = Double.parseDouble(value);
        }
    }

    public Amount(double value, AmountUnit unit) {
        this(value);
        this.unit = unit;
    }

    public Amount(double value) {
        this.value = value;
    }

    /**
     * Convert and return a new Amount instance in the target AmountUnit.
     *
     * @param targetUnit - the target unit
     * @return the value in the target unit
     * @see AmountUnit
     */

    @SuppressWarnings("unchecked")
    public Amount convert(AmountUnit targetUnit) {
        if (unit.equals(targetUnit)) {
            return new Amount(getValue(), unit);
        } else {
            Measure measure = Measure.valueOf(value, unit.toUnit());
            double valueInTargetUnit = measure.doubleValue(targetUnit.toUnit());
            return new Amount(valueInTargetUnit, targetUnit);
        }
    }

    /**
     * Convert and return a new Amount instance in the target AmountPerUnit.
     *
     * @param targetPerUnit - the target perUnit
     * @return the Amount in the target perUnit
     * @see AmountPerUnit
     */
    @SuppressWarnings("unchecked")
    public Amount convert(AmountPerUnit targetPerUnit) {

        if (!(unit instanceof AmountCompoundUnit)) return new Amount(value);

        AmountCompoundUnit cUnit = (AmountCompoundUnit) unit;

        if (cUnit.hasDifferentPerUnit(targetPerUnit)) {
            Measure measure = Measure.valueOf(value, cUnit.getPerUnit().toUnit().inverse());
            return new Amount(measure.doubleValue(targetPerUnit.toUnit().inverse()), targetPerUnit);
        } else {
            return new Amount(getValue(), unit);
        }
    }

    /**
     * Compares this Amount's unit with the specified AmountUnit for equality.
     *
     * @param unit - the unit to compare
     * @return returns true if the supplied AmountUnit is not considered equal to the unit of the current instance.
     */
    public boolean hasDifferentUnits(AmountUnit unit) {
        return !this.unit.equals(unit);
    }

    /**
     * Tests if the units of the two Amounts permit the operation.
     * Amounts may only be operated upon if they have the same unit or if the given Amount's unit is the dimensionless unit.
     *
     * @param amount the Amount to be tested.
     * @return true if the operation can be performed.
     */
    private boolean canOperateOnAmount(Amount amount) {
        return amount.unit.equals(unit) || amount.unit.equals(AmountUnit.ONE);
    }

    public double getValue() {
        return value;
    }

    public AmountUnit getUnit() {
        return unit;
    }

    /**
     * Returns the string representation of the value of this Amount in standard decimal notation with a precision
     * of up to 17 decimal places. Note that the unit is NOT returned.
     *
     * @return string representation of this Amount.
     */
    @Override
    public String toString() {
        NumberFormat f = NumberFormat.getInstance();
         if (f instanceof DecimalFormat) {
             ((DecimalFormat) f).applyPattern("0.0################");
         }

         return f.format(value);
    }

    /**
     * Compares this Amount with the specified Object for equality.
     * This method considers two Amount objects equal only if they are equal in value and unit.
     *
     * @param o Object to which this Amount is to be compared.
     * @return true if and only if the specified Object is an Amount whose value and unit are equal to this Amount's.
     */
    @Override
    public final boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Amount)) {
            return false;
        }
        
        Amount d = (Amount) o;

        return Double.doubleToLongBits(d.value) == Double.doubleToLongBits(this.value)
            && d.unit.equals(this.unit);
    }

    /**
     * Returns the hash code for this Amount.
     * Note that two Amount objects that are numerically equal in value but differ in unit will not  have the same hash code.
     *
     * @return hash code for this Amount.
     */
    @Override
    public int hashCode() {
        int result = 17;

        // value
        Long bits = Double.doubleToLongBits(value);
        int valueCode = (int) (bits ^ (bits >>> 32));
        result = 37 * result + valueCode;

        // unit
        int unitCode = unit.hashCode();
        result = 37 * result + unitCode;

        return result;
    }

    /**
     * Returns an Amount whose value is (this + amount).
     *
     * @param amount value to be added to this Amount.
     * @return this + amount.
     * @throws IllegalArgumentException if the unit of amount differs from this unit or AmountUnit.ONE.
     */
    public Amount add(Amount amount) {
        if (canOperateOnAmount(amount)) {
            return new Amount(getValue() + (amount.getValue()), unit);
        } else {
            throw new IllegalArgumentException("Cannot add unit " + amount.unit + " to unit " + unit);
        }
    }

    /**
     * Returns an Amount whose value is (this - amount).
     *
     * @param amount value to be subtracted from this Amount.
     * @return this - amount.
     * @throws IllegalArgumentException if the unit of amount differs from this unit or AmountUnit.ONE.
     */
    public Amount subtract(Amount amount) {
        if (canOperateOnAmount(amount)) {
            return new Amount(getValue() - (amount.getValue()), unit);
        } else {
            throw new IllegalArgumentException("Cannot subtract unit " + amount.unit + " from unit " + unit);
        }
    }

    /**
     * Returns an Amount whose value is (this / amount).
     *
     * @param amount value by which this Amount is to be divided.
     * @return this / amount.
     * @throws IllegalArgumentException if the unit of amount differs from this unit or AmountUnit.ONE.
     */
    public Amount divide(Amount amount) {
        if (canOperateOnAmount(amount)) {
            return new Amount(getValue() / (amount.getValue()), unit);
        } else {
            throw new IllegalArgumentException("Cannot divide unit " + amount.unit + " with unit " + unit);
        }
    }

    /**
     * Returns an Amount whose value is (this * amount).
     *
     * @param amount value to be multiplied by this Amount.
     * @return this * amount.
     * @throws IllegalArgumentException if the unit of amount differs from this unit or AmountUnit.ONE.
     */
    public Amount multiply(Amount amount) {
        if (canOperateOnAmount(amount)) {
            return new Amount(getValue() * (amount.getValue()), unit);
        } else {
            throw new IllegalArgumentException("Cannot multiply unit " + amount.unit + " with unit " + unit);
        }
    }
}
