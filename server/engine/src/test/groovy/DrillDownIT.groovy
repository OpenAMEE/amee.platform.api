import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class DrillDownIT extends BaseApiTest {

  @Test
  // @Ignore("Drill down SQL won't work in HSQLDB or H2")
  void doDrillDownJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.3/categories/Cooking/drill')
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
  }
}
