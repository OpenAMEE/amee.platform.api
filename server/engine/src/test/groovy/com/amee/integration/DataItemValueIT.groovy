package com.amee.integration

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import static org.restlet.data.Status.*
import groovyx.net.http.HttpResponseException

import org.joda.time.DateTime
import org.junit.Test

/**
 * Tests for the Data Item Value API. This API has been available since version 3.4.
 *
 * Data Item Values for a particular Item Value Definition can form a history. The values at the min mysql DATETIME
 * value are automatically created, can be updated but cannot be removed. It's possible to create, modify and remove
 * values for any point after the min DATETIME value 1000-01-01 00:00:00 and up to 9999-12-31 23:59:59. It's not
 * possible to have two values at the same point in a history. The current time resolution is down to the second.
 */
class DataItemValueIT extends BaseApiTest {

    public static final int SLEEP_TIME = 2000

    /**
     * Tests for creation, fetch and deletion of a historical Data Item Value using JSON responses.
     *
     * Create a new Data Item Value by POSTing to '/categories/{UID|wikiName}/items/{UID}/values/{path}' where
     * the second UID is for the Data Item and the path is the Item Value Definition path.
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>value
     * <li>startDate
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Data Item Value by sending a DELETE request
     * to '/categories/{UID|wikiName}/items/{UID}/values/{path}/{UID}'. The last UID is the UID of the Data Item
     * VAlue.
     */
    @Test
    void createDataItemValueJson() {
        versions.each { version -> createDataItemValueJson(version) }
    }

    def createDataItemValueJson(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Sleep a little to ensure the isNear calculation below will be accurate.
            sleep(SLEEP_TIME)

            // Create a DataItemValue.
            def responsePost = client.post(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy",
                    body: ['value': 10],
                    requestContentType: URLENC,
                    contentType: JSON)

            // Is Location available?
            assertTrue responsePost.headers['Location'] != null
            assertTrue responsePost.headers['Location'].value != null
            def location = responsePost.headers['Location'].value
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Get new DataItemValue UID.
            def uid = location.split('/')[10]
            assertTrue uid != null
            assertOkJson responsePost, SUCCESS_CREATED.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Get the new DataItemValue.
            def responseGetDIV = client.get(
                    path: "${location};full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseGetDIV.status
            assertEquals 'application/json', responseGetDIV.contentType
            assertTrue responseGetDIV.data instanceof net.sf.json.JSON
            assertEquals 'OK', responseGetDIV.data.status
            assertEquals 10, responseGetDIV.data.value.value

            // Get the DataItem, check it has same modified time-stamp as the DIV.
            def responseGetDI = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE;full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseGetDI.status
            def modifiedDI = new DateTime(responseGetDI.data.item.modified)
            def modifiedDIV = new DateTime(responseGetDIV.data.value.modified)
            assertTrue isNear(modifiedDIV, modifiedDI)

            // Then delete the DIV.
            def responseDelete = client.delete(path: location)
            assertOkJson responseDelete, SUCCESS_OK.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // We should get a 404 here.
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals CLIENT_ERROR_NOT_FOUND.code, e.response.status
            }
        }
    }

    /**
     * Tests for creation, fetch and deletion of a historical Data Item Value using XML responses.
     *
     * See notes for createDataItemValueJson above.
     */
    @Test
    void createDataItemValueXml() {
        versions.each { version -> createDataItemValueXml(version) }
    }

    def createDataItemValueXml(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Sleep a little to ensure the isNear calculation below will be accurate.
            sleep(SLEEP_TIME)

            // Create a DataItemValue.
            def responsePost = client.post(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy",
                    body: ['value': 10],
                    requestContentType: URLENC,
                    contentType: XML)

            // Is Location available?
            assertTrue responsePost.headers['Location'] != null
            assertTrue responsePost.headers['Location'].value != null
            def location = responsePost.headers['Location'].value
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Get new DataItemValue UID.
            def uid = location.split('/')[10]
            assertTrue uid != null
            assertOkXml responsePost, SUCCESS_CREATED.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Get the new DataItemValue.
            def responseGetDIV = client.get(
                    path: "${location};full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, responseGetDIV.status
            assertEquals 'application/xml', responseGetDIV.contentType
            assertEquals 'OK', responseGetDIV.data.Status.text()
            assertEquals "10", responseGetDIV.data.Value.Value.text()

            // Get the DataItem, check it has same modified time-stamp as the DIV.
            def responseGetDI = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE;full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, responseGetDI.status
            def modifiedDI = new DateTime(responseGetDI.data.Item.@modified.text())
            def modifiedDIV = new DateTime(responseGetDIV.data.Value.@modified.text())
            assertTrue isNear(modifiedDIV, modifiedDI)

            // Then delete the DIV.
            def responseDelete = client.delete(path: location)
            assertOkJson responseDelete, SUCCESS_OK.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // We should get a 404 here.
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals CLIENT_ERROR_NOT_FOUND.code, e.response.status
            }
        }
    }

    /**
     * Tests for modification of a Data Item Value using JSON responses.
     *
     * Modify a new Data Item Value by PUTing to '/categories/{UID|wikiName}/items/{UID}/values/{path}/{UID}' where
     * the second UID is for the Data Item, the path is the Item Value Definition path and the last UID is the
     * Data Item Value UID.
     *
     * Supported PUT parameters are:
     *
     * <ul>
     * <li>value
     * <li>startDate (only supported for historical values)
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     */
    @Test
    void modifyDataItemValueJson() {
        versions.each { version -> modifyDataItemValueJson(version) }
    }

    def modifyDataItemValueJson(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Sleep a little to ensure the isNear calculation below will be accurate.
            sleep(SLEEP_TIME)

            // Get the DataItemValue.
            def responseGetDIV1 = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/4E920EFDB233/values/country/902F1ED2C15F;full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseGetDIV1.status
            assertTrue responseGetDIV1.data.value.value.startsWith('United Kingdom')

            // Update the DataItemValue.
            def responsePut = client.put(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/4E920EFDB233/values/country/902F1ED2C15F",
                    body: ['value': "United Kingdom (modified by createDataItemValueJson_${version})"],
                    requestContentType: URLENC,
                    contentType: JSON)
            assertOkJson responsePut, SUCCESS_OK.code, '902F1ED2C15F'

            // Get the DataItemValue again.
            def responseGetDIV2 = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/4E920EFDB233/values/country/902F1ED2C15F;full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseGetDIV2.status
            assertTrue "United Kingdom (modified by createDataItemValueJson_${version})" == responseGetDIV2.data.value.value

            // Get the DataItem, check it has same modified time-stamp as the DIV.
            def responseGetDI = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/4E920EFDB233;full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseGetDI.status
            def modifiedDI = new DateTime(responseGetDI.data.item.modified)
            def modifiedDIV = new DateTime(responseGetDIV2.data.value.modified)
            assertTrue isNear(modifiedDIV, modifiedDI)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)
        }
    }

    /**
     * Tests for modification of a Data Item Value using XML responses.
     *
     * See notes for createDataItemValueJson above.
     */
    @Test
    void modifyDataItemValueXml() {
        versions.each { version -> modifyDataItemValueXml(version) }
    }

    def modifyDataItemValueXml(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Sleep a little to ensure the isNear calculation below will be accurate.
            sleep(SLEEP_TIME)

            // Get the DataItemValue.
            def responseGetDIV1 = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/4E920EFDB233/values/country/902F1ED2C15F;full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, responseGetDIV1.status
            assertTrue responseGetDIV1.data.Value.Value.text().startsWith('United Kingdom')

            // Update the DataItemValue.
            def responsePut = client.put(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/4E920EFDB233/values/country/902F1ED2C15F",
                    body: ['value': "United Kingdom (modified by createDataItemValueXml_${version})"],
                    requestContentType: URLENC,
                    contentType: XML)
            assertOkXml responsePut, SUCCESS_OK.code, '902F1ED2C15F'

            // Get the DataItemValue again.
            def responseGetDIV2 = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/4E920EFDB233/values/country/902F1ED2C15F;full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, responseGetDIV2.status
            assertTrue "United Kingdom (modified by createDataItemValueXml_${version})" == responseGetDIV2.data.Value.Value.text()

            // Get the DataItem, check it has same modified time-stamp as the DIV.
            def responseGetDI = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/4E920EFDB233;full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, responseGetDI.status
            def modifiedDI = new DateTime(responseGetDI.data.Item.@modified.text())
            def modifiedDIV = new DateTime(responseGetDIV2.data.Value.@modified.text())
            assertTrue isNear(modifiedDIV, modifiedDI)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)
        }
    }

    /**
     * Test fetching DataItemValues with the default (now) query start date.
     */
    @Test
    void getDataItemValuesForDefault() {
        getDataItemValues('289CCD5394AC', 0.81999, '2006-01-01T00:00:00Z', null)
    }

    /**
     * Test fetching DataItemValues with 'CURRENT' (now) as the query start date.
     */
    @Test
    void getDataItemValuesForCurrent() {
        getDataItemValues('289CCD5394AC', 0.81999, '2006-01-01T00:00:00Z', 'CURRENT')
    }

    /**
     * Test fetching DataItemValues with a query start date just before an actual start date.
     */
    @Test
    void getDataItemValuesWithStartDateJustBeforeNextStartDate() {
        getDataItemValues('DD6A1E4E829B', 0.74639, '2001-01-01T00:00:00Z', '2001-12-31T23:59:59Z')
    }

    /**
     * Test fetching DataItemValues with a query start date that has an exact match.
     */
    @Test
    void getDataItemValuesWithExactStartDate() {
        getDataItemValues('387C597FF2C4', 0.76426, '2002-01-01T00:00:00Z', '2002-01-01T00:00:00Z')
    }

    /**
     * Test fetching DataItemValues with a query start date at some point between actual start dates
     */
    @Test
    void getDataItemValuesWithInBetweenStartDate() {
        getDataItemValues('387C597FF2C4', 0.76426, '2002-01-01T00:00:00Z', '2002-08-01T00:00:00Z')
    }

    /**
     * Test fetching DataItemValues with 'FIRST' (min mysql DATETIME) as the query start date.
     */
    @Test
    void getDataItemValuesForFirstDate() {
        getDataItemValues('B3823E43A635', 0.8199856, '1000-01-01T00:00:00Z', 'FIRST') // The unix EPOCH.
    }

    /**
     * Test fetching DataItemValues with 'LAST' (end of epoch) as the query start date.
     */
    @Test
    void getDataItemValuesForLastDate() {
        getDataItemValues('289CCD5394AC', 0.81999, '2006-01-01T00:00:00Z', 'LAST') // The end of unix time.
    }

    /**
     * Tests fetching Data Item Values from the Data Item and Data Item Values resource.
     *
     * GET a list of Data Item Values from '/categories/{UID|wikiName}/items/{UID}' for a lightweight value
     * list along with full details on the Data Item or from '/categories/{UID|wikiName}/items/{UID}/values' for
     * full Data Item Value details.
     *
     * The list of values will by default only include values applicable NOW. This can be altered using the startDate
     * parameter, see the various tests above for examples of this.
     *
     * Data Item Value GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>path - include the data item value's path and full path.
     * <li>category - include the data item value's data category UID and wiki name.
     * <li>item - include the data item's UID.
     * <li>itemValueDefinition - include the item value definition UID, name and path.
     * </ul>
     *
     * @param uid of the expected historical DataItemValue
     * @param value of the expected historical DataItemValue
     * @param actualStartDate of the expected historical DataItemValue
     * @param queryStartDate the start date to match DataItemValues in a history against
     */
    def getDataItemValues(uid, value, actualStartDate, queryStartDate) {
        // Test the values within the DataItem values resource.
        versions.each { version -> getDataItemValuesJson(version, uid, 0D+value, actualStartDate, queryStartDate, true) }
        versions.each { version -> getDataItemValuesXml(version, uid, value, actualStartDate, queryStartDate, true) }
        // Test the values embedded within the DataItem resource itself.
        versions.each { version -> getDataItemValuesJson(version, uid, 0D+value, actualStartDate, queryStartDate, false) }
        versions.each { version -> getDataItemValuesXml(version, uid, value, actualStartDate, queryStartDate, false) }
    }

    def getDataItemValuesJson(version, uid, value, actualStartDate, queryStartDate, testValuesResource) {
        if (version >= 3.4) {
            def query = [:]
            if (queryStartDate) {
                query['startDate'] = queryStartDate
            }
            def response = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE${testValuesResource ? '/values' : ''};full",
                    query: query,
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            def values = testValuesResource ? response.data.values : response.data.item.values
            assertEquals 3, values.size()
            assert ['massCO2PerEnergy', 'source', 'country'].sort() == values.collect { it.path }.sort()
            assert [true, false, false].sort() == values.collect { it.history }.sort()
            if (testValuesResource) {
                assert [uid, '609405C3BC0C', '4097E4D3851A'].sort() == values.collect { it.uid }.sort()
            }
            assert [value, 'http://www.ghgprotocol.org/calculation-tools/all-tools', 'United Arab Emirates'].sort() == values.collect { it.value }.sort()
            if (testValuesResource) {
                assert [actualStartDate, '1000-01-01T00:00:00Z', '1000-01-01T00:00:00Z'].sort() == values.collect { it.startDate }.sort()
            }
            assert ['kg/(kW·h)', null, null] == values.collect { it?.unit }
        }
    }

    def getDataItemValuesXml(version, uid, value, actualStartDate, queryStartDate, testValuesResource) {
        if (version >= 3.4) {
            def query = [:]
            if (queryStartDate) {
                query['startDate'] = queryStartDate
            }
            def response = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE${testValuesResource ? '/values' : ''};full",
                    query: query,
                    contentType: XML)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()
            def values = testValuesResource ? response.data.Values.Value : response.data.Item.Values.Value
            assertEquals 3, values.size()
            assert ['massCO2PerEnergy', 'source', 'country'].sort() == values.Path*.text().sort()
            assert ['true', 'false', 'false'].sort() == values.@history*.text().sort()
            if (testValuesResource) {
                assert [uid, '609405C3BC0C', '4097E4D3851A'].sort() == values.@uid*.text().sort()
            }
            assert [value+"", 'http://www.ghgprotocol.org/calculation-tools/all-tools', 'United Arab Emirates'].sort() == values.Value*.text().sort()
            if (testValuesResource) {
                assert [actualStartDate, '1000-01-01T00:00:00Z', '1000-01-01T00:00:00Z'].sort() == values.StartDate*.text().sort()
            }
            assert ['kg/(kW·h)'] == values.Unit*.text().sort()
        }
    }

    /**
     * Test fetching item value history with no constraints.
     */
    @Test
    void getDataItemValueHistoryNoConstraints() {
        getDataItemValueHistory(8, false, 6.4407656, null, null, null, null)
    }

    /**
     * Test fetching item value history with a start date for filtering.
     */
    @Test
    void getDataItemValueHistoryWithStartDate() {
        getDataItemValueHistory(7, false, 5.62078, '2000-01-01T00:00:00Z', null, null, null)
    }

    /**
     * Test fetching item value history with start and end dates for filtering.
     */
    @Test
    void getDataItemValueHistoryWithStartAndEndDate() {
        getDataItemValueHistory(2, false, 1.75677, '2003-02-01T00:00:00Z', '2005-02-01T00:00:00Z', null, null)
    }

    /**
     * Test fetching item value history with start date & result limit for filtering.
     */
    @Test
    void getDataItemValueHistoryWithStartDateAndResultLimit() {
        getDataItemValueHistory(3, true, 2.23908, '2000-01-01T00:00:00Z', null, null, 3)
    }

    /**
     * Test fetching item value history with a result start & result limit for filtering.
     */
    @Test
    void getDataItemValueHistoryWithResultStartAndResultLimit() {
        getDataItemValueHistory(4, true, 3.22881, null, null, 2, 4)
    }

    /**
     * Test fetching item value history with a result start for filtering.
     */
    @Test
    void getDataItemValueHistoryWithJustResultStart() {
        getDataItemValueHistory(6, false, 4.89235, null, null, 2, null)
    }

    /**
     * Tests fetching Data Item Values for a particular Item Value Definition. A value at the epoch will always be
     * available but the primary purpose of this resource is to fetch multiple values that form a history.
     *
     * GET the list of values from '/categories/{UID|wikiName}/items/{UID}/values/{path}' where
     * the second UID is for the Data Item and the path is the Item Value Definition path.
     *
     * Supported GET parameters are:
     *
     * <ul>
     * <li>startDate
     * <li>endDate
     * <li>resultStart
     * <li>resultLimit
     * </ul>
     *
     * See the various tests just above for examples on how to use these.
     */
    @Test
    void getDataItemValueHistoryWithJustResultLimit() {
        getDataItemValueHistory(4, true, 3.0590656, null, null, 0, 4)
    }

    def getDataItemValueHistory(count, truncated, sum, queryStartDate, queryEndDate, resultStart, resultLimit) {
        // Create query.
        def query = [:]
        if (queryStartDate) {
            query['startDate'] = queryStartDate
        }
        if (queryEndDate) {
            query['endDate'] = queryEndDate
        }
        if (resultStart) {
            query['resultStart'] = resultStart
        }
        if (resultLimit) {
            query['resultLimit'] = resultLimit
        }
        // Run tests for JSON & XML.
        versions.each { version -> getDataItemValueHistoryJson(version, count, truncated, sum, query) }
        versions.each { version -> getDataItemValueHistoryXml(version, count, truncated, sum, query) }
    }

    def getDataItemValueHistoryJson(version, count, truncated, sum, query) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy",
                    query: query,
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals truncated, response.data.resultsTruncated
            def values = response.data.values
            assertEquals count, values.size()
            assertEquals("", sum, (values.collect { new Double(it.value) }).sum(), 0.0001)
        }
    }

    def getDataItemValueHistoryXml(version, count, truncated, sum, query) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy",
                    query: query,
                    contentType: XML)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()
            assertEquals truncated, new Boolean(response.data.Values.@truncated.text())
            def values = response.data.Values.Value.Value*.text()
            values = values.collect {new Double(it)}
            assertEquals count, values.size()
            assertEquals("", sum, values.sum(), 0.0001)
        }
    }

    /**
     * Test fetching DataItemValues with 'CURRENT' (now) as the item value identifier.
     */
    @Test
    void getDataItemValueForCurrent() {
        getDataItemValue('289CCD5394AC', 0.81999, '2006-01-01T00:00:00Z', 'CURRENT')
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date just before an actual start date.
     */
    @Test
    void getDataItemValueWithStartDateJustBeforeNextStartDate() {
        getDataItemValue('DD6A1E4E829B', 0.74639, '2001-01-01T00:00:00Z', '2001-12-31T23:59:59Z')
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date that has an exact match.
     */
    @Test
    void getDataItemValueWithExactStartDate() {
        getDataItemValue('387C597FF2C4', 0.76426, '2002-01-01T00:00:00Z', '2002-01-01T00:00:00Z')
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date at some point between actual start dates
     */
    @Test
    void getDataItemValueWithInBetweenStartDate() {
        getDataItemValue('387C597FF2C4', 0.76426, '2002-01-01T00:00:00Z', '2002-08-01T00:00:00Z')
    }

    /**
     * Test fetching DataItemValue with 'FIRST' (min MySQL DATETIME value) as the item value identifier.
     */
    @Test
    void getDataItemValueForFirstDate() {
        getDataItemValue('B3823E43A635', 0.8199856, '1000-01-01T00:00:00Z', 'FIRST')
    }

    /**
     * Test fetching DataItemValue with 'LAST' (end of epoch) as the item value identifier.
     */
    @Test
    void getDataItemValueForLastDate() {
        getDataItemValue('289CCD5394AC', 0.81999, '2006-01-01T00:00:00Z', 'LAST')
    }

    /**
     * Test fetching DataItemValue by UID.
     */
    @Test
    void getDataItemValueByUid() {
        getDataItemValue('387C597FF2C4', 0.76426, '2002-01-01T00:00:00Z', '387C597FF2C4')
    }

    /**
     * Tests fetching a single Data Item Value.
     *
     * GET the Data Item Value from '/categories/{UID|wikiName}/items/{UID}/values/{path}/{identifier}' where
     * the second UID is for the Data Item, the path is the Item Value Definition path and the identifier is for
     * selecting the Data Item Value.
     *
     * See the various tests just above for examples on how to use the identifier.
     */
    def getDataItemValue(uid, value, startDate, path) {
        versions.each { version -> getDataItemValueJson(version, uid, value, startDate, path) }
        versions.each { version -> getDataItemValueXml(version, uid, value, startDate, path) }
    }

    def getDataItemValueJson(version, uid, value, startDate, path) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy/${path};full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            def itemValue = response.data.value
            assert 'massCO2PerEnergy' == itemValue.path
            assert itemValue.history
            assert uid == itemValue.uid
            assert value == itemValue.value
            assert startDate == itemValue.startDate
            assert 'kg/(kW·h)' == itemValue.unit
        }
    }

    def getDataItemValueXml(version, uid, value, startDate, path) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy/${path};full",
                    contentType: XML)
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()
            def itemValue = response.data.Value
            assert 'massCO2PerEnergy' == itemValue.Path.text()
            assert itemValue.@history.text()
            assert uid == itemValue.@uid.text()
            assert ""+value == itemValue.Value.text()
            assert startDate == itemValue.StartDate.text()
            assert 'kg/(kW·h)' == itemValue.Unit.text()
        }
    }

    @Test
    void updateWithNoValue() {
        setAdminUser()
        updateDataItemValueField('387C597FF2C4', 'value', 'typeMismatch', '')
    }

    @Test
    void updateWithSomethingThatIsNotANumber() {
        setAdminUser()
        updateDataItemValueField('387C597FF2C4', 'value', 'typeMismatch', 'not_a_number')
    }

    @Test
    void updateWithBadStartDate() {
        setAdminUser()
        updateDataItemValueField('289CCD5394AC', 'startDate', 'typeMismatch', 'not_a_date')
    }

    @Test
    void updateWithDuplicateStartDate() {
        setAdminUser()
        updateDataItemValueField('289CCD5394AC', 'startDate', 'duplicate', '2002-01-01T00:00:00Z')
    }

    @Test
    void updateWithMinStartDate() {
        setAdminUser()
        updateDataItemValueField('289CCD5394AC', 'startDate', 'start_before_min', '1000-01-01T00:00:00Z')
    }

    @Test
    void updateWithBeforeMinStartDate() {
        setAdminUser()
        updateDataItemValueField('289CCD5394AC', 'startDate', 'start_before_min', '0999-01-01T00:00:00Z')
    }

    @Test
    void updateWithFirstStartDate() {
        setAdminUser()
        updateDataItemValueField('289CCD5394AC', 'startDate', 'start_before_min', 'FIRST')
    }

    @Test
    void updateWithLastStartDate() {
        setAdminUser()
        updateDataItemValueField('289CCD5394AC', 'startDate', 'end_after_max', 'LAST')
    }

    @Test
    void updateWithAfterMaxStartDate() {
        setAdminUser()
        updateDataItemValueField('289CCD5394AC', 'startDate', 'end_after_max', '10000-01-01T00:00:00Z')
    }

    /**
     * Submits a single Data Item Value field value and tests the result. An error is expected.
     *
     * @param path of the Data Item Value that is being updated
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateDataItemValueField(path, field, code, value) {
        updateDataItemValueField(path, field, code, value, 3.4)
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
    def updateDataItemValueField(path, field, code, value, since) {
        versions.each { version -> updateDataItemValueFieldJson(path, field, code, value, since, version) }
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
            // Get value before update.
            def responseBefore = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy/${path}",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseBefore.status
            def valueBefore = responseBefore.data.value.value
            try {
                // Create form body.
                def body = [:]
                body[field] = value
                // Update Data Item Value.
                client.put(
                        path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy/${path}",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail 'Response status code should have been 400 (' + field + ', ' + code + ').'
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assertEquals CLIENT_ERROR_BAD_REQUEST.code, response.status
                assertEquals 'application/json', response.contentType
                assertTrue response.data instanceof net.sf.json.JSON
                assertEquals 'INVALID', response.data.status
                assertTrue([field] == response.data.validationResult.errors.collect {it.field})
                assertTrue([code] == response.data.validationResult.errors.collect {it.code})
            }
            // Get value after update.
            def responseAfter = client.get(
                    path: "/${version}/categories/Greenhouse_Gas_Protocol_international_electricity/items/585E708CB4BE/values/massCO2PerEnergy/${path}",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseAfter.status
            def valueAfter = responseAfter.data.value.value
            assert valueAfter == valueBefore
        }
    }
}