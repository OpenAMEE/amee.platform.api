package com.amee.integration

import net.sf.json.JSON
import org.junit.Test

/**
 * Tests for the Profile Item API.
 */
class ProfileItemIT extends BaseApiTest {

    /**
     * Test fetching a number of profile items with JSON responses.
     *
     * Profile item GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>amounts - include the emission amounts for profile items.
     * <li>name - include the profile item name.
     * <li>dates - include the profile item start and end dates.
     * <li>category - include the data category UID and wikiName values.
     * </ul>
     *
     */
    @Test
    void getProfileItemsJson() {
        versions.each { version -> getProfileItemsJson(version) }
    }

    def getProfileItemsJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON
            def response = client.get(path: "/${version}/profiles/UCP4SKANF6CS/items;full")
            assertEquals 200, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertFalse response.data.resultsTruncated
            assertEquals profileItemUids.size(), response.data.items.size()
            def responseUids = response.data.items.collect { it.uid }.sort()
            assert profileItemUids == responseUids

            // Should  be sorted by creation date
            assertTrue response.data.items.first().created < response.data.items.last().created
        }
    }
}
