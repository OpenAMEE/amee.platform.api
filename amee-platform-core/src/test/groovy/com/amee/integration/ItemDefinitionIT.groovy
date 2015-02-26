package com.amee.integration

import com.amee.domain.data.ItemDefinition
import groovyx.net.http.HttpResponseException
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

/**
 * Tests for the Item Definition API.
 */
class ItemDefinitionIT extends BaseApiTest {

    // Page one when sorted by name, resultStart is 0 and resultLimit is 4.
    static def itemDefinitionNames1 = [
        'APITestDimlessHistory',
        'APITestGHGElectricity',
        'Computers Generic',
        'Cooking']

    // Page two when sorted by name, resultStart is 4 and resultLimit is 4.
    static def itemDefinitionNames2 = [
        'Entertainment Generic',
        'GHGElectricity',
        'GHGP international grid electricity',
        'GHGUSSubregion']

    def static expectedUsageNames = ['usage1', 'usage2']
    def static expectedUsagePresents = ['false', 'true']

    /**
     * Tests for creation, fetch and deletion of an Item Definition using JSON responses.
     *
     * Create a new Item Definition by POSTing to '/definitions' (since 3.4.0).
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>drillDown
     * <li>usages
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) an Item Definition by sending a DELETE request to '/definitions/{UID}' (since 3.4.0).
     */
    @Test
    void createDeleteItemDefinition() {
        versions.each { version -> createDeleteItemDefinition(version) }
    }

    def createDeleteItemDefinition(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a new ItemDefinition
            def responsePost = client.post(
                    path: "/$version/definitions",
                    body: [name: 'test', drillDown: 'foo,bar', usages: 'baz,quux'],
                    requestContentType: URLENC,
                    contentType: JSON)
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")
            String uid = location.split('/')[5]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Get the new ItemDefinition
            def responseGet = client.get(path: "$location;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.itemDefinition.name == 'test'
            assert responseGet.data.itemDefinition.drillDown == 'foo,bar'
            assert responseGet.data.itemDefinition.usages.size() == 2
            assert responseGet.data.itemDefinition.usages.collect { it.name } == ['baz', 'quux']
            assert responseGet.data.itemDefinition.usages.collect { it.present } == ['false', 'false']

            // Delete it
            def responseDelete = client.delete(path: location)
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Should get a 404 here
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    /**
     * Test fetching a list of ItemDefinitions with a JSON response.
     *
     * Pagination may be used by setting the following query parameters:
     * <ul>
     * <li>resultStart - Zero-based starting index offset to support result-set 'pagination'. Defaults to 0.
     * <li>resultLimit - Limit the number of entries in the result-set. Defaults to 50 with a max of 100.
     * </ul>
     *
     * Item Definition GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>name - include the Item Definition name.
     * <li>drillDown - include the Item Definition drillDown values.
     * <li>usages - include the Item Definition usages values.
     * <li>algorithms - include the Item Definition algorithm UID and name values.
     * </ul>
     */
    @Test
    void getItemDefinitionsPageOneJson() {
        versions.each { version -> getItemDefinitionsPageOneJson(version) }
    }

    def getItemDefinitionsPageOneJson(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/$version/definitions;name",
                    query: [resultStart: 0, resultLimit: 4],
                    contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.itemDefinitions.size() == itemDefinitionNames1.size()
            assert response.data.itemDefinitions.collect { it.name } == itemDefinitionNames1
        }
    }

    @Test
    void getItemDefinitionsPageTwoJson() {
        versions.each { version -> getItemDefinitionsPageTwoJson(version) }
    }

    def getItemDefinitionsPageTwoJson(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/$version/definitions;name",
                    query: [resultStart: 4, resultLimit: 4],
                    contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.resultsTruncated
            assert response.data.itemDefinitions.size() == itemDefinitionNames2.size()
            assert response.data.itemDefinitions.collect { it.name } == itemDefinitionNames2
        }
    }

    /**
     * Tests fetching a list of Item Definitions with the given name using JSON.
     *
     * Names do not have to be unique and this request may return more than one result.
     */
    @Test
    void getItemDefinitionsByNameJson() {
        versions.each { version -> getItemDefinitionsByNameJson(version) }
    }

    def getItemDefinitionsByNameJson(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/$version/definitions;name",
                    query: [name: 'cooking'],
                    contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert !response.data.resultsTruncated
            assert response.data.itemDefinitions.size() == 1
            assert response.data.itemDefinitions.collect { it.uid } == ['WD5M1LM2X3W4']
            assert response.data.itemDefinitions.collect { it.name } == ['Cooking']
        }
    }

    /**
     * Tests trying to fetch Item Definitions using invalid name strings.
     */
    @Test
    void getItemDefinitionsByNameInvalidJson() {
        getItemDefinitionsByNameJson('short', '12')
        getItemDefinitionsByNameJson('long', String.randomString(ItemDefinition.NAME_MAX_SIZE + 1))
    }

    def getItemDefinitionsByNameJson(code, value) {
        versions.each { version -> getItemDefinitionsByNameJson(code, value, version) }
    }

    def getItemDefinitionsByNameJson(code, value, version) {
        if (version >= 3.3) {
            try {
                def query = [:]
                query['name'] = value
                client.get(
                        path: "/$version/definitions;name",
                        query: query,
                        contentType: JSON)
                fail 'Response status code should have been 400 (' + code + ').'
            } catch (HttpResponseException e) {
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'
                assert response.data.validationResult.errors.collect { it.code } == [code]
            }
        }
    }

    /**
     * Test fetching a list of ItemDefinitions with an XML response.
     */
    @Test
    void getItemDefinitionsPageOneXml() {
        versions.each { version -> getItemDefinitionsPageOneXml(version) }
    }

    def getItemDefinitionsPageOneXml(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/$version/definitions;name",
                    query: [resultStart: 0, resultLimit: 4],
                    contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.ItemDefinitions.@truncated.text() == 'true'
            def allItemDefinitions = response.data.ItemDefinitions.ItemDefinition
            assert allItemDefinitions.size() == itemDefinitionNames1.size()
            assert allItemDefinitions.Name*.text() == itemDefinitionNames1
        }
    }

    @Test
    void getItemDefinitionsPageTwoXml() {
        versions.each { version -> getItemDefinitionsPageTwoXml(version) }
    }

    def getItemDefinitionsPageTwoXml(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/$version/definitions;name",
                    query: [resultStart: 4, resultLimit: 4],
                    contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.ItemDefinitions.@truncated.text() == 'true'
            def allItemDefinitions = response.data.ItemDefinitions.ItemDefinition
            assert allItemDefinitions.size() == itemDefinitionNames2.size()
            assert allItemDefinitions.Name*.text() == itemDefinitionNames2
        }
    }

    /**
     * Tests fetching a single Item Definition by UID using a JSON response.
     */
    @Test
    void getItemDefinitionJson() {
        versions.each { version -> getItemDefinitionJson(version) }
    }

    def getItemDefinitionJson(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.itemDefinition.name == 'Computers Generic'
            assert response.data.itemDefinition.drillDown == 'device,rating'
            assert response.data.itemDefinition.usages.size() == expectedUsageNames.size()
            assert response.data.itemDefinition.usages.collect { it.name } == expectedUsageNames
            assert response.data.itemDefinition.usages.collect { it.present } == expectedUsagePresents
        }
    }

    /**
     * Tests fetching a single Item Definition by UID using an XML response.
     */
    @Test
    void getItemDefinitionXml() {
        versions.each { version -> getItemDefinitionXml(version) }
    }

    def getItemDefinitionXml(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.ItemDefinition.Name.text() == 'Computers Generic'
            assert response.data.ItemDefinition.DrillDown.text() == 'device,rating'
            def allUsages = response.data.ItemDefinition.Usages.Usage
            assert allUsages.size() == expectedUsageNames.size()
            assert allUsages.Name*.text() == expectedUsageNames
            assert allUsages.@present*.text() == expectedUsagePresents
        }
    }

    /**
     * Tests fetching a single Item Definition with algorithms using a JSON response.
     */
    @Test
    void getItemDefinitionWithAlgorithmsJson() {
        versions.each { version -> getItemDefinitionWithAlgorithmsJson(version) }
    }

    def getItemDefinitionWithAlgorithmsJson(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/WD5M1LM2X3W4;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.itemDefinition.name == 'Cooking'
            assert response.data.itemDefinition.drillDown == 'fuel,numberOfPeople'

            if (version > 3.4) {
                assert response.data.itemDefinition.algorithms.size() == 2
                assert response.data.itemDefinition.algorithms.collect { it.name }.sort() == ['ZZZ Name', 'default']
            }
        }
    }

    /**
     * Tests fetching a single Item Definition with algorithms using an XML response.
     */
    @Test
    void getItemDefinitionWithAlgorithmsXml() {
        versions.each { version -> getItemDefinitionWithAlgorithmsXml(version) }
    }

    def getItemDefinitionWithAlgorithmsXml(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/WD5M1LM2X3W4;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.ItemDefinition.Name.text() == 'Cooking'
            assert response.data.ItemDefinition.DrillDown.text() == 'fuel,numberOfPeople'

            if (version > 3.4) {
                def allAlgorithms = response.data.ItemDefinition.Algorithms.Algorithm
                assert allAlgorithms.size() == 2
                assert allAlgorithms.Name*.text().sort() == ['ZZZ Name', 'default']
            }
        }
    }

    /**
     * Tests that an Item Definition can be updated with valid values.
     *
     * Update an Item Definition by sending a PUT request to '/definitions/{UID}'.
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     */
    @Test
    void updateItemDefinitionJson() {
        versions.each { version -> updateItemDefinitionJson(version) }
    }

    def updateItemDefinitionJson(version) {
        if (version >= 3.1) {
            setAdminUser()

            // 1) Do the update.
            def responsePut = client.put(
                    path: "/$version/definitions/65RC86G6KMRA",
                    body: [name: 'newName', drillDown: 'newDrillDownA,newDrillDownB', usages: 'usage1,usage2,usage3'],
                    requestContentType: URLENC,
                    contentType: JSON)
            assertOkJson(responsePut, SUCCESS_OK.code, '65RC86G6KMRA')

            // We added a usage.
            expectedUsageNames[2] = 'usage3'
            expectedUsagePresents[2] = 'true'

            // 2) Check values have been updated.
            def responseGet = client.get(path: "/$version/definitions/65RC86G6KMRA;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.itemDefinition.name == 'newName'
            assert responseGet.data.itemDefinition.drillDown == 'newDrillDownA,newDrillDownB'
            assert responseGet.data.itemDefinition.usages.size() == expectedUsageNames.size()
            assert responseGet.data.itemDefinition.usages.collect { it.name } == expectedUsageNames
            assert responseGet.data.itemDefinition.usages.collect { it.present } == expectedUsagePresents
        }
    }

    /**
     * Tests validation rules.
     *
     * <ul>
     *    <li>name - nonempty, min: 3, max: 255
     *    <li>drillDown - max: 255
     *    <li>usages - max: 255
     * <ul>
     */
    @Test
    void updateInvalidItemDefinition() {
        setAdminUser()
        updateItemDefinitionFieldJson('name', 'empty', '')
        updateItemDefinitionFieldJson('name', 'short', 'a')
        updateItemDefinitionFieldJson('name', 'long', String.randomString(ItemDefinition.NAME_MAX_SIZE + 1))
        updateItemDefinitionFieldJson('drillDown', 'long', String.randomString(ItemDefinition.DRILL_DOWN_MAX_SIZE + 1))
//        updateItemDefinitionFieldJson('usages', 'long', String.randomString(ItemDefinition.USAGES_MAX_SIZE + 1))
    }

    /**
     * Submits a single Item Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateItemDefinitionFieldJson(field, code, value) {
        updateItemDefinitionFieldJson(field, code, value, 3.1)
    }

    /**
     * Submits a single Item Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateItemDefinitionFieldJson(field, code, value, since) {
        versions.each { version -> updateItemDefinitionFieldJson(field, code, value, since, version) }
    }

    /**
     * Submits a single Item Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    def updateItemDefinitionFieldJson(field, code, value, since, version) {
        if (version >= since) {
            try {
                def body = [(field): value]
                client.put(
                        path: "/$version/definitions/46IYVBS555M7",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail("Response status code should have been 400 (${field}, ${code}).")
            } catch (HttpResponseException e) {
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'

                // NOTE: 'usages' becomes 'usagesString' on the server-side.
                def fieldErrors = response.data.validationResult.errors.collect { it.field }
                if (field == 'usages') {
                    field = 'usagesString'
                }
                assert fieldErrors == [field]
                assert response.data.validationResult.errors.collect { it.code } == [code]
            }
        }
    }
}