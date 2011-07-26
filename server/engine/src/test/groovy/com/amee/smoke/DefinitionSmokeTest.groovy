package com.amee.smoke

import org.junit.Test
import static org.junit.Assert.*

/**
 * Smoke tests for the /definitions resources.
 */
class DefinitionSmokeTest extends BaseSmokeTest {

    @Test
    void listItemDefinitions() {
        def response = client.get(path: "/3/definitions")
        assertResponseOk response
        assertTrue response.data.itemDefinitions.size() > 1
    }

    @Test
    void getItemDefinition(){
        def response = client.get(path: "/3/definitions/${config.uid.itemDefinition.IPCC_military_aircraft.a10};full")
        assertResponseOk response
        assertEquals "IPCC military aircraft", response.data.itemDefinition.name
    }

    @Test
    void getItemValueDefinitions() {
        def response = client.get(path: "/3/definitions/${config.uid.itemDefinition.IPCC_military_aircraft.a10}/values")
        assertResponseOk response
        assertTrue response.data.itemValueDefinitions.size() > 1
    }

    @Test
    void getItemValueDefinition() {
        def response = client.get(
            path: "/3/definitions/4B5BB862D5B3/values/${config.uid.itemValueDefinition.IPCC_military_aircraft.a10.flightDuration};full")
        assertResponseOk response
        assertEquals "flightDuration", response.data.itemValueDefinition.path
    }

    @Test
    void getReturnValueDefinitions() {
        def response = client.get(path: "/3/definitions/${config.uid.itemDefinition.DEFRA_Great_Circle_Route}/returnvalues")
        assertResponseOk response
        assertTrue response.data.returnValueDefinitions.size() > 1
    }

    @Test
    void getReturnValueDefinition() {
        def response = client.get(
            path: "/3/definitions/${config.uid.itemDefinition.DEFRA_Great_Circle_Route}/returnvalues/${config.uid.returnValueDefinition.DEFRA_Great_Circle_Route.totalDirectCO2e};type")
        assertResponseOk response
        assertEquals "totalDirectCO2e", response.data.returnValueDefinition.type
    }

    @Test
    void getAlgorithms() {
        def response = client.get(path: "/3/definitions/${config.uid.itemDefinition.IPCC_military_aircraft.a10}/algorithms")
        assertResponseOk response
        assertTrue response.data.algorithms.size() > 0
    }

    @Test
    void getAlgorithm() {
        def response = client.get(
            path: "/3/definitions/${config.uid.itemDefinition.IPCC_military_aircraft.a10}/algorithms/${config.uid.algorithm.IPCC_military_aircraft.default};full")
        assertResponseOk response
        assertEquals "default", response.data.algorithm.name
    }
}
