package com.amee.platform.science;

import org.apache.commons.lang3.StringUtils;

import javax.measure.unit.Unit;
import java.util.HashMap;
import java.util.Map;

public abstract class EcoinventUnits {

    public static class EcoinventUnit {

        private String ecoinventUnit;
        private Unit unit;
        private String substance;

        private EcoinventUnit() {
            super();
        }

        public EcoinventUnit(String ecoinventUnit, Unit unit) {
            this();
            this.ecoinventUnit = ecoinventUnit;
            this.unit = unit;
        }

        public EcoinventUnit(String ecoinventUnit, Unit unit, String substance) {
            this(ecoinventUnit, unit);
            this.substance = substance;
        }

        public String getEcoinventUnit() {
            return ecoinventUnit;
        }

        public Unit getUnit() {
            return unit;
        }

        public boolean hasUnit() {
            return unit != null;
        }

        public String getSubstance() {
            return substance;
        }

        public boolean hasSubstance() {
            return !StringUtils.isBlank(getSubstance());
        }
    }

    private final static Map<String, EcoinventUnit> ECOINVENT_UNITS;

    static {
        ECOINVENT_UNITS = new HashMap<String, EcoinventUnit>();
        ECOINVENT_UNITS.put("CTU", new EcoinventUnit("CTU", Unit.ONE));
        ECOINVENT_UNITS.put("DALYs", new EcoinventUnit("DALYs", Unit.ONE));
        ECOINVENT_UNITS.put("ELU", new EcoinventUnit("ELU", Unit.ONE));
        ECOINVENT_UNITS.put("LU", new EcoinventUnit("LU", Unit.ONE));
        ECOINVENT_UNITS.put("MJ", new EcoinventUnit("MJ", AmountUnit.valueOf("MJ").toUnit()));
        ECOINVENT_UNITS.put("MJ-Eq", new EcoinventUnit("MJ-Eq", AmountUnit.valueOf("MJ").toUnit(), "MJ-Eq"));
        ECOINVENT_UNITS.put("Nm3", new EcoinventUnit("Nm3", Unit.ONE));
        ECOINVENT_UNITS.put("UBP", new EcoinventUnit("UBP", Unit.ONE));
        ECOINVENT_UNITS.put("h", new EcoinventUnit("h", AmountUnit.valueOf("h").toUnit()));
        ECOINVENT_UNITS.put("ha", new EcoinventUnit("ha", AmountUnit.valueOf("ha").toUnit()));
        ECOINVENT_UNITS.put("kBq", new EcoinventUnit("kBq", AmountUnit.valueOf("kBq").toUnit()));
        ECOINVENT_UNITS.put("kWh", new EcoinventUnit("kWh", AmountUnit.valueOf("kWh").toUnit()));
        ECOINVENT_UNITS.put("kg", new EcoinventUnit("kg", AmountUnit.valueOf("kg").toUnit()));
        ECOINVENT_UNITS.put("kg 1,4-DCB-Eq", new EcoinventUnit("kg 1,4-DCB-Eq", AmountUnit.valueOf("kg").toUnit(), "1,4-DCB-Eq"));
        ECOINVENT_UNITS.put("kg 2,4-D-Eq", new EcoinventUnit("kg", AmountUnit.valueOf("kg").toUnit(), "2,4-D-Eq"));
        ECOINVENT_UNITS.put("kg CFC-11-Eq", new EcoinventUnit("kg CFC-11-Eq", AmountUnit.valueOf("kg").toUnit(), "CFC-11-Eq"));
        ECOINVENT_UNITS.put("kg CO2-Eq", new EcoinventUnit("kg CO2-Eq", AmountUnit.valueOf("kg").toUnit(), "CO2-Eq"));
        ECOINVENT_UNITS.put("kg Fe-Eq", new EcoinventUnit("kg Fe-Eq", AmountUnit.valueOf("kg").toUnit(), "Fe-Eq"));
        ECOINVENT_UNITS.put("kg N", new EcoinventUnit("kg N", AmountUnit.valueOf("kg").toUnit(), "N"));
        ECOINVENT_UNITS.put("kg N-Eq", new EcoinventUnit("kg N-Eq", AmountUnit.valueOf("kg").toUnit(), "N-Eq"));
        ECOINVENT_UNITS.put("kg NMVOC", new EcoinventUnit("kg NMVOC", AmountUnit.valueOf("kg").toUnit(), "NMVOC"));
        ECOINVENT_UNITS.put("kg NO3-", new EcoinventUnit("kg NO3-", AmountUnit.valueOf("kg").toUnit(), "NO3-"));
        ECOINVENT_UNITS.put("kg NOx-Eq", new EcoinventUnit("kg NOx-Eq", AmountUnit.valueOf("kg").toUnit(), "NOx-Eq"));
        ECOINVENT_UNITS.put("kg P", new EcoinventUnit("kg P", AmountUnit.valueOf("kg").toUnit(), "P"));
        ECOINVENT_UNITS.put("kg P-Eq", new EcoinventUnit("kg P-Eq", AmountUnit.valueOf("kg").toUnit(), "P-Eq"));
        ECOINVENT_UNITS.put("kg PM10-Eq", new EcoinventUnit("kg PM10-Eq", AmountUnit.valueOf("kg").toUnit(), "PM10-Eq"));
        ECOINVENT_UNITS.put("kg PM2.5-Eq", new EcoinventUnit("kg PM2.5-Eq", AmountUnit.valueOf("kg").toUnit(), "PM2.5-Eq"));
        ECOINVENT_UNITS.put("kg PO4-Eq", new EcoinventUnit("kg PO4-Eq", AmountUnit.valueOf("kg").toUnit(), "PO4-Eq"));
        ECOINVENT_UNITS.put("kg SO2-Eq", new EcoinventUnit("kg SO2-Eq", AmountUnit.valueOf("kg").toUnit(), "SO2-Eq"));
        ECOINVENT_UNITS.put("kg SWU", new EcoinventUnit("kg SWU", AmountUnit.valueOf("kg").toUnit(), "SWU"));
        ECOINVENT_UNITS.put("kg U235-Eq", new EcoinventUnit("kg U235-Eq", AmountUnit.valueOf("kg").toUnit(), "U235-Eq"));
        ECOINVENT_UNITS.put("kg antimony-Eq", new EcoinventUnit("kg antimony-Eq", AmountUnit.valueOf("kg").toUnit(), "antimony-Eq"));
        ECOINVENT_UNITS.put("kg benzene-Eq", new EcoinventUnit("kg benzene-Eq", AmountUnit.valueOf("kg").toUnit(), "benzene-Eq"));
        ECOINVENT_UNITS.put("kg ethylene-Eq", new EcoinventUnit("kg ethylene-Eq", AmountUnit.valueOf("kg").toUnit(), "ethylene-Eq"));
        ECOINVENT_UNITS.put("kg formed ozone", new EcoinventUnit("kg formed ozone", AmountUnit.valueOf("kg").toUnit(), "formed ozone"));
        ECOINVENT_UNITS.put("kg oil-Eq", new EcoinventUnit("kg oil-Eq", AmountUnit.valueOf("kg").toUnit(), "oil-Eq"));
        ECOINVENT_UNITS.put("kg toluene-Eq", new EcoinventUnit("kg toluene-Eq", AmountUnit.valueOf("kg").toUnit(), "toluene-Eq"));
        ECOINVENT_UNITS.put("kg waste", new EcoinventUnit("kg waste", AmountUnit.valueOf("kg").toUnit(), "waste"));
        ECOINVENT_UNITS.put("km", new EcoinventUnit("km", AmountUnit.valueOf("km").toUnit()));
        ECOINVENT_UNITS.put("m", new EcoinventUnit("m", AmountUnit.valueOf("m").toUnit()));
        ECOINVENT_UNITS.put("m2", new EcoinventUnit("m2", AmountUnit.valueOf("m^2").toUnit()));
        ECOINVENT_UNITS.put("m2.ppm.h", new EcoinventUnit("m2.ppm.h", AmountUnit.valueOf("m^2").toUnit(), "ppm.h"));
        ECOINVENT_UNITS.put("m2a", new EcoinventUnit("m2a", Unit.ONE));
        ECOINVENT_UNITS.put("m3", new EcoinventUnit("m3", AmountUnit.valueOf("m^3").toUnit()));
        ECOINVENT_UNITS.put("m3 air", new EcoinventUnit("m3 air", AmountUnit.valueOf("m^3").toUnit(), "air"));
        ECOINVENT_UNITS.put("m3 soil", new EcoinventUnit("m3 soil", AmountUnit.valueOf("m^3").toUnit(), "soil"));
        ECOINVENT_UNITS.put("m3 waste water", new EcoinventUnit("m3 waste water", AmountUnit.valueOf("m^3").toUnit(), "waste water"));
        ECOINVENT_UNITS.put("m3 waste", new EcoinventUnit("m3 waste", AmountUnit.valueOf("m^3").toUnit(), "waste"));
        ECOINVENT_UNITS.put("m3a", new EcoinventUnit("m3a", Unit.ONE));
        ECOINVENT_UNITS.put("ma", new EcoinventUnit("ma", Unit.ONE));
        ECOINVENT_UNITS.put("moles of H+-Eq", new EcoinventUnit("moles of H+-Eq", AmountUnit.valueOf("mol").toUnit(), "of H+-Eq"));
        ECOINVENT_UNITS.put("person.ppm.h", new EcoinventUnit("person.ppm.h", Unit.ONE, "ppm.h"));
        ECOINVENT_UNITS.put("pig place", new EcoinventUnit("pig place", Unit.ONE, "pig place"));
        ECOINVENT_UNITS.put("pkm", new EcoinventUnit("pkm", Unit.ONE));
        ECOINVENT_UNITS.put("points", new EcoinventUnit("points", Unit.ONE));
        ECOINVENT_UNITS.put("tkm", new EcoinventUnit("tkm", Unit.ONE));
        ECOINVENT_UNITS.put("unit", new EcoinventUnit("unit", Unit.ONE));
        ECOINVENT_UNITS.put("vkm", new EcoinventUnit("vkm", Unit.ONE));
    }

    public static EcoinventUnit getEcoinventUnit(String ecoinventUnit) {
        if (isValidEcoinventUnit(ecoinventUnit)) {
            return ECOINVENT_UNITS.get(ecoinventUnit);
        } else {
            throw new IllegalArgumentException(ecoinventUnit + " is not a valid Ecoinvent unit.");
        }
    }

    public static boolean isValidEcoinventUnit(String ecoinventUnit) {
        return ECOINVENT_UNITS.containsKey(ecoinventUnit);
    }
}