package com.amee.integration

import com.amee.base.utils.UidGen
import com.amee.domain.unit.AMEEUnit
import groovyx.net.http.HttpResponseException
import org.junit.Test

import javax.measure.unit.NonSI

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

/**
 * Tests for the Unit API.
 */
class UnitIT extends BaseApiTest {

    def units = [
            [uid: '1BB3DAA7A390', name: 'Test Unit One', internalSymbol: 'kg', externalSymbol: 'zkg'],
            [uid: '2BB3DAA7A390', name: 'Test Unit Two', internalSymbol: 'kWh', externalSymbol: 'zkWh'],
            [uid: '3BB3DAA7A390', name: 'Test Unit Three', internalSymbol: 'm', externalSymbol: 'zm']
    ]

    def allUnits = units + [uid: '4BB3DAA7A390', name: 'Test Unit Four', internalSymbol: 'km', externalSymbol: 'zkm']

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
        versions.each { version -> createAndRemoveUnit(version, true) }
        versions.each { version -> createAndRemoveUnit(version, false) }
    }

    def createAndRemoveUnit(version, useUnitTypeResource) {
        if (version >= 3.5) {
            createAndRemoveUnit(version, 'Ounce', 'oz', 'ounce', useUnitTypeResource)
            createAndRemoveUnit(version, 'Angstrom', NonSI.ANGSTROM.toString(), 'ang', useUnitTypeResource)
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
                    path: "/$version/units/types/1AA3DAA7A390/units",
                    body: [name: name, internalSymbol: internalSymbol, externalSymbol: externalSymbol],
                    requestContentType: URLENC,
                    contentType: JSON)
        } else {

            // Use the base Units resource.
            responsePost = client.post(
                    path: "/$version/units",
                    body: [name: name, internalSymbol: internalSymbol, externalSymbol: externalSymbol, unitType: '1AA3DAA7A390'],
                    requestContentType: URLENC,
                    contentType: JSON)
        }

        // Get and check the location.
        String unitLocation = responsePost.headers['Location'].value
        String unitUid = unitLocation.split('/')[8]
        assert UidGen.INSTANCE_12.isValid(unitUid)
        assertOkJson(responsePost, SUCCESS_CREATED.code, unitUid)

        // Fetch the Unit.
        def response = client.get(path: "$unitLocation;full", contentType: JSON)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.unit.name == name
        assert response.data.unit.internalSymbol == internalSymbol
        assert response.data.unit.externalSymbol == externalSymbol

        // Then delete the Unit.
        def responseDelete = client.delete(path: unitLocation)
        assertOkJson(responseDelete, SUCCESS_OK.code, unitUid)

        // We should get a 404 here.
        try {
            client.get(path: unitLocation)
            fail 'Should have thrown an exception'
        } catch (HttpResponseException e) {
            assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
        }
    }

    def createAndRemoveUnitXml(version, name, internalSymbol, externalSymbol, useUnitTypeResource) {

        setAdminUser()

        // Create a new Unit.
        def responsePost
        if (useUnitTypeResource) {

            // Use the Unit Type Resource.
            responsePost = client.post(
                    path: "/$version/units/types/1AA3DAA7A390/units",
                    body: [name: name, internalSymbol: internalSymbol, externalSymbol: externalSymbol],
                    requestContentType: URLENC,
                    contentType: XML)
        } else {

            // Use the base Units resource.
            responsePost = client.post(
                    path: "/$version/units",
                    body: [name: name, internalSymbol: internalSymbol, externalSymbol: externalSymbol, unitType: '1AA3DAA7A390'],
                    requestContentType: URLENC,
                    contentType: XML)
        }

        // Get and check the location.
        String unitLocation = responsePost.headers['Location'].value
        String unitUid = unitLocation.split('/')[8]
        assert UidGen.INSTANCE_12.isValid(unitUid)
        assertOkXml(responsePost, SUCCESS_CREATED.code, unitUid)

        // Fetch the Unit.
        def response = client.get(path: "$unitLocation;full", contentType: XML)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.Unit.Name.text() == name
        assert response.data.Unit.InternalSymbol.text() == internalSymbol
        assert response.data.Unit.ExternalSymbol.text() == externalSymbol

        // Then delete the Unit.
        def responseDelete = client.delete(path: unitLocation, contentType: XML)
        assertOkXml(responseDelete, SUCCESS_OK.code, unitUid)

        // We should get a 404 here.
        try {
            client.get(path: unitLocation)
            fail 'Should have thrown an exception'
        } catch (HttpResponseException e) {
            assertEquals(CLIENT_ERROR_NOT_FOUND.code, e.response.status)
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
        versions.each { version -> getSingleUnit(version) }
    }

    def getSingleUnit(version) {
        getSingleUnitJson(version)
        getSingleUnitXml(version)
    }

    def getSingleUnitJson(version) {
        if (version >= 3.5) {
            def response = client.get(path: "/$version/units/types/AAA3DAA7A390/units/kg;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.unit.uid == '1BB3DAA7A390'
            assert response.data.unit.name == 'Test Unit One'
            assert response.data.unit.symbol == 'zkg'
            assert response.data.unit.internalSymbol == 'kg'
            assert response.data.unit.externalSymbol == 'zkg'
            assert response.data.alternatives.size() == 2
            assert response.data.alternatives.collect { it.uid }.sort() == ['2BB3DAA7A390', '3BB3DAA7A390'].sort()
            assert response.data.alternatives.collect { it.name }.sort() == ['Test Unit Two', 'Test Unit Three'].sort()
            assert response.data.alternatives.collect { it.symbol }.sort() == ['zkWh', 'zm'].sort()
        }
    }

    def getSingleUnitXml(version) {
        if (version >= 3.5) {
            def response = client.get(path: "/$version/units/types/AAA3DAA7A390/units/kg;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.Unit.@uid.text() == '1BB3DAA7A390'
            assert response.data.Unit.Name.text() == 'Test Unit One'
            assert response.data.Unit.Symbol.text() == 'zkg'
            assert response.data.Unit.InternalSymbol.text() == 'kg'
            assert response.data.Unit.ExternalSymbol.text() == 'zkg'
            def allAlternatives = response.data.Alternatives.Unit
            assert allAlternatives.size() == 2
            assert allAlternatives.@uid*.text().sort() == ['2BB3DAA7A390', '3BB3DAA7A390'].sort()
            assert allAlternatives.Name*.text().sort() == ['Test Unit Two', 'Test Unit Three'].sort()
            assert allAlternatives.Symbol*.text().sort() == ['zkWh', 'zm'].sort()
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
        versions.each { version -> getAllUnitsForUnitType(version) }
    }

    def getAllUnitsForUnitType(version) {
        getAllUnitsForUnitTypeJson(version)
        getAllUnitsForUnitTypeXml(version)
    }

    def getAllUnitsForUnitTypeJson(version) {
        if (version >= 3.5) {
            def response = client.get(path: "/$version/units/types/AAA3DAA7A390/units;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.units.size() == units.size()
            assert response.data.units.collect { it.uid }.sort() == units.collect { it.uid }.sort()
            assert response.data.units.collect { it.name }.sort() == units.collect { it.name }.sort()
            assert response.data.units.collect { it.internalSymbol }.sort() == units.collect { it.internalSymbol }.sort()
            assert response.data.units.collect { it.externalSymbol }.sort() == units.collect { it.externalSymbol }.sort()
        }
    }

    def getAllUnitsForUnitTypeXml(version) {
        if (version >= 3.5) {
            def response = client.get(path: "/$version/units/types/AAA3DAA7A390/units;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def allUnits = response.data.Units.Unit
            assert allUnits.size() == units.size()
            assert allUnits.@uid*.text().sort() == units.collect { it.uid }.sort()
            assert allUnits.Name*.text().sort() == units.collect { it.name }.sort()
            assert allUnits.InternalSymbol*.text().sort() == units.collect { it.internalSymbol }.sort()
            assert allUnits.ExternalSymbol*.text().sort() == units.collect { it.externalSymbol }.sort()
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
        versions.each { version -> getAllUnits(version) }
    }

    def getAllUnits(version) {
        getAllUnitsJson(version)
        getAllUnitsXml(version)
    }

    def getAllUnitsJson(version) {
        if (version >= 3.5) {
            def response = client.get(path: "/$version/units;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.units.size() == allUnits.size()
            assert response.data.units.collect { it.uid }.sort() == allUnits.collect { it.uid }.sort()
            assert response.data.units.collect { it.name }.sort() == allUnits.collect { it.name }.sort()
            assert response.data.units.collect { it.internalSymbol }.sort() == allUnits.collect { it.internalSymbol }.sort()
            assert response.data.units.collect { it.externalSymbol }.sort() == allUnits.collect { it.externalSymbol }.sort()
        }
    }

    def getAllUnitsXml(version) {
        if (version >= 3.5) {
            def response = client.get(path: "/$version/units;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def units = response.data.Units.Unit
            assert units.size() == allUnits.size()
            assert units.@uid*.text().sort() == allUnits.collect { it.uid }.sort()
            assert units.Name*.text().sort() == allUnits.collect { it.name }.sort()
            assert units.InternalSymbol*.text().sort() == allUnits.collect { it.internalSymbol }.sort()
            assert units.ExternalSymbol*.text().sort() == allUnits.collect {it.externalSymbol }.sort()
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
        updateUnitFieldJson('name', 'long', String.randomString(AMEEUnit.NAME_MAX_SIZE + 1))
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
        updateUnitFieldJson('internalSymbol', 'long', String.randomString(AMEEUnit.SYMBOL_MAX_SIZE + 1))
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
        updateUnitFieldJson('externalSymbol', 'long', String.randomString(AMEEUnit.SYMBOL_MAX_SIZE + 1))
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