package com.amee.platform.science;

import org.junit.Test;

import javax.measure.unit.NonSI;
import javax.measure.unit.SI;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

public class AmountUnitTest {

    private static final AmountUnit KILOGRAM = new AmountUnit(SI.KILOGRAM);
    private static final AmountPerUnit YEAR = new AmountPerUnit(NonSI.YEAR);

    @Test
    public void testEqualsAndHashCode() {
        AmountUnit au1 = AmountUnit.valueOf("kWh");
        AmountUnit au2 = AmountUnit.valueOf("kWh");
        assertEquals(au1, au2);
        assertEquals(au1.hashCode(), au2.hashCode());

        AmountUnit au3 = AmountUnit.valueOf("MWh");
        assertFalse(au1.equals(au3));
        assertFalse(au1.hashCode() == au3.hashCode());

        AmountUnit apu1 = AmountPerUnit.valueOf("month");
        assertFalse(au1.equals(apu1));
        assertFalse(au1.hashCode() == apu1.hashCode());

        AmountUnit acu1 = AmountCompoundUnit.valueOf(KILOGRAM, YEAR);
        assertFalse(au1.equals(acu1));
        assertFalse(au1.hashCode() == acu1.hashCode());
    }

    @Test
    public void testUnits() {

        // Watt
        assertEquals(AmountUnit.valueOf("kWh").toUnit(), SI.KILO(SI.WATT).times(NonSI.HOUR));
        assertEquals(AmountUnit.valueOf("MWh").toUnit(), SI.MEGA(SI.WATT).times(NonSI.HOUR));
        assertEquals(AmountUnit.valueOf("GWh").toUnit(), SI.GIGA(SI.WATT).times(NonSI.HOUR));
        assertEquals(AmountUnit.valueOf("TWh").toUnit(), SI.TERA(SI.WATT).times(NonSI.HOUR));

        // BTU
        assertEquals(AmountUnit.valueOf("BTU_ThirtyNineF").toUnit(), SI.JOULE.times(1059.67));
        assertEquals(AmountUnit.valueOf("kBTU_ThirtyNineF").toUnit(), SI.KILO(SI.JOULE.times(1059.67)));
        assertEquals(AmountUnit.valueOf("MBTU_ThirtyNineF").toUnit(), SI.MEGA(SI.JOULE.times(1059.67)));
        assertEquals(AmountUnit.valueOf("MMBTU_ThirtyNineF").toUnit(), SI.MEGA(SI.JOULE.times(1059.67)));
        assertEquals(AmountUnit.valueOf("mmBTU_ThirtyNineF").toUnit(), SI.MEGA(SI.JOULE.times(1059.67)));
        assertEquals(AmountUnit.valueOf("GBTU_ThirtyNineF").toUnit(), SI.GIGA(SI.JOULE.times(1059.67)));
        assertEquals(AmountUnit.valueOf("TBTU_ThirtyNineF").toUnit(), SI.TERA(SI.JOULE.times(1059.67)));

        assertEquals(AmountUnit.valueOf("BTU_Mean").toUnit(), SI.JOULE.times(1055.87));
        assertEquals(AmountUnit.valueOf("kBTU_Mean").toUnit(), SI.KILO(SI.JOULE.times(1055.87)));
        assertEquals(AmountUnit.valueOf("MBTU_Mean").toUnit(), SI.MEGA(SI.JOULE.times(1055.87)));
        assertEquals(AmountUnit.valueOf("MMBTU_Mean").toUnit(), SI.MEGA(SI.JOULE.times(1055.87)));
        assertEquals(AmountUnit.valueOf("mmBTU_Mean").toUnit(), SI.MEGA(SI.JOULE.times(1055.87)));
        assertEquals(AmountUnit.valueOf("GBTU_Mean").toUnit(), SI.GIGA(SI.JOULE.times(1055.87)));
        assertEquals(AmountUnit.valueOf("TBTU_Mean").toUnit(), SI.TERA(SI.JOULE.times(1055.87)));

        assertEquals(AmountUnit.valueOf("BTU_IT").toUnit(), SI.JOULE.times(1055.05585262));
        assertEquals(AmountUnit.valueOf("kBTU_IT").toUnit(), SI.KILO(SI.JOULE.times(1055.05585262)));
        assertEquals(AmountUnit.valueOf("MBTU_IT").toUnit(), SI.MEGA(SI.JOULE.times(1055.05585262)));
        assertEquals(AmountUnit.valueOf("MMBTU_IT").toUnit(), SI.MEGA(SI.JOULE.times(1055.05585262)));
        assertEquals(AmountUnit.valueOf("mmBTU_IT").toUnit(), SI.MEGA(SI.JOULE.times(1055.05585262)));
        assertEquals(AmountUnit.valueOf("GBTU_IT").toUnit(), SI.GIGA(SI.JOULE.times(1055.05585262)));
        assertEquals(AmountUnit.valueOf("TBTU_IT").toUnit(), SI.TERA(SI.JOULE.times(1055.05585262)));

        assertEquals(AmountUnit.valueOf("BTU_ISO").toUnit(), SI.JOULE.times(1055.056));
        assertEquals(AmountUnit.valueOf("kBTU_ISO").toUnit(), SI.KILO(SI.JOULE.times(1055.056)));
        assertEquals(AmountUnit.valueOf("MBTU_ISO").toUnit(), SI.MEGA(SI.JOULE.times(1055.056)));
        assertEquals(AmountUnit.valueOf("MMBTU_ISO").toUnit(), SI.MEGA(SI.JOULE.times(1055.056)));
        assertEquals(AmountUnit.valueOf("mmBTU_ISO").toUnit(), SI.MEGA(SI.JOULE.times(1055.056)));
        assertEquals(AmountUnit.valueOf("GBTU_ISO").toUnit(), SI.GIGA(SI.JOULE.times(1055.056)));
        assertEquals(AmountUnit.valueOf("TBTU_ISO").toUnit(), SI.TERA(SI.JOULE.times(1055.056)));

        assertEquals(AmountUnit.valueOf("BTU_FiftyNineF").toUnit(), SI.JOULE.times(1054.804));
        assertEquals(AmountUnit.valueOf("kBTU_FiftyNineF").toUnit(), SI.KILO(SI.JOULE.times(1054.804)));
        assertEquals(AmountUnit.valueOf("MBTU_FiftyNineF").toUnit(), SI.MEGA(SI.JOULE.times(1054.804)));
        assertEquals(AmountUnit.valueOf("MMBTU_FiftyNineF").toUnit(), SI.MEGA(SI.JOULE.times(1054.804)));
        assertEquals(AmountUnit.valueOf("mmBTU_FiftyNineF").toUnit(), SI.MEGA(SI.JOULE.times(1054.804)));
        assertEquals(AmountUnit.valueOf("GBTU_FiftyNineF").toUnit(), SI.GIGA(SI.JOULE.times(1054.804)));
        assertEquals(AmountUnit.valueOf("TBTU_FiftyNineF").toUnit(), SI.TERA(SI.JOULE.times(1054.804)));

        assertEquals(AmountUnit.valueOf("BTU_SixtyF").toUnit(), SI.JOULE.times(1054.68));
        assertEquals(AmountUnit.valueOf("kBTU_SixtyF").toUnit(), SI.KILO(SI.JOULE.times(1054.68)));
        assertEquals(AmountUnit.valueOf("MBTU_SixtyF").toUnit(), SI.MEGA(SI.JOULE.times(1054.68)));
        assertEquals(AmountUnit.valueOf("MMBTU_SixtyF").toUnit(), SI.MEGA(SI.JOULE.times(1054.68)));
        assertEquals(AmountUnit.valueOf("mmBTU_SixtyF").toUnit(), SI.MEGA(SI.JOULE.times(1054.68)));
        assertEquals(AmountUnit.valueOf("GBTU_SixtyF").toUnit(), SI.GIGA(SI.JOULE.times(1054.68)));
        assertEquals(AmountUnit.valueOf("TBTU_SixtyF").toUnit(), SI.TERA(SI.JOULE.times(1054.68)));

        assertEquals(AmountUnit.valueOf("BTU_SixtyThreeF").toUnit(), SI.JOULE.times(1054.6));
        assertEquals(AmountUnit.valueOf("kBTU_SixtyThreeF").toUnit(), SI.KILO(SI.JOULE.times(1054.6)));
        assertEquals(AmountUnit.valueOf("MBTU_SixtyThreeF").toUnit(), SI.MEGA(SI.JOULE.times(1054.6)));
        assertEquals(AmountUnit.valueOf("MMBTU_SixtyThreeF").toUnit(), SI.MEGA(SI.JOULE.times(1054.6)));
        assertEquals(AmountUnit.valueOf("mmBTU_SixtyThreeF").toUnit(), SI.MEGA(SI.JOULE.times(1054.6)));
        assertEquals(AmountUnit.valueOf("GBTU_SixtyThreeF").toUnit(), SI.GIGA(SI.JOULE.times(1054.6)));
        assertEquals(AmountUnit.valueOf("TBTU_SixtyThreeF").toUnit(), SI.TERA(SI.JOULE.times(1054.6)));

        assertEquals(AmountUnit.valueOf("BTU_Thermochemical").toUnit(), SI.JOULE.times(1054.35026444));
        assertEquals(AmountUnit.valueOf("kBTU_Thermochemical").toUnit(), SI.KILO(SI.JOULE.times(1054.35026444)));
        assertEquals(AmountUnit.valueOf("MBTU_Thermochemical").toUnit(), SI.MEGA(SI.JOULE.times(1054.35026444)));
        assertEquals(AmountUnit.valueOf("MMBTU_Thermochemical").toUnit(), SI.MEGA(SI.JOULE.times(1054.35026444)));
        assertEquals(AmountUnit.valueOf("mmBTU_Thermochemical").toUnit(), SI.MEGA(SI.JOULE.times(1054.35026444)));
        assertEquals(AmountUnit.valueOf("GBTU_Thermochemical").toUnit(), SI.GIGA(SI.JOULE.times(1054.35026444)));
        assertEquals(AmountUnit.valueOf("TBTU_Thermochemical").toUnit(), SI.TERA(SI.JOULE.times(1054.35026444)));

        // Therm (100,000 BTU)
        assertEquals(AmountUnit.valueOf("thm_ThirtyNineF").toUnit(), SI.JOULE.times(1059.67).times(100000));
        assertEquals(AmountUnit.valueOf("thm_Mean").toUnit(), SI.JOULE.times(1055.87).times(100000));
        assertEquals(AmountUnit.valueOf("thm_IT").toUnit(), SI.JOULE.times(1055.05585262).times(100000));
        assertEquals(AmountUnit.valueOf("thm_ec").toUnit(), AmountUnit.valueOf("thm_IT").toUnit());
        assertEquals(AmountUnit.valueOf("thm_ISO").toUnit(), SI.JOULE.times(1055.056).times(100000));
        assertEquals(AmountUnit.valueOf("thm_FiftyNineF").toUnit(), SI.JOULE.times(1054.804).times(100000));
        assertEquals(AmountUnit.valueOf("thm_us").toUnit(), AmountUnit.valueOf("thm_FiftyNineF").toUnit());
        assertEquals(AmountUnit.valueOf("thm_SixtyF").toUnit(), SI.JOULE.times(1054.68).times(100000));
        assertEquals(AmountUnit.valueOf("thm_SixtyThreeF").toUnit(), SI.JOULE.times(1054.6).times(100000));
        assertEquals(AmountUnit.valueOf("thm_Thermochemical").toUnit(), SI.JOULE.times(1054.35026444).times(100000));

        // gal & oz
        assertEquals(AmountUnit.valueOf("gal").toUnit(), NonSI.GALLON_LIQUID_US);
        assertEquals(AmountUnit.valueOf("oz").toUnit(), NonSI.OUNCE);
        assertEquals(AmountUnit.valueOf("gal_uk").toUnit(), NonSI.GALLON_UK);
        assertEquals(AmountUnit.valueOf("gallon_uk").toUnit(), NonSI.GALLON_UK);
        assertEquals(AmountUnit.valueOf("oz_fl").toUnit(), NonSI.OUNCE_LIQUID_US);
        assertEquals(AmountUnit.valueOf("oz_fl_uk").toUnit(), NonSI.OUNCE_LIQUID_UK);

        // Barrel
        assertEquals(AmountUnit.valueOf("bbl").toUnit(), NonSI.GALLON_LIQUID_US.times(42));

        // One
        assertEquals(AmountUnit.ONE.toString(), "");

        // Square metre.
        assertEquals(SI.SQUARE_METRE, AmountUnit.valueOf("m^2").toUnit());

        // Cubic metre.
        assertEquals(SI.CUBIC_METRE, AmountUnit.valueOf("m^3").toUnit());
    }
}
