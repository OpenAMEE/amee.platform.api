package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.joda.time.DateTime
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

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
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy",
                    body: [value: 10],
                    requestContentType: URLENC,
                    contentType: JSON)

            // Is Location available?
            assert responsePost.headers['Location'] != null
            assert responsePost.headers['Location'].value != null
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")

            // Get new DataItemValue UID.
            String uid = location.split('/')[10]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Get the new DataItemValue.
            def responseGetDIV = client.get(path: "$location;full", contentType: JSON)
            assert responseGetDIV.status == SUCCESS_OK.code
            assert responseGetDIV.contentType == 'application/json'
            assert responseGetDIV.data.status == 'OK'
            assert responseGetDIV.data.value.value == 10

            // Get the DataItem, check it has same modified time-stamp as the DIV.
            def responseGetDI = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0;full",
                    contentType: JSON)
            assert responseGetDI.status == SUCCESS_OK.code
            DateTime modifiedDI = new DateTime(responseGetDI.data.item.modified)
            DateTime modifiedDIV = new DateTime(responseGetDIV.data.value.modified)
            assert isNear(modifiedDIV, modifiedDI)

            // Then delete the DIV.
            def responseDelete = client.delete(path: location)
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // We should get a 404 here.
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
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
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy",
                    body: [value: 10],
                    requestContentType: URLENC,
                    contentType: XML)

            // Is Location available?
            assert responsePost.headers['Location'] != null
            assert responsePost.headers['Location'].value != null
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")

            // Get new DataItemValue UID.
            String uid = location.split('/')[10]
            assertOkXml(responsePost, SUCCESS_CREATED.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Get the new DataItemValue.
            def responseGetDIV = client.get(path: "$location;full", contentType: XML)
            assert responseGetDIV.status == SUCCESS_OK.code
            assert responseGetDIV.contentType == 'application/xml'
            assert responseGetDIV.data.Status.text() == 'OK'
            assert responseGetDIV.data.Value.Value.text() == '10'

            // Get the DataItem, check it has same modified time-stamp as the DIV.
            def responseGetDI = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0;full",
                    contentType: XML)
            assert responseGetDI.status == SUCCESS_OK.code
            DateTime modifiedDI = new DateTime(responseGetDI.data.Item.@modified.text())
            DateTime modifiedDIV = new DateTime(responseGetDIV.data.Value.@modified.text())
            assert isNear(modifiedDIV, modifiedDI)

            // Then delete the DIV.
            def responseDelete = client.delete(path: location)
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // We should get a 404 here.
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
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
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/FZPGSLDK8HJO/values/country/V57YBZDLNELP;full",
                    contentType: JSON)
            assert responseGetDIV1.status == SUCCESS_OK.code
            assert responseGetDIV1.data.value.value.startsWith('United Kingdom')

            // Update the DataItemValue.
            def responsePut = client.put(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/FZPGSLDK8HJO/values/country/V57YBZDLNELP",
                    body: [value: "United Kingdom (modified by createDataItemValueJson_$version)"],
                    requestContentType: URLENC,
                    contentType: JSON)
            assertOkJson(responsePut, SUCCESS_OK.code, 'V57YBZDLNELP')

            // Get the DataItemValue again.
            def responseGetDIV2 = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/FZPGSLDK8HJO/values/country/V57YBZDLNELP;full",
                    contentType: JSON)
            assert responseGetDIV2.status == SUCCESS_OK.code
            assert "United Kingdom (modified by createDataItemValueJson_$version)" == responseGetDIV2.data.value.value

            // Get the DataItem, check it has same modified time-stamp as the DIV.
            def responseGetDI = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/FZPGSLDK8HJO;full",
                    contentType: JSON)
            assert responseGetDI.status == SUCCESS_OK.code
            DateTime modifiedDI = new DateTime(responseGetDI.data.item.modified)
            DateTime modifiedDIV = new DateTime(responseGetDIV2.data.value.modified)
            assert isNear(modifiedDIV, modifiedDI)

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
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/FZPGSLDK8HJO/values/country/V57YBZDLNELP;full",
                    contentType: XML)
            assert responseGetDIV1.status == SUCCESS_OK.code
            assert responseGetDIV1.data.Value.Value.text().startsWith('United Kingdom')

            // Update the DataItemValue.
            def responsePut = client.put(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/FZPGSLDK8HJO/values/country/V57YBZDLNELP",
                    body: ['value': "United Kingdom (modified by createDataItemValueXml_$version)"],
                    requestContentType: URLENC,
                    contentType: XML)
            assertOkXml(responsePut, SUCCESS_OK.code, 'V57YBZDLNELP')

            // Get the DataItemValue again.
            def responseGetDIV2 = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/FZPGSLDK8HJO/values/country/V57YBZDLNELP;full",
                    contentType: XML)
            assert responseGetDIV2.status == SUCCESS_OK.code
            assert "United Kingdom (modified by createDataItemValueXml_$version)" == responseGetDIV2.data.Value.Value.text()

            // Get the DataItem, check it has same modified time-stamp as the DIV.
            def responseGetDI = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/FZPGSLDK8HJO;full",
                    contentType: XML)
            assert responseGetDI.status == SUCCESS_OK.code
            DateTime modifiedDI = new DateTime(responseGetDI.data.Item.@modified.text())
            DateTime modifiedDIV = new DateTime(responseGetDIV2.data.Value.@modified.text())
            assert isNear(modifiedDIV, modifiedDI)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)
        }
    }

    /**
     * Test fetching DataItemValues with the default (now) query start date.
     */
    @Test
    void getDataItemValuesForDefault() {
        getDataItemValues('91119I4I1CIS', 0.81999, '2006-01-01T00:00:00Z', null)
    }

    /**
     * Test fetching DataItemValues with 'CURRENT' (now) as the query start date.
     */
    @Test
    void getDataItemValuesForCurrent() {
        getDataItemValues('91119I4I1CIS', 0.81999, '2006-01-01T00:00:00Z', 'CURRENT')
    }

    /**
     * Test fetching DataItemValues with a query start date just before an actual start date.
     */
    @Test
    void getDataItemValuesWithStartDateJustBeforeNextStartDate() {
        getDataItemValues('MLTM20389T5F', 0.74639, '2001-01-01T00:00:00Z', '2001-12-31T23:59:59Z')
    }

    /**
     * Test fetching DataItemValues with a query start date that has an exact match.
     */
    @Test
    void getDataItemValuesWithExactStartDate() {
        getDataItemValues('H10OSN5NJ2WQ', 0.76426, '2002-01-01T00:00:00Z', '2002-01-01T00:00:00Z')
    }

    /**
     * Test fetching DataItemValues with a query start date at some point between actual start dates
     */
    @Test
    void getDataItemValuesWithInBetweenStartDate() {
        getDataItemValues('H10OSN5NJ2WQ', 0.76426, '2002-01-01T00:00:00Z', '2002-08-01T00:00:00Z')
    }

    /**
     * Test fetching DataItemValues with 'FIRST' (epoch) as the query start date.
     */
    @Test
    void getDataItemValuesForFirstDate() {
        getDataItemValues('1I8KS6GEROR8', 0.8199856, '1970-01-01T00:00:00Z', 'FIRST'); // The unix EPOCH.
    }

    /**
     * Test fetching DataItemValues with 'LAST' (end of epoch) as the query start date.
     */
    @Test
    void getDataItemValuesForLastDate() {
        getDataItemValues('91119I4I1CIS', 0.81999, '2006-01-01T00:00:00Z', 'LAST') // The end of unix time.
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
        versions.each { version -> getDataItemValuesJson(version, uid, value as double, actualStartDate, queryStartDate, true) }
        versions.each { version -> getDataItemValuesXml(version, uid, value, actualStartDate, queryStartDate, true) }
        // Test the values embedded within the DataItem resource itself.
        versions.each { version -> getDataItemValuesJson(version, uid, value as double, actualStartDate, queryStartDate, false) }
        versions.each { version -> getDataItemValuesXml(version, uid, value, actualStartDate, queryStartDate, false) }
    }

    def getDataItemValuesJson(version, uid, value, actualStartDate, queryStartDate, testValuesResource) {
        if (version >= 3.4) {
            def query = [:]
            if (queryStartDate) {
                query['startDate'] = queryStartDate
            }
            def response = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0${testValuesResource ? '/values' : ''};full",
                    query: query,
                    contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            def values = testValuesResource ? response.data.values : response.data.item.values
            assert values.size() == 3
            assert values.collect { it.path }.sort() == ['country', 'massCO2PerEnergy', 'source']
            assert values.collect { it.history }.sort() == [false, false, true]
            if (testValuesResource) {
                assert values.collect { it.uid }.sort() == [uid, 'GPY14DZ73PC9', 'AH3CUBC3VNP6'].sort()
            }
            assert values.collect { it.value }.sort { it as String }  == [value, 'http://www.ghgprotocol.org/calculation-tools/all-tools', 'United Arab Emirates'].sort { it as String }
            if (testValuesResource) {
                assert values.collect { it.startDate }.sort() == [actualStartDate, '1970-01-01T00:00:00Z', '1970-01-01T00:00:00Z'].sort()
            }
            assert values.collect { it?.unit } == ['kg/(kW·h)', null, null]
        }
    }

    def getDataItemValuesXml(version, uid, value, actualStartDate, queryStartDate, testValuesResource) {
        if (version >= 3.4) {
            def query = [:]
            if (queryStartDate) {
                query['startDate'] = queryStartDate
            }
            def response = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0${testValuesResource ? '/values' : ''};full",
                    query: query,
                    contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def values = testValuesResource ? response.data.Values.Value : response.data.Item.Values.Value
            assert values.size() == 3
            assert values.Path*.text().sort() == ['country', 'massCO2PerEnergy', 'source']
            assert values.@history*.text().sort() == ['false', 'false', 'true']
            if (testValuesResource) {
                assert values.@uid*.text().sort() == [uid, 'GPY14DZ73PC9', 'AH3CUBC3VNP6'].sort()
            }
            assert values.Value*.text().sort() == [value + "", 'http://www.ghgprotocol.org/calculation-tools/all-tools', 'United Arab Emirates'].sort()
            if (testValuesResource) {
                assert values.StartDate*.text().sort() == [actualStartDate, '1970-01-01T00:00:00Z', '1970-01-01T00:00:00Z'].sort()
            }
            assert values.Unit*.text().sort() == ['kg/(kW·h)']
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
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy",
                    query: query,
                    contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.resultsTruncated == truncated
            def values = response.data.values
            assert values.size() == count
            assertEquals(sum, (values.collect { new Double(it.value) }).sum(), 0.0001)
        }
    }

    def getDataItemValueHistoryXml(version, count, truncated, sum, query) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy",
                    query: query,
                    contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert new Boolean(response.data.Values.@truncated.text()) == truncated
            def values = response.data.Values.Value.Value*.text()
            values = values.collect { new Double(it) }
            assert values.size() == count
            assertEquals(sum, values.sum(), 0.0001)
        }
    }

    /**
     * Test fetching DataItemValues with 'CURRENT' (now) as the item value identifier.
     */
    @Test
    void getDataItemValueForCurrent() {
        getDataItemValue('91119I4I1CIS', 0.81999, '2006-01-01T00:00:00Z', 'CURRENT')
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date just before an actual start date.
     */
    @Test
    void getDataItemValueWithStartDateJustBeforeNextStartDate() {
        getDataItemValue('MLTM20389T5F', 0.74639, '2001-01-01T00:00:00Z', '2001-12-31T23:59:59Z')
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date that has an exact match.
     */
    @Test
    void getDataItemValueWithExactStartDate() {
        getDataItemValue('H10OSN5NJ2WQ', 0.76426, '2002-01-01T00:00:00Z', '2002-01-01T00:00:00Z')
    }

    /**
     * Test fetching DataItemValue with an item value identifier start date at some point between actual start dates
     */
    @Test
    void getDataItemValueWithInBetweenStartDate() {
        getDataItemValue('H10OSN5NJ2WQ', 0.76426, '2002-01-01T00:00:00Z', '2002-08-01T00:00:00Z')
    }

    /**
     * Test fetching DataItemValue with 'FIRST' (epoch) as the item value identifier.
     */
    @Test
    void getDataItemValueForFirstDate() {
        getDataItemValue('1I8KS6GEROR8', 0.8199856, '1970-01-01T00:00:00Z', 'FIRST')
    }

    /**
     * Test fetching DataItemValue with 'LAST' (end of epoch) as the item value identifier.
     */
    @Test
    void getDataItemValueForLastDate() {
        getDataItemValue('91119I4I1CIS', 0.81999, '2006-01-01T00:00:00Z', 'LAST')
    }

    /**
     * Test fetching DataItemValue by UID.
     */
    @Test
    void getDataItemValueByUid() {
        getDataItemValue('H10OSN5NJ2WQ', 0.76426, '2002-01-01T00:00:00Z', 'H10OSN5NJ2WQ')
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
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy/$path;full",
                    contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            def itemValue = response.data.value
            assert itemValue.path == 'massCO2PerEnergy'
            assert itemValue.history
            assert itemValue.uid == uid
            assert itemValue.value == value
            assert itemValue.startDate == startDate
            assert itemValue.unit == 'kg/(kW·h)'
        }
    }

    def getDataItemValueXml(version, uid, value, startDate, path) {
        if (version >= 3.4) {
            def response = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy/$path;full",
                    contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def itemValue = response.data.Value
            assert itemValue.Path.text() == 'massCO2PerEnergy'
            assert itemValue.@history.text()
            assert itemValue.@uid.text() == uid
            assert itemValue.Value.text() == value as String
            assert itemValue.StartDate.text() == startDate
            assert itemValue.Unit.text() == 'kg/(kW·h)'
        }
    }

    @Test
    void updateWithNoValue() {
        setAdminUser()
        updateDataItemValueField('H10OSN5NJ2WQ', 'value', 'typeMismatch', '')
    }

    @Test
    void updateWithSomethingThatIsNotANumber() {
        setAdminUser()
        updateDataItemValueField('H10OSN5NJ2WQ', 'value', 'typeMismatch', 'not_a_number')
    }

    @Test
    void updateWithBadStartDate() {
        setAdminUser()
        updateDataItemValueField('91119I4I1CIS', 'startDate', 'typeMismatch', 'not_a_date')
    }

    @Test
    void updateWithDuplicateStartDate() {
        setAdminUser()
        updateDataItemValueField('91119I4I1CIS', 'startDate', 'duplicate', '2002-01-01T00:00:00Z')
    }

    @Test
    void updateWithMinStartDate() {
        setAdminUser()
        updateDataItemValueField('91119I4I1CIS', 'startDate', 'start_before_min', '1970-01-01T00:00:00Z')
    }

    @Test
    void updateWithBeforeMinStartDate() {
        setAdminUser()
        updateDataItemValueField('91119I4I1CIS', 'startDate', 'start_before_min', '0999-01-01T00:00:00Z')
    }

    @Test
    void updateWithFirstStartDate() {
        setAdminUser()
        updateDataItemValueField('91119I4I1CIS', 'startDate', 'start_before_min', 'FIRST')
    }

    @Test
    void updateWithLastStartDate() {
        setAdminUser()
        updateDataItemValueField('91119I4I1CIS', 'startDate', 'end_after_max', 'LAST')
    }

    @Test
    void updateWithAfterMaxStartDate() {
        setAdminUser()
        updateDataItemValueField('91119I4I1CIS', 'startDate', 'end_after_max', '10000-01-01T00:00:00Z')
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
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy/$path",
                    contentType: JSON)
            assert responseBefore.status == SUCCESS_OK.code
            def valueBefore = responseBefore.data.value.value
            try {
                // Create form body.
                def body = [:]
                body[field] = value
                // Update Data Item Value.
                client.put(
                        path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy/$path",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail('Response status code should have been 400 (' + field + ', ' + code + ').')
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'
                assert [field] == response.data.validationResult.errors.collect { it.field }
                assert [code] == response.data.validationResult.errors.collect { it.code }
            }
            // Get value after update.
            def responseAfter = client.get(
                    path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/AWPA0IZH4VQ0/values/massCO2PerEnergy/$path",
                    contentType: JSON)
            assert responseAfter.status == SUCCESS_OK.code
            def valueAfter = responseAfter.data.value.value
            assert valueAfter == valueBefore
        }
    }
}