package com.amee.integration

import com.amee.domain.algorithm.AbstractAlgorithm
import groovyx.net.http.HttpResponseException
import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import static org.restlet.data.Status.*

/**
 * Tests for the Algorithm API. This API is available since version 3.4.0.
 */
class AlgorithmIT extends BaseApiTest {

    /**
     * Tests for creation, fetch and deletion of an Algorithm using JSON responses.
     *
     * Create a new Algorithm by POSTing to '/definitions/{UID}/algorithms'. The UID value is for the
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
                    path: "/$version/definitions/WD5M1LM2X3W4/algorithms",
                    body: [name: 'test', content: 'xxx'],
                    requestContentType: URLENC,
                    contentType: JSON)
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")
            String uid = location.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Get the new Algorithm.
            def responseGet = client.get(path: "$location;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.algorithm.name == 'test'
            assert responseGet.data.algorithm.content == 'xxx'

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
                    path: "/$version/definitions/WD5M1LM2X3W4/algorithms",
                    body: [name: 'test', content: 'xxx'],
                    requestContentType: URLENC,
                    contentType: XML)
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")
            String uid = location.split('/')[7]
            assertOkXml(responsePost, SUCCESS_CREATED.code, uid)

            // Get the new Algorithm.
            def responseGet = client.get(path: "$location;full", contentType: XML)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/xml'
            assert responseGet.data.Status.text() == 'OK'
            assert responseGet.data.Algorithm.Name.text() == 'test'
            assert responseGet.data.Algorithm.Content.text() == 'xxx'

            // Delete it
            def responseDelete = client.delete(path: location, contentType: XML)
            assertOkXml(responseDelete, SUCCESS_OK.code, uid)

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
     * Tests fetching a list of Algorithms using JSON responses.
     *
     * Fetch the Algorithm list by sending a GET request to '/definitions/{UID}/algorithms'. The UID value is for
     * the Item Definition the Algorithms belong to.
     *
     * The same matrix parameters described in getAlgorithmJson are supported.
     */
    @Test
    void getAlgorithmsJson() {
        versions.each { version -> getAlgorithmsJson(version) }
    }

    def getAlgorithmsJson(version) {
        if (version >= 3.4) {
            def response = client.get(path: "/$version/definitions/WD5M1LM2X3W4/algorithms;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.algorithms.size() == 2
            
            // Should be sorted by name
            assert response.data.algorithms.collect { it.name } == response.data.algorithms.collect { it.name }.sort { a, b -> a.compareToIgnoreCase(b) }
        }
    }

    /**
     * Tests fetching a list of Algorithms using XML responses.
     *
     * See documentation for getAlgorithmsJson above.
     */
    @Test
    void getAlgorithmsXml() {
        versions.each { version -> getAlgorithmsXml(version) }
    }

    def getAlgorithmsXml(version) {
        if (version >= 3.4) {
            def response = client.get(path: "/$version/definitions/WD5M1LM2X3W4/algorithms;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def allAlgorithms = response.data.Algorithms.Algorithm
            assert allAlgorithms.size() == 2
            
            // Should be sorted by name
            assert allAlgorithms.Name*.text() == allAlgorithms.Name*.text().sort { a, b -> a.compareToIgnoreCase(b) }
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
            def response = client.get(path: "/$version/definitions/WD5M1LM2X3W4/algorithms/8A852387D2B7;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.algorithm.name == 'default'
            assert response.data.algorithm.itemDefinition.uid == 'WD5M1LM2X3W4'
            assert response.data.algorithm.itemDefinition.name == 'Cooking'
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
            def response = client.get(path: "/$version/definitions/WD5M1LM2X3W4/algorithms/8A852387D2B7;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.Algorithm.Name.text() == 'default'
            assert response.data.Algorithm.ItemDefinition.@uid.text() == 'WD5M1LM2X3W4'
            assert response.data.Algorithm.ItemDefinition.Name.text() == 'Cooking'
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
        versions.each { version -> updateAlgorithmJson(version) }
    }

    def updateAlgorithmJson(version) {
        if (version >= 3.4) {
            setAdminUser()

            // 1) Do the update.
            def responsePut = client.put(
                    path: "/$version/definitions/WD5M1LM2X3W4/algorithms/8A852387DAAA",
                    body: [name: 'ZZZ New Name JSON', content: 'New content JSON.'],
                    requestContentType: URLENC,
                    contentType: JSON)
            assertOkJson(responsePut, SUCCESS_OK.code, '8A852387DAAA')

            // 2) Check values have been updated.
            def responseGet = client.get(path: "/$version/definitions/WD5M1LM2X3W4/algorithms/8A852387DAAA;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.algorithm.name == 'ZZZ New Name JSON'
            assert responseGet.data.algorithm.content == 'New content JSON.'
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
                    path: "/$version/definitions/WD5M1LM2X3W4/algorithms/8A852387DAAA",
                    body: [name: 'ZZZ New Name XML', content: 'New content XML.'],
                    requestContentType: URLENC,
                    contentType: XML)
            assertOkXml(responsePut, SUCCESS_OK.code, '8A852387DAAA')

            // 2) Check values have been updated.
            def responseGet = client.get(path: "/$version/definitions/WD5M1LM2X3W4/algorithms/8A852387DAAA;full", contentType: XML)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/xml'
            assert responseGet.data.Status.text() == 'OK'
            assert responseGet.data.Algorithm.Name.text() == 'ZZZ New Name XML'
            assert responseGet.data.Algorithm.Content.text() == 'New content XML.'
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
            setAdminUser()
            updateAlgorithmFieldJson('name', 'empty', '')
            updateAlgorithmFieldJson('name', 'long', String.randomString(256))
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
    @Ignore
    void updateWithInvalidContent() {
        versions.each { version -> updateWithInvalidContent(version) }
    }

    def updateWithInvalidContent(version) {
        if (version >= 3.4) {
            setAdminUser()
            updateAlgorithmFieldJson('content', 'long', String.randomString(AbstractAlgorithm.CONTENT_MAX_SIZE + 1))
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
        versions.each { version -> updateAlgorithmFieldJson(field, code, value, since, version) }
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
                        path: "/$version/definitions/WD5M1LM2X3W4/algorithms/8A852387DAAA",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail "Response status code should have been 400 (${field}, ${code})."
            } catch (HttpResponseException e) {
                
                // Handle error response containing a ValidationResult.
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'
                assert [field] == response.data.validationResult.errors.collect {it.field}
                assert [code] == response.data.validationResult.errors.collect {it.code}
            }
        }
    }
}
