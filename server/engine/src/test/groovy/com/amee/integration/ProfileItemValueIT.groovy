package com.amee.integration

import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import groovyx.net.http.HttpResponseException

/**
 * Tests for the Profile Item Value API. This API has been available since version 3.6.
 */
class ProfileItemValueIT extends BaseApiTest {

    def profileUid = '46OLHG2D9LWM'
    def profileItemUid = 'J5OCT81E66FT'

    /**
     * Tests fetching profile item values for a profile item with JSON and XML responses.
     *
     * Profile Item Value GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>category - include the profile item value's data category UID and wiki name.
     * <li>item - include the profile item's UID.
     * <li>itemValueDefinition - include the item value definition UID, name and path.
     * </ul>
     */
    @Test
    void getProfileItemValues() {
        versions.each { version -> getProfileItemValuesJson(version) }
        versions.each { version -> getProfileItemValuesXml(version) }
    }

    def getProfileItemValuesJson(version) {
        if (version >= 3.6) {
            def response = client.get(
                path: "/${version}/profiles/${profileUid}/items/${profileItemUid}/values;full",
                contentType: JSON)
            assertEquals 200, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status

            def itemValues = response.data.values
            assertValueJson(itemValues, 'numberOwned', 1, null, null, null, profileItemUid, 'Computers_generic', 'Number Owned')
            assertValueJson(itemValues, 'onStandby', 'mostly', null, null, null, profileItemUid, 'Computers_generic', 'On standby')
        }
    }
    
    def getProfileItemValuesXml(version) {
        if (version >= 3.6) {
            def response = client.get(
                path: "/${version}/profiles/${profileUid}/items/${profileItemUid}/values;full",
                contentType: XML)
            assertEquals 200, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()

            def itemValues = response.data.Values.Value
            assertValueXml(itemValues, 'numberOwned', '1', null, null, null, profileItemUid, 'Computers_generic', 'Number Owned')
            assertValueXml(itemValues, 'onStandby', 'mostly', null, null, null, profileItemUid, 'Computers_generic', 'On standby')
        }
    }

    /**
     * Tests creating a profile item value with non-default unit and perUnit.
     */
    @Test
    void units() {
        versions.each { version -> units(version) }
    }

    def units(version) {
        if (version >= 3.6) {

            // Create a profile item with standard units (kWH/year)
            // Electricity_by_Country; country=Albania
            def responsePost = client.post(
                path: "/${version}/profiles/${profileUid}/items",
                body: [
                    name: 'standard',
                    dataItemUid: '963A90C107FA',
                    'values.energyPerTime': '5'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            assertEquals 201, responsePost.status
            def uid = responsePost.headers['Location'].value.split('/')[7]

            // Get the profile item
            def responseGet = client.get(
                path: "/${version}/profiles/${profileUid}/items/${uid};amounts",
                contentType: JSON)

            // Check the calculated amount
            assertEquals 200, responseGet.status
            assertEquals 1, responseGet.data.item.amounts.amount.size()
            def amount = responseGet.data.item.amounts.amount[0]
            assertNotNull amount
            assertEquals 10.0, amount.value, 0.0001
            assertEquals 'kg', amount.unit
            assertEquals 'year', amount.perUnit
            assertTrue amount.default

            // Check the values
            responseGet = client.get(
                path: "/${version}/profiles/${profileUid}/items/${uid}/values;full",
                contentType: JSON)
            def itemValues = responseGet.data.values
            assertValueJson(itemValues, 'energyPerTime', 5, 'kWh', 'year', 'kW·h/year', uid, 'Electricity_by_Country', 'Energy per Time')

            // Delete the profile item
            def responseDelete = client.delete(path: "/${version}/profiles/${profileUid}/items/${uid}")
            assertEquals 200, responseDelete.status

            // Check it was deleted
            // We should get a 404 here.
            try {
                client.get(path: "/${version}/profiles/${profileUid}/items/${uid}")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status
            }

            // Create a profile item with different units (MWh/month)
            responsePost = client.post(
                path: "/${version}/profiles/${profileUid}/items",
                body: [
                    name: 'non-standard',
                    dataItemUid: '963A90C107FA',
                    'values.energyPerTime': '5',
                    'units.energyPerTime' : 'MWh',
                    'perUnits.energyPerTime' : 'month'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            assertEquals 201, responsePost.status
            uid = responsePost.headers['Location'].value.split('/')[7]

            // Get the profile item
            responseGet = client.get(
                path: "/${version}/profiles/${profileUid}/items/${uid};amounts",
                contentType: JSON)

            // Check the calculated amount
            assertEquals 200, responseGet.status
            assertEquals 1, responseGet.data.item.amounts.amount.size()
            amount = responseGet.data.item.amounts.amount[0]
            assertNotNull amount
            assertEquals 120000.0, amount.value, 0.0001
            assertEquals 'kg', amount.unit
            assertEquals 'year', amount.perUnit
            assertTrue amount.default

            // Check the values
            responseGet = client.get(
                path: "/${version}/profiles/${profileUid}/items/${uid}/values;full",
                contentType: JSON)
            itemValues = responseGet.data.values
            assertValueJson(itemValues, 'energyPerTime', 5, 'MWh', 'month', 'MW·h/month', uid, 'Electricity_by_Country', 'Energy per Time')

            // Delete the profile item
            responseDelete = client.delete(path: "/${version}/profiles/${profileUid}/items/${uid}")
            assertEquals 200, responseDelete.status

            // Check it was deleted
            // We should get a 404 here.
            try {
                client.get(path: "/${version}/profiles/${profileUid}/items/${uid}")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status
            }
        }
    }

    /**
     * Helper method to check a value is present and correct. JSON version.
     *
     * @param itemValues the array of values to check.
     * @param path the expected item value path.
     * @param value the expected value, eg 5.83.
     * @param unit the expected unit, eg kg. Only present for number values.
     * @param perUnit the expected perUnit, eg year. Only present for number values.
     * @param compoundUnit the expected compound unit, eg MW·h/month. Only present for number values.
     * @param itemUid the expected profile item UID.
     * @param wikiName the expected wikiName.
     * @param itemValueDefName the expected item value definition name.
     */
    def assertValueJson(itemValues, path, value, unit, perUnit, compoundUnit, itemUid, wikiName, itemValueDefName) {
        def itemValue = itemValues.find { it.itemValueDefinition.path == path }
        assertNotNull itemValue
        if (itemValue.value instanceof Double) {
            assertEquals value, itemValue.value, 0.000001
        } else {
            assertEquals value, itemValue.value
        }
        if (unit) {
            assertEquals unit, itemValue.unit
        }
        if (perUnit) {
            assertEquals perUnit, itemValue.perUnit
        }
        if (compoundUnit) {
            assertEquals compoundUnit, itemValue.compoundUnit
        }
        assertEquals itemUid, itemValue.item.uid
        assertEquals wikiName, itemValue.category.wikiName
        assertEquals itemValueDefName, itemValue.itemValueDefinition.name
    }

    /**
     * Helper method to check a value is present and correct. XML version.
     *
     * @param itemValues the array of values to check.
     * @param path the expected item value path.
     * @param value the expected value, eg 5.83.
     * @param unit the expected unit, eg kg. Only present for number values.
     * @param perUnit the expected perUnit, eg year. Only present for number values.
     * @param compoundUnit the expected compound unit, eg MW·h/month. Only present for number values.
     * @param itemUid the expected profile item UID.
     * @param wikiName the expected wikiName.
     * @param itemValueDefName the expected item value definition name.
     */
    def assertValueXml(itemValues, path, value, unit, perUnit, compoundUnit, itemUid, wikiName, itemValueDefName) {
        def itemValue = itemValues.find { it.ItemValueDefinition.Path == path }
        assertNotNull itemValue
        assertEquals value, itemValue.Value.text()
        if (unit) {
            assertEquals unit, itemValue.Unit.text()
        }
        if (perUnit) {
            assertEquals perUnit, itemValue.PerUnit.text()
        }
        if (compoundUnit) {
            assertEquals compoundUnit, itemValue.CompoundUnit.text()
        }
        assertEquals itemUid, itemValue.Item.@uid.text()
        assertEquals wikiName, itemValue.Category.WikiName.text()
        assertEquals itemValueDefName, itemValue.ItemValueDefinition.Name.text()
    }
}
