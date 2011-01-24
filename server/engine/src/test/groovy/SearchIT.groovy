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

  @Test
  void searchJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.2/search',
            query: ['q': 'water', 'excTags': 'ecoinvent', 'types': 'DC'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals 1, response.data.results.size()
  }

  @Test
  void searchXml() {
    client.contentType = XML
    def response = client.get(path: '/3.2/search',
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
    try {
      client.contentType = JSON
      client.get(path: '/3.2/search',
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
