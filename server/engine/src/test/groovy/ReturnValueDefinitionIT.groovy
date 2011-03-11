import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Return Value Definition API.
 *
 * TODO: Document Return Value Definition API fully here. See https://jira.amee.com/browse/PL-9550 to vote on this task.
 */
class ReturnValueDefinitionIT extends BaseApiTest {

    def static returnValueDefinitionUids = ['B0268549CD9C', '6008F958CE20'];
    def static returnValueDefinitionTypes = ['co2', 'co2e'];

    /**
     * This test also checks for the case described in: https://jira.amee.com/browse/PL-3692
     */
    @Test
    void createReturnValueDefinition() {
        versions.each { version -> createReturnValueDefinition(version) }
    }

    def createReturnValueDefinition(version) {
        if (version >= 3.1) {
            setAdminUser();

            // Check RVD list is as expected, pre-update.
            getReturnValueDefinitionsJson();

            // Create a new RVD.
            def responsePost = client.post(
                    path: "/${version}/definitions/11D3548466F2/returnvalues",
                    body: ['type': 'CO2',
                            'unit': 'kg',
                            'perUnit': 'month',
                            'valueDefinition': '45433E48B39F'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, responsePost.status;
            assertTrue responsePost.headers['Location'] != null;
            assertTrue responsePost.headers['Location'].value != null;
            def location = responsePost.headers['Location'].value;
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Add new RVD to local state.
            def uid = location.split('/')[7];

            // Get the new RVD.
            def responseGet = client.get(
                    path: location,
                    contentType: JSON);
            assertEquals 200, responseGet.status;
            assertEquals 'application/json', responseGet.contentType;
            assertTrue responseGet.data instanceof net.sf.json.JSON;
            assertEquals 'OK', responseGet.data.status;

            // Find new RVD in list of RVDs.
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2/returnvalues",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            def uids = response.data.returnValueDefinitions.collect {it.uid};
            assertTrue uids.contains(uid);

            // Then delete it.
            def responseDelete = client.delete(path: location);
            assertEquals 200, responseDelete.status;

            // We should get a 404 here.
            try {
                client.get(path: location);
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
        }
    }

    @Test
    void removeReturnValueDefinitionJson() {
        versions.each { version -> removeReturnValueDefinitionJson(version) }
    }

    def removeReturnValueDefinitionJson(version) {
        if (version >= 3.1) {
            setAdminUser();
            // Create a new RVD.
            def responsePost = client.post(
                    path: "/${version}/definitions/11D3548466F2/returnvalues",
                    body: ['type': 'CO2',
                            'unit': 'kg',
                            'perUnit': 'month',
                            'valueDefinition': '45433E48B39F'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, responsePost.status;
            assertTrue responsePost.headers['Location'] != null;
            assertTrue responsePost.headers['Location'].value != null;
            def location = responsePost.headers['Location'].value;
            // Then delete it
            def responseDelete = client.delete(path: location);
            assertEquals 200, responseDelete.status;
            // We should get a 404 here
            try {
                client.get(path: location);
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
        }
    }

    @Test
    void getReturnValueDefinitionJson() {
        versions.each { version -> getReturnValueDefinitionJson(version) }
    }

    def getReturnValueDefinitionJson(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2/returnvalues/B0268549CD9C;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals 'B0268549CD9C', response.data.returnValueDefinition.uid;
            assertEquals 'co2', response.data.returnValueDefinition.type;
            assertEquals 'kg', response.data.returnValueDefinition.unit;
            assertEquals 'month', response.data.returnValueDefinition.perUnit;
            assertEquals 'true', response.data.returnValueDefinition['default'];
            assertEquals '11D3548466F2', response.data.returnValueDefinition.itemDefinition.uid;
            assertEquals 'Computers Generic', response.data.returnValueDefinition.itemDefinition.name;
            assertEquals '45433E48B39F', response.data.returnValueDefinition.valueDefinition.uid;
            assertEquals 'amount', response.data.returnValueDefinition.valueDefinition.name;
            if (version >= 3.4) {
                assertEquals 'DOUBLE', response.data.returnValueDefinition.valueDefinition.valueType;
            } else {
                assertEquals 'DECIMAL', response.data.returnValueDefinition.valueDefinition.valueType;
            }
            if (version >= 3.2) {
                assertEquals '2010-08-17T15:13:41Z', response.data.returnValueDefinition.created;
                assertEquals '2010-08-17T15:13:41Z', response.data.returnValueDefinition.modified;
                assertEquals 'ACTIVE', response.data.returnValueDefinition.status;
            }
        }
    }

    @Test
    void getReturnValueDefinitionXml() {
        versions.each { version -> getReturnValueDefinitionXml(version) }
    }

    def getReturnValueDefinitionXml(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2/returnvalues/B0268549CD9C;full",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals 'B0268549CD9C', response.data.ReturnValueDefinition.@uid.text();
            assertEquals 'co2', response.data.ReturnValueDefinition.Type.text();
            assertEquals 'kg', response.data.ReturnValueDefinition.Unit.text();
            assertEquals 'month', response.data.ReturnValueDefinition.PerUnit.text();
            assertEquals 'true', response.data.ReturnValueDefinition.Default.text();
            assertEquals '11D3548466F2', response.data.ReturnValueDefinition.ItemDefinition.@uid.text();
            assertEquals 'Computers Generic', response.data.ReturnValueDefinition.ItemDefinition.Name.text();
            assertEquals '45433E48B39F', response.data.ReturnValueDefinition.ValueDefinition.@uid.text();
            assertEquals 'amount', response.data.ReturnValueDefinition.ValueDefinition.Name.text();
            if (version >= 3.4) {
                assertEquals 'DOUBLE', response.data.ReturnValueDefinition.ValueDefinition.ValueType.text();
            } else {
                assertEquals 'DECIMAL', response.data.ReturnValueDefinition.ValueDefinition.ValueType.text();
            }
            if (version >= 3.2) {
                assertEquals '2010-08-17T15:13:41Z', response.data.ReturnValueDefinition.@created.text();
                assertEquals '2010-08-17T15:13:41Z', response.data.ReturnValueDefinition.@modified.text();
                assertEquals 'ACTIVE', response.data.ReturnValueDefinition.@status.text();
            }
        }
    }

    @Test
    void getReturnValueDefinitionsJson() {
        versions.each { version -> getReturnValueDefinitionsJson(version) }
    }

    def getReturnValueDefinitionsJson(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2/returnvalues;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals returnValueDefinitionUids.size(), response.data.returnValueDefinitions.size();
            assertEquals returnValueDefinitionUids.sort(), response.data.returnValueDefinitions.collect {it.uid}.sort();

            // Should  be sorted by type
            assertTrue response.data.returnValueDefinitions.first().type.compareToIgnoreCase(response.data.returnValueDefinitions.last().type) < 0
        }
    }

    @Test
    void getReturnValueDefinitionsXml() {
        versions.each { version -> getReturnValueDefinitionsXml(version) }
    }

    def getReturnValueDefinitionsXml(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2/returnvalues;full",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            def allReturnValueDefinitions = response.data.ReturnValueDefinitions.ReturnValueDefinition;
            assertEquals returnValueDefinitionUids.size(), allReturnValueDefinitions.size();
            assertEquals returnValueDefinitionUids.sort(), allReturnValueDefinitions.@uid*.text().sort();

            // Should  be sorted by type
            assertTrue allReturnValueDefinitions[0].Type.text().compareToIgnoreCase(allReturnValueDefinitions[-1].Type.text()) < 0
        }
    }

    @Test
    void updateReturnValueDefinitionJson() {
        versions.each { version -> updateReturnValueDefinitionJson(version) }
    }

    def updateReturnValueDefinitionJson(version) {
        if (version >= 3.1) {
            setAdminUser();
            // 1) Do the update.
            def responsePut = client.put(
                    path: "/${version}/definitions/11D3548466F2/returnvalues/6008F958CE20",
                    body: ['type': 'drink',
                            'unit': 'bbl',
                            'perUnit': 'day'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 204, responsePut.status;
            // 2) Check values have been updated.
            def responseGet = client.get(
                    path: '/3.1/definitions/11D3548466F2/returnvalues/6008F958CE20;full',
                    contentType: JSON);
            assertEquals 200, responseGet.status;
            assertEquals 'application/json', responseGet.contentType;
            assertTrue responseGet.data instanceof net.sf.json.JSON;
            assertEquals 'OK', responseGet.data.status;
            assertEquals 'drink', responseGet.data.returnValueDefinition.type;
            assertEquals 'bbl', responseGet.data.returnValueDefinition.unit;
            assertEquals 'day', responseGet.data.returnValueDefinition.perUnit;
        }
    }

    @Test
    void defaultType() {
        versions.each { version -> defaultType(version) }
    }

    def defaultType(version) {
        if (version >= 3.1) {
            setAdminUser()

            // Get the current default type.
            def responseGet = client.get(path: "/${version}/definitions/11D3548466F2/returnvalues/B0268549CD9C;full", contentType: JSON)
            assertEquals 200, responseGet.status
            assertEquals 'true', responseGet.data.returnValueDefinition['default']

            // 1. Handle POST.
            // Add a new return value definition with default type true.
            def responsePost = client.post(
                    path: "/${version}/definitions/11D3548466F2/returnvalues",
                    body: ['type': 'new',
                            'unit': 'kg',
                            'perUnit': 'day',
                            'valueDefinition': '45433E48B39F',
                            'defaultType': true],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, responsePost.status
            def location = responsePost.headers['Location'].value

            // Check the new one is default.
            responseGet = client.get(path: location + ';full', contentType: JSON)
            assertEquals 200, responseGet.status
            assertEquals 'true', responseGet.data.returnValueDefinition['default']

            // Check the old one is no longer default.
            responseGet = client.get(path: "/${version}/definitions/11D3548466F2/returnvalues/B0268549CD9C;full", contentType: JSON)
            assertEquals 200, responseGet.status
            assertEquals 'false', responseGet.data.returnValueDefinition['default']

            // 2. Handle PUT.
            // Update the old one to be the default again.
            def responsePut = client.put(
                    path: "/${version}/definitions/11D3548466F2/returnvalues/B0268549CD9C",
                    body: ['type': 'co2',
                            'unit': 'kg',
                            'perUnit': 'month',
                            'valueDefinition': '45433E48B39F',
                            'defaultType': true],
                    requestContentType: URLENC,
                    contentType: JSON)
            assertEquals 204, responsePut.status

            // Check new one is now not the default.
            responseGet = client.get(path: location + ';full', contentType: JSON)
            assertEquals 200, responseGet.status
            assertEquals 'false', responseGet.data.returnValueDefinition['default']

            // Check old one is now the default.
            responseGet = client.get(path: "/${version}/definitions/11D3548466F2/returnvalues/B0268549CD9C;full", contentType: JSON)
            assertEquals 200, responseGet.status
            assertEquals 'true', responseGet.data.returnValueDefinition['default']

            // 3. Handle DELETE.
            // Then delete the new one.
            def responseDelete = client.delete(path: location);
            assertEquals 200, responseDelete.status;
            // We should get a 404 here.
            try {
                client.get(path: location);
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
        }
    }

    @Test
    void updateInvalidReturnValueDefinition() {
        setAdminUser();
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'type', 'empty', '');
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'type', 'long', String.randomString(256));
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'type', 'duplicate', 'co2');
        updateReturnValueDefinitionFieldJson('B0268549CD9C', 'defaultType', 'no_default_type', 'false');
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'unit', 'typeMismatch', 'not_a_unit');
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'perUnit', 'typeMismatch', 'not_a_per_unit');
        updateReturnValueDefinitionFieldJson('6008F958CE20', 'valueDefinition', 'typeMismatch', 'AAAAAAAAAAAA');
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
        versions.each { version -> updateReturnValueDefinitionFieldJson(uid, field, code, value, since, version) };
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
                def body = [:];
                body[field] = value;
                client.put(
                        path: "/${version}/definitions/11D3548466F2/returnvalues/${uid}",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON);
                fail 'Response status code should have been 400 (' + field + ', ' + code + ').';
            } catch (HttpResponseException e) {
                def response = e.response;
                assertEquals 400, response.status;
                assertEquals 'application/json', response.contentType;
                assertTrue response.data instanceof net.sf.json.JSON;
                assertEquals 'INVALID', response.data.status;
                assertTrue([field] == response.data.validationResult.errors.collect {it.field});
                assertTrue([code] == response.data.validationResult.errors.collect {it.code});
            }
        }
    }
}
