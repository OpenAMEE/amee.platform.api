package com.amee.integration

import com.amee.domain.data.ReturnValueDefinition
import groovyx.net.http.HttpResponseException
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

/**
 * Tests for the Return Value Definition API.
 *
 */
class ReturnValueDefinitionIT extends BaseApiTest {

    def static returnValueDefinitionUids = ['B0268549CD9C', '6008F958CE20']

    /**
     * Tests for creation, fetch and deletion of a Return Value Definition using JSON responses.
     *
     * Create a new Return Value Definition by POSTing to '/definitions/{UID}/returnvalues'
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>type
     * <li>unit
     * <li>perUnit
     * <li>defaultType
     * <li>valueDefinition
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Return Value Definition by sending a DELETE request to '/definitions/{UID}/returnvalues/{UID}'.
     *
     * This test also checks for the case described in: https://jira.amee.com/browse/PL-3692
     */
    @Test
    void createReturnValueDefinition() {
        versions.each { version -> createReturnValueDefinition(version) }
    }

    def createReturnValueDefinition(version) {
        if (version >= 3.1) {
            setAdminUser()

            // Check RVD list is as expected, pre-update.
            getReturnValueDefinitionsJson()

            // Create a new RVD.
            def responsePost = client.post(
                    path: "/$version/definitions/65RC86G6KMRA/returnvalues",
                    body: [type: 'CO2', unit: 'kg', perUnit: 'month', valueDefinition: 'OMU53CZCY970', name: 'Test Return Value Definition'],
                    requestContentType: URLENC,
                    contentType: JSON)

            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")

            String uid = location.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Get the new RVD.
            def responseGet = client.get(path: location, contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'

            // Find new RVD in list of RVDs.
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            def uids = response.data.returnValueDefinitions.collect { it.uid }
            assert uids.contains(uid)

            // Then delete it.
            def responseDelete = client.delete(path: location)
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // We should get a 404 here.
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    /**
     * Test fetching a list of Return Value Definitions for an Item Definition with JSON response.
     *
     * Return Value Definition GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>itemDefinition - include the ItemDefinition UID and name values
     * <li>valueDefinition - include the ValueDefinition UID, name and type values
     * <li>audit - include the status, created and modified values.
     * <li>type - include the return value type, eg 'CO2'.
     * <li>units - include the unit and perUnit values.
     * <li>flags - include the default flag. True if the return value is the default type.
     * </ul>
     *
     * Return Value Definitions are sorted by type.
     */
    @Test
    void getReturnValueDefinitionsJson() {
        versions.each { version -> getReturnValueDefinitionsJson(version) }
    }

    def getReturnValueDefinitionsJson(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.returnValueDefinitions.size() == returnValueDefinitionUids.size()
            assert response.data.returnValueDefinitions.collect { it.uid }.sort() == returnValueDefinitionUids.sort()

            // Should  be sorted by type
            assert response.data.returnValueDefinitions.first().type.compareToIgnoreCase(response.data.returnValueDefinitions.last().type) < 0
        }
    }

    /**
     * Test fetching a number of Return Value Definitions with XML response.
     */
    @Test
    void getReturnValueDefinitionsXml() {
        versions.each { version -> getReturnValueDefinitionsXml(version) }
    }

    def getReturnValueDefinitionsXml(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def allReturnValueDefinitions = response.data.ReturnValueDefinitions.ReturnValueDefinition
            assert allReturnValueDefinitions.size() == returnValueDefinitionUids.size()
            assert allReturnValueDefinitions.@uid*.text().sort() == returnValueDefinitionUids.sort()

            // Should  be sorted by type
            assert allReturnValueDefinitions[0].Type.text().compareToIgnoreCase(allReturnValueDefinitions[-1].Type.text()) < 0
        }
    }

    /**
     * Tests fetching a single Return Value Definition using JSON.
     */
    @Test
    void getReturnValueDefinitionJson() {
        versions.each { version -> getReturnValueDefinitionJson(version) }
    }

    def getReturnValueDefinitionJson(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues/B0268549CD9C;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.returnValueDefinition.uid == 'B0268549CD9C'
            assert response.data.returnValueDefinition.type == 'co2'
            assert response.data.returnValueDefinition.unit == 'kg'
            assert response.data.returnValueDefinition.perUnit == 'month'
            assert response.data.returnValueDefinition['default'] == 'true'
			assert response.data.returnValueDefinition.name == 'Test Return Value Definition'
            assert response.data.returnValueDefinition.itemDefinition.uid == '65RC86G6KMRA'
            assert response.data.returnValueDefinition.itemDefinition.name == 'Computers Generic'
            assert response.data.returnValueDefinition.valueDefinition.uid == 'OMU53CZCY970'
            assert response.data.returnValueDefinition.valueDefinition.name == 'amount'
            if (version >= 3.4) {
                assert response.data.returnValueDefinition.valueDefinition.valueType == 'DOUBLE'
            } else {
                assert response.data.returnValueDefinition.valueDefinition.valueType == 'DECIMAL'
            }
            if (version >= 3.2) {
                assert response.data.returnValueDefinition.created == '2010-08-17T15:13:41Z'
                assert response.data.returnValueDefinition.modified == '2010-08-17T15:13:41Z'
                assert response.data.returnValueDefinition.status == 'ACTIVE'
            }
        }
    }

    /**
     * Tests fetching a single Return Value Definition using XML.
     */
    @Test
    void getReturnValueDefinitionXml() {
        versions.each { version -> getReturnValueDefinitionXml(version) }
    }

    def getReturnValueDefinitionXml(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues/B0268549CD9C;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.ReturnValueDefinition.@uid.text() == 'B0268549CD9C'
            assert response.data.ReturnValueDefinition.Type.text() == 'co2'
            assert response.data.ReturnValueDefinition.Unit.text() == 'kg'
            assert response.data.ReturnValueDefinition.PerUnit.text() == 'month'
            assert response.data.ReturnValueDefinition.Default.text() == 'true'
			assert response.data.ReturnValueDefinition.Name.text() == 'Test Return Value Definition'
            assert response.data.ReturnValueDefinition.ItemDefinition.@uid.text() == '65RC86G6KMRA'
            assert response.data.ReturnValueDefinition.ItemDefinition.Name.text() == 'Computers Generic'
            assert response.data.ReturnValueDefinition.ValueDefinition.@uid.text() == 'OMU53CZCY970'
            assert response.data.ReturnValueDefinition.ValueDefinition.Name.text() == 'amount'
            if (version >= 3.4) {
                assert response.data.ReturnValueDefinition.ValueDefinition.ValueType.text() == 'DOUBLE'
            } else {
                assert response.data.ReturnValueDefinition.ValueDefinition.ValueType.text() == 'DECIMAL'
            }
            if (version >= 3.2) {
                assert response.data.ReturnValueDefinition.@created.text() == '2010-08-17T15:13:41Z'
                assert response.data.ReturnValueDefinition.@modified.text() == '2010-08-17T15:13:41Z'
                assert response.data.ReturnValueDefinition.@status.text() == 'ACTIVE'
            }
        }
    }

    /**
     * Tests updating a ReturnValueDefinition.
     */
    @Test
    void updateReturnValueDefinitionJson() {
        versions.each { version -> updateReturnValueDefinitionJson(version) }
    }

    def updateReturnValueDefinitionJson(version) {
        if (version >= 3.1) {
            setAdminUser()

            // 1) Do the update.
            def responsePut = client.put(
                    path: "/$version/definitions/65RC86G6KMRA/returnvalues/6008F958CE20",
                    body: [type: 'drink', unit: 'bbl', perUnit: 'day'],
                    requestContentType: URLENC,
                    contentType: JSON)
            assertOkJson(responsePut, SUCCESS_OK.code, '6008F958CE20')

            // 2) Check values have been updated.
            def responseGet = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues/6008F958CE20;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.returnValueDefinition.type == 'drink'
            assert responseGet.data.returnValueDefinition.unit == 'bbl'
            assert responseGet.data.returnValueDefinition.perUnit == 'day'
        }
    }

    /**
     * Tests handling of the default type.
     *
     * There can be only one return value definition for a particular item definition with default set to true.
     *
     * Updating a return value definition by setting the default type to true will set any other existing default type to false.
     */
    @Test
    void defaultType() {
        versions.each { version -> defaultType(version) }
    }

    def defaultType(version) {
        if (version >= 3.1) {
            setAdminUser()

            // Get the current default type.
            def responseGet = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues/B0268549CD9C;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.returnValueDefinition['default'] == 'true'

            // 1. Handle POST.
            // Add a new return value definition with default type true.
            def responsePost = client.post(
                    path: "/$version/definitions/65RC86G6KMRA/returnvalues",
                    body: [type: 'new', unit: 'kg', perUnit: 'day', valueDefinition: 'OMU53CZCY970',
                           name: 'Test Return Value Definition', defaultType: true],
                    requestContentType: URLENC,
                    contentType: JSON)
            assert responsePost.status == SUCCESS_CREATED.code
            String location = responsePost.headers['Location'].value

            // Check the new one is default.
            responseGet = client.get(path: "$location;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.returnValueDefinition['default'] == 'true'

            // Check the old one is no longer default.
            responseGet = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues/B0268549CD9C;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.returnValueDefinition['default'] == 'false'

            // 2. Handle PUT.
            // Update the old one to be the default again.
            def responsePut = client.put(
                    path: "/$version/definitions/65RC86G6KMRA/returnvalues/B0268549CD9C",
                    body: [type: 'co2', unit: 'kg', perUnit: 'month', valueDefinition: 'OMU53CZCY970', defaultType: true],
                    requestContentType: URLENC,
                    contentType: JSON)
            assert responsePut.status == SUCCESS_OK.code

            // Check new one is now not the default.
            responseGet = client.get(path: "$location;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.returnValueDefinition['default'] == 'false'

            // Check old one is now the default.
            responseGet = client.get(path: "/$version/definitions/65RC86G6KMRA/returnvalues/B0268549CD9C;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.returnValueDefinition['default'] == 'true'

            // 3. Handle DELETE.
            // Then delete the new one.
            def responseDelete = client.delete(path: location)
            assert responseDelete.status == SUCCESS_OK.code

            // We should get a 404 here.
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    /**
     * Tests the validation rules.
     *
     * <ul>
     *     <li>type - non-empty, unique, min: 1, max: 255</li>
     *     <li>defaultType - must be one with default set to true</li>
     *     <li>unit - valid unit, max: 255</li>
     *     <li>perUnit - value perUnit, max: 255</li>
     *     <li>valueDefinition - valid UID</li>
     * </ul>
     */
    @Test
    void updateInvalidReturnValueDefinition() {
        setAdminUser()
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'type', 'empty', '')
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'type', 'long', String.randomString(ReturnValueDefinition.TYPE_MAX_SIZE + 1))
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'type', 'duplicate', 'co2')
        updateReturnValueDefinitionFieldJson('B0268549CD9C', 'defaultType', 'no_default_type', 'false')
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'unit', 'typeMismatch', 'not_a_unit')
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'perUnit', 'typeMismatch', 'not_a_per_unit')
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'valueDefinition', 'typeMismatch', 'AAAAAAAAAAAA')
    }

    /**
     * Submits a single Return Value Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateReturnValueDefinitionFieldJson(uid, field, code, value) {
        updateReturnValueDefinitionFieldJson(uid, field, code, value, 3.1)
    }

    /**
     * Submits a single Return Value Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateReturnValueDefinitionFieldJson(uid, field, code, value, since) {
        versions.each { version -> updateReturnValueDefinitionFieldJson(uid, field, code, value, since, version) }
    }

    /**
     * Submits a single Return Value Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    def updateReturnValueDefinitionFieldJson(uid, field, code, value, since, version) {
        if (version >= since) {
            try {
                def body = [:]
                body[field] = value
                client.put(
                        path: "/$version/definitions/65RC86G6KMRA/returnvalues/$uid",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail 'Response status code should have been 400 (' + field + ', ' + code + ').'
            } catch (HttpResponseException e) {
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'
                assert response.data.validationResult.errors.collect { it.field } == [field]
                assert response.data.validationResult.errors.collect { it.code } == [code]
            }
        }
    }
}
