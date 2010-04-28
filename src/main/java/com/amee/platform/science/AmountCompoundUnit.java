package com.amee.platform.science;

import javax.measure.unit.Unit;

public class AmountCompoundUnit extends AmountUnit {

    private AmountPerUnit perUnit;

    protected AmountCompoundUnit(AmountUnit unit, AmountPerUnit perUnit) {
        super(unit.toUnit());
        this.perUnit = perUnit;
    }

    public static AmountCompoundUnit valueOf(AmountUnit unit, AmountPerUnit perUnit) {
        return new AmountCompoundUnit(unit, perUnit);
    }

    public Unit toUnit() {
        return unit.divide(perUnit.toUnit());
    }

    public boolean hasDifferentPerUnit(AmountPerUnit perUnit) {
        return !this.perUnit.equals(perUnit);
    }

    public AmountPerUnit getPerUnit() {
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
