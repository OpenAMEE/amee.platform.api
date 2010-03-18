package com.amee.platform.science;

import javax.measure.DecimalMeasure;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * An AMEE abstraction of a decimal value.
 * <p/>
 * Provides for basic string-to-decimal validation, unit conversion and scale and precision definitions.
 */
public class Decimal {

    // Precision, Scale and MathContext properties.
    public final static int PRECISION = 21;
    public final static int SCALE = 6;
    public final static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final MathContext CONTEXT = new MathContext(PRECISION, ROUNDING_MODE);

    // Representing a decimal amount of 0.000000.
    public final static BigDecimal BIG_DECIMAL_ZERO = BigDecimal.valueOf(0, SCALE);
    public final static Decimal ZERO = new Decimal(BIG_DECIMAL_ZERO);

    // Default decimal value to ZERO.
    private BigDecimal decimal = BIG_DECIMAL_ZERO;

    // Default unit to the dimensionless unit, ONE.
    private DecimalUnit unit = DecimalUnit.ONE;

    /**
     * A Decimal representing the supplied value and unit.
     *
     * @param decimal - the String representation of the decimal value
     * @param unit    - the unit of the decimal value
     */
    public Decimal(String decimal, DecimalUnit unit) {
        this(decimal);
        this.unit = unit;
    }

    /**
     * A Decimal representing the supplied unit-less value.
     *
     * @param decimal - the String representation of the decimal value
     */
    public Decimal(String decimal) {

        if (decimal == null) {
            throw new IllegalArgumentException("The String decimal must be non-null");
        }

        // Many decimal DataItem values in the DB have values of "-" so we need to handle this here. 
        if (decimal.isEmpty() || decimal.equals("-")) {
            this.decimal = BIG_DECIMAL_ZERO;
        } else {
            scale(decimal);
        }
    }

    public Decimal(Double decimal) {
        scale(decimal.toString());
    }

    public Decimal(Float decimal) {
        scale(decimal.toString());
    }

    public Decimal(Long decimal) {
        scale(decimal.toString());
    }

    public double doubleValue() {
        return decimal.doubleValue();
    }

    private Decimal(BigDecimal decimal) {
        scale(decimal);
    }

    private Decimal(BigDecimal decimal, DecimalUnit unit) {
        this(decimal);
        this.unit = unit;
    }

    /**
     * Scale the supplied value according to the AMEE standard precision and scale and store to this Decimal.
     *
     * @param decimal value to scale
     */
    protected void scale(String decimal) {
        try {
            scale(new BigDecimal(decimal));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The provided string could not be parsed as a decimal: " + decimal);
        }
    }

    /**
     * Scale the supplied value according to the AMEE standard precision and scale and store to this Decimal.
     *
     * @param decimal value to scale
     */
    protected void scale(BigDecimal decimal) {
        BigDecimal scaled = decimal.setScale(SCALE, ROUNDING_MODE);
        if (scaled.precision() > PRECISION) {
            throw new IllegalArgumentException("Precision of '" + scaled + "' exceeds '" + PRECISION + "'");
        }
        this.decimal = scaled;
    }

    /**
     * Convert and return a new Decimal instance in the target DecimalUnit.
     *
     * @param targetUnit - the target unit
     * @return the decimal in the target unit
     * @see DecimalUnit
     */
    @SuppressWarnings("unchecked")
    public Decimal convert(DecimalUnit targetUnit) {
        if (!unit.equals(targetUnit)) {
            DecimalMeasure dm = DecimalMeasure.valueOf(getValue(), unit.toUnit());
            BigDecimal converted = dm.to(targetUnit.toUnit(), Decimal.CONTEXT).getValue();
            BigDecimal scaled = converted.setScale(Decimal.SCALE, Decimal.ROUNDING_MODE);
            return new Decimal(scaled);
        } else {
            return new Decimal(getValue(), unit);
        }
    }

    /**
     * Convert and return a new Decimal instance in the target DecimalPerUnit.
     *
     * @param targetPerUnit - the target perUnit
     * @return the Decimal in the target perUnit
     * @see DecimalPerUnit
     */
    @SuppressWarnings("unchecked")
    public Decimal convert(DecimalPerUnit targetPerUnit) {

        if (!(unit instanceof DecimalCompoundUnit)) return new Decimal(getValue());

        DecimalCompoundUnit cUnit = (DecimalCompoundUnit) unit;

        if (cUnit.hasDifferentPerUnit(targetPerUnit)) {
            DecimalMeasure dm = DecimalMeasure.valueOf(getValue(), cUnit.getPerUnit().toUnit().inverse());
            BigDecimal converted = dm.to(targetPerUnit.toUnit().inverse(), Decimal.CONTEXT).getValue();
            BigDecimal scaled = converted.setScale(Decimal.SCALE, Decimal.ROUNDING_MODE);
            return new Decimal(scaled);
        } else {
            return new Decimal(getValue());
        }
    }

    /**
     * @param unit - the unit to compare
     * @return returns true is the supplied DecimalUnit is not considered equal to the unit of the current instance.
     */
    public boolean hasDifferentUnits(DecimalUnit unit) {
        return !this.unit.equals(unit);
    }

    public BigDecimal getValue() {
        return decimal;
    }

    public DecimalUnit getUnit() {
        return unit;
    }

    public String toString() {
        return getValue().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Decimal))
            return false;

        return getValue().compareTo(((Decimal) o).getValue()) == 0;
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    public Decimal add(Decimal decimal) {
        return new Decimal(getValue().add(decimal.getValue(), CONTEXT));
    }

    public Decimal subtract(Decimal decimal) {
        return new Decimal(getValue().subtract(decimal.getValue(), CONTEXT));
    }

    public Decimal divide(Decimal decimal) {
        return new Decimal(getValue().divide(decimal.getValue(), CONTEXT));
    }

    public Decimal multiply(Decimal decimal) {
        return new Decimal(getValue().multiply(decimal.getValue(), CONTEXT));
    }
}
