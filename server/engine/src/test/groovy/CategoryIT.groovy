import static org.junit.Assert.*
import org.junit.Test

class CategoryIT extends BaseApiTest {

    @Test
    void test() {
        def response = client.get(path: '/3/categories')
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
    }
}
