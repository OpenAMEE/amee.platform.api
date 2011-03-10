import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Algorithm API.
 */
class AlgorithmIT extends BaseApiTest {

    static def versions = [3.4]

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

    @Test
    void getAlgorithmsJson() {
        versions.each { version -> getAlgorithmsJson(version) };
    }

    def getAlgorithmsJson(version) {
        if (version >= 3.1) {
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

    @Test
    void getAlgorithmsXml() {
        versions.each { version -> getAlgorithmsXml(version) };
    }

    def getAlgorithmsXml(version) {
        if (version >= 3.1) {
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

    @Test
    void getAlgorithmJson() {
        versions.each { version -> getAlgorithmJson(version) }
    }

    def getAlgorithmJson(version) {
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

    @Test
    void getAlgorithmXml() {
        versions.each { version -> getAlgorithmXml(version) }
    }

    def getAlgorithmXml(version) {
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

    @Test
    void updateAlgorithmJson() {
        versions.each { version -> updateAlgorithmJson(version) };
    }

    def updateAlgorithmJson(version) {
        setAdminUser();
        // 1) Do the update.
        def responsePut = client.put(
                path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387DAAA",
                body: ['name': 'ZZZ New Name JSON',
                        'content': 'New content JSON.'],
                requestContentType: URLENC,
                contentType: JSON);
        assertEquals 201, responsePut.status;
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

    @Test
    void updateAlgorithmXml() {
        versions.each { version -> updateAlgorithmXml(version) }
    }

    def updateAlgorithmXml(version) {
        setAdminUser()
        // 1) Do the update.
        def responsePut = client.put(
                path: "/${version}/definitions/1B3B44CAE90C/algorithms/8A852387DAAA",
                body: ['name': 'ZZZ New Name XML',
                        'content': 'New content XML.'],
                requestContentType: URLENC,
                contentType: XML)
        assertEquals 201, responsePut.status
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

    @Test
    void updateInvalidAlgorithm() {
        setAdminUser();
        updateAlgorithmFieldJson('name', 'empty', '');
        updateAlgorithmFieldJson('name', 'long', String.randomString(256));
        updateAlgorithmFieldJson('content', 'long', String.randomString(32768));
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
    void updateAlgorithmFieldJson(field, code, value, since, version) {
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
