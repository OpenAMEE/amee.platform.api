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
        getDataItemValuesJson(version, '289CCD5394AC', '0.81999', null); // Now.
        getDataItemValuesJson(version, '289CCD5394AC', '0.81999', 'CURRENT'); // Now.
        getDataItemValuesJson(version, '387C597FF2C4', '0.76426', '2002-08-01T00:00:00Z'); // A point in 2002.
        getDataItemValuesJson(version, 'B3823E43A635', '0.8199856', 'FIRST'); // The unix EPOCH.
        getDataItemValuesJson(version, '289CCD5394AC', '0.81999', 'LAST'); // The end of unix time.
    }

    def getDataItemValuesJson(version, uid, value, startDate) {
        def query = [:];
        if (startDate) {
            query['startDate'] = startDate;
        }
        def response = client.get(
                path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values;full",
                query: query,
                contentType: JSON);
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        def values = response.data.values;
        assertEquals 3, values.size();
        assert [uid, '609405C3BC0C', '4097E4D3851A'].sort() == values.collect { it.uid }.sort();
        assert [value, 'http://www.ghgprotocol.org/calculation-tools/all-tools', 'United Arab Emirates'].sort() == values.collect { it.value }.sort();
    }
}
