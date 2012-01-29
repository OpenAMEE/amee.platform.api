package com.amee.integration

import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Profile Item Value API. This API has been available since version 3.6.
 */
class ProfileItemValueIT extends BaseApiTest {

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
                path: "/${version}/profiles/46OLHG2D9LWM/items/${profileItemUid}/values;full",
                contentType: JSON)
            assertEquals 200, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status

            def itemValues = response.data.values
            assertValueJson(itemValues, 'numberOwned', 1, profileItemUid, 'Computers_generic', 'Number Owned')
            assertValueJson(itemValues, 'onStandby', 'mostly', profileItemUid, 'Computers_generic', 'On standby')
        }
    }
    
    def getProfileItemValuesXml(version) {
        if (version >= 3.6) {
            def response = client.get(
                path: "/${version}/profiles/46OLHG2D9LWM/items/${profileItemUid}/values;full",
                contentType: XML)
            assertEquals 200, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()

            def itemValues = response.data.Values.Value
            assertValueXml(itemValues, 'numberOwned', 1, profileItemUid, 'Computers_generic', 'Number Owned')
        }
    }

    /**
     * Helper method to check a value is present and correct. JSON version.
     *
     * @param itemValues the array of values to check.
     * @param path the expected item value path.
     * @param value the expected value, eg 5.83.
     * @param itemUid the expected profile item UID.
     * @param wikiName the expected wikiName.
     * @param itemDefName the expected item definition name.
     */
    def assertValueJson(itemValues, path, value, itemUid, wikiName, itemDefName) {
        def itemValue = itemValues.find { it.itemValueDefinition.path == path }
        assertNotNull itemValue
        if (itemValue.value instanceof Double) {
            assertEquals value, itemValue.value, 0.000001
        } else {
            assertEquals value, itemValue.value
        }
        assertEquals itemUid, itemValue.item.uid
        assertEquals wikiName, itemValue.category.wikiName
        assertEquals itemDefName, itemValue.itemValueDefinition.name
    }

    /**
     * Helper method to check a value is present and correct. XML version.
     *
     * @param itemValues the array of values to check.
     * @param path the expected item value path.
     * @param value the expected value, eg 5.83.
     * @param itemUid the expected profile item UID.
     * @param wikiName the expected wikiName.
     * @param itemDefName the expected item definition name.
     */
    def assertValueXml(itemValues, path, value, itemUid, wikiName, itemDefName) {
        def itemValue = itemValues.find { it.ItemValueDefinition.Path.text() == path }
        assertNotNull itemValue
    }
}
