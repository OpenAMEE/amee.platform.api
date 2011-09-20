package com.amee.platform.science;

import org.apache.commons.lang.StringUtils;

import javax.measure.quantity.Power;
import javax.measure.quantity.Quantity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * An AmountUnit represents the unit of an Amount, eg kWh.
 */
public class AmountUnit {

    protected final static UnitFormat UNIT_FORMAT = UnitFormat.getInstance();

    // Define various watt based units.
    private final static Unit<Power> KILOWATT = SI.WATT.times(1000);
    private final static Unit<Power> MEGAWATT = KILOWATT.times(1000);
    private final static Unit<Power> GIGAWATT = MEGAWATT.times(1000);
    private final static Unit<Power> TERAWATT = GIGAWATT.times(1000);
    private final static Unit<? extends Quantity> KILOWATT_HOUR = KILOWATT.times(NonSI.HOUR);
    private final static Unit<? extends Quantity> MEGAWATT_HOUR = MEGAWATT.times(NonSI.HOUR);
    private final static Unit<? extends Quantity> GIGAWATT_HOUR = GIGAWATT.times(NonSI.HOUR);
    private final static Unit<? extends Quantity> TERAWATT_HOUR = TERAWATT.times(NonSI.HOUR);

    // Define BTUs.
    // Based on: http://en.wikipedia.org/wiki/British_thermal_unit (21/12/2009)
    private final static Unit<? extends Quantity> BTU_39F = SI.JOULE.times(1059.67);
    private final static Unit<? extends Quantity> BTU_MEAN = SI.JOULE.times(1055.87);
    private final static Unit<? extends Quantity> BTU_IT = SI.JOULE.times(1055.05585262);
    private final static Unit<? extends Quantity> BTU_ISO = SI.JOULE.times(1055.056);
    private final static Unit<? extends Quantity> BTU_59F = SI.JOULE.times(1054.804);
    private final static Unit<? extends Quantity> BTU_60F = SI.JOULE.times(1054.68);
    private final static Unit<? extends Quantity> BTU_63F = SI.JOULE.times(1054.6);
    private final static Unit<? extends Quantity> BTU_THERMOCHEMICAL = SI.JOULE.times(1054.35026444);

    // Define THERMs.
    private final static Unit<? extends Quantity> THERM_39F = BTU_39F.times(100000);
    private final static Unit<? extends Quantity> THERM_MEAN = BTU_MEAN.times(100000);
    private final static Unit<? extends Quantity> THERM_IT = BTU_IT.times(100000);
    private final static Unit<? extends Quantity> THERM_ISO = BTU_ISO.times(100000);
    private final static Unit<? extends Quantity> THERM_59F = BTU_59F.times(100000);
    private final static Unit<? extends Quantity> THERM_60F = BTU_60F.times(100000);
    private final static Unit<? extends Quantity> THERM_63F = BTU_63F.times(100000);
    private final static Unit<? extends Quantity> THERM_THERMOCHEMICAL = BTU_THERMOCHEMICAL.times(100000);

    // Define barrels
    private final static Unit<? extends Quantity> BARREL_OIL = NonSI.GALLON_LIQUID_US.times(42);

    // Define pound-mole
    private final static Unit<? extends Quantity> POUND_MOLE = SI.MOLE.times(453.59237);

    {
        // Create usable ASCII representations. JScience will use non-ASCII characters by default.
        UNIT_FORMAT.label(KILOWATT_HOUR, "kWh");
        UNIT_FORMAT.label(MEGAWATT_HOUR, "MWh");
        UNIT_FORMAT.label(GIGAWATT_HOUR, "GWh");
        UNIT_FORMAT.label(TERAWATT_HOUR, "TWh");

        // BTUs.
        UNIT_FORMAT.label(BTU_39F, "BTU_ThirtyNineF");
        UNIT_FORMAT.label(SI.KILO(BTU_39F), "kBTU_ThirtyNineF");
        UNIT_FORMAT.label(SI.MEGA(BTU_39F), "MBTU_ThirtyNineF");
        UNIT_FORMAT.alias(SI.MEGA(BTU_39F), "MMBTU_ThirtyNineF");
        UNIT_FORMAT.alias(SI.MEGA(BTU_39F), "mmBTU_ThirtyNineF");
        UNIT_FORMAT.label(SI.GIGA(BTU_39F), "GBTU_ThirtyNineF");
        UNIT_FORMAT.label(SI.TERA(BTU_39F), "TBTU_ThirtyNineF");

        UNIT_FORMAT.label(BTU_MEAN, "BTU_Mean");
        UNIT_FORMAT.label(SI.KILO(BTU_MEAN), "kBTU_Mean");
        UNIT_FORMAT.label(SI.MEGA(BTU_MEAN), "MBTU_Mean");
        UNIT_FORMAT.alias(SI.MEGA(BTU_MEAN), "MMBTU_Mean");
        UNIT_FORMAT.alias(SI.MEGA(BTU_MEAN), "mmBTU_Mean");
        UNIT_FORMAT.label(SI.GIGA(BTU_MEAN), "GBTU_Mean");
        UNIT_FORMAT.label(SI.TERA(BTU_MEAN), "TBTU_Mean");

        UNIT_FORMAT.label(BTU_IT, "BTU_IT");
        UNIT_FORMAT.label(SI.KILO(BTU_IT), "kBTU_IT");
        UNIT_FORMAT.label(SI.MEGA(BTU_IT), "MBTU_IT");
        UNIT_FORMAT.alias(SI.MEGA(BTU_IT), "MMBTU_IT");
        UNIT_FORMAT.alias(SI.MEGA(BTU_IT), "mmBTU_IT");
        UNIT_FORMAT.label(SI.GIGA(BTU_IT), "GBTU_IT");
        UNIT_FORMAT.label(SI.TERA(BTU_IT), "TBTU_IT");

        UNIT_FORMAT.label(BTU_ISO, "BTU_ISO");
        UNIT_FORMAT.label(SI.KILO(BTU_ISO), "kBTU_ISO");
        UNIT_FORMAT.label(SI.MEGA(BTU_ISO), "MBTU_ISO");
        UNIT_FORMAT.label(SI.MEGA(BTU_ISO), "MMBTU_ISO");
        UNIT_FORMAT.label(SI.MEGA(BTU_ISO), "mmBTU_ISO");
        UNIT_FORMAT.label(SI.GIGA(BTU_ISO), "GBTU_ISO");
        UNIT_FORMAT.label(SI.TERA(BTU_ISO), "TBTU_ISO");

        UNIT_FORMAT.label(BTU_59F, "BTU_FiftyNineF");
        UNIT_FORMAT.label(SI.KILO(BTU_59F), "kBTU_FiftyNineF");
        UNIT_FORMAT.label(SI.MEGA(BTU_59F), "MBTU_FiftyNineF");
        UNIT_FORMAT.alias(SI.MEGA(BTU_59F), "MMBTU_FiftyNineF");
        UNIT_FORMAT.alias(SI.MEGA(BTU_59F), "mmBTU_FiftyNineF");
        UNIT_FORMAT.label(SI.GIGA(BTU_59F), "GBTU_FiftyNineF");
        UNIT_FORMAT.label(SI.TERA(BTU_59F), "TBTU_FiftyNineF");

        UNIT_FORMAT.label(BTU_60F, "BTU_SixtyF");
        UNIT_FORMAT.label(SI.KILO(BTU_60F), "kBTU_SixtyF");
        UNIT_FORMAT.label(SI.MEGA(BTU_60F), "MBTU_SixtyF");
        UNIT_FORMAT.alias(SI.MEGA(BTU_60F), "MMBTU_SixtyF");
        UNIT_FORMAT.alias(SI.MEGA(BTU_60F), "mmBTU_SixtyF");
        UNIT_FORMAT.label(SI.GIGA(BTU_60F), "GBTU_SixtyF");
        UNIT_FORMAT.label(SI.TERA(BTU_60F), "TBTU_SixtyF");

        UNIT_FORMAT.label(BTU_63F, "BTU_SixtyThreeF");
        UNIT_FORMAT.label(SI.KILO(BTU_63F), "kBTU_SixtyThreeF");
        UNIT_FORMAT.label(SI.MEGA(BTU_63F), "MBTU_SixtyThreeF");
        UNIT_FORMAT.alias(SI.MEGA(BTU_63F), "MMBTU_SixtyThreeF");
        UNIT_FORMAT.alias(SI.MEGA(BTU_63F), "mmBTU_SixtyThreeF");
        UNIT_FORMAT.label(SI.GIGA(BTU_63F), "GBTU_SixtyThreeF");
        UNIT_FORMAT.label(SI.TERA(BTU_63F), "TBTU_SixtyThreeF");

        UNIT_FORMAT.label(BTU_THERMOCHEMICAL, "BTU_Thermochemical");
        UNIT_FORMAT.label(SI.KILO(BTU_THERMOCHEMICAL), "kBTU_Thermochemical");
        UNIT_FORMAT.label(SI.MEGA(BTU_THERMOCHEMICAL), "MBTU_Thermochemical");
        UNIT_FORMAT.alias(SI.MEGA(BTU_THERMOCHEMICAL), "MMBTU_Thermochemical");
        UNIT_FORMAT.alias(SI.MEGA(BTU_THERMOCHEMICAL), "mmBTU_Thermochemical");
        UNIT_FORMAT.label(SI.GIGA(BTU_THERMOCHEMICAL), "GBTU_Thermochemical");
        UNIT_FORMAT.label(SI.TERA(BTU_THERMOCHEMICAL), "TBTU_Thermochemical");

        // THERMs.
        UNIT_FORMAT.label(THERM_39F, "thm_ThirtyNineF");
        UNIT_FORMAT.label(THERM_MEAN, "thm_Mean");
        UNIT_FORMAT.label(THERM_IT, "thm_IT");
        UNIT_FORMAT.alias(THERM_IT, "thm_ec");
        UNIT_FORMAT.label(THERM_ISO, "thm_ISO");
        UNIT_FORMAT.label(THERM_59F, "thm_FiftyNineF");
        UNIT_FORMAT.alias(THERM_59F, "thm_us");
        UNIT_FORMAT.label(THERM_60F, "thm_SixtyF");
        UNIT_FORMAT.label(THERM_63F, "thm_SixtyThreeF");
        UNIT_FORMAT.label(THERM_THERMOCHEMICAL, "thm_Thermochemical");

        // Ensure that "gal" and "oz" are sensible for AMEE.
        // JScience will bizarrely default "gal" and "oz" to UK units for UK Locale.
        UNIT_FORMAT.label(NonSI.GALLON_LIQUID_US, "gal");
        UNIT_FORMAT.label(NonSI.OUNCE, "oz");

        // For GALLON_UK, explicitly declare gal_uk as canonical and gallon_uk as the alias.
        UNIT_FORMAT.label(NonSI.GALLON_UK, "gal_uk");
        UNIT_FORMAT.alias(NonSI.GALLON_UK, "gallon_uk");

        // Need to explicitly declare these otherwise we get a parse error.
        UNIT_FORMAT.label(NonSI.OUNCE_LIQUID_US, "oz_fl");
        UNIT_FORMAT.label(NonSI.OUNCE_LIQUID_UK, "oz_fl_uk");

        // Barrel
        UNIT_FORMAT.label(BARREL_OIL, "bbl");

        // Pound-mole
        UNIT_FORMAT.label(POUND_MOLE, "lbmol");
    }

    public static final AmountUnit ONE = new AmountUnit(Unit.ONE);
    protected Unit unit = Unit.ONE;

    public AmountUnit(Unit unit) {
        this.unit = unit;
    }

    public static AmountUnit valueOf(String unit) {
        return new AmountUnit(internalValueOf(unit));
    }

    public AmountCompoundUnit with(AmountPerUnit perUnit) {
        return AmountCompoundUnit.valueOf(this, perUnit);
    }

    public boolean isCompatibleWith(String unit) {
        return StringUtils.isNotBlank(unit) && this.unit.isCompatible(internalValueOf(unit));
    }

    // This is like Unit.valueOf but forces use of UNIT_FORMAT instead.
    protected static Unit<? extends Quantity> internalValueOf(CharSequence unit) {
        if ((unit == null) || (unit.length() == 0)) {
            throw new IllegalArgumentException("The unit argument is blank.");
        }
        try {
            return UNIT_FORMAT.parseProductUnit(unit, new ParsePosition(0));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Compares this AmountUnit with the specified Object for equality.
     * This method considers two AmountUnit objects equal only if they are equal type and unit.
     * Note that mixed-type comparison is allowed, but a subclass will never compare equal to this.
     *
     * @param o Object to which this AmountUnit is to be compared.
     * @return true if and only if the specified Object is an AmountUnit whose unit is equal to this AmountUnit's.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (o != null && getClass() == o.getClass()) {
            AmountUnit a = (AmountUnit) o;
            return unit.equals(a.unit);
        }

        return false;
    }

    /**
     * Returns the hash code for this AmountUnit.
     *
     * @return hash code for this AmountUnit.
     */
    @Override
    public int hashCode() {
        return unit.hashCode();
    }

    public Unit toUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return UNIT_FORMAT.format(toUnit());
    }
}
