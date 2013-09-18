package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import static org.restlet.data.Status.*

/**
 * Tests for the Unit API.
 */
class UnitIT extends BaseApiTest {

    def unitUids = [
            '1BB3DAA7A390',
            '2BB3DAA7A390',
            '3BB3DAA7A390']

    def unitNames = [
            'Test Unit One',
            'Test Unit Two',
            'Test Unit Three']

    def unitInternalSymbols = [
            'kg',
            'kWh',
            'm']

    def unitExternalSymbols = [
            'zkg',
            'zkWh',
            'zm']

    def allUnitUids = unitUids + ['4BB3DAA7A390']

    def allUnitNames = unitNames + ['Test Unit Four']

    def allUnitInternalSymbols = unitInternalSymbols + ['km']

    def allUnitExternalSymbols = unitExternalSymbols + ['zkm']

    /**
     * Tests for creation, fetch and deletion of a Unit using JSON & XML responses.
     *
     * Create a new Unit by POSTing to '/units/types/{UID|name}/units'
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>internalSymbol
     * <li>externalSymbol
     * </ul>
     *
     * See getAllUnitsForUnitTypeJson below for supported GET matrix parameters.
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Unit by sending a DELETE request to '/units/types/{UID|name}/units/{UID/symbol}'.
     *
     */
    @Test
    void createAndRemoveUnit() {
        com.amee.integration.BaseApiTest.versions.each { version -> createAndRemoveUnit(version, true) }
        com.amee.integration.BaseApiTest.versions.each { version -> createAndRemoveUnit(version, false) }
    }

    def createAndRemoveUnit(version, useUnitTypeResource) {
        if (version >= 3.5) {
            createAndRemoveUnit(version, 'Ounce', 'oz', 'ounce', useUnitTypeResource)
            createAndRemoveUnit(version, 'Angstrom', javax.measure.unit.NonSI.ANGSTROM.toString(), 'ang', useUnitTypeResource)
            createAndRemoveUnit(version, 'Meters Per Second', 'm/s', 'm/s', useUnitTypeResource)
        }
    }

    def createAndRemoveUnit(version, name, internalSymbol, externalSymbol, useUnitTypeResource) {
        createAndRemoveUnitJson(version, name, internalSymbol, externalSymbol, useUnitTypeResource)
        createAndRemoveUnitXml(version, name, internalSymbol, externalSymbol, useUnitTypeResource)
    }

    def createAndRemoveUnitJson(version, name, internalSymbol, externalSymbol, useUnitTypeResource) {

        setAdminUser()

        // Create a new Unit.
        def responsePost
        if (useUnitTypeResource) {

            // Use the Unit Type Resource.
            responsePost = client.post(
                    path: "/${version}/units/types/1AA3DAA7A390/units",
                    body: [
                            name: name,
                            internalSymbol: internalSymbol,
                            externalSymbol: externalSymbol],
                    requestContentType: URLENC,
                    contentType: JSON)
        } else {

            // Use the base Units resource.
            responsePost = client.post(
                    path: "/${version}/units",
                    body: [
                            name: name,
                            internalSymbol: internalSymbol,
                            externalSymbol: externalSymbol,
                            unitType: '1AA3DAA7A390'],
                    requestContentType: URLENC,
                    contentType: JSON)
        }

        // Get and check the location.
        def unitLocation = responsePost.headers['Location'].value
        def unitUid = unitLocation.split('/')[8]
        assertTrue unitUid.size() == 12
        assertOkJson responsePost, SUCCESS_CREATED.code, unitUid

        // Fetch the Unit.
        def response = client.get(
                path: "${unitLocation};full",
                contentType: JSON)
        assertEquals SUCCESS_OK.code, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals name, response.data.unit.name
        assertEquals internalSymbol, response.data.unit.internalSymbol
        assertEquals externalSymbol, response.data.unit.externalSymbol

        // Then delete the Unit.
        def responseDelete = client.delete(path: "${unitLocation}")
        assertOkJson responseDelete, SUCCESS_OK.code, unitUid

        // We should get a 404 here.
        try {
            client.get(path: "${unitLocation}")
            fail 'Should have thrown an exception'
        } catch (HttpResponseException e) {
            assertEquals CLIENT_ERROR_NOT_FOUND.code, e.response.status
        }
    }

    def createAndRemoveUnitXml(version, name, internalSymbol, externalSymbol, useUnitTypeResource) {

        setAdminUser()

        // Create a new Unit.
        def responsePost
        if (useUnitTypeResource) {

            // Use the Unit Type Resource.
            responsePost = client.post(
                    path: "/${version}/units/types/1AA3DAA7A390/units",
                    body: [
                            name: name,
                            internalSymbol: internalSymbol,
                            externalSymbol: externalSymbol],
                    requestContentType: URLENC,
                    contentType: XML)
        } else {

            // Use the base Units resource.
            responsePost = client.post(
                    path: "/${version}/units",
                    body: [
                            name: name,
                            internalSymbol: internalSymbol,
                            externalSymbol: externalSymbol,
                            unitType: '1AA3DAA7A390'],
                    requestContentType: URLENC,
                    contentType: XML)
        }

        // Get and check the location.
        def unitLocation = responsePost.headers['Location'].value
        def unitUid = unitLocation.split('/')[8]
        assertTrue unitUid.size() == 12
        assertOkXml responsePost, SUCCESS_CREATED.code, unitUid

        // Fetch the Unit.
        def response = client.get(
                path: "${unitLocation};full",
                contentType: XML)
        assertEquals SUCCESS_OK.code, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals name, response.data.Unit.Name.text()
        assertEquals internalSymbol, response.data.Unit.InternalSymbol.text()
        assertEquals externalSymbol, response.data.Unit.ExternalSymbol.text()

        // Then delete the Unit.
        def responseDelete = client.delete(path: "${unitLocation}", contentType: XML)
        assertOkXml responseDelete, SUCCESS_OK.code, unitUid

        // We should get a 404 here.
        try {
            client.get(path: "${unitLocation}")
            fail 'Should have thrown an exception'
        } catch (HttpResponseException e) {
            assertEquals CLIENT_ERROR_NOT_FOUND.code, e.response.status
        }
    }

    /**
     * Tests fetching a single unit using JSON & XML.
     *
     * Unit GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>symbols - include the internalSymbol and externalSymbol values.
     * <li>unitType - include the unitType value. This is the UID and name of the Unit Type.
     * <li>internalUnit - include the JScience toString value based in the internalSymbol.
     * <li>alternatives - include a list of alternative units (those that share the unit type).
     * </ul>
     *
     * By default the unit UID, name and implicit symbol are included.
     */
    @Test
    void getSingleUnit() {
        com.amee.integration.BaseApiTest.versions.each { version -> getSingleUnit(version) }
    }

    def getSingleUnit(version) {
        getSingleUnitJson(version)
        getSingleUnitXml(version)
    }

    def getSingleUnitJson(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units/types/AAA3DAA7A390/units/kg;full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals '1BB3DAA7A390', response.data.unit.uid
            assertEquals 'Test Unit One', response.data.unit.name
            assertEquals 'zkg', response.data.unit.symbol
            assertEquals 'kg', response.data.unit.internalSymbol
            assertEquals 'zkg', response.data.unit.externalSymbol
            assertEquals 2, response.data.alternatives.size()
            assertEquals(['2BB3DAA7A390', '3BB3DAA7A390'].sort(), response.data.alternatives.collect {it.uid}.sort())
            assertEquals(['Test Unit Two', 'Test Unit Three'].sort(), response.data.alternatives.collect {it.name}.sort())
            assertEquals(['zkWh', 'zm'].sort(), response.data.alternatives.collect {it.symbol}.sort())
        }
    }

    def getSingleUnitXml(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units/types/AAA3DAA7A390/units/kg;full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()
            assertEquals '1BB3DAA7A390', response.data.Unit.@uid.text()
            assertEquals 'Test Unit One', response.data.Unit.Name.text()
            assertEquals 'zkg', response.data.Unit.Symbol.text()
            assertEquals 'kg', response.data.Unit.InternalSymbol.text()
            assertEquals 'zkg', response.data.Unit.ExternalSymbol.text()
            def allAlternatives = response.data.Alternatives.Unit
            assertEquals 2, allAlternatives.size()
            assertEquals(['2BB3DAA7A390', '3BB3DAA7A390'].sort(), allAlternatives.@uid*.text().sort())
            assertEquals(['Test Unit Two', 'Test Unit Three'].sort(), allAlternatives.Name*.text().sort())
            assertEquals(['zkWh', 'zm'].sort(), allAlternatives.Symbol*.text().sort())
        }
    }

    /**
     * Tests fetching a list of Units for a Unit Type using JSON & XML.
     *
     * Units GET requests support the same matrix parameters as GETs for a single unit, except for the
     * alternatives matrix parameter.
     *
     * By default the unit UID, name and implicit symbol are included.
     *
     * Units are sorted by symbol.
     */
    @Test
    void getAllUnitsForUnitType() {
        com.amee.integration.BaseApiTest.versions.each { version -> getAllUnitsForUnitType(version) }
    }

    def getAllUnitsForUnitType(version) {
        getAllUnitsForUnitTypeJson(version)
        getAllUnitsForUnitTypeXml(version)
    }

    def getAllUnitsForUnitTypeJson(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units/types/AAA3DAA7A390/units;full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals unitUids.size(), response.data.units.size()
            assertEquals unitUids.sort(), response.data.units.collect {it.uid}.sort()
            assertEquals unitNames.sort(), response.data.units.collect {it.name}.sort()
            assertEquals unitInternalSymbols.sort(), response.data.units.collect {it.internalSymbol}.sort()
            assertEquals unitExternalSymbols.sort(), response.data.units.collect {it.externalSymbol}.sort()
        }
    }

    def getAllUnitsForUnitTypeXml(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units/types/AAA3DAA7A390/units;full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()
            def allUnits = response.data.Units.Unit
            assertEquals unitUids.size(), allUnits.size()
            assertEquals unitUids.sort(), allUnits.@uid*.text().sort()
            assertEquals unitNames.sort(), allUnits.Name*.text().sort()
            assertEquals unitInternalSymbols.sort(), allUnits.InternalSymbol*.text().sort()
            assertEquals unitExternalSymbols.sort(), allUnits.ExternalSymbol*.text().sort()
        }
    }

    /**
     * Tests fetching a list of all Units JSON & XML.
     *
     * Units GET requests support the same matrix parameters as GETs for a single unit, except for the
     * alternatives matrix parameter.
     *
     * By default the unit UID, name and implicit symbol are included.
     *
     * Units are sorted by symbol.
     */
    @Test
    void getAllUnits() {
        com.amee.integration.BaseApiTest.versions.each { version -> getAllUnits(version) }
    }

    def getAllUnits(version) {
        getAllUnitsJson(version)
        getAllUnitsXml(version)
    }

    def getAllUnitsJson(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units;full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals allUnitUids.size(), response.data.units.size()
            assertEquals allUnitUids.sort(), response.data.units.collect {it.uid}.sort()
            assertEquals allUnitNames.sort(), response.data.units.collect {it.name}.sort()
            assertEquals allUnitInternalSymbols.sort(), response.data.units.collect {it.internalSymbol}.sort()
            assertEquals allUnitExternalSymbols.sort(), response.data.units.collect {it.externalSymbol}.sort()
        }
    }

    def getAllUnitsXml(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units;full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()
            def allUnits = response.data.Units.Unit
            assertEquals allUnitUids.size(), allUnits.size()
            assertEquals allUnitUids.sort(), allUnits.@uid*.text().sort()
            assertEquals allUnitNames.sort(), allUnits.Name*.text().sort()
            assertEquals allUnitInternalSymbols.sort(), allUnits.InternalSymbol*.text().sort()
            assertEquals allUnitExternalSymbols.sort(), allUnits.ExternalSymbol*.text().sort()
        }
    }

    /**
     * Tests the validation rules for the Unit name field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Mandatory.
     * <li>Unique on lower case of entire string amongst all Units.
     * <li>No longer than 255 characters.
     * </ul>
     */
    @Test
    void updateWithInvalidName() {
        setAdminUser()
        updateUnitFieldJson('name', 'empty', '')
        updateUnitFieldJson('name', 'long', String.randomString(256))
    }

    /**
     * Tests the validation rules for the Unit internalSymbol field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Mandatory.
     * <li>Unique on lower case of entire string amongst all symbols in all Units.
     * <li>No longer than 255 characters.
     * <li>Must be a valid unit symbol recognised by JScience.
     * </ul>
     */
    @Test
    void updateWithInvalidInternalSymbol() {
        setAdminUser()
        updateUnitFieldJson('internalSymbol', 'empty', '')
        updateUnitFieldJson('internalSymbol', 'long', String.randomString(256))
        updateUnitFieldJson('internalSymbol', 'duplicate', 'kWh'); // Existing internalSymbol.
        updateUnitFieldJson('internalSymbol', 'format', 'not_a_real_unit_symbol')
    }

    /**
     * Tests the validation rules for the Unit externalSymbol field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Optional.
     * <li>Unique on lower case of entire string amongst all symbols in all Units.
     * <li>No longer than 255 characters.
     * </ul>
     */
    @Test
    void updateWithInvalidExternalSymbol() {
        setAdminUser()
        updateUnitFieldJson('externalSymbol', 'long', String.randomString(256))
    }

    /**
     * Submits a single Unit field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateUnitFieldJson(field, code, value) {
        updateInvalidFieldJson("/units/types/AAA3DAA7A390/units/1BB3DAA7A390", field, code, value, 3.5)
    }
}