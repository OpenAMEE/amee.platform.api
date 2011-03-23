import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Tests for the Data Category drill down API. This API has been available since version 3.3.
 */
class DrillDownIT extends BaseApiTest {

    /**
     * Tests drill down GETs against a Data Category using JSON responses.
     *
     * The parameters this resource supports are available in the representations as part of the
     * choices node in the representation.
     *
     * The purpose and behaviour of this resource is similar to the AMEE API V1 / V2 drill down resource. For
     * some background see http://my.amee.com/developers/wiki/DrillDown. The main difference is that the V3
     * implementation is quicker (backed by Lucene search) and the representations are tidier.
     */
    @Test
    void canDrillDownJson() {
        doDrillDownJson(['nothing_to_see': 'here'], 21, 'numberOfPeople');
        doDrillDownJson(['numberOfPeople': '5'], 5, 'fuel');
        doDrillDownJson(['numberOfPeople': '5', 'fuel': 'Gas'], 1, 'uid');
    }

    def doDrillDownJson(query, choicesSize, choicesName) {
        versions.each { version -> doDrillDownJson(query, choicesSize, choicesName, version) };
    }

    def doDrillDownJson(query, choicesSize, choicesName, version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/${version}/categories/Cooking/drill",
                    query: query,
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals choicesSize, response.data.drill.choices.values.size();
            assertEquals choicesName, response.data.drill.choices.name;
        }
    }

    /**
     * Tests drill down GETs against a Data Category using XML responses.
     *
     * See notes for canDrillDownJson above.
     */
    @Test
    void canDrillDownXml() {
        doDrillDownXml(['nothing_to_see': 'here'], 21, 'numberOfPeople');
        doDrillDownXml(['numberOfPeople': '5'], 5, 'fuel');
        doDrillDownXml(['numberOfPeople': '5', 'fuel': 'Gas'], 1, 'uid');
    }

    def doDrillDownXml(query, choicesSize, choicesName) {
        versions.each { version -> doDrillDownXml(query, choicesSize, choicesName, version) };
    }

    def doDrillDownXml(query, choicesSize, choicesName, version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/${version}/categories/Cooking/drill",
                    query: query,
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals choicesSize, response.data.Drill.Choices.Values.Value.size();
            assertEquals choicesName, response.data.Drill.Choices.Name.text();
        }
    }
}
