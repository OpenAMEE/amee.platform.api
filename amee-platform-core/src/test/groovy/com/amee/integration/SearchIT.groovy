package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.*
import static org.restlet.data.Status.*

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
        versions.each { version -> searchJson(version) }
    }

    def searchJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/$version/search", query: [q: 'electricity', types: 'DC'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.results.size() == 9
        assert response.data.results.collect { it.type } == Collections.nCopies(9, 'category')
    }

    /**
     * Search using JSON response, excluding a tag.
     */
    @Test
    void searchJsonWithExcTags() {
        versions.each { version -> searchJsonWithExcTags(version) }
    }

    def searchJsonWithExcTags(version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(path: "/$version/search;tags", query: [q: 'electricity', excTags: 'country', types: 'DC'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.results.size() == 7
            response.data.results.each { cat ->
                assert !(cat.tags.collect { it.tag }.contains('country'))
            }
        }
    }

    /**
     * Search using JSON response for a particular tag.
     */
    @Test
    void searchJsonWithTags() {
        versions.each { version -> searchJsonWithTags(version) }
    }

    def searchJsonWithTags(version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(path: "/$version/search;tags", query: [q: 'electricity', tags: 'country', types: 'DC'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.results.size() == 2
            response.data.results.each { cat ->
                assert cat.tags.collect { it.tag }.contains('country')
            }
        }
    }

    /**
     * Search (for DataItems only) using XML response.
     */
    @Test
    void searchXml() {
        versions.each { version -> searchXml(version) }
    }

    def searchXml(version) {
        client.contentType = XML
        def response = client.get(path: "/$version/search", query: [q: 'electricity', types: 'DI'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        def allResults = response.data.Results.Item
        assert allResults.size() == 25
    }

    /**
     * Search using XML response, excluding a tag.
     */
    @Test
    void searchXmlWithExcTags() {
        versions.each { version -> searchXmlWithExcTags(version) }
    }

    def searchXmlWithExcTags(version) {
        if (version >= 3.2) {
            client.contentType = XML
            def response = client.get(path: "/$version/search;tags", query: [q: 'electricity', excTags: 'country', types: 'DC'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def allResults = response.data.Results.Category
            assert allResults.size() == 7
            def allTags = response.data.Results.Category.Tags.Tag.collect{ it.text() }
            assert !allTags.contains('country')
        }
    }

    /**
     * Search using XML response for a particular tag.
     */
    @Test
    void searchXmlWithTags() {
        versions.each { version -> searchXmlWithTags(version) }
    }

    def searchXmlWithTags(version) {
        if (version >= 3.2) {
            client.contentType = XML
            def response = client.get(path: "/$version/search;tags", query: [q: 'electricity', tags: 'country', types: 'DC'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def allResults = response.data.Results.Category
            assert allResults.size() == 2
            def allTags = response.data.Results.Category.Tags.Tag.collect{ it.text() }
            assert allTags.contains('country')
        }
    }

    /**
     * See: https://jira.amee.com/browse/PL-5516
     * Platform assumes query string is valid lucene syntax.
     */
    @Test
    void searchWithInvalidQueryJson() {
        versions.each { version -> searchWithInvalidQueryJson(version) }
    }

    def searchWithInvalidQueryJson(version) {
        try {
            client.contentType = JSON
            client.get(path: "/$version/search", query: [q: 'cooking\\'])
            fail 'Response status code should have been 400.'
        } catch (HttpResponseException e) {
            def response = e.response
            assert response.status == CLIENT_ERROR_BAD_REQUEST.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'INVALID'
        }
    }
}
