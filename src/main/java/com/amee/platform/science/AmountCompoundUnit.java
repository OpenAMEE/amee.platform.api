package com.amee.platform.science;

import javax.measure.unit.Unit;

/**
 * Represents a 'compound unit' - a unit and a dimension. Eg, km/h, kg/m.
 * Note, this is a different concept from javax.measure.unit.CompoundUnit.
 */
public class AmountCompoundUnit extends AmountUnit {

    private AmountPerUnit perUnit;

    protected AmountCompoundUnit(AmountUnit unit, AmountPerUnit perUnit) {
        super(unit.toUnit());
        this.perUnit = perUnit;
    }

    public static AmountCompoundUnit valueOf(AmountUnit unit, AmountPerUnit perUnit) {
        return new AmountCompoundUnit(unit, perUnit);
    }

    @Override
    public Unit toUnit() {
        return unit.divide(perUnit.toUnit());
    }

    public boolean hasDifferentPerUnit(AmountPerUnit perUnit) {
        return !this.perUnit.equals(perUnit);
    }

    public AmountPerUnit getPerUnit() {
        return perUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        
        if (!(o instanceof AmountCompoundUnit)) {
            return false;
        }

        AmountCompoundUnit acu = (AmountCompoundUnit) o;
        return acu.unit.equals(unit) && acu.perUnit.equals(perUnit);
    }
}
