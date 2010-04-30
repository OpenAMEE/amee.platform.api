package com.amee.platform.science;

import javax.measure.Measure;
import javax.measure.unit.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

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
     * A Amount representing the supplied unit-less value.
     *
     * @param value - the String representation of the value value
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

    // TODO: genericise so this is not needed?
    @SuppressWarnings("unchecked")
    public Amount convert(AmountUnit targetUnit) {
        if (!unit.equals(targetUnit)) {
            Measure measure = Measure.valueOf(value, unit.toUnit());
            return new Amount(measure.doubleValue(targetUnit.toUnit()), targetUnit);
        } else {
            return new Amount(getValue(), unit);
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
            Measure dm = Measure.valueOf(value, cUnit.getPerUnit().toUnit().inverse());
            return new Amount(dm.doubleValue(targetPerUnit.toUnit().inverse()), targetPerUnit);
        } else {
            return new Amount(getValue(), unit);
        }
    }

    /**
     * @param unit - the unit to compare
     * @return returns true if the supplied AmountUnit is not considered equal to the unit of the current instance.
     */
    public boolean hasDifferentUnits(AmountUnit unit) {
        return !this.unit.equals(unit);
    }

    public double getValue() {
        return value;
    }

    public AmountUnit getUnit() {
        return unit;
    }

    // TODO: Should we include the unit here?
    @Override
    public String toString() {
        NumberFormat f = NumberFormat.getInstance();
         if (f instanceof DecimalFormat) {
             ((DecimalFormat) f).applyPattern("0.0################");
         }

         return f.format(value);
    }

    // TODO: Is this correct? Should 2 Amounts with different units be equal?
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Amount)) {
            return false;
        }
        
        Amount d = (Amount) o;
        if (Double.doubleToLongBits(d.value) != Double.doubleToLongBits(this.value)) {
            return false;
        }

        if (!d.unit.equals(this.unit)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;

        Long bits = Double.doubleToLongBits(value);
        int valueCode = (int) (bits ^ (bits >>> 32));
        result = 37 * result + valueCode;

        int unitCode = unit.hashCode();
        result = 37 * result + unitCode;

        return result;
    }

    // TODO: these methods ignore the units??
    public Amount add(Amount amount) {
        return new Amount(getValue() + (amount.getValue()));
    }

    public Amount subtract(Amount amount) {
        return new Amount(getValue() - (amount.getValue()));
    }

    public Amount divide(Amount amount) {
        return new Amount(getValue() / (amount.getValue()));
    }

    public Amount multiply(Amount amount) {
        return new Amount(getValue() * (amount.getValue()));
    }
}
