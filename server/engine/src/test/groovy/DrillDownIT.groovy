import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class DrillDownIT extends BaseApiTest {

    static def versions = [3.3]

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
        client.contentType = JSON;
        def response = client.get(
                path: "/${version}/categories/Cooking/drill",
                query: query);
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        assertEquals choicesSize, response.data.drill.choices.values.size();
        assertEquals choicesName, response.data.drill.choices.name;
    }
}
