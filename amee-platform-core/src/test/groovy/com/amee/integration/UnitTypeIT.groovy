package com.amee.integration

import com.amee.base.utils.UidGen
import com.amee.domain.unit.AMEEUnitType
import groovyx.net.http.HttpResponseException
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

/**
 * Tests for the Unit API.
 */
class UnitTypeIT extends BaseApiTest {

    def unitTypes = [
            [uid: 'AAA3DAA7A390', name: 'Test Unit Type One'],
            [uid: '1AA3DAA7A390', name: 'Test Unit Type Two']]

    /**
     * Tests for creation, fetch and deletion of a Unit Type using JSON & XML responses.
     *
     * Create a new Unit Type by POSTing to '/units/types'
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Unit Type by sending a DELETE request to '/units/types/{UID|name}'.
     *
     */
    @Test
    void createAndRemoveUnitType() {
        versions.each { version -> createAndRemoveUnitType(version) }
    }

    def createAndRemoveUnitType(version) {
        createAndRemoveUnitTypeJson(version)
        createAndRemoveUnitTypeXml(version)
    }

    def createAndRemoveUnitTypeJson(version) {
        if (version >= 3.5) {

            setAdminUser()

            def name = 'Unit Type To Be Deleted'

            // Create a new Unit Type.
            def responsePost = client.post(
                    path: "/$version/units/types",
                    body: [name: name],
                    requestContentType: URLENC,
                    contentType: JSON)

            // Get and check the location.
            String unitTypeLocation = responsePost.headers['Location'].value
            String unitTypeUid = unitTypeLocation.split('/')[6]
            assert UidGen.INSTANCE_12.isValid(unitTypeUid)
            assertOkJson(responsePost, SUCCESS_CREATED.code, unitTypeUid)

            // Fetch the Unit Type.
            def response = client.get(path: "/$version/units/types/$name;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.unitType.name == name

            // Then delete the Unit Type.
            def responseDelete = client.delete(path: "/$version/units/types/$name")
            assertOkJson(responseDelete, SUCCESS_OK.code, unitTypeUid)

            // We should get a 404 here.
            try {
                client.get(path: "/$version/units/types/$name")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    def createAndRemoveUnitTypeXml(version) {
        if (version >= 3.5) {

            setAdminUser()

            def name = 'Unit Type To Be Deleted'

            // Create a new Unit Type.
            def responsePost = client.post(
                    path: "/$version/units/types",
                    body: [name: name],
                    requestContentType: URLENC,
                    contentType: XML)

            // Get and check the location.
            String unitTypeLocation = responsePost.headers['Location'].value
            String unitTypeUid = unitTypeLocation.split('/')[6]
            assert UidGen.INSTANCE_12.isValid(unitTypeUid)
            assertOkXml(responsePost, SUCCESS_CREATED.code, unitTypeUid)

            // Fetch the Unit Type.
            def response = client.get(path: "/$version/units/types/$name;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.UnitType.Name.text() == name

            // Then delete the Unit Type.
            def responseDelete = client.delete(path: "/$version/units/types/$name", contentType: XML)
            assertOkXml(responseDelete, SUCCESS_OK.code, unitTypeUid)

            // We should get a 404 here.
            try {
                client.get(path: "/$version/units/types/$name")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    /**
     * Tests fetching a list of all Unit Types using JSON & XML.
     *
     * Unit Types GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * </ul>
     *
     * Unit Types are sorted by name.
     */
    @Test
    void getAllUnitTypes() {
        versions.each { version -> getAllUnitTypes(version) }
    }

    def getAllUnitTypes(version) {
        getAllUnitTypesJson(version)
        getAllUnitTypesXml(version)
    }

    def getAllUnitTypesJson(version) {
        if (version >= 3.5) {
            def response = client.get(path: "/$version/units/types", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.unitTypes.size() == unitTypes.size()
            assert response.data.unitTypes.collect { it.uid }.sort() == unitTypes.collect { it.uid }.sort()
            assert response.data.unitTypes.collect { it.name } == unitTypes.collect { it.name }.sort { a, b -> a.compareToIgnoreCase(b) }
        }
    }

    def getAllUnitTypesXml(version) {
        if (version >= 3.5) {
            def response = client.get(path: "/$version/units/types", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def allUnitTypes = response.data.UnitTypes.UnitType
            assert allUnitTypes.size() == unitTypes.size()
            assert allUnitTypes.@uid*.text().sort() == unitTypes.collect { it.uid }.sort()
            assert allUnitTypes.Name*.text().sort() == unitTypes.collect { it.name }.sort { a, b -> a.compareToIgnoreCase(b) }
        }
    }

    /**
     * Tests the validation rules for the Unit Type name field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Mandatory.
     * <li>Unique on lower case of entire string amongst all Unit Types.
     * <li>No longer than 255 characters.
     * </ul>
     */
    @Test
    void updateWithInvalidName() {
        setAdminUser()
        updateUnitTypeFieldJson('name', 'empty', '')
        updateUnitTypeFieldJson('name', 'long', String.randomString(AMEEUnitType.NAME_MAX_SIZE + 1))
        updateUnitTypeFieldJson('name', 'duplicate', 'Test Unit Type Two'); // Normal case.
        updateUnitTypeFieldJson('name', 'duplicate', 'test unit type two'); // Lower case.
    }

    /**
     * Submits a single Unit Type field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateUnitTypeFieldJson(field, code, value) {
        updateInvalidFieldJson("/units/types/AAA3DAA7A390", field, code, value, 3.5)
    }
}