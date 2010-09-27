package com.amee.domain.data;

import com.amee.platform.science.AmountUnit;
import org.apache.commons.lang.StringUtils;

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

    private final static Map<String, EcoinventUnit> ECOINVENT_UNITS;static {
        ECOINVENT_UNITS = new HashMap<String, EcoinventUnit>() {
            {
                put("CTU", new EcoinventUnit("CTU", null));
                put("DALYs", new EcoinventUnit("DALYs", null));
                put("ELU", new EcoinventUnit("ELU", null));
                put("LU", new EcoinventUnit("LU", null));
                put("MJ", new EcoinventUnit("MJ", AmountUnit.valueOf("MJ").toUnit()));
                put("MJ-Eq", new EcoinventUnit("MJ-Eq", AmountUnit.valueOf("MJ").toUnit(), "MJ-Eq"));
                put("Nm3", new EcoinventUnit("Nm3", null));
                put("UBP", new EcoinventUnit("UBP", null));
                put("h", new EcoinventUnit("h", AmountUnit.valueOf("h").toUnit()));
                put("ha", new EcoinventUnit("ha", AmountUnit.valueOf("ha").toUnit()));
                put("kBq", new EcoinventUnit("kBq", AmountUnit.valueOf("kBq").toUnit()));
                put("kWh", new EcoinventUnit("kWh", AmountUnit.valueOf("kWh").toUnit()));
                put("kg", new EcoinventUnit("kg", AmountUnit.valueOf("kg").toUnit()));
                put("kg 1,4-DCB-Eq", new EcoinventUnit("kg 1,4-DCB-Eq", AmountUnit.valueOf("kg").toUnit(), "1,4-DCB-Eq"));
                put("kg 2,4-D-Eq", new EcoinventUnit("kg", AmountUnit.valueOf("kg").toUnit(), "2,4-D-Eq"));
                put("kg CFC-11-Eq", new EcoinventUnit("kg CFC-11-Eq", AmountUnit.valueOf("kg").toUnit(), "CFC-11-Eq"));
                put("kg CO2-Eq", new EcoinventUnit("kg CO2-Eq", AmountUnit.valueOf("kg").toUnit(), "CO2-Eq"));
                put("kg Fe-Eq", new EcoinventUnit("kg Fe-Eq", AmountUnit.valueOf("kg").toUnit(), "Fe-Eq"));
                put("kg N", new EcoinventUnit("kg N", AmountUnit.valueOf("kg").toUnit(), "N"));
                put("kg N-Eq", new EcoinventUnit("kg N-Eq", AmountUnit.valueOf("kg").toUnit(), "N-Eq"));
                put("kg NMVOC", new EcoinventUnit("kg NMVOC", AmountUnit.valueOf("kg").toUnit(), "NMVOC"));
                put("kg NO3-", new EcoinventUnit("kg NO3-", AmountUnit.valueOf("kg").toUnit(), "NO3-"));
                put("kg NOx-Eq", new EcoinventUnit("kg NOx-Eq", AmountUnit.valueOf("kg").toUnit(), "NOx-Eq"));
                put("kg P", new EcoinventUnit("kg P", AmountUnit.valueOf("kg").toUnit(), "P"));
                put("kg P-Eq", new EcoinventUnit("kg P-Eq", AmountUnit.valueOf("kg").toUnit(), "P-Eq"));
                put("kg PM10-Eq", new EcoinventUnit("kg PM10-Eq", AmountUnit.valueOf("kg").toUnit(), "PM10-Eq"));
                put("kg PM2.5-Eq", new EcoinventUnit("kg PM2.5-Eq", AmountUnit.valueOf("kg").toUnit(), "PM2.5-Eq"));
                put("kg PO4-Eq", new EcoinventUnit("kg PO4-Eq", AmountUnit.valueOf("kg").toUnit(), "PO4-Eq"));
                put("kg SO2-Eq", new EcoinventUnit("kg SO2-Eq", AmountUnit.valueOf("kg").toUnit(), "SO2-Eq"));
                put("kg SWU", new EcoinventUnit("kg SWU", AmountUnit.valueOf("kg").toUnit(), "SWU"));
                put("kg U235-Eq", new EcoinventUnit("kg U235-Eq", AmountUnit.valueOf("kg").toUnit(), "U235-Eq"));
                put("kg antimony-Eq", new EcoinventUnit("kg antimony-Eq", AmountUnit.valueOf("kg").toUnit(), "antimony-Eq"));
                put("kg benzene-Eq", new EcoinventUnit("kg benzene-Eq", AmountUnit.valueOf("kg").toUnit(), "benzene-Eq"));
                put("kg ethylene-Eq", new EcoinventUnit("kg ethylene-Eq", AmountUnit.valueOf("kg").toUnit(), "ethylene-Eq"));
                put("kg formed ozone", new EcoinventUnit("kg formed ozone", AmountUnit.valueOf("kg").toUnit(), "formed ozone"));
                put("kg oil-Eq", new EcoinventUnit("kg oil-Eq", AmountUnit.valueOf("kg").toUnit(), "oil-Eq"));
                put("kg toluene-Eq", new EcoinventUnit("kg toluene-Eq", AmountUnit.valueOf("kg").toUnit(), "toluene-Eq"));
                put("kg waste", new EcoinventUnit("kg waste", AmountUnit.valueOf("kg").toUnit(), "waste"));
                put("km", new EcoinventUnit("km", AmountUnit.valueOf("km").toUnit()));
                put("m", new EcoinventUnit("m", AmountUnit.valueOf("m").toUnit()));
                put("m2", new EcoinventUnit("m2", AmountUnit.valueOf("m^2").toUnit()));
                put("m2.ppm.h", new EcoinventUnit("m2.ppm.h", AmountUnit.valueOf("m^2").toUnit(), "ppm.h"));
                put("m2a", new EcoinventUnit("m2a", null));
                put("m3", new EcoinventUnit("m3", AmountUnit.valueOf("m^3").toUnit()));
                put("m3 air", new EcoinventUnit("m3 air", AmountUnit.valueOf("m^3").toUnit(), "air"));
                put("m3 soil", new EcoinventUnit("m3 soil", AmountUnit.valueOf("m^3").toUnit(), "soil"));
                put("m3 waste water", new EcoinventUnit("m3 waste water", AmountUnit.valueOf("m^3").toUnit(), "waste water"));
                put("m3 waste", new EcoinventUnit("m3 waste", AmountUnit.valueOf("m^3").toUnit(), "waste"));
                put("m3a", new EcoinventUnit("m3a", null));
                put("ma", new EcoinventUnit("ma", null));
                put("moles of H+-Eq", new EcoinventUnit("moles of H+-Eq", AmountUnit.valueOf("mol").toUnit(), "of H+-Eq"));
                put("person.ppm.h", new EcoinventUnit("person.ppm.h", null, "ppm.h"));
                put("pig place", new EcoinventUnit("pig place", null, "pig place"));
                put("pkm", new EcoinventUnit("pkm", null));
                put("points", new EcoinventUnit("points", null));
                put("tkm", new EcoinventUnit("tkm", null));
                put("unit", new EcoinventUnit("unit", null));
                put("vkm", new EcoinventUnit("vkm", null));
            }
        };
    }

    public static EcoinventUnit getEcoinventUnit(String ecoinventUnit) {
        return ECOINVENT_UNITS.get(ecoinventUnit);
    }
}