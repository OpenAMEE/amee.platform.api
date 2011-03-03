import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static org.junit.Assert.*

class DataItemValueIT extends BaseApiTest {

    static def versions = [3.4]

    @Test
    void createDataItemJson() {
        versions.each { version -> createDataItemJson(version) }
    }

    def createDataItemJson(version) {
        setAdminUser();
        // Create a DataItem.
        def responsePost = client.post(
                path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy",
                body: ['value': 10],
                requestContentType: URLENC,
                contentType: JSON);
        assertEquals 201, responsePost.status
        // Is Location available?
        assertTrue responsePost.headers['Location'] != null;
        assertTrue responsePost.headers['Location'].value != null;
        def location = responsePost.headers['Location'].value;
        assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")
        // Get new DataItemValue UID.
        def uid = location.split('/')[10];
        assertTrue uid != null;
        // Sleep a little to give the index a chance to be updated.
        sleep(1000);
        // Get the new DataItem.
        def responseGet = client.get(
                path: "${location};full",
                contentType: JSON);
        assertEquals 200, responseGet.status;
        assertEquals 'application/json', responseGet.contentType;
        assertTrue responseGet.data instanceof net.sf.json.JSON;
        assertEquals 'OK', responseGet.data.status;
        assertEquals "10", responseGet.data.value.value;
        // Then delete it.
        def responseDelete = client.delete(path: location);
        assertEquals 200, responseDelete.status;
        // Sleep a little to give the index a chance to be updated.
        sleep(1000);
        // We should get a 404 here.
        try {
            client.get(path: location);
            fail 'Should have thrown an exception';
        } catch (HttpResponseException e) {
            assertEquals 404, e.response.status;
        }
    }

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
        getDataItemValueHistoryJson(2, false, 1.75677, '2003-02-01T00:00:00Z', '2005-02-01T00:00:00Z', null, null);
    }

    /**
     * Test fetching item value history with start date & result limit for filtering.
     */
    @Test
    void getDataItemValueHistoryWithStartDateAndResultLimitJson() {
        getDataItemValueHistoryJson(3, true, 2.23908, '2000-01-01T00:00:00Z', null, null, 3);
    }

    /**
     * Test fetching item value history with a result start & result limit for filtering.
     */
    @Test
    void getDataItemValueHistoryWithResultStartAndResultLimitJson() {
        getDataItemValueHistoryJson(4, true, 3.22881, null, null, 2, 4);
    }

    /**
     * Test fetching item value history with a result start for filtering.
     */
    @Test
    void getDataItemValueHistoryWithJustResultStartJson() {
        getDataItemValueHistoryJson(6, false, 4.89235, null, null, 2, null);
    }

    /**
     * Test fetching item value history with a result limit for filtering.
     */
    @Test
    void getDataItemValueHistoryWithJustResultLimitJson() {
        getDataItemValueHistoryJson(4, true, 3.0590656, null, null, 0, 4);
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
            query['endDate'] = queryEndDate;
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

    /**
     * Test fetching DataItemValues with 'CURRENT' (now) as the item value identifier.
     */
    @Test
    void getDataItemValueForCurrentJson() {
        getDataItemValueJson('289CCD5394AC', '0.81999', '2006-01-01T00:00:00Z', 'CURRENT');
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date just before an actual start date.
     */
    @Test
    void getDataItemValueWithStartDateJustBeforeNextStartDateJson() {
        getDataItemValueJson('DD6A1E4E829B', '0.74639', '2001-01-01T00:00:00Z', '2001-12-31T23:59:59Z');
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date that has an exact match.
     */
    @Test
    void getDataItemValueWithExactStartDateJson() {
        getDataItemValueJson('387C597FF2C4', '0.76426', '2002-01-01T00:00:00Z', '2002-01-01T00:00:00Z');
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date at some point between actual start dates
     */
    @Test
    void getDataItemValueWithInBetweenStartDateJson() {
        getDataItemValueJson('387C597FF2C4', '0.76426', '2002-01-01T00:00:00Z', '2002-08-01T00:00:00Z');
    }

    /**
     * Test fetching DataItemValue with 'FIRST' (epoch) as the item value identifier.
     */
    @Test
    void getDataItemValueForFirstDateJson() {
        getDataItemValueJson('B3823E43A635', '0.8199856', '1970-01-01T00:00:00Z', 'FIRST');
    }

    /**
     * Test fetching DataItemValue with 'LAST' (end of epoch) as the item value identifier.
     */
    @Test
    void getDataItemValueForLastDateJson() {
        getDataItemValueJson('289CCD5394AC', '0.81999', '2006-01-01T00:00:00Z', 'LAST');
    }

    /**
     * Test fetching DataItemValue by UID.
     */
    @Test
    void getDataItemValueByUidJson() {
        getDataItemValueJson('387C597FF2C4', '0.76426', '2002-01-01T00:00:00Z', '387C597FF2C4');
    }

    def getDataItemValueJson(uid, value, startDate, path) {
        versions.each { version -> getDataItemValueJson(version, uid, value, startDate, path) }
    }

    def getDataItemValueJson(version, uid, value, startDate, path) {
        def response = client.get(
                path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy/${path};full",
                contentType: JSON);
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        def itemValue = response.data.value;
        assert 'massCO2PerEnergy' == itemValue.path;
        assert itemValue.history;
        assert uid == itemValue.uid;
        assert value == itemValue.value;
        assert startDate == itemValue.startDate;
        assert 'kg' == itemValue.unit;
        assert 'kWh' == itemValue.perUnit;
        // TODO: Test below doesn't seem to work.
        // assert 'kg/(kW·h)' == itemValue.compoundUnit;
    }

    @Test
    void updateWithNoValue() {
        setAdminUser();
        updateDataItemValueFieldJson('387C597FF2C4', 'value', 'typeMismatch', '');
    }

    @Test
    void updateWithSomethingThatIsNotANumber() {
        setAdminUser();
        updateDataItemValueFieldJson('387C597FF2C4', 'value', 'typeMismatch', 'not_a_number');
    }

    @Test
    void updateWithBadStartDate() {
        setAdminUser();
        updateDataItemValueFieldJson('289CCD5394AC', 'startDate', 'typeMismatch', 'not_a_date');
    }

    @Test
    void updateWithDuplicateStartDate() {
        setAdminUser();
        updateDataItemValueFieldJson('289CCD5394AC', 'startDate', 'duplicate', '2002-01-01T00:00:00Z');
    }

    @Test
    void updateWithEpochStartDate() {
        setAdminUser();
        updateDataItemValueFieldJson('289CCD5394AC', 'startDate', 'epoch', '1970-01-01T00:00:00Z');
    }

    @Test
    void updateWithBeforeEpochStartDate() {
        setAdminUser();
        updateDataItemValueFieldJson('289CCD5394AC', 'startDate', 'epoch', '1969-01-01T00:00:00Z');
    }

    @Test
    void updateWithFirstStartDate() {
        setAdminUser();
        updateDataItemValueFieldJson('289CCD5394AC', 'startDate', 'epoch', 'FIRST');
    }

    @Test
    void updateWithLastStartDate() {
        setAdminUser();
        updateDataItemValueFieldJson('289CCD5394AC', 'startDate', 'end_of_epoch', 'LAST');
    }

    @Test
    void updateWithEndOfEpochStartDate() {
        setAdminUser();
        updateDataItemValueFieldJson('289CCD5394AC', 'startDate', 'end_of_epoch', '2038-01-19T03:14:00Z');
    }

    @Test
    void updateWithAfterEndOfEpochStartDate() {
        setAdminUser();
        updateDataItemValueFieldJson('289CCD5394AC', 'startDate', 'end_of_epoch', '2039-01-01T00:00:00Z');
    }

    /**
     * Submits a single Data Item Value field value and tests the result. An error is expected.
     *
     * @param path of the Data Item Value that is being updated
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateDataItemValueFieldJson(path, field, code, value) {
        updateDataItemValueFieldJson(path, field, code, value, 3.4)
    }

    /**
     * Submits a single Data Item Value field value and tests the result. An error is expected.
     *
     * @param path of the Data Item Value that is being updated
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateDataItemValueFieldJson(path, field, code, value, since) {
        versions.each { version -> updateDataItemValueFieldJson(path, field, code, value, since, version) };
    }

    /**
     * Submits a single Data Item Value field value and tests the result. An error is expected.
     *
     * @param path of the Data Item Value that is being updated
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    void updateDataItemValueFieldJson(path, field, code, value, since, version) {
        if (version >= since) {
            try {
                // Create form body.
                def body = [:];
                body[field] = value;
                // Update Data Item Value.
                client.put(
                        path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy/${path}",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON);
                fail 'Response status code should have been 400 (' + field + ', ' + code + ').';
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response;
                assertEquals 400, response.status;
                assertEquals 'application/json', response.contentType;
                assertTrue response.data instanceof net.sf.json.JSON;
                assertEquals 'INVALID', response.data.status;
                assertTrue([field] == response.data.validationResult.errors.collect {it.field});
                assertTrue([code] == response.data.validationResult.errors.collect {it.code});
            }
        }
    }
}