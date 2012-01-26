package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Item Definition API.
 */
class ItemDefinitionIT extends BaseApiTest {

    // Page one when sorted by name, resultStart is 0 and resultLimit is 4.
    static def itemDefinitionNames1 = [
            'Computers Generic',
            'Cooking',
            'EcoSpold',
            'Entertainment Generic'];

    // Page two when sorted by name, resultStart is 4 and resultLimit is 4.
    static def itemDefinitionNames2 = [
            'GHGElectricity',
            'GHGP international grid electricity',
            'GHGUSSubregion',
            'ICE v2 by mass'];

    def static expectedUsageNames = ['usage1', 'usage2'];
    def static expectedUsagePresents = ['false', 'true'];

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
                    path: "/${version}/definitions",
                    body: [
                            'name': 'test',
                            'drillDown': 'foo,bar',
                            'usages': 'baz,quux'],
                    requestContentType: URLENC,
                    contentType: JSON)
            assertEquals 201, responsePost.status
            def location = responsePost.headers['Location'].value;
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Get the new ItemDefinition
            def responseGet = client.get(
                    path: "${location};full",
                    contentType: JSON)
            assertEquals 200, responseGet.status
            assertEquals 'application/json', responseGet.contentType
            assertTrue responseGet.data instanceof net.sf.json.JSON
            assertEquals 'OK', responseGet.data.status
            assertEquals 'test', responseGet.data.itemDefinition.name
            assertEquals 'foo,bar', responseGet.data.itemDefinition.drillDown
            assertEquals 2, responseGet.data.itemDefinition.usages.size();
            assertEquals(['baz', 'quux'], responseGet.data.itemDefinition.usages.collect {it.name});
            assertEquals(['false', 'false'], responseGet.data.itemDefinition.usages.collect {it.present});

            // Delete it
            def responseDelete = client.delete(path: location)
            assertEquals 200, responseDelete.status

            // Should get a 404 here
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status
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
        versions.each { version -> getItemDefinitionsPageOneJson(version) };
    }

    def getItemDefinitionsPageOneJson(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/${version}/definitions;name",
                    query: ['resultStart': 0, 'resultLimit': 4],
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals itemDefinitionNames1.size(), response.data.itemDefinitions.size();
            assert itemDefinitionNames1 == response.data.itemDefinitions.collect {it.name};
        }
    }

    @Test
    void getItemDefinitionsPageTwoJson() {
        versions.each { version -> getItemDefinitionsPageTwoJson(version) };
    }

    def getItemDefinitionsPageTwoJson(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/${version}/definitions;name",
                    query: ['resultStart': 4, 'resultLimit': 4],
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertTrue response.data.resultsTruncated;
            assertEquals itemDefinitionNames2.size(), response.data.itemDefinitions.size();
            assert itemDefinitionNames2 == response.data.itemDefinitions.collect {it.name};
        }
    }

    /**
     * Tests fetching a list of Item Definitions with the given name using JSON.
     *
     * Names do not have to be unique and this request may return more than one result.
     */
    @Test
    void getItemDefinitionsByNameJson() {
        versions.each { version -> getItemDefinitionsByNameJson(version) };
    }

    def getItemDefinitionsByNameJson(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/${version}/definitions;name",
                    query: ['name': 'cooking'],
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertFalse response.data.resultsTruncated;
            assertEquals 1, response.data.itemDefinitions.size();
            assert ['1B3B44CAE90C'] == response.data.itemDefinitions.collect {it.uid};
            assert ['Cooking'] == response.data.itemDefinitions.collect {it.name};
        }
    }

    /**
     * Tests trying to fetch Item Definitions using invalid name strings.
     */
    @Test
    void getItemDefinitionsByNameInvalidJson() {
        getItemDefinitionsByNameJson('short', '12');
        getItemDefinitionsByNameJson('long', String.randomString(256));
    }

    def getItemDefinitionsByNameJson(code, value) {
        versions.each { version -> getItemDefinitionsByNameJson(code, value, version) };
    }

    def getItemDefinitionsByNameJson(code, value, version) {
        if (version >= 3.3) {
            try {
                def query = [:];
                query['name'] = value;
                client.get(
                        path: "/${version}/definitions;name",
                        query: query,
                        contentType: JSON);
                fail 'Response status code should have been 400 (' + code + ').';
            } catch (HttpResponseException e) {
                def response = e.response;
                assertEquals 400, response.status;
                assertEquals 'application/json', response.contentType;
                assertTrue response.data instanceof net.sf.json.JSON;
                assertEquals 'INVALID', response.data.status;
                assertTrue([code] == response.data.validationResult.errors.collect {it.code});
            }
        }
    }

    /**
     * Test fetching a list of ItemDefinitions with an XML response.
     */
    @Test
    void getItemDefinitionsPageOneXml() {
        versions.each { version -> getItemDefinitionsPageOneXml(version) };
    }

    def getItemDefinitionsPageOneXml(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/${version}/definitions;name",
                    query: ['resultStart': 0, 'resultLimit': 4],
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals 'true', response.data.ItemDefinitions.@truncated.text();
            def allItemDefinitions = response.data.ItemDefinitions.ItemDefinition;
            assertEquals itemDefinitionNames1.size(), allItemDefinitions.size();
            assert itemDefinitionNames1 == allItemDefinitions.Name*.text();
        }
    }

    @Test
    void getItemDefinitionsPageTwoXml() {
        versions.each { version -> getItemDefinitionsPageTwoXml(version) };
    }

    def getItemDefinitionsPageTwoXml(version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/${version}/definitions;name",
                    query: ['resultStart': 4, 'resultLimit': 4],
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals 'true', response.data.ItemDefinitions.@truncated.text();
            def allItemDefinitions = response.data.ItemDefinitions.ItemDefinition;
            assertEquals itemDefinitionNames2.size(), allItemDefinitions.size();
            assert itemDefinitionNames2 == allItemDefinitions.Name*.text();
        }
    }

    /**
     * Tests fetching a single Item Definition by UID using a JSON response.
     */
    @Test
    void getItemDefinitionJson() {
        versions.each { version -> getItemDefinitionJson(version) };
    }

    def getItemDefinitionJson(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals 'Computers Generic', response.data.itemDefinition.name;
            assertEquals 'device,rating', response.data.itemDefinition.drillDown;
            assertEquals expectedUsageNames.size(), response.data.itemDefinition.usages.size();
            assert expectedUsageNames == response.data.itemDefinition.usages.collect {it.name};
            assert expectedUsagePresents == response.data.itemDefinition.usages.collect {it.present};
        }
    }

    /**
     * Tests fetching a single Item Definition by UID using an XML response.
     */
    @Test
    void getItemDefinitionXml() {
        versions.each { version -> getItemDefinitionXml(version) };
    }

    def getItemDefinitionXml(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2;full",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals 'Computers Generic', response.data.ItemDefinition.Name.text();
            assertEquals 'device,rating', response.data.ItemDefinition.DrillDown.text();
            def allUsages = response.data.ItemDefinition.Usages.Usage;
            assertEquals expectedUsageNames.size(), allUsages.size();
            assertTrue(expectedUsageNames == allUsages.Name*.text());
            assertTrue(expectedUsagePresents == allUsages.@present*.text());
        }
    }

    /**
     * Tests fetching a single Item Definition with algorithms using a JSON response.
     */
    @Test
    void getItemDefinitionWithAlgorithmsJson() {
        versions.each { version -> getItemDefinitionWithAlgorithmsJson(version) };
    }

    def getItemDefinitionWithAlgorithmsJson(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/1B3B44CAE90C;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals 'Cooking', response.data.itemDefinition.name;
            assertEquals 'numberOfPeople,fuel', response.data.itemDefinition.drillDown;

            if (version > 3.4) {
                assertEquals 2, response.data.itemDefinition.algorithms.size();
                assert ['default', 'ZZZ Name'].sort() == response.data.itemDefinition.algorithms.collect {it.name}.sort();
            }
        }
    }

    /**
     * Tests fetching a single Item Definition with algorithms using an XML response.
     */
    @Test
    void getItemDefinitionWithAlgorithmsXml() {
        versions.each { version -> getItemDefinitionWithAlgorithmsXml(version) };
    }

    def getItemDefinitionWithAlgorithmsXml(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/1B3B44CAE90C;full",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals 'Cooking', response.data.ItemDefinition.Name.text();
            assertEquals 'numberOfPeople,fuel', response.data.ItemDefinition.DrillDown.text();

            if (version > 3.4) {
                def allAlgorithms = response.data.ItemDefinition.Algorithms.Algorithm;
                assertEquals 2, allAlgorithms.size();
                assertTrue(['default', 'ZZZ Name'].sort() == allAlgorithms.Name*.text().sort());
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
        versions.each { version -> updateItemDefinitionJson(version) };
    }

    def updateItemDefinitionJson(version) {
        if (version >= 3.1) {
            setAdminUser();

            // 1) Do the update.
            def responsePut = client.put(
                    path: "/${version}/definitions/11D3548466F2",
                    body: [
                            'name': 'newName',
                            'drillDown': 'newDrillDownA,newDrillDownB',
                            'usages': 'usage1,usage2,usage3'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 204, responsePut.status;

            // We added a usage.
            expectedUsageNames[2] = 'usage3';
            expectedUsagePresents[2] = 'true';

            // 2) Check values have been updated.
            def responseGet = client.get(
                    path: "/${version}/definitions/11D3548466F2;full",
                    contentType: JSON);
            assertEquals 200, responseGet.status;
            assertEquals 'application/json', responseGet.contentType;
            assertTrue responseGet.data instanceof net.sf.json.JSON;
            assertEquals 'OK', responseGet.data.status;
            assertEquals 'newName', responseGet.data.itemDefinition.name;
            assertEquals 'newDrillDownA,newDrillDownB', responseGet.data.itemDefinition.drillDown;
            assertEquals expectedUsageNames.size(), responseGet.data.itemDefinition.usages.size();
            assertTrue(expectedUsageNames == responseGet.data.itemDefinition.usages.collect {it.name});
            assertTrue(expectedUsagePresents == responseGet.data.itemDefinition.usages.collect {it.present});
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
        setAdminUser();
        updateItemDefinitionFieldJson('name', 'empty', '');
        updateItemDefinitionFieldJson('name', 'short', 'a');
        updateItemDefinitionFieldJson('name', 'long', String.randomString(256));
        updateItemDefinitionFieldJson('drillDown', 'long', String.randomString(256));
        updateItemDefinitionFieldJson('usages', 'long', String.randomString(32768));
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
        versions.each { version -> updateItemDefinitionFieldJson(field, code, value, since, version) };
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
                        path: "/${version}/definitions/BB33FDB20228",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail "Response status code should have been 400 (${field}, ${code})."
            } catch (HttpResponseException e) {
                def response = e.response
                assertEquals 400, response.status
                assertEquals 'application/json', response.contentType
                assertTrue response.data instanceof net.sf.json.JSON
                assertEquals 'INVALID', response.data.status

                // NOTE: 'usages' becomes 'usagesString' on the server-side.
                def fieldErrors = response.data.validationResult.errors.collect {it.field};
                if (field == 'usages') {
                    field = 'usagesString'
                }
                assertEquals([field], fieldErrors)
                assertEquals([code], response.data.validationResult.errors.collect {it.code})
            }
        }
    }
}