import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class DataItemValueIT extends BaseApiTest {

    static def versions = [3.4]

    /**
     * Test fetching a number of DataItemValues with JSON responses.
     */
    @Test
    void getDataItemValuesJson() {
        versions.each { version -> getDataItemValuesJson(version) }
    }

    def getDataItemValuesJson(version) {
        client.contentType = JSON;
        def response = client.get(
                path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values;full")
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        def values = response.data.values;
        assertEquals 3, values.size();
        assert ['289CCD5394AC', '609405C3BC0C', '4097E4D3851A'].sort() == values.collect { it.uid }.sort();
        assert ['0.81999', 'http://www.ghgprotocol.org/calculation-tools/all-tools', 'United Arab Emirates'].sort() == values.collect { it.value }.sort();
    }
}
