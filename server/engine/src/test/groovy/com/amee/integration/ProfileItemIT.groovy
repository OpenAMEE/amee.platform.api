package com.amee.integration

import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import groovyx.net.http.HttpResponseException

/**
 * Tests for the Profile Item API. This API has been available since version 3.6.
 */
class ProfileItemIT extends BaseApiTest {

    def cookingProfileUid = 'UCP4SKANF6CS'
    def cookingProfileItemUids = ['J7TICQCEMGEA', 'CR2IS4R423WK']
    def computersGenericProfileUid = '46OLHG2D9LWM'

    // ICE_v2_by_mass; material=Lime, type=General
    def dataItemUid = 'NX9WAFL8MUCL'
    def categoryUid = 'IPQZMZPFQBDB'
    def categoryWikiName = 'ICE_v2_by_mass'

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
    void createAndRemoveProfileItemJson() {
        versions.each { version -> createAndRemoveProfileItemJson(version) }
    }
    
    def createAndRemoveProfileItemJson(version) {
        if (version >= 3.6) {
            
            // Create the profile item
            def responsePost = client.post(
                path: "/${version}/profiles/${cookingProfileUid}/items",
                body: [
                    name: 'test1',
                    dataItemUid: dataItemUid,
                    startDate: '2012-01-26T10:00:00Z',
                    endDate: '2012-02-26T12:00:00Z',
                    'values.mass': '5'],
                requestContentType: URLENC,
                contentType: JSON)

            assertEquals 201, responsePost.status

            // Is Location available?
            assertNotNull responsePost.headers['Location']
            assertNotNull responsePost.headers['Location'].value
            def location = responsePost.headers['Location'].value
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Get new ProfileItem UID.
            def uid = location.split('/')[7]
            assertNotNull uid

            // Get the profile item
            def responseGet = client.get(
                path: "/${version}/profiles/${cookingProfileUid}/items/${uid};full",
                contentType: JSON)

            assertEquals 200, responseGet.status
            assertEquals 'application/json', responseGet.contentType
            assertTrue responseGet.data instanceof net.sf.json.JSON
            assertEquals 'OK', responseGet.data.status
            assertEquals 'test1', responseGet.data.item.name
            assertEquals '2012-01-26T10:00:00Z', responseGet.data.item.startDate
            assertEquals '2012-02-26T12:00:00Z', responseGet.data.item.endDate
            assertEquals categoryUid, responseGet.data.item.categoryUid
            assertEquals categoryWikiName, responseGet.data.item.categoryWikiName

            // Amounts
            assertEquals 3, responseGet.data.item.amounts.amount.size()
            assertContainsAmount(responseGet.data.item.amounts.amount, 'CO2', 3.8, 'kg', '', true)
            assertContainsAmount(responseGet.data.item.amounts.amount, 'energy', 26.5, 'MJ', '', false)
            assertContainsAmount(responseGet.data.item.amounts.amount, 'CO2e', 3.9000000000000004, 'kg', '', false)

            // Delete the profile item
            def responseDelete = client.delete(path: "/${version}/profiles/${cookingProfileUid}/items/${uid}")
            assertEquals 200, responseDelete.status

            // Check it was deleted
            // We should get a 404 here.
            try {
                client.get(path: "/${version}/profiles/${cookingProfileUid}/items/${uid}")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status
            }
        }
    }

    /**
     * Tests creating a duplicate profile item. A profile item is considered duplicate if it has the same:
     * profile ID, data category ID, data item ID, start date, name.
     */
    @Test
    void createDuplicateProfileItem() {
        versions.each { version -> createDuplicateProfileItem(version) }
    }
    
    def createDuplicateProfileItem(version) {
        if (version >= 3.6) {

            // Create a profile item
            def responsePost = client.post(
                path: "/${version}/profiles/${cookingProfileUid}/items",
                body: [
                    name: 'dupe',
                    dataItemUid: dataItemUid,
                    startDate: '2012-01-26T10:00:00Z'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            assertEquals 201, responsePost.status
            def uid = responsePost.headers['Location'].value.split('/')[7]

            // Try creating a duplicate
            try {
                client.post(
                    path: "/${version}/profiles/${cookingProfileUid}/items",
                    body: [
                        name: 'dupe',
                        dataItemUid: dataItemUid,
                        startDate: '2012-01-26T10:00:00Z'],
                    requestContentType: URLENC,
                    contentType: JSON)
                fail 'Should have thrown exception'
            } catch (HttpResponseException e) {

                // Should have been rejected.
                assertEquals 400, e.response.status;
            }

            // Clean up
            def responseDelete = client.delete(path: "/${version}/profiles/${cookingProfileUid}/items/${uid}")

            // Should have been deleted
            assertEquals 200, responseDelete.status
        }
    }

    /**
     * Tests creating a profile item with overlapping dates and the same name.
     * Two profile items cannot have the same dataItemUid and overlapping dates unless they also have different names.
     */
    @Test
    @Ignore("See: PL-11203")
    void createOverlappingProfileItem() {
        versions.each { version -> createOverlappingProfileItem(version) }
    }
    
    def createOverlappingProfileItem(version) {
        if (version >= 3.6) {

            // Create a profile item
            def responsePost = client.post(
                path: "/${version}/profiles/${cookingProfileUid}/items",
                body: [
                    name: 'overlap',
                    dataItemUid: dataItemUid,
                    startDate: '2012-01-26T10:00:00Z',
                    endDate: '2012-05-10T12:00:00Z'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            assertEquals 201, responsePost.status
            def uid1 = responsePost.headers['Location'].value.split('/')[7]

            // Try creating an overlapping item with the same name
            try {
                client.post(
                    path: "/${version}/profiles/${cookingProfileUid}/items",
                    body: [
                        name: 'overlap',
                        dataItemUid: dataItemUid,
                        startDate: '2012-03-26T10:00:00Z',
                        endDate: '2012-10-15T13:00:00Z'],
                    requestContentType: URLENC,
                    contentType: JSON)
                fail 'Should have thrown exception'
            } catch (HttpResponseException e) {

                // Should have been rejected.
                assertEquals 400, e.response.status;
            }

            // Create an overlapping item with a different name
            responsePost = client.post(
                path: "/${version}/profiles/${cookingProfileUid}/items",
                body: [
                    name: 'overlap with different name',
                    dataItemUid: dataItemUid,
                    startDate: '2012-03-26T10:00:00Z',
                    endDate: '2012-10-15T13:00:00Z'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            assertEquals 201, responsePost.status
            def uid2 = responsePost.headers['Location'].value.split('/')[7]

            // Clean up
            def responseDelete = client.delete(path: "/${version}/profiles/${cookingProfileUid}/items/${uid1}")
            assertEquals 200, responseDelete.status

            responseDelete = client.delete(path: "/${version}/profiles/${cookingProfileUid}/items/${uid2}")
            assertEquals 200, responseDelete.status
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
        def response = client.get(path: "/${version}/profiles/${cookingProfileUid}/items;full", contentType: JSON)
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertFalse response.data.resultsTruncated
        assertEquals cookingProfileItemUids.size(), response.data.items.size()
        assert cookingProfileItemUids.sort() == response.data.items.collect { it.uid }.sort()

        // Should  be sorted by creation date
        assertTrue response.data.items.first().created < response.data.items.last().created
    }

    def getProfileItemsXml(version) {
        def response = client.get(path: "/${version}/profiles/${cookingProfileUid}/items;full", contentType: XML)
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals 'false', response.data.Items.@truncated.text()
        def profileItems = response.data.Items.Item
        assertEquals cookingProfileItemUids.size(), profileItems.size()
        assert cookingProfileItemUids.sort() == profileItems.@uid*.text().sort()

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
        def response = client.get(path: "/${version}/profiles/${cookingProfileUid}/items/J7TICQCEMGEA;full", contentType: JSON)
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
        def response = client.get(path: "/${version}/profiles/${cookingProfileUid}/items/J7TICQCEMGEA;full", contentType: XML)
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text()
        assertEquals 'Cooking', response.data.Item.CategoryWikiName.text()
        assertEquals 'test', response.data.Item.Name.text()

        // The test user's time zone is Europe/London so 2011-10-12T16:13:00Z == 2011-10-12T17:13:00+01:00
        assertEquals '2011-10-12T17:13:00+01:00', response.data.Item.StartDate.text()
        assertEquals '', response.data.Item.EndDate.text()

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

    /**
     * Tests getting a list of profile items the user is not authorised for.
     */
    @Test
    void getProfileItemsUnauthorised() {
        versions.each { version -> getProfileItemsUnauthorised(version) }
    }

    def getProfileItemsUnauthorised(version) {
        if (version >= 3.6) {

            // We just use the ecoinvent user because it is a different user.
            setEcoinventUser()
            try {
                client.get(path: "/${version}/profiles/${cookingProfileUid}/items", contentType: JSON)
                fail 'Expected 403'
            } catch (HttpResponseException e) {
                def response = e.response
                assertEquals 403, response.status
                assertEquals 403, response.data.status.code
                assertEquals 'Forbidden', response.data.status.name
            }
        }
    }

    /**
     * Tests getting a single profile item the user is not authorised for.
     */
    @Test
    void getSingleProfileItemUnauthorised() {
        versions.each { version -> getSingleProfileItemUnauthorised(version) }
    }
    
    def getSingleProfileItemUnauthorised(version) {
        if (version >= 3.6) {

            // We just use the ecoinvent user because it is a different user.
            setEcoinventUser()
            try {
                client.get(path: "/${version}/profiles/${cookingProfileUid}/items/J7TICQCEMGEA", contentType: JSON)
                fail 'Expected 403'
            } catch (HttpResponseException e) {
                def response = e.response
                assertEquals 403, response.status
                assertEquals 403, response.data.status.code
                assertEquals 'Forbidden', response.data.status.name
            }
        }
    }


    /**
     * Tests the validation rules for the Profile Item name field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Optional.
     * <li>Duplicates are allowed.
     * <li>No longer than 255 chars.
     * </ul>
     */
    @Test
    void updateWithInvalidName() {
        updateProfileItemFieldJson('name', 'long', String.randomString(256), 3.6)
    }

    /**
     * Tests the validation rules for the Profile Item date fields.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>All are optional.
     * <li>startDate must be before endDate.
     * <li>dates must be within 1970-01-01 00:00:00 => Almost (less 7 seconds) the last unix time, which is 2038-01-19 03:14:07.
     *     This time is seven seconds less than the last unix because StartEndDate is not sensitive to seconds.
     * </ul>
     */
    @Test
    void updateWithInvalidDates() {
        updateProfileItemFieldJson('startDate', 'epoch.startDate', '1950-01-01T12:00:00Z', 3.6)
        updateProfileItemFieldJson('startDate', 'end_of_epoch.startDate', '2040-01-01T12:00:00Z', 3.6)

        updateProfileItemFieldJson('endDate', 'end_of_epoch.endDate', '2040-01-01T12:00:00Z', 3.6)
        updateProfileItemFieldJson('endDate', 'end_before_start.endDate', '2000-01-01T12:00:00Z', 3.6)

        updateProfileItemFieldJson('duration', 'end_of_epoch.endDate', 'P100Y', 3.6)
    }

    /**
     * Tests the validation rules for the Profile Item values.
     *
     * The rules depend on the Item Value Definition for the value being updated.
     *
     * <ul>
     * <li>integer values must not be empty and must be a Java integer.
     * <li>double values must not be empty and must be a Java double.
     * <li>text values are optional and must be no longer than 32767 characters.
     * </ul>
     */
    @Test
    void updateWithValues() {
        updateProfileItemFieldJson('values.numberOwned', 'typeMismatch', 'not_an_integer', 3.6);
        updateProfileItemFieldJson('values.numberOwned', 'typeMismatch', '1.1', 3.6); // Not an integer either.
        updateProfileItemFieldJson('values.numberOwned', 'typeMismatch', '', 3.6);
        updateProfileItemFieldJson('values.onStandby', 'long', String.randomString(32768), 3.6);
        updateProfileItemFieldJson('values.onStandby', 'long', String.randomString(32768), 3.6);

        // TODO: test doubles?
    }

    /**
     * Submits a single Profile Item field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateProfileItemFieldJson(field, code, value, since) {
        versions.each { version -> updateProfileItemFieldJson(field, code, value, since, version) }
    }

    /**
     * Submits a single Profile Item field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    void updateProfileItemFieldJson(field, code, value, since, version) {
        if (version >= since) {
            try {
                // Create form body.
                def body = [:]
                body[field] = value
                // Update ProfileItem.
                client.put(
                    path: "/${version}/profiles/${computersGenericProfileUid}/items/J5OCT81E66FT",
                    body: body,
                    requestContentType: URLENC,
                    contentType: JSON)
                fail 'Response status code should have been 400 (' + field + ', ' + code + ').'
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assertEquals 400, response.status
                assertEquals 'application/json', response.contentType
                assertTrue response.data instanceof net.sf.json.JSON
                assertEquals 'INVALID', response.data.status
                assert [field] == response.data.validationResult.errors.collect {it.field}
                assert [code] == response.data.validationResult.errors.collect {it.code}
            }
        }
    }

    /**
     * Helper method to check an amount is present and correct. Designed for json. Check with xml...
     *
     * @param amounts the array of amounts to check.
     * @param type the expected type, eg CO2.
     * @param value the expected value, eg 5.83.
     * @param unit the expected unit, eg kg.
     * @param perUnit the expected perUnit, eg month.
     * @param isDefault is this amount the default type?
     */
    def assertContainsAmount(amounts, type, value, unit, perUnit, isDefault) {
        def amount = amounts.find { it.type == type }
        assertNotNull amount
        assertEquals value, amount.value, 0.000001
        assertEquals unit, amount.unit
        assertEquals perUnit, amount.perUnit
        if (isDefault) {
            assertEquals isDefault, amount.default
        } else {
            assertNull amount.default
        }
    }
}
