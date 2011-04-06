package com.amee.domain.unit;

import java.util.Comparator;

/**
 * Compares two AMEEUnits by their symbol properties. Preference is given to the externalSymbol over
 * the internalSymbol. The internalSymbol is mandatory and externalSymbol is optional, allowing externalSymbol
 * to override the internalSymbol value.
 */
public class AMEEUnitSymbolComparator implements Comparator<AMEEUnit> {

    @Override
    public int compare(AMEEUnit unit1, AMEEUnit unit2) {
        if (unit1.hasExternalSymbol() && unit2.hasExternalSymbol()) {
            return unit1.getExternalSymbol().compareToIgnoreCase(unit2.getExternalSymbol());
        } else if (unit1.hasExternalSymbol()) {
            return unit1.getExternalSymbol().compareToIgnoreCase(unit2.getInternalSymbol());
        } else if (unit2.hasExternalSymbol()) {
            return unit1.getInternalSymbol().compareToIgnoreCase(unit2.getExternalSymbol());
        } else {
            return unit1.getInternalSymbol().compareToIgnoreCase(unit2.getInternalSymbol());
        }
    }
}
