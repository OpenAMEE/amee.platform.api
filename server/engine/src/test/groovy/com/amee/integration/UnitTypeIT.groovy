package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Unit API.
 */
class UnitTypeIT extends BaseApiTest {

    def unitTypeUids = [
            'AAA3DAA7A390',
            '1AA3DAA7A390'];

    def unitTypeNames = [
            'Test Unit Type One',
            'Test Unit Type Two'];

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
        com.amee.integration.BaseApiTest.versions.each { version -> createAndRemoveUnitType(version) }
    }

    def createAndRemoveUnitType(version) {
        createAndRemoveUnitTypeJson(version);
        createAndRemoveUnitTypeXml(version);
    }

    def createAndRemoveUnitTypeJson(version) {
        if (version >= 3.5) {

            setAdminUser();

            def name = 'Unit Type To Be Deleted';

            // Create a new Unit Type.
            def responsePost = client.post(
                    path: "/${version}/units/types",
                    body: [name: name],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, responsePost.status;

            // Get and check the location.
            def unitTypeLocation = responsePost.headers['Location'].value;
            def unitTypeUid = unitTypeLocation.split('/')[6];
            assertTrue unitTypeUid.size() == 12;

            // Fetch the Unit Type.
            def response = client.get(
                    path: "/${version}/units/types/${name};full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals name, response.data.unitType.name;

            // Then delete the Unit Type.
            def responseDelete = client.delete(path: "/${version}/units/types/${name}");
            assertEquals 200, responseDelete.status;

            // We should get a 404 here.
            try {
                client.get(path: "/${version}/units/types/${name}");
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
        }
    }

    def createAndRemoveUnitTypeXml(version) {
        if (version >= 3.5) {

            setAdminUser();

            def name = 'Unit Type To Be Deleted';

            // Create a new Unit Type.
            def responsePost = client.post(
                    path: "/${version}/units/types",
                    body: [name: name],
                    requestContentType: URLENC,
                    contentType: XML);
            assertEquals 201, responsePost.status;

            // Get and check the location.
            def unitTypeLocation = responsePost.headers['Location'].value;
            def unitTypeUid = unitTypeLocation.split('/')[6];
            assertTrue unitTypeUid.size() == 12;

            // Fetch the Unit Type.
            def response = client.get(
                    path: "/${version}/units/types/${name};full",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals name, response.data.UnitType.Name.text();

            // Then delete the Unit Type.
            def responseDelete = client.delete(path: "/${version}/units/types/${name}");
            assertEquals 200, responseDelete.status;

            // We should get a 404 here.
            try {
                client.get(path: "/${version}/units/types/${name}");
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
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
        com.amee.integration.BaseApiTest.versions.each { version -> getAllUnitTypes(version) }
    }

    def getAllUnitTypes(version) {
        getAllUnitTypesJson(version);
        getAllUnitTypesXml(version);
    }

    def getAllUnitTypesJson(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units/types",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals unitTypeUids.size(), response.data.unitTypes.size();
            assertEquals unitTypeUids.sort(), response.data.unitTypes.collect {it.uid}.sort();
            assertEquals unitTypeNames.sort { a, b -> a.compareToIgnoreCase(b) }, response.data.unitTypes.collect {it.name};
        }
    }

    def getAllUnitTypesXml(version) {
        if (version >= 3.5) {
            def response = client.get(
                    path: "/${version}/units/types",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            def allUnitTypes = response.data.UnitTypes.UnitType;
            assertEquals unitTypeUids.size(), allUnitTypes.size();
            assertEquals unitTypeUids.sort(), allUnitTypes.@uid*.text().sort();
            assertEquals unitTypeNames.sort { a, b -> a.compareToIgnoreCase(b) }, allUnitTypes.Name*.text().sort();
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
        setAdminUser();
        updateUnitTypeFieldJson('name', 'empty', '');
        updateUnitTypeFieldJson('name', 'long', String.randomString(256));
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