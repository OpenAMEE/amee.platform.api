package com.amee.integration

import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Profile Item API.
 */
class ProfileItemIT extends BaseApiTest {

    def profileItemUids = ['J7TICQCEMGEA', 'CR2IS4R423WK']

    /**
     * Tests for creation, fetch and deletion of a Profile Item using JSON responses.
     *
     * Create a new Profile Item by POSTing to '/profiles/{UID}/items' (since 3.6.0).
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>either dataItemUid or category path and drills (see PL-260).
     * <li>startDate
     * <li>endDate
     * <li>duration
     * <li>values.{path}*
     * </ul>
     *
     * The 'values.{path}' parameter can be used to set Profile Item Values.
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Profile Item by sending a DELETE request to '/profiles/{UID}/items/{UID}' (since 3.6.0).
     */
    @Test
    @Ignore("Profile Item creation not yet implemented")
    void createAndRemoveProfileItemJson() {
        versions.each { version -> createAndRemoveProfileItemJson(version) }
    }
    
    def createAndRemoveProfileItemJson(version) {
        if (version >= 3.6) {

            // TODO
        }
    }

    /**
     * Test fetching a number of profile items with JSON and XML responses.
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
    void getProfileItems() {
        versions.each { version -> getProfileItems(version) }
    }
    
    def getProfileItems(version) {
        if (version >= 3.6) {
            getProfileItemsJson(version)
            getProfileItemsXml(version)
        }
    }

    def getProfileItemsJson(version) {
        def response = client.get(path: "/${version}/profiles/UCP4SKANF6CS/items;full", contentType: JSON)
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertFalse response.data.resultsTruncated
        assertEquals profileItemUids.size(), response.data.items.size()
        assert profileItemUids.sort() == response.data.items.collect { it.uid }.sort()

        // Should  be sorted by creation date
        assertTrue response.data.items.first().created < response.data.items.last().created
    }

    def getProfileItemsXml(version) {
        def response = client.get(path: "/${version}/profiles/UCP4SKANF6CS/items;full", contentType: XML)
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals 'false', response.data.Items.@truncated.text()
        def profileItems = response.data.Items.Item
        assertEquals profileItemUids.size(), profileItems.size()
        assert profileItemUids.sort() == profileItems.@uid*.text().sort()

        // Should be sorted by creation date
        assertTrue profileItems[0].@created.text() < profileItems[-1].@created.text()
    }

    /**
     * Tests fetching a single Profile Item with JSON and XML responses.
     *
     * Get a single Profile Item by sending a GET request to '/profiles/{UID}/items/{UID}'.
     *
     * Profile Item GET requests support the following matrix parameters to modify the response:
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>amounts - include the profile item's calculation amounts.
     * <li>name - include the profile item's name.
     * <li>dates - include the profile item's start and end dates.
     * <li>category - include the category used by this profile item.
     * </ul>
     */
    @Test
    void getSingleProfileItem() {
        versions.each { version -> getSingleProfileItem(version) }
    }
    
    def getSingleProfileItem(version) {
        if (version >= 3.6) {
            getSingleProfileItemJson(version)
            getSingleProfileItemXml(version)
        }
    }

    def getSingleProfileItemJson(version) {
        def response = client.get(path: "/${version}/profiles/UCP4SKANF6CS/items/J7TICQCEMGEA;full", contentType: JSON)
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals '54C8A44254AA', response.data.item.categoryUid
        assertEquals 'Cooking', response.data.item.categoryWikiName
        assertEquals 'test', response.data.item.name

        // The test user's time zone is Europe/London so 2011-10-12T16:13:00Z == 2011-10-12T17:13:00+01:00
        assertEquals '2011-10-12T17:13:00+01:00', response.data.item.startDate
        assertEquals '', response.data.item.endDate

        // TODO: update calculations to return more than one GHG type.
        // Amounts
        assertEquals 1, response.data.item.amounts.amount.size()
        assertEquals 'CO2', response.data.item.amounts.amount[0].type
        assertEquals 'year', response.data.item.amounts.amount[0].perUnit
        assertEquals 'kg', response.data.item.amounts.amount[0].unit
        assertTrue response.data.item.amounts.amount[0].default
        assertEquals 233.35999999999999, response.data.item.amounts.amount[0].value, 0.000001
        
        // Notes
        assertEquals 1, response.data.item.amounts.notes.size()
        assertEquals 'comment', response.data.item.amounts.notes[0].type
        assertEquals 'This is a comment', response.data.item.amounts.notes[0].value
    }

    def getSingleProfileItemXml(version) {
        def response = client.get(path: "/${version}/profiles/UCP4SKANF6CS/items/J7TICQCEMGEA;full", contentType: XML)
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text()
        assertEquals 'Cooking', response.data.Item.CategoryWikiName.text()
        assertEquals 'test', response.data.Item.Name.text()

        // The test user's time zone is Europe/London so 2011-10-12T16:13:00Z == 2011-10-12T17:13:00+01:00
        assertEquals '2011-10-12T17:13:00+01:00', response.data.Item.StartDate.text()
        assertEquals '', response.data.Item.EndDate.text()

        // TODO: update calculations to return more than one GHG type.
        // Amounts
        assertEquals 1, response.data.Item.Amounts.Amount.size()
        assertEquals 'CO2', response.data.Item.Amounts.Amount[0].@type.text()
        assertEquals 'year', response.data.Item.Amounts.Amount[0].@perUnit.text()
        assertEquals 'kg', response.data.Item.Amounts.Amount[0].@unit.text()
        assertEquals 'true', response.data.Item.Amounts.Amount[0].@default.text()
        assertEquals '233.35999999999999', response.data.Item.Amounts.Amount[0].text()

        // Notes
        assertEquals 1, response.data.Item.Amounts.Notes.Note.size()
        assertEquals 'comment', response.data.Item.Amounts.Notes.Note[0].@type.text()
        assertEquals 'This is a comment', response.data.Item.Amounts.Notes.Note[0].text()
    }
}
