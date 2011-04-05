import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

/**
 * Tests for the Unit API.
 *
 */
class UnitIT extends BaseApiTest {

    /**
     * Tests for creation, fetch and deletion of a UnitType using JSON responses.
     *
     * Create a new UnitType by POSTing to '/units/types'
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a UnitType by sending a DELETE request to '/units/types/{UID|name}'.
     *
     */
    @Test
    void createAndRemoveUnitTypeJson() {
        versions.each { version -> createAndRemoveUnitTypeJson(version) }
    }

    def createAndRemoveUnitTypeJson(version) {
        if (version >= 3.5) {

            setAdminUser();

            def name = 'Unit Type To Be Deleted';

            // Create a new UnitType.
            def responsePost = client.post(
                    path: "/${version}/units/types",
                    body: [name: name],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, responsePost.status;

            // TODO: Fetch the UnitType.

            // Then delete the UnitType.
            def responseDelete = client.delete(path: "/${version}/units/types/${name}");
            assertEquals 200, responseDelete.status;

            // We should get a 404 here.
            try {
                client.get(path: "/${version}/units/types/${name}");
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
        }
    }
}