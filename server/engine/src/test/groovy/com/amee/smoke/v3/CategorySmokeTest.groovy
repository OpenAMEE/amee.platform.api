package com.amee.smoke.v3

import com.amee.smoke.BaseSmokeTest
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Smoke tests for the /categories resources.
 */
class CategorySmokeTest extends BaseSmokeTest {

    @Test
    void listCategories() {
        def response = client.get(path: "/3/categories")
        assertResponseOk response
        assertTrue response.data.categories.size() > 1
    }

    @Test
    void getCategory() {
        def response = client.get(path: "/3/categories/IPCC_military_aircraft")
        assertResponseOk response
        assertEquals "IPCC_military_aircraft", response.data.category.wikiName
    }

    @Test
    void getCategoryItems() {
        def response = client.get(path: "/3/categories/IPCC_military_aircraft/items")
        assertResponseOk response
        assertTrue response.data.items.size() > 1
    }

    @Test
    void getItem() {
        def response = client.get(
            path: "/3/categories/IPCC_military_aircraft/items/${config.uid.item.IPCC_military_aircraft.a10};full")
        assertResponseOk response
        assertEquals "A-10A", response.data.item.label
    }

    @Test
    void dataItemCalculation() {
        def response = client.get(
            path: "/3/categories/IPCC_military_aircraft/items/${config.uid.item.IPCC_military_aircraft.a10}/calculation",
            query: ["flightDuration": "1"])
        assertResponseOk response
        assertEquals(95.5576256544, response.data.amounts[0].value, DELTA)
    }

    @Test
    void getItemValues() {
        def response = client.get(
            path: "/3/categories/IPCC_military_aircraft/items/${config.uid.item.IPCC_military_aircraft.a10}/values")
        assertResponseOk response
        assertTrue response.data.values.size() > 1
    }

    @Test
    void getItemValueHistory() {
        def response = client.get(
            path: "/3/categories/UK_energy_by_supplier/items/${config.uid.item.UK_energy_by_supplier.british_gas}/values/kgCO2PerKWh")
        assertResponseOk response
        assertTrue response.data.values.size() > 1
    }

    @Test
    void getItemValue() {
        def response = client.get(
            path: "/3/categories/UK_energy_by_supplier/items/${config.uid.item.UK_energy_by_supplier.british_gas}/values/kgCO2PerKWh/${config.uid.itemValue.UK_energy_by_supplier.british_gas.kgCO2PerKWh}")
        assertResponseOk response
        assertEquals "2009-01-01T00:00:00Z", response.data.value.startDate
    }

    @Test
    void getTags() {
        def response = client.get(path: "/3/categories/IPCC_military_aircraft/tags")
        assertResponseOk(response)
        assertTrue response.data.tags.size() > 1
    }

    @Test
    void getDrills() {
        def response = client.get(path: "/3/categories/IPCC_military_aircraft/drill")
        assertResponseOk(response)
        assertTrue response.data.drill.choices.values.size() > 1
    }
}
