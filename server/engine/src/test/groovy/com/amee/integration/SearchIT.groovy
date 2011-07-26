package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.*

/**
 * Tests for the Search API.
 */
class SearchIT extends BaseApiTest {

    /**
     * Search using JSON response.
     *
     * Supported URL parameters are:
     * <ul>
     *     <li>q: the lucene query string (see searchable fields below)</li>
     *     <li>types: comma separated list of the types of entity to return. [DC, DI]</li>
     *     <li>tags: only include results with the given tags</li>
     *     <li>excTags: do not include results with the given tags</li>
     * </ul>
     *
     * Searchable fields are:
     * <ul>
     *     <li>name</li>
     *     <li>wikiName</li>
     *     <li>path</li>
     *     <li>provenance</li>
     *     <li>authority</li>
     *     <li>wikiDoc</li>
     *     <li>itemDefinitionName</li>
     *     <li>label</li>
     *     <li>tags</li>
     * </ul>
     */
    @Test
    void searchJson() {
        com.amee.integration.BaseApiTest.versions.each { version -> searchJson(version) }
    }

    def searchJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/search",
                query: ['q': 'water', 'types': 'DC'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals 2, response.data.results.size()
    }

    /**
     * Search using JSON response, excluding a tag.
     */
    @Test
    void searchJsonWithExcTags() {
        com.amee.integration.BaseApiTest.versions.each { version -> searchJsonWithExcTags(version) }
    }

    def searchJsonWithExcTags(version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(
                    path: "/${version}/search",
                    query: ['q': 'water', 'excTags': 'ecoinvent', 'types': 'DC'])
            assertEquals 200, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals 1, response.data.results.size()
            assertFalse response.data.results.first().wikiName.toString().startsWith('Ecoinvent')
        }
    }

    /**
     * Search using JSON response for a particular tag.
     */
    @Test
    void searchJsonWithTags() {
        com.amee.integration.BaseApiTest.versions.each { version -> searchJsonWithTags(version) }
    }

    def searchJsonWithTags(version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(
                    path: "/${version}/search",
                    query: ['q': 'water', 'tags': 'ecoinvent', 'types': 'DC'])
            assertEquals 200, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals 1, response.data.results.size()
            assertTrue response.data.results.first().wikiName.toString().startsWith('Ecoinvent')
        }
    }

    /**
     * Search (for DataItems only) using XML response.
     */
    @Test
    void searchXml() {
        com.amee.integration.BaseApiTest.versions.each { version -> searchXml(version) }
    }

    def searchXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/search",
                query: ['q': 'water', 'types': 'DI'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        def allResults = response.data.Results.Item
        assertEquals 10, allResults.size()
    }

    /**
     * Search using XML response, excluding a tag.
     */
    @Test
    void searchXmlWithExcTags() {
        com.amee.integration.BaseApiTest.versions.each { version -> searchXmlWithExcTags(version) }
    }

    def searchXmlWithExcTags(version) {
        if (version >= 3.2) {
            client.contentType = XML
            def response = client.get(
                    path: "/${version}/search",
                    query: ['q': 'water', 'excTags': 'ecoinvent', 'types': 'DC'])
            assertEquals 200, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()
            def allResults = response.data.Results.Category
            assertEquals 1, allResults.size()
            assertFalse allResults[0].WikiName.text().startsWith('Ecoinvent')
        }
    }

    /**
     * Search using XML response for a particular tag.
     */
    @Test
    void searchXmlWithTags() {
        com.amee.integration.BaseApiTest.versions.each { version -> searchXmlWithTags(version) }
    }

    def searchXmlWithTags(version) {
        if (version >= 3.2) {
            client.contentType = XML
            def response = client.get(
                    path: "/${version}/search",
                    query: ['q': 'water', 'tags': 'ecoinvent', 'types': 'DC'])
            assertEquals 200, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()
            def allResults = response.data.Results.Category
            assertEquals 1, allResults.size()
            assertTrue allResults[0].WikiName.text().startsWith('Ecoinvent')
        }
    }

    /**
     * See: https://jira.amee.com/browse/PL-5516
     * Platform assumes query string is valid lucene syntax.
     */
    @Test
    void searchWithInvalidQueryJson() {
        com.amee.integration.BaseApiTest.versions.each { version -> searchWithInvalidQueryJson(version) }
    }

    def searchWithInvalidQueryJson(version) {
        try {
            client.contentType = JSON
            client.get(
                    path: "/${version}/search",
                    query: ['q': 'cooking\\'])
            fail 'Response status code should have been 400.';
        } catch (HttpResponseException e) {
            def response = e.response;
            assertEquals 400, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'INVALID', response.data.status;
        }
    }
}
