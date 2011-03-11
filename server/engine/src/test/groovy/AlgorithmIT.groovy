import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Algorithm API. This API is available since version 3.4.0.
 */
class AlgorithmIT extends BaseApiTest {

    /**
     * Tests for creation, fetch and deletion of an Algorithm using JSON responses.
     *
     * Create a new Algorithm by POSTing to '/definitions/{UID}'. The UID value is for the
     * Item Definition the new Algorithm should belong to.
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name - the name of the Algorithm.
     * <li>content - the algorithm JavaScript content.
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) an Algorithm by sending a DELETE request to '/definitions/{UID}/algorithms/{UID}'. The
     * first UID is for the Item Definition and the second is for the Algorithm.
     */
    @Test
    void createDeleteAlgorithmJson() {
        versions.each { version -> createDeleteAlgorithmJson(version) }
    }

    def createDeleteAlgorithmJson(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a new Algorithm.
            def responsePost = client.post(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms",
                    body: ['name': 'test',
                            'content': 'xxx'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, responsePost.status;
            def location = responsePost.headers['Location'].value;
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}");

            // Get the new Algorithm.
            def responseGet = client.get(
                    path: "${location};full",
                    contentType: JSON);
            assertEquals 200, responseGet.status;
            assertEquals 'application/json', responseGet.contentType;
            assertTrue responseGet.data instanceof net.sf.json.JSON;
            assertEquals 'OK', responseGet.data.status;
            assertEquals 'test', responseGet.data.algorithm.name;
            assertEquals 'xxx', responseGet.data.algorithm.content;

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
     * Tests for creation, fetch and deletion of an Algorithm using XML responses.
     *
     * See documentation for createDeleteAlgorithmJson above.
     */
    @Test
    void createDeleteAlgorithmXml() {
        versions.each { version -> createDeleteAlgorithmXml(version) }
    }

    def createDeleteAlgorithmXml(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a new Algorithm.
            def responsePost = client.post(
                    path: "/${version}/definitions/11D3548466F2/algorithms",
                    body: ['name': 'test',
                            'content': 'xxx'],
                    requestContentType: URLENC,
                    contentType: XML)
            assertEquals 201, responsePost.status
            def location = responsePost.headers['Location'].value;
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Get the new Algorithm.
            def responseGet = client.get(
                    path: "${location};full",
                    contentType: XML)
            assertEquals 200, responseGet.status
            assertEquals 'application/xml', responseGet.contentType
            assertEquals 'OK', responseGet.data.Status.text()
            assertEquals 'test', responseGet.data.Algorithm.Name.text()
            assertEquals 'xxx', responseGet.data.Algorithm.Content.text()

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
     * Tests fetching a list of Algorithms using JSON responses.
     *
     * Fetch the Algorithm list by sending a GET request to '/definitions/{UID}/algorithms'. The UID value is for
     * the Item Definition the Algorithms belong to.
     *
     * The same matrix parameters described in getAlgorithmJson are supported.
     */
    @Test
    void getAlgorithmsJson() {
        versions.each { version -> getAlgorithmsJson(version) };
    }

    def getAlgorithmsJson(version) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals 2, response.data.algorithms.size();
            // Should be sorted by name
            assertTrue response.data.algorithms.first().name.compareToIgnoreCase(response.data.algorithms.last().name) < 0;
        }
    }

    /**
     * Tests fetching a list of Algorithms using XML responses.
     *
     * See documentation for getAlgorithmsJson above.
     */
    @Test
    void getAlgorithmsXml() {
        versions.each { version -> getAlgorithmsXml(version) };
    }

    def getAlgorithmsXml(version) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms;full",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            def allAlgorithms = response.data.Algorithms.Algorithm;
            assertEquals 2, allAlgorithms.size();
            // Should be sorted by name
            assertTrue allAlgorithms[0].Name.text().compareToIgnoreCase(allAlgorithms[-1].Name.text()) < 0;
        }
    }

    /**
     * Tests fetching an Algorithm using JSON responses.
     *
     * Fetch an Algorithm by sending a GET request to '/definitions/{UID}/algorithms/{UID}'. The first UID is for
     * the Item Definition and the second is for the Algorithm.
     *
     * Algorithm GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>audit - include the status, created and modified values.
     * <li>name - include the name.
     * <li>content - include the JavaScript content.
     * <li>itemDefinition - include the UID and name of the associated Item Definition (if linked).
     * </ul>
     */
    @Test
    void getAlgorithmJson() {
        versions.each { version -> getAlgorithmJson(version) }
    }

    def getAlgorithmJson(version) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387D2B7;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals 'default', response.data.algorithm.name;
            assertEquals '1B3B44CAE90C', response.data.algorithm.itemDefinition.uid;
            assertEquals 'Cooking', response.data.algorithm.itemDefinition.name;
        }
    }

    /**
     * Tests fetching an Algorithm using XML responses.
     *
     * See documentation for getAlgorithmJson above.
     */
    @Test
    void getAlgorithmXml() {
        versions.each { version -> getAlgorithmXml(version) }
    }

    def getAlgorithmXml(version) {
    if (version >= 3.4) {
            def response = client.get(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387D2B7;full",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals 'default', response.data.Algorithm.Name.text();
            assertEquals '1B3B44CAE90C', response.data.Algorithm.ItemDefinition.@uid.text();
            assertEquals 'Cooking', response.data.Algorithm.ItemDefinition.Name.text();
        }
    }

    /**
     * Tests updating an Algorithm using JSON responses.
     *
     * Update an Algorithm by POSTing to '/definitions/{UID}/algorithms/{UID}'.  The first UID is for
     * the Item Definition and the second is for the Algorithm.
     *
     * For supported parameters see documentation for createDeleteAlgorithmJson above.
     */
    @Test
    void updateAlgorithmJson() {
        versions.each { version -> updateAlgorithmJson(version) };
    }

    def updateAlgorithmJson(version) {
        if (version >= 3.4) {
            setAdminUser();
            // 1) Do the update.
            def responsePut = client.put(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387DAAA",
                    body: ['name': 'ZZZ New Name JSON',
                            'content': 'New content JSON.'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 204, responsePut.status;
            // 2) Check values have been updated.
            def responseGet = client.get(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387DAAA;full",
                    contentType: JSON);
            assertEquals 200, responseGet.status;
            assertEquals 'application/json', responseGet.contentType;
            assertTrue responseGet.data instanceof net.sf.json.JSON;
            assertEquals 'OK', responseGet.data.status;
            assertEquals 'ZZZ New Name JSON', responseGet.data.algorithm.name;
            assertEquals 'New content JSON.', responseGet.data.algorithm.content;
        }
    }

    /**
     * Tests updating an Algorithm using XML responses.
     *
     * See documentation for updateAlgorithmJson above.
     */
    @Test
    void updateAlgorithmXml() {
        versions.each { version -> updateAlgorithmXml(version) }
    }

    def updateAlgorithmXml(version) {
        if (version >= 3.4) {
            setAdminUser()
            // 1) Do the update.
            def responsePut = client.put(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387DAAA",
                    body: ['name': 'ZZZ New Name XML',
                            'content': 'New content XML.'],
                    requestContentType: URLENC,
                    contentType: XML)
            assertEquals 204, responsePut.status
            // 2) Check values have been updated.
            def responseGet = client.get(
                    path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387DAAA;full",
                    contentType: XML)
            assertEquals 200, responseGet.status
            assertEquals 'application/xml', responseGet.contentType
            assertEquals 'OK', responseGet.data.Status.text()
            assertEquals 'ZZZ New Name XML', responseGet.data.Algorithm.Name.text()
            assertEquals 'New content XML.', responseGet.data.Algorithm.Content.text()
        }
    }

    /**
     * Tests the validation rules for the Algorithm name field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Mandatory.
     * <li>Duplicates are allowed.
     * <li>No longer than 255 chars.
     * <li>Must not be empty.
     * </ul>
     */
    @Test
    void updateWithInvalidName() {
        versions.each { version -> updateWithInvalidName(version) }
    }

    def updateWithInvalidName(version) {
        if (version >= 3.4) {
            setAdminUser();
            updateAlgorithmFieldJson('name', 'empty', '');
            updateAlgorithmFieldJson('name', 'long', String.randomString(256));
        }
    }

    /**
     * Tests the validation rules for the Algorithm content field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Optional.
     * <li>No longer than 32767 chars.
     * </ul>
     */
    @Test
    void updateWithInvalidContent() {
        versions.each { version -> updateWithInvalidContent(version) }
    }

    def updateWithInvalidContent(version) {
        if (version >= 3.4) {
            setAdminUser();
            updateAlgorithmFieldJson('content', 'long', String.randomString(32768));
        }
    }

    /**
     * Submits a single Algorithm field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateAlgorithmFieldJson(field, code, value) {
        updateAlgorithmFieldJson(field, code, value, 3.4)
    }

    /**
     * Submits a single Algorithm field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateAlgorithmFieldJson(field, code, value, since) {
        versions.each { version -> updateAlgorithmFieldJson(field, code, value, since, version) };
    }

    /**
     * Submits a single Algorithm field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    def updateAlgorithmFieldJson(field, code, value, since, version) {
        if (version >= since) {
            try {
                // Create form body.
                def body = [(field): value]
                // Update Algorithm.
                client.put(
                        path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387DAAA",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail "Response status code should have been 400 (${field}, ${code})."
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assertEquals 400, response.status
                assertEquals 'application/json', response.contentType
                assertTrue response.data instanceof net.sf.json.JSON
                assertEquals 'INVALID', response.data.status
                assertTrue([field] == response.data.validationResult.errors.collect {it.field})
                assertTrue([code] == response.data.validationResult.errors.collect {it.code})
            }
        }
    }
}
