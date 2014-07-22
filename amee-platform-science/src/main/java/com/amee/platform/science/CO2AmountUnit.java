package com.amee.platform.science;

import org.apache.commons.lang3.StringUtils;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public class CO2AmountUnit extends AmountCompoundUnit {

    // The default unit
    private static final AmountUnit UNIT = new AmountUnit(SI.KILOGRAM);

    // The default perUnit
    private static final AmountPerUnit PER_UNIT = new AmountPerUnit(NonSI.YEAR);

    // The default compound unit (i.e. unit/perUnit)
    public static final CO2AmountUnit DEFAULT = new CO2AmountUnit(UNIT, PER_UNIT);

    public CO2AmountUnit(AmountUnit unit, AmountPerUnit perUnit) {
        super(unit, perUnit);
    }

    public CO2AmountUnit(String unit, String perUnit) {
        super(parseUnit(unit), parsePerUnit(perUnit));
    }

    private static AmountUnit parseUnit(String unit) {
        return StringUtils.isNotBlank(unit) ? AmountUnit.valueOf(unit) : UNIT;
    }

    private static AmountPerUnit parsePerUnit(String perUnit) {
        return StringUtils.isNotBlank(perUnit) ? AmountPerUnit.valueOf(perUnit) : PER_UNIT;
    }

    /**
     * Does this instance represent an external unit.
     *
     * @return true if the current instance represents the default unit for a CO2 amount calculated by AMEE
     */
    public boolean isExternal() {
        return !this.equals(DEFAULT);
    }

    /**
     * Does the supplied AmountPerUnit represent an external unit.
     * @param perUnit
     * @return true if the current instance represents the default AmountPerUnit for a CO2 amount calculated by AMEE
     */
    public static boolean isExternal(AmountPerUnit perUnit) {
        return !PER_UNIT.equals(perUnit);
    }

    /**
     * Does the supplied AmountUnit represent an external unit.
     * @param unit
     * @return true if the current instance represents the default AmountUnit for a CO2 amount calculated by AMEE
     */
    public static boolean isExternal(AmountUnit unit) {
        return !UNIT.equals(unit);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public Unit getBaseUnit() {
        return unit;
    }
}

