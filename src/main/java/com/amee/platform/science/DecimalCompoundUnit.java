package com.amee.platform.science;

import javax.measure.unit.Unit;

public class DecimalCompoundUnit extends DecimalUnit {

    private DecimalPerUnit perUnit;

    protected DecimalCompoundUnit(DecimalUnit unit, DecimalPerUnit perUnit) {
        super(unit.toUnit());
        this.perUnit = perUnit;
    }

    public static DecimalCompoundUnit valueOf(DecimalUnit unit, DecimalPerUnit perUnit) {
        return new DecimalCompoundUnit(unit, perUnit);
    }

    public Unit toUnit() {
        return unit.divide(perUnit.toUnit());
    }

    public boolean hasDifferentPerUnit(DecimalPerUnit perUnit) {
        return !this.perUnit.equals(perUnit);
    }

    public DecimalPerUnit getPerUnit() {
        return perUnit;
    }
//
//    public void getElement(Element parent, Document document) {
//        parent.appendChild(APIUtils.getElement(document, "Unit", unit.toString()));
//        parent.appendChild(APIUtils.getElement(document, "PerUnit", perUnit.toString()));
//    }
//
//    public void getJSONObject(JSONObject parent) throws JSONException {
//        parent.put("unit", unit.toString());
//        parent.put("perUnit", perUnit.toString());
//    }
}
