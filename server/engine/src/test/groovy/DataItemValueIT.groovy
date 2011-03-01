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
        getDataItemValuesJson(version, '289CCD5394AC', '0.81999', '2006-01-01T00:00:00Z', null); // Now.
        getDataItemValuesJson(version, '289CCD5394AC', '0.81999', '2006-01-01T00:00:00Z', 'CURRENT'); // Now.
        getDataItemValuesJson(version, 'DD6A1E4E829B', '0.74639', '2001-01-01T00:00:00Z', '2001-12-31T23:59:59Z'); // Just before the next startDate.
        getDataItemValuesJson(version, '387C597FF2C4', '0.76426', '2002-01-01T00:00:00Z', '2002-01-01T00:00:00Z'); // Exact startDate.
        getDataItemValuesJson(version, '387C597FF2C4', '0.76426', '2002-01-01T00:00:00Z', '2002-08-01T00:00:00Z'); // A point after the startDate.
        getDataItemValuesJson(version, 'B3823E43A635', '0.8199856', '1970-01-01T00:00:00Z', 'FIRST'); // The unix EPOCH.
        getDataItemValuesJson(version, '289CCD5394AC', '0.81999', '2006-01-01T00:00:00Z', 'LAST'); // The end of unix time.
    }

    def getDataItemValuesJson(version, uid, value, actualStartDate, queryStartDate) {
        def query = [:];
        if (queryStartDate) {
            query['startDate'] = queryStartDate;
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
        assert ['massCO2PerEnergy', 'source', 'country'].sort() == values.collect { it.path }.sort();
        assert [true, false, false].sort() == values.collect { it.history }.sort();
        assert [uid, '609405C3BC0C', '4097E4D3851A'].sort() == values.collect { it.uid }.sort();
        assert [value, 'http://www.ghgprotocol.org/calculation-tools/all-tools', 'United Arab Emirates'].sort() == values.collect { it.value }.sort();
        assert [actualStartDate, '1970-01-01T00:00:00Z', '1970-01-01T00:00:00Z'].sort() == values.collect { it.startDate }.sort();
        assert ['kg', null, null] == values.collect { it?.unit };
        assert ['kWh', null, null] == values.collect { it?.perUnit };
        // TODO: Test below doesn't seem to work.
        // assert ['kg/(kW·h)', null, null] == values.collect { it?.compoundUnit };
    }
}
