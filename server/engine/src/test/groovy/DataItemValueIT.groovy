import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class DataItemValueIT extends BaseApiTest {

    static def versions = [3.4]

    /**
     * Test fetching DataItemValues with the default (now) query start date.
     */
    @Test
    void getDataItemValuesForDefaultJson() {
        getDataItemValuesJson('289CCD5394AC', '0.81999', '2006-01-01T00:00:00Z', null);
    }

    /**
     * Test fetching DataItemValues with 'CURRENT' (now) as the query start date.
     */
    @Test
    void getDataItemValuesForCurrentJson() {
        getDataItemValuesJson('289CCD5394AC', '0.81999', '2006-01-01T00:00:00Z', 'CURRENT');
    }

    /**
     * Test fetching DataItemValues with a query start date just before an actual start date.
     */
    @Test
    void getDataItemValuesWithStartDateJustBeforeNextStartDateJson() {
        getDataItemValuesJson('DD6A1E4E829B', '0.74639', '2001-01-01T00:00:00Z', '2001-12-31T23:59:59Z');
    }

    /**
     * Test fetching DataItemValues with a query start date that has an exact match.
     */
    @Test
    void getDataItemValuesWithExactStartDateJson() {
        getDataItemValuesJson('387C597FF2C4', '0.76426', '2002-01-01T00:00:00Z', '2002-01-01T00:00:00Z');
    }

    /**
     * Test fetching DataItemValues with a query start date at some point between actual start dates
     */
    @Test
    void getDataItemValuesWithInBetweenStartDateJson() {
        getDataItemValuesJson('387C597FF2C4', '0.76426', '2002-01-01T00:00:00Z', '2002-08-01T00:00:00Z');
    }

    /**
     * Test fetching DataItemValues with 'FIRST' (epoch) as the query start date.
     */
    @Test
    void getDataItemValuesForFirstDateJson() {
        getDataItemValuesJson('B3823E43A635', '0.8199856', '1970-01-01T00:00:00Z', 'FIRST'); // The unix EPOCH.
    }

    /**
     * Test fetching DataItemValues with 'LAST' (end of epoch) as the query start date.
     */
    @Test
    void getDataItemValuesForLastDateJson() {
        getDataItemValuesJson('289CCD5394AC', '0.81999', '2006-01-01T00:00:00Z', 'LAST'); // The end of unix time.
    }

    def getDataItemValuesJson(uid, value, actualStartDate, queryStartDate) {
        versions.each { version -> getDataItemValuesJson(version, uid, value, actualStartDate, queryStartDate) }
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

    /**
     * Test fetching item value history with no constraints.
     */
    @Test
    void getDataItemValueHistoryNoConstraintsJson() {
        getDataItemValueHistoryJson(8, false, 6.4407656, null, null, null, null);
    }

    /**
     * Test fetching item value history with a start date for filtering.
     */
    @Test
    void getDataItemValueHistoryWithStartDateJson() {
        getDataItemValueHistoryJson(7, false, 5.62078, '2000-01-01T00:00:00Z', null, null, null);
    }

    /**
     * Test fetching item value history with start and end dates for filtering.
     */
    @Test
    void getDataItemValueHistoryWithStartAndEndDateJson() {
        getDataItemValueHistoryJson(2, false, 1.66354, '2003-02-01T00:00:00Z', '2005-02-01T00:00:00Z', null, null);
    }

    /**
     * Test fetching item value history with start date & result limit for filtering.
     */
    @Test
    void getDataItemValueHistoryWithStartDateAndResultLimitJson() {
        getDataItemValueHistoryJson(3, false, 2.23908, '2000-01-01T00:00:00Z', null, null, 3);
    }

    def getDataItemValueHistoryJson(count, truncated, sum, queryStartDate, queryEndDate, resultStart, resultLimit) {
        versions.each { version -> getDataItemValueHistoryJson(version, count, truncated, sum, queryStartDate, queryEndDate, resultStart, resultLimit) }
    }

    def getDataItemValueHistoryJson(version, count, truncated, sum, queryStartDate, queryEndDate, resultStart, resultLimit) {
        def query = [:];
        if (queryStartDate) {
            query['startDate'] = queryStartDate;
        }
        if (queryEndDate) {
            query['startDate'] = queryEndDate;
        }
        if (resultStart) {
            query['resultStart'] = resultStart;
        }
        if (resultLimit) {
            query['resultLimit'] = resultLimit;
        }
        def response = client.get(
                path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy",
                query: query,
                contentType: JSON);
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        assertEquals truncated, response.data.resultsTruncated;
        def values = response.data.values;
        assertEquals count, values.size();
        assertEquals("", sum, (values.collect { new Double(it.value) }).sum(), 0.0001);
    }
}
