import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.*

class SearchIT extends BaseApiTest {

    @Test
    void searchJson() {
        client.contentType = JSON
        def response = client.get(path: '/3/search',
            query: ['q': 'cooking'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals 6, response.data.results.size()
    }

    @Test
    void searchXml() {
        client.contentType = XML
        def response = client.get(path: '/3/search',
            query: ['q': 'cooking'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()

        def allResults = response.data.Results.children()
        assertEquals 6, allResults.size()
    }
}
