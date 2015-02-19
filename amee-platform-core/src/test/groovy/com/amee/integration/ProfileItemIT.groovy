package com.amee.integration

import com.amee.domain.DataItemService
import com.amee.domain.item.profile.ProfileItem
import com.amee.domain.item.profile.ProfileItemTextValue
import groovyx.net.http.HttpResponseException
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import static org.restlet.data.Status.*

/**
 * Tests for the Profile Item API. This API has been available since version 3.6.
 */
class ProfileItemIT extends BaseApiTest {

    // Days in a year
    static def DAYS_IN_YEAR = 365.242199

    def cookingProfileUid = 'UCP4SKANF6CS'
    def cookingProfileItemUids = ['J7TICQCEMGEA',  'CR2IS4R423WK']
    def computersGenericProfileUid = '46OLHG2D9LWM'

    def selectByProfileUid = 'TP437QW12VEV'
    def selectByProfileItemUids = [start: '8G534LCOMF8Z', end: '5W6K9PWM5OXD', span: '7LXKYQAY237H']

    // ICE_v2_by_mass; material=Lime, type=General
    def dataItemUid = 'R80Z2HQRYRR6'
    def categoryUid = 'G7TA4MLV5R8Q'
    def categoryWikiName = 'ICE_v2_by_mass'

    /**
     * Tests for creation, fetch, update and deletion of a Profile Item using JSON and XML responses.
     * Tests creation by uid and category + drill downs.
     *
     * Create a new Profile Item by POSTing to '/profiles/{UID}/items' (since 3.6.0).
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>dataItemUid
     * <li>category
     * <li>{drill_path}*
     * <li>startDate
     * <li>endDate
     * <li>duration
     * <li>values.{path}*
     * <li>note</li>
     * <li>units.{path}*
     * <li>perUnits.{path}*
     * </ul>
     *
     * The 'values.{path}' parameter can be used to set Profile Item Values.
     * The 'units.{path}' parameter can be used to set Profile Item Value units.
     * The 'perUnits.{path}' parameter can be used to set Profile Item Value perUnits.
     *
     * See {@link ProfileItemValueIT} tests for examples of supplying different units and perUnits.
     *
     * You may specify the data item using either the dataItemUid parameter or a combination of the
     * category parameter and drill values. Category wiki name and drills have been supported since 3.6.0.
     * Eg, category=Electricity_by_Country&country=Albania
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Update a Profile Item by sending a PUT request to '/profiles/{UID}/items' (since 3.6.0)
     *
     * Delete (TRASH) a Profile Item by sending a DELETE request to '/profiles/{UID}/items/{UID}' (since 3.6.0).
     */
    @Test
    void createAndRemoveProfileItemJson() {
        versions.each { version -> createAndRemoveProfileItemByUidJson(version) }
        versions.each { version -> createAndRemoveProfileItemByCategoryXml(version) }
    }

    def createAndRemoveProfileItemByUidJson(version) {
        if (version >= 3.6) {

            // Create the profile item
            def responsePost = client.post(
                path: "/$version/profiles/$cookingProfileUid/items",
                body: [
                    name: 'test1',
                    dataItemUid: dataItemUid,
                    startDate: '2012-01-26T10:00:00Z',
                    endDate: '2012-02-26T12:00:00Z',
                    'values.mass': '5',
                    note: 'Test note'],
                requestContentType: URLENC,
                contentType: JSON)

            // Is Location available?
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")

            // Get new ProfileItem UID.
            String uid = location.split('/')[7]

            // Success response
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Get the profile item
            def responseGet = client.get(path: "/$version/profiles/$cookingProfileUid/items/$uid;full", contentType: JSON)

            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.item.name == 'test1'
            assert responseGet.data.item.startDate == '2012-01-26T10:00:00Z'
            assert responseGet.data.item.endDate == '2012-02-26T12:00:00Z'
            assert responseGet.data.item.categoryUid == categoryUid
            assert responseGet.data.item.categoryWikiName == categoryWikiName
            assert responseGet.data.item.note == 'Test note'

            // Amounts
            assert responseGet.data.item.output.amounts.size() == 3
            assertContainsAmountJson(responseGet.data.item.output.amounts, 'CO2', 3.8, 'kg', true)
            assertContainsAmountJson(responseGet.data.item.output.amounts, 'energy', 26.5, 'MJ', false)
            assertContainsAmountJson(responseGet.data.item.output.amounts, 'CO2e', 3.9000000000000004, 'kg', false)

            // Update the profile item
            def responsePut = client.put(
                path: "/$version/profiles/$cookingProfileUid/items/$uid",
                body: [note: 'Updated note'],
                requestContentType: URLENC,
                contentType: JSON)
            assertOkJson(responsePut, SUCCESS_OK.code, uid)

            // Get the updated profile item
            responseGet = client.get(path: "/$version/profiles/$cookingProfileUid/items/$uid;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.item.note == 'Updated note'

            // Delete the profile item
            def responseDelete = client.delete(path: "/$version/profiles/$cookingProfileUid/items/$uid")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Check it was deleted
            // We should get a 404 here.
            try {
                client.get(path: "/$version/profiles/$cookingProfileUid/items/$uid")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    def createAndRemoveProfileItemByCategoryXml(version) {
        if (version >= 3.6) {

            // Create the profile item
            def responsePost = client.post(
                path: "/$version/profiles/$cookingProfileUid/items",
                body: [
                    name: 'test2',
                    category: 'ICE_v2_by_mass',
                    material: 'Lime',
                    type: 'General',
                    startDate: '2012-01-26T10:00:00Z',
                    endDate: '2012-02-26T12:00:00Z',
                    'values.mass': '5'],
                requestContentType: URLENC,
                contentType: XML)

            // Is Location available?
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")

            // Get new ProfileItem UID.
            String uid = location.split('/')[7]

            // Success response
            assertOkXml(responsePost, SUCCESS_CREATED.code, uid)

            // Get the profile item
            def responseGet = client.get(path: "/$version/profiles/$cookingProfileUid/items/$uid;full", contentType: XML)

            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/xml'
            assert responseGet.data.Status.text() == 'OK'
            assert responseGet.data.Item.Name.text() == 'test2'
            assert responseGet.data.Item.StartDate.text() == '2012-01-26T10:00:00Z'
            assert responseGet.data.Item.EndDate.text() == '2012-02-26T12:00:00Z'
            assert responseGet.data.Item.CategoryUid.text() == categoryUid
            assert responseGet.data.Item.CategoryWikiName.text() == categoryWikiName

            // Amounts
            assert responseGet.data.Item.Output.Amounts.Amount.size() == 3
            assertContainsAmountXml(responseGet.data.Item.Output.Amounts.Amount, 'CO2', 3.8, 'kg', true)
            assertContainsAmountXml(responseGet.data.Item.Output.Amounts.Amount, 'energy', 26.5, 'MJ', false)
            assertContainsAmountXml(responseGet.data.Item.Output.Amounts.Amount, 'CO2e', 3.9000000000000004, 'kg', false)

            // Delete the profile item
            def responseDelete = client.delete(path: "/$version/profiles/$cookingProfileUid/items/$uid", contentType: XML)
            assertOkXml(responseDelete, SUCCESS_OK.code, uid)

            // Check it was deleted
            // We should get a 404 here.
            try {
                client.get(path: "/$version/profiles/$cookingProfileUid/items/$uid", contentType: XML)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    @Test
    void createAndRemoveProfileItemsByUid(){
        versions.each { version -> createAndRemoveProfileItemsByUidJson(version) }
        versions.each { version -> createAndRemoveProfileItemsByUidXML(version) }
    }

    def createAndRemoveProfileItemsByUidJson(version) {
        if(version >= 3.6) {
            def requestBody = '''
            {
              "profileItems":[
                {
                  "dataItemUid":"HEDE07J83VR2",
                  "energyPerTime":101,
                  "responsibleArea":101,
                  "totalArea":101
                },
                {
                  "dataItemUid":"HEDE07J83VR2",
                  "energyPerTime":201,
                  "responsibleArea":201,
                  "totalArea":201
                }
              ]
            }
            '''

            // Create the profile items
            def responsePost = client.post(
                path: "/$version/profiles/$cookingProfileUid/items",
                body: requestBody,
                requestContentType: JSON,
                contentType: JSON)

            assert responsePost.status == SUCCESS_CREATED.code
            assert responsePost.headers['Location'] == null

            assert responsePost.data.profileItems.size() == 2

            String uid0 = responsePost.data.profileItems[0].uid
            assert uid0 != null
            String location0 = responsePost.data.profileItems[0].location
            assert location0.contains("/profiles/$cookingProfileUid/items")

            String uid1 = responsePost.data.profileItems[1].uid
            assert uid1 != null
            String location1 = responsePost.data.profileItems[1].location
            assert location1.contains("/profiles/$cookingProfileUid/items")

            // Cleanup first profile item
            def responseDelete = client.delete(path: location0, contentType: JSON)
            assertOkJson(responseDelete, SUCCESS_OK.code, uid0)

            // Check it was deleted - we should get a 404 here.
            try {
                client.get(path: location0, contentType: JSON)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }

            // Cleanup second profile item
            responseDelete = client.delete(path: location1, contentType: JSON)
            assertOkJson(responseDelete, SUCCESS_OK.code, uid1)

            // Check it was deleted - we should get a 404 here.
            try {
                client.get(path: location1, contentType: JSON)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    def createAndRemoveProfileItemsByUidXML(version) {
        if(version >= 3.6) {
            def requestBody =
            '''<?xml version="1.0" encoding="UTF-8"?>
            <ProfileCategory>
              <ProfileItems>
                <ProfileItem>
                  <dataItemUid>HEDE07J83VR2</dataItemUid>
                  <energyPerTime>101</energyPerTime>
                  <responsibleArea>101</responsibleArea>
                  <totalArea>101</totalArea>
                </ProfileItem>
                <ProfileItem>
                  <dataItemUid>HEDE07J83VR2</dataItemUid>
                  <energyPerTime>201</energyPerTime>
                  <responsibleArea>201</responsibleArea>
                  <totalArea>201</totalArea>
                </ProfileItem>
              </ProfileItems>
            </ProfileCategory>
            '''

            // Create the profile items
            def responsePost = client.post(
                path: "/$version/profiles/$cookingProfileUid/items",
                body: requestBody,
                requestContentType: XML,
                contentType: XML)

            assert responsePost.status == SUCCESS_CREATED.code
            assert responsePost.headers['Location'] == null

            assert responsePost.data.ProfileItems.ProfileItem.size() == 2

            String uid0 = responsePost.data.ProfileItems.ProfileItem[0].Entity.text()
            assert uid0 != null
            String location0 = responsePost.data.ProfileItems.ProfileItem[0].Location.text()
            assert location0.contains("/profiles/$cookingProfileUid/items")

            String uid1 = responsePost.data.ProfileItems.ProfileItem[1].Entity.text()
            assert uid1 != null
            String location1 = responsePost.data.ProfileItems.ProfileItem[1].Location.text()
            assert location1.contains("/profiles/$cookingProfileUid/items")

            // Cleanup first profile item
            def responseDelete = client.delete(path: location0, contentType: XML)
            assertOkXml(responseDelete, SUCCESS_OK.code, uid0)

            // Check it was deleted - we should get a 404 here.
            try {
                client.get(path: location0, contentType: XML)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }

            // Cleanup second profile item
            responseDelete = client.delete(path: location1, contentType: XML)
            assertOkXml(responseDelete, SUCCESS_OK.code, uid1)

            // Check it was deleted - we should get a 404 here.
            try {
                client.get(path: location1, contentType: XML)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
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
                path: "/$version/profiles/$cookingProfileUid/items",
                body: [name: 'dupe', dataItemUid: dataItemUid, startDate: '2012-01-26T10:00:00Z'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            String uid = responsePost.headers['Location'].value.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Try creating a duplicate
            try {
                client.post(
                    path: "/$version/profiles/$cookingProfileUid/items",
                    body: [name: 'dupe', dataItemUid: dataItemUid, startDate: '2012-01-26T10:00:00Z'],
                    requestContentType: URLENC,
                    contentType: JSON)
                fail 'Should have thrown exception'
            } catch (HttpResponseException e) {

                // Should have been rejected.
                assert e.response.status == CLIENT_ERROR_BAD_REQUEST.code
            }

            // Clean up
            def responseDelete = client.delete(path: "/$version/profiles/$cookingProfileUid/items/$uid")

            // Should have been deleted
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)
        }
    }

    /**
     * Tests creating a profile item with overlapping dates and the same name.
     * Two profile items cannot have the same dataItemUid and overlapping dates unless they also have different names.
     */
    @Test
    void createOverlappingProfileItem() {
        versions.each { version -> createOverlappingProfileItem(version) }
    }

    def createOverlappingProfileItem(version) {
        if (version >= 3.6) {

            // Create a profile item
            def responsePost = client.post(
                path: "/$version/profiles/$cookingProfileUid/items",
                body: [name: 'overlap', dataItemUid: dataItemUid, startDate: '2012-01-26T10:00:00Z', endDate: '2012-05-10T12:00:00Z'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            String uid1 = responsePost.headers['Location'].value.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid1)

            // Try creating an overlapping item with the same name
            try {
                client.post(
                    path: "/$version/profiles/$cookingProfileUid/items",
                    body: [name: 'overlap', dataItemUid: dataItemUid, startDate: '2012-03-26T10:00:00Z', endDate: '2012-10-15T13:00:00Z'],
                    requestContentType: URLENC,
                    contentType: JSON)
                fail 'Should have thrown exception'
            } catch (HttpResponseException e) {

                // Should have been rejected.
                assert e.response.status == CLIENT_ERROR_BAD_REQUEST.code
            }

            // Create an overlapping item with a different name
            responsePost = client.post(
                path: "/$version/profiles/$cookingProfileUid/items",
                body: [name: 'overlap with different name', dataItemUid: dataItemUid, startDate: '2012-03-26T10:00:00Z', endDate: '2012-10-15T13:00:00Z'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            String uid2 = responsePost.headers['Location'].value.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid2)

            // Clean up
            def responseDelete = client.delete(path: "/$version/profiles/$cookingProfileUid/items/$uid1")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid1)

            responseDelete = client.delete(path: "/$version/profiles/$cookingProfileUid/items/$uid2")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid2)
        }
    }

    /**
     * Tests creation of profile items with valid and invalid choices specified.
     */
    @Test
    void createProfileItemWithChoice() {
        versions.each { version -> createProfileItemWithChoice(version) }
    }

    def createProfileItemWithChoice(version) {
        if(version >= 3.6){
            // Try creating a profile item with an invalid choice
            try{
                client.post(
                    path: "/$version/profiles/$computersGenericProfileUid/items",
                    body: [name: 'invalidChoice', dataItemUid: '4A1PR4YZSMIJ', 'values.onStandby': 'notAValidChoice'],
                    requestContentType: URLENC,
                    contentType: JSON)
                fail 'Should have thrown exception'
            } catch(HttpResponseException e){
                // Should have been rejected
                assert CLIENT_ERROR_BAD_REQUEST.code == e.response.status
            }

            // Try creating a profile item with a valid choice
            def responsePost = client.post(
                path: "/${version}/profiles/${computersGenericProfileUid}/items",
                body: [name: 'validChoice', dataItemUid: '4A1PR4YZSMIJ', 'values.onStandby': 'always'],
                requestContentType: URLENC,
                contentType: JSON)

            // Should have been created
            String uid = responsePost.headers['Location'].value.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Clean up
            def responseDelete = client.delete(path: "/$version/profiles/$computersGenericProfileUid/items/$uid")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)
        }
    }

    /**
     * Test fetching a number of profile items with JSON and XML responses.
     *
     * Get a list of profile items by sending a GET request to /profiles/{UID}/items.
     * The startDate and endDate query parameters can be used to define a "query window" for the request.
     * Profile items are sorted by creation date.
     *
     * Profile item list GET requests support the following query parameters.
     *
     * <ul>
     *     <li>startDate - start date for query window. Defaults to start of current month.
     *     <li>endDate - end date for query window. Defaults to infinitely far in the future.
     *     <li>duration - alternative to specifying endDate.
     *     <li>selectBy - Setting this to 'start' will only include items which start during the query window.
     *                    Setting 'end' will include only items which end during the window.
     *                    The default behaviour is to include any item that intersects the query window.
     *     <li>mode - Set the calculation mode used. By default, emission values for items are for the whole item,
     *                not just the part of the item that intersects the query window.
     *                To get just the emissions that took place during the query window, set this parameter to 'prorata'.
     *</ul>
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
     * <li>note</li>
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
        def response = client.get(path: "/$version/profiles/$cookingProfileUid/items;full", contentType: JSON)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert !response.data.resultsTruncated
        assert response.data.items.size() == cookingProfileItemUids.size()
        assert response.data.items.collect { it.uid }.sort() == cookingProfileItemUids.sort()

        // Should  be sorted by creation date
        assert response.data.items.first().created < response.data.items.last().created
    }

    def getProfileItemsXml(version) {
        def response = client.get(path: "/$version/profiles/$cookingProfileUid/items;full", contentType: XML)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.Items.@truncated.text() == 'false'
        def profileItems = response.data.Items.Item
        assert profileItems.size() == cookingProfileItemUids.size()
        assert profileItems.@uid*.text().sort() == cookingProfileItemUids.sort()

        // Should be sorted by creation date
        assert profileItems[0].@created.text() < profileItems[-1].@created.text()
    }

    /**
     * Tests fetching a list of profile items restricted by the selectBy parameter.
     */
    @Test
    void getProfileItemsSelectBy() {
        versions.each { version -> getProfileItemsSelectByJson(version) }
    }

    def getProfileItemsSelectByJson(version) {
        if (version >= 3.6) {

            // Query window is from April to June

            // Default is to get all items that intersect query window
            def response = client.get(
                path: "/$version/profiles/$selectByProfileUid/items",
                query: [startDate: '2012-04-01T09:00:00Z', endDate: '2012-06-01T09:00:00Z'],
                contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert !response.data.resultsTruncated
            assert response.data.items.size() == selectByProfileItemUids.size()
            assert response.data.items.collect { it.uid }.sort() == selectByProfileItemUids.collect { it.value }.sort()

            // duration parameter can be used instead of endDate
            response = client.get(
                path: "/$version/profiles/$selectByProfileUid/items",
                query: [startDate: '2012-04-01T09:00:00Z', duration: 'P2M'],
                contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert !response.data.resultsTruncated
            assert response.data.items.size() == selectByProfileItemUids.size()
            assert response.data.items.collect { it.uid }.sort() == selectByProfileItemUids.collect { it.value }.sort()

            // selectBy=startDate selects items that start in the window
            response = client.get(
                path: "/$version/profiles/$selectByProfileUid/items",
                query: [startDate: '2012-04-01T09:00:00Z', endDate: '2012-06-01T09:00:00Z', selectBy: 'start'],
                contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert !response.data.resultsTruncated
            assert response.data.items.size() == 1
            assert response.data.items[0].uid == selectByProfileItemUids['start']

            // selectBy=endDate selects items that end in the window
            response = client.get(
                path: "/$version/profiles/$selectByProfileUid/items",
                query: [startDate: '2012-04-01T09:00:00Z', endDate: '2012-06-01T09:00:00Z', selectBy: 'end'],
                contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert !response.data.resultsTruncated
            assert response.data.items.size() == 1
            assert response.data.items[0].uid == selectByProfileItemUids['end']

            // No items in the window
            response = client.get(
                path: "/$version/profiles/$selectByProfileUid/items",
                query: [startDate: '2000-04-01T09:00:00Z', endDate: '2000-06-01T09:00:00Z'],
                contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert !response.data.resultsTruncated
            assert response.data.items.size() == 0
        }
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
     * <li>note - include the item's note
     * </ul>
     *
     * Profile item GET requests support the following query parameters.
     *
     * <ul>
     * <li>returnUnits.{type}* - units for return values. Eg returnUnits.CO2=g.
     * <li>returnPerUnits.{type}* - perUnits for return values. Eg returnPerUnits.CO2=month.
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
        def response = client.get(path: "/$version/profiles/$cookingProfileUid/items/J7TICQCEMGEA;full", contentType: JSON)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.item.categoryUid == '1H5QPB38KZ8Z'
        assert response.data.item.categoryWikiName == 'Cooking'
        assert response.data.item.name == 'test'

        // The test user's time zone is Europe/London so 2011-10-12T16:13:00Z == 2011-10-12T17:13:00+01:00
        assert response.data.item.startDate == '2011-10-12T17:13:00+01:00'
        assert response.data.item.endDate == ''

        // Amounts
        assert response.data.item.output.amounts.size() == 1
        assert response.data.item.output.amounts[0].type == 'CO2'
        assert response.data.item.output.amounts[0].unit == 'kg/year'
        assert response.data.item.output.amounts[0].default
        assertEquals(233.35999999999999, response.data.item.output.amounts[0].value, 0.000001)

        // Notes
        assert response.data.item.output.notes.size() == 1
        assert response.data.item.output.notes[0].type == 'comment'
        assert response.data.item.output.notes[0].value == 'This is a comment'
    }

    def getSingleProfileItemXml(version) {
        def response = client.get(path: "/$version/profiles/$cookingProfileUid/items/J7TICQCEMGEA;full", contentType: XML)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.Item.CategoryUid.text() == '1H5QPB38KZ8Z'
        assert response.data.Item.CategoryWikiName.text() == 'Cooking'
        assert response.data.Item.Name.text() == 'test'

        // The test user's time zone is Europe/London so 2011-10-12T16:13:00Z == 2011-10-12T17:13:00+01:00
        assert response.data.Item.StartDate.text() == '2011-10-12T17:13:00+01:00'
        assert response.data.Item.EndDate.text() == ''

        // Amounts
        assert response.data.Item.Output.Amounts.Amount.size() == 1
        assert response.data.Item.Output.Amounts.Amount[0].@type.text() == 'CO2'
        assert response.data.Item.Output.Amounts.Amount[0].@unit.text() == 'kg/year'
        assert response.data.Item.Output.Amounts.Amount[0].@default.text() == 'true'
        assert response.data.Item.Output.Amounts.Amount[0].text() == '233.35999999999999'

        // Notes
        assert response.data.Item.Output.Notes.Note.size() == 1
        assert response.data.Item.Output.Notes.Note[0].@type.text() == 'comment'
        assert response.data.Item.Output.Notes.Note[0].text() == 'This is a comment'
    }

    @Test
    void getProfileItemCustomReturnUnits() {
        versions.each { version -> getProfileItemCustomReturnUnits(version) }
    }

    def getProfileItemCustomReturnUnits(version) {
        if (version >= 3.6) {
            def response = client.get(
                path: "/$version/profiles/$cookingProfileUid/items/J7TICQCEMGEA;amounts",
                query: ['returnUnits.CO2': 'g', 'returnPerUnits.CO2': 'month'],
                contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'

            // Amounts
            assert response.data.item.output.amounts.size() == 1
            assert response.data.item.output.amounts[0].type == 'CO2'
            assert response.data.item.output.amounts[0].unit == 'g/month'
            assert response.data.item.output.amounts[0].default
            assertEquals(19446.6666666667, response.data.item.output.amounts[0].value, 0.000001)
        }
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
            setOtherUser()
            try {
                client.get(path: "/$version/profiles/$cookingProfileUid/items", contentType: JSON)
                fail 'Expected 403'
            } catch (HttpResponseException e) {
                def response = e.response
                assert response.status == CLIENT_ERROR_FORBIDDEN.code
                assert response.data.status.code == CLIENT_ERROR_FORBIDDEN.code
                assert response.data.status.name == 'Forbidden'
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
            setOtherUser()
            try {
                client.get(path: "/$version/profiles/$cookingProfileUid/items/J7TICQCEMGEA", contentType: JSON)
                fail 'Expected 403'
            } catch (HttpResponseException e) {
                def response = e.response
                assert response.status == CLIENT_ERROR_FORBIDDEN.code
                assert response.data.status.code == CLIENT_ERROR_FORBIDDEN.code
                assert response.data.status.name == 'Forbidden'
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
        updateProfileItemFieldJson('name', 'long', String.randomString(ProfileItem.NAME_MAX_SIZE + 1), 3.6)
    }

    /**
     * Tests the validation rules for the Profile Item date fields.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>All are optional.
     * <li>startDate must be before endDate.
     * <li>dates must be within 1970-01-01 00:00:00 => 9999-12-31 23:59:59, which is from the epoch to the last
     * date supported by MySQL DATETIME columns.
     * </ul>
     */
    @Test
    void updateWithInvalidDates() {
        updateProfileItemFieldJson('startDate', 'start_before_min.startDate', '999-01-01T12:00:00Z', 3.6)
        updateProfileItemFieldJson('startDate', 'end_after_max.startDate', '10000-01-01T12:00:00Z', 3.6)

        updateProfileItemFieldJson('endDate', 'end_after_max.endDate', '10000-01-01T12:00:00Z', 3.6)
        updateProfileItemFieldJson('endDate', 'end_before_start.endDate', '2000-01-01T12:00:00Z', 3.6)

        updateProfileItemFieldJson('duration', 'end_after_max.endDate', 'P10000Y', 3.6)

        // Invalid format for duration
        updateProfileItemFieldJson('duration', 'format', '10Y', 3.6)
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
        updateProfileItemFieldJson('values.numberOwned', 'typeMismatch', 'not_an_integer', 3.6)
        updateProfileItemFieldJson('values.numberOwned', 'typeMismatch', '1.1', 3.6) // Not an integer either.
        updateProfileItemFieldJson('values.numberOwned', 'typeMismatch', '', 3.6)
        updateProfileItemFieldJson('values.onStandby', 'long', String.randomString(ProfileItemTextValue.VALUE_SIZE + 1), 3.6)

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
    def updateProfileItemFieldJson(field, code, value, since, version) {
        if (version >= since) {
            try {
                // Create form body.
                def body = [:]
                body[field] = value
                // Update ProfileItem.
                client.put(
                    path: "/$version/profiles/$computersGenericProfileUid/items/J5OCT81E66FT",
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
                assert response.data.validationResult.errors.collect { it.field } == [field]
                assert response.data.validationResult.errors.collect { it.code } == [code]
            }
        }
    }

    // TODO: updateProfileItemJson see: updateDataItemJson

    /**
     * Tests calculations are performed correctly with different query ranges.
     * This test taken from the v2 time_resolution_spec.rb test.
     */
    @Test
    void timeSeries() {
        versions.each { version -> timeSeries(version) }
    }

    def timeSeries(version) {
        if (version >= 3.6) {

            // cases relating to a single valued data series

            // cases relating to profile item within query range
            incase(version, 'WT0S', 1, 9, 2, 7, true, false, 2.0 * 23.0)
            incase(version, 'W00S', 1, 9, 2, 7, false, false, 2.0 * 23.0)
            incase(version, 'WTRS', 1, 9, 2, 7, true, true, 2.0 * 23.0 * 5.0 / DAYS_IN_YEAR)
            incase(version, 'W0RS', 1, 9, 2, 7, false, true, 2.0 * 23.0)

            incase(version, 'WT01', 1, 9, 2, 7, true, false, 2.0 * 23.0, false)
            incase(version, 'W001', 1, 9, 2, 7, false, false, 2.0 * 23.0, false)
            incase(version, 'WTR1', 1, 9, 2, 7, true, true, 2.0 * 23.0 * 5.0 / DAYS_IN_YEAR, false)
            incase(version, 'W0R1', 1, 9, 2, 7, false, true, 2.0 * 23.0, false)

            incase(version, 'WT0E', 41, 49, 42, 47, true, false, 17.0 * 23.0)
            incase(version, 'W00E', 41, 49, 42, 47, false, false, 17.0 * 23.0)
            incase(version, 'WTRE', 41, 49, 42, 47, true, true, 17.0 * 23.0 * 5.0 / DAYS_IN_YEAR)
            incase(version, 'W0RE', 41, 49, 42, 47, false, true, 17.0 * 23.0)

            // cases relating to profile item partially overlapping query range
            incase(version, 'PT0S', 1, 5, 2, 7, true, false, 2.0 * 23.0)
            incase(version, 'P00S', 1, 5, 2, 7, false, false, 2.0 * 23.0)
            incase(version, 'PTRS', 1, 5, 2, 7, true, true, 2.0 * 23.0 * 3.0 / DAYS_IN_YEAR)
            incase(version, 'P0RS', 1, 5, 2, 7, false, true, 2.0 * 23.0 * 3.0 / 5.0)

            incase(version, 'PT01', 1, 5, 2, 7, true, false, 2.0 * 23.0, false)
            incase(version, 'P001', 1, 5, 2, 7, false, false, 2.0 * 23.0, false)
            incase(version, 'PTR1', 1, 5, 2, 7, true, true, 2.0 * 23.0 * 3.0 / DAYS_IN_YEAR, false)
            incase(version, 'P0R1', 1, 5, 2, 7, false, true, 2.0 * 23.0 * 3.0 / 5.0, false)

            incase(version, 'PT0E', 41, 45, 42, 47, true, false, 17.0 * 23.0)
            incase(version, 'P00E', 41, 45, 42, 47, false, false, 17.0 * 23.0)
            incase(version, 'PTRE', 41, 45, 42, 47, true, true, 17.0 * 23.0 * 3.0 / DAYS_IN_YEAR)
            incase(version, 'P0RE', 41, 45, 42, 47, false, true, 17.0 * 23.0 * 3.0 / 5.0)

            // cases relating to profile item wholly overlapping query range
            incase(version, 'LT0S', 2, 7, 1, 9, true, false, 2.0 * 23.0)
            incase(version, 'L00S', 2, 7, 1, 9, false, false, 2.0 * 23.0)
            incase(version, 'LTRSD', 2, 7, 1, 9, true, true, 2.0 * 23.0 * 5.0 / DAYS_IN_YEAR)
            incase(version, 'L0RS', 2, 7, 1, 9, false, true, 2.0 * 23.0 * 5.0 / 8.0)

            incase(version, 'LT01', 2, 7, 1, 9, true, false, 2.0 * 23.0, false)
            incase(version, 'L001', 2, 7, 1, 9, false, false, 2.0 * 23.0, false)
            incase(version, 'LTR1D', 2, 7, 1, 9, true, true, 2.0 * 23.0 * 5.0 / DAYS_IN_YEAR, false)
            incase(version, 'L0R1', 2, 7, 1, 9, false, true, 2.0 * 23.0 * 5.0 / 8.0, false)

            incase(version, 'LT0E', 42, 47, 41, 49, true, false, 17.0 * 23.0)
            incase(version, 'L00E', 42, 47, 41, 49, false, false, 17.0 * 23.0)
            incase(version, 'LTRED', 42, 47, 41, 49, true, true, 17.0 * 23.0 * 5.0 / DAYS_IN_YEAR)

            incase(version, 'L0RE', 42, 47, 41, 49, false, true, 17.0 * 23.0 * 5.0 / 8.0)

            // cases relating to profile item without end partially overlapping range
            incase(version, 'ET0S', 1, 5, 2, null, true, false, 17.0 * 23.0)
            incase(version, 'E00S', 1, 5, 2, null, false, false, 17.0 * 23.0)
            incase(version, 'ETRS', 1, 5, 2, null, true, true, 2.0 * 23.0 * 3.0 / DAYS_IN_YEAR)
            incase(version, 'E0RSD', 1, 5, 2, null, false, true, 2.0 * 23.0) // I (JH) disagree w spec
            // incase('E0RSD', 1, 5, 2, null, false, true, 0) // I think this

            incase(version, 'ET01', 1, 5, 2, null, true, false, 2.0 * 23.0, false)
            incase(version, 'E001', 1, 5, 2, null, false, false, 2.0 * 23.0, false)
            incase(version, 'ETR1', 1, 5, 2, null, true, true, 2.0 * 23.0 * 3.0 / DAYS_IN_YEAR, false)
            incase(version, 'E0R1D', 1, 5, 2, null, false, true, 2.0 * 23.0, false) // I (JH) disagree w spec
            // incase('E0R1D', 1, 5, 2, null, false, true, 0, false, version)

            incase(version, 'ET0E', 41, 45, 42, null, true, false, 17.0 * 23.0)
            incase(version, 'E00E', 41, 45, 42, null, false, false, 17.0 * 23.0)
            incase(version, 'ETRE', 41, 45, 42, null, true, true, 17.0 * 23.0 * 3.0 / DAYS_IN_YEAR)
            incase(version, 'E0RED', 41, 45, 42, null, false, true, 17.0 * 23.0) // I (JH) disagree w spec
            // incase('E0RED', 41, 45, 42, null, false, true, 0, version)

            // cases relating to multi-valued data items
            // 2, 3, 11, 13, 17
            // cases relating to profile item within query range
            incase(version, 'WT0M', 1, 39, 12, 27, true, false, 23.0 * (3.0 * 8.0 + 11.0 * 7.0) / 15.0)
            incase(version, 'W00M', 1, 39, 12, 27, false, false, 23.0 * (3.0 * 8.0 + 11.0 * 7.0) / 15.0)
            incase(version, 'WTRM', 1, 39, 12, 27, true, true, 23.0 * (3.0 * 8.0 + 11.0 * 7.0) / DAYS_IN_YEAR)
            incase(version, 'W0RM', 1, 39, 12, 27, false, true, 23.0 * (3.0 * 8.0 + 11.0 * 7.0) / 15.0)

            incase(version, 'WT0F', 1, 49, 12, 37, true, false, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 7.0) / 25.0)
            incase(version, 'W00F', 1, 49, 12, 37, false, false, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 7.0) / 25.0)
            incase(version, 'WTRF', 1, 49, 12, 37, true, true, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 7.0) / DAYS_IN_YEAR)
            incase(version, 'W0RF', 1, 49, 12, 37, false, true, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 7.0) / 25.0)

            // cases relating to profile item partially overlapping query range
            incase(version, 'PT0M', 1, 25, 12, 37, true, false, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 7.0) / 25.0)
            incase(version, 'P00M', 1, 25, 12, 37, false, false, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 7.0) / 25.0)
            incase(version, 'PTRM', 1, 25, 12, 37, true, true, 23.0 * (3.0 * 8.0 + 11.0 * 5.0) / DAYS_IN_YEAR)
            incase(version, 'P0RM', 1, 25, 12, 37, false, true, 23.0 * (3.0 * 8.0 + 11.0 * 5.0) / 25.0)

            incase(version, 'PT0F', 1, 35, 12, 47, true, false, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 10.0 + 17.0 * 7.0) / 35.0)
            incase(version, 'P00F', 1, 35, 12, 47, false, false, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 10.0 + 17.0 * 7.0) / 35.0)
            incase(version, 'PTRF', 1, 35, 12, 47, true, true, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 5.0) / DAYS_IN_YEAR)
            incase(version, 'P0RF', 1, 35, 12, 47, false, true, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 5.0) / 35.0)

            // cases relating to profile item wholly overlapping query range
            incase(version, 'LT0M', 12, 27, 1, 39, true, false, 23.0 * (2.0 * 9.0 + 3.0 * 10.0 + 11.0 * 10.0 + 13.0 * 9.0) / 38.0)
            incase(version, 'L00M', 12, 27, 1, 39, false, false, 23.0 * (2.0 * 9.0 + 3.0 * 10.0 + 11.0 * 10.0 + 13.0 * 9.0) / 38.0)
            incase(version, 'LTRMD', 12, 27, 1, 39, true, true, 23.0 * (8.0 * 3.0 + 11.0 * 7.0) / DAYS_IN_YEAR)
            incase(version, 'L0RM', 12, 27, 1, 39, false, true, 23.0 * (8.0 * 3.0 + 11.0 * 7.0) / 38.0)

            incase(version, 'LT0F', 12, 37, 1, 49, true, false, 23.0 * (2.0 * 9.0 + 3.0 * 10.0 + 11.0 * 10.0 + 13.0 * 10.0 + 17.0 * 9.0) / 48.0)
            incase(version, 'L00F', 12, 37, 1, 49, false, false, 23.0 * (2.0 * 9.0 + 3.0 * 10.0 + 11.0 * 10.0 + 13.0 * 10.0 + 17.0 * 9.0) / 48.0)
            incase(version, 'LTRFD', 12, 37, 1, 49, true, true, 23.0 * (8.0 * 3.0 + 11.0 * 10.0 + 13.0 * 7.0) / DAYS_IN_YEAR)
            incase(version, 'L0RF', 12, 37, 1, 49, false, true, 23.0 * (8.0 * 3.0 + 11.0 * 10.0 + 13.0 * 7.0) / 48.0)

            // cases relating to profile item without end partially overlapping range
            incase(version, 'ET0F', 1, 35, 12, null, true, false, 17.0 * 23.0)
            incase(version, 'E00F', 1, 35, 12, null, false, false, 17.0 * 23.0)
            incase(version, 'ETRF', 1, 35, 12, null, true, true, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 5.0) / DAYS_IN_YEAR)
            incase(version, 'E0RFD', 1, 35, 12, null, false, true, 23.0 * (3.0 * 8.0 + 11.0 * 10.0 + 13.0 * 5.0) / 23.0)
            // incase('E0RFD', 1, 35, 12, null, false, true, 0) // i (JH) disagree w spec

        }
    }

    def incase(version, code, querystart, queryend, profilestart, profileend, perunit, prorata, objective, isseries = true) {

        DateTimeFormatter dateFormat = ISODateTimeFormat.dateTimeNoMillis()

        // check_calculation_range

        // Create a profile item
        def postParams = [name: code, 'values.energyPerTime': 23.0]
        postParams.dataItemUid = getDataItemUid(isseries, perunit)
        if (profilestart) {
            def profileStartDate = DataItemService.EPOCH + profilestart
            postParams.startDate = dateFormat.print(profileStartDate.getTime())
        }
        if (profileend) {
            def profileEndDate = DataItemService.EPOCH + profileend
            postParams.endDate = dateFormat.print(profileEndDate.getTime())
        }

        def responsePost = client.post(
            path: "/$version/profiles/UCP4SKANF6CS/items",
            body: postParams,
            requestContentType: URLENC,
            contentType: JSON)

        String location = responsePost.headers['Location'].value
        String uid = location.split('/')[7]
        assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

        // Fetch profile items and check values
        def queryParams = [:]
        if (querystart) {
            def queryStartDate = DataItemService.EPOCH + querystart
            queryParams.startDate = dateFormat.print(queryStartDate.getTime())
        }
        if (queryend) {
            def queryEndDate = DataItemService.EPOCH + queryend
            queryParams.endDate = dateFormat.print(queryEndDate.getTime())
        }
        if (prorata) {
            queryParams.mode = 'prorata'
        }
        def responseGet = client.get(path: "/${version}/profiles/UCP4SKANF6CS/items;name;amounts", query: queryParams, contentType: JSON)
        assert responseGet.status == SUCCESS_OK.code

        def item = responseGet.data.items.find { it.uid == uid }
        assert item.name == code

        // Amounts
        assert item.output.amounts.size() == 1
        assertContainsAmountJson(item.output.amounts, 'CO2', objective, 'kg/year', true)

        // Delete the profile item
        def responseDelete = client.delete(path: "/$version/profiles/UCP4SKANF6CS/items/$uid")
        assertOkJson(responseDelete, SUCCESS_OK.code, uid)

        // Check it was deleted
        // We should get a 404 here.
        try {
            client.get(path: "/$version/profiles/UCP4SKANF6CS/items/$uid")
            fail 'Should have thrown an exception'
        } catch (HttpResponseException e) {
            assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
        }
    }

    def getDataItemUid(isSeries, hasPerUnit) {
        if (isSeries) {
            if (hasPerUnit) {
                return '1F6B12D982B5'
            } else {
                return '58E2285C5310'
            }
        } else {
            if (hasPerUnit) {
                return 'A68029BBC709'
            } else {
                return '0BE050A60037'
            }
        }
    }

    /**
     * Helper method to check an amount is present and correct. Designed for json.
     *
     * Precision is 1e-4
     *
     * @param amounts the array of amounts to check.
     * @param type the expected type, eg CO2.
     * @param value the expected value, eg 5.83.
     * @param unit the expected unit, eg kg.
     * @param isDefault is this amount the default type?
     */
    def assertContainsAmountJson(amounts, String type, double value, String unit, boolean isDefault) {
        def amount = amounts.find { it.type == type }
        assertNotNull amount
        assertEquals(value, amount.value, 0.0001)
        assert amount.unit == unit
        assert amount.default == isDefault
    }

    /**
     * Helper method to check an amount is present and correct. Designed for xml.
     *
     * Precision is 1e-4
     *
     * @param amounts the array of amounts to check.
     * @param type the expected type, eg CO2.
     * @param value the expected value, eg 5.83.
     * @param unit the expected unit, eg kg.
     * @param isDefault is this amount the default type?
     */
    def assertContainsAmountXml(amounts, String type, double value, String unit, boolean isDefault) {
        def amount = amounts.find { it.@type.text() == type }
        assertNotNull amount
        assertEquals(value, amount.text() as double, 0.0001)
        assert amount.@unit.text() == unit
        assert amount.@default.text() as boolean == isDefault
    }
}
