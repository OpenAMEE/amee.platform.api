package com.amee.domain.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EcoinventUnitsTest {

    @Test
    public void canGetEcoinventUnit() {
        EcoinventUnits.EcoinventUnit eu = EcoinventUnits.getEcoinventUnit("kg 1,4-DCB-Eq");
        assertNotNull(eu);
        assertEquals("kg", eu.getUnit().toString());
        assertEquals("1,4-DCB-Eq", eu.getSubstance());
        assertEquals("kg 1,4-DCB-Eq", eu.getEcoinventUnit());
    }
}
