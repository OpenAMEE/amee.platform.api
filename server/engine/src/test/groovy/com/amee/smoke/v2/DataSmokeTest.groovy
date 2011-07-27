package com.amee.smoke.v2

import com.amee.smoke.BaseSmokeTest
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class DataSmokeTest extends BaseSmokeTest {

    @Test
    void getCategory() {
        def response = client.get(path: "/data/transport/plane/specific/military/ipcc")
        assertResponseOk response
        assertEquals "ipcc", response.data.dataCategory.path
    }

    @Test
    void drillDown() {
        def response = client.get(path: "/data/transport/plane/specific/military/ipcc/drill")
        assertResponseOk response
        assertTrue response.data.choices.choices.size() > 1
    }

    @Test
    void getDataItem() {
        def response = client.get(
            path: "/data/transport/plane/specific/military/ipcc/${config.uid.item.IPCC_military_aircraft.a10}")
        assertResponseOk response
    }

    @Test
    void getDataItemValue() {
        def response = client.get(
            path: "/data/home/energy/uk/suppliers/${config.uid.item.UK_energy_by_supplier.british_gas}/${config.uid.itemValue.UK_energy_by_supplier.british_gas.kgCO2PerKWh}")
        assertResponseOk response
    }

}
