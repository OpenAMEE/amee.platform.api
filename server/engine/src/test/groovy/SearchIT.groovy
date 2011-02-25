import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.*

/**
 * Tests for the Search API.
 *
 * TODO: Document Search API fully here. See https://jira.amee.com/browse/PL-9551 to vote on this task.
 */
class SearchIT extends BaseApiTest {

    static def versions = [3.0, 3.2]

    @Test
    void searchJson() {
        versions.each { version -> searchJson(version) }
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

    @Test
    void searchJsonWithExcTags() {
        versions.each { version -> searchJsonWithExcTags(version) }
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
        }
    }

    @Test
    void searchXml() {
        versions.each { version -> searchXml(version) }
    }

    def searchXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/search",
                query: ['q': 'water', 'types': 'DC'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        def allResults = response.data.Results.children()
        assertEquals 2, allResults.size()
    }

    /**
     * See: https://jira.amee.com/browse/PL-5516
     */
    @Test
    void searchWithInvalidQueryJson() {
        versions.each { version -> searchWithInvalidQueryJson(version) }
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
