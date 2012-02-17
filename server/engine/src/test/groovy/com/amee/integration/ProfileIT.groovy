package com.amee.integration

import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import groovyx.net.http.HttpResponseException

/**
 * Tests for the Profile API. This API has been available since version 3.6.
 */
class ProfileIT extends BaseApiTest {

    // See import.sql
    def profileUids = ['UCP4SKANF6CS', '46OLHG2D9LWM', 'TP437QW12VEV']
    def categoryNames = ['Cooking', 'Generic', 'Generic']
    def categoryWikiNames = ['Cooking', 'Computers_generic', 'Computers_generic']

    /**
     * Tests for creation, fetch and deletion of a Profile using JSON & XML responses.
     *
     * Create a new Profile by POSTing to '/profiles'
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>profile=true (required)
     * </ul>
     *
     * Note: the submitted profile parameter is just to provide a body to the request. It is not used in creation.
     *
     * Delete (TRASH) a Profile by sending a DELETE request to '/profiles/{UID}'.
     *
     */
    @Test
    void createAndRemoveProfile() {
        versions.each { version -> createAndRemoveProfile(version) }
    }

    def createAndRemoveProfile(version) {
        if (version >= 3.6) {
            createAndRemoveProfileJson(version)
            createAndRemoveProfileXml(version)
        }
    }

    def createAndRemoveProfileJson(version) {

        // Create a new Profile.
        def responsePost = client.post(
            path: "/${version}/profiles",
            body: [profile: "true"],
            requestContentType: URLENC,
            contentType: JSON)

        assertEquals 201, responsePost.status

        // Get and check the Location.
        def profileLocation = responsePost.headers['Location'].value
        def profileUid = profileLocation.split('/')[5]
        assertEquals 12, profileUid.size()

        // Fetch the Profile.
        def responseGet = client.get(path: "${profileLocation};full", contentType: JSON)
        assertEquals 200, responseGet.status
        assertEquals 'application/json', responseGet.contentType
        assertTrue responseGet.data instanceof net.sf.json.JSON
        assertEquals 'OK', responseGet.data.status
        assertEquals profileUid, responseGet.data.profile.uid
        assertTrue responseGet.data.profile.categories.isEmpty()

        // Delete it
        def responseDelete = client.delete(path: profileLocation)
        assertEquals 200, responseDelete.status

        // Check it has been deleted
        try {
            client.get(path: profileLocation)
            fail 'Should have thrown Exception'
        } catch (HttpResponseException e) {
            assertEquals 404, e.response.status
        }
    }

    def createAndRemoveProfileXml(version) {

        // Create a new Profile.
        def responsePost = client.post(
            path: "/${version}/profiles",
            body: [profile: "true"],
            requestContentType: URLENC,
            contentType: XML)

        assertEquals 201, responsePost.status

        // Get and check the Location.
        def profileLocation = responsePost.headers['Location'].value
        def profileUid = profileLocation.split('/')[5]
        assertEquals 12, profileUid.size()

        // Fetch the Profile.
        def responseGet = client.get(path: "${profileLocation};full", contentType: XML)
        assertEquals 200, responseGet.status
        assertEquals 'application/xml', responseGet.contentType
        assertEquals 'OK', responseGet.data.Status.text()
        assertEquals profileUid, responseGet.data.Profile.@uid.text()
        assertTrue responseGet.data.Profile.Categories.Category.isEmpty()

        // Delete it
        def responseDelete = client.delete(path: profileLocation)
        assertEquals 200, responseDelete.status

        // Check it has been deleted
        try {
            client.get(path: profileLocation)
            fail 'Should have thrown Exception'
        } catch (HttpResponseException e) {
            assertEquals 404, e.response.status
        }
    }

    /**
     * Tests fetching a single Profile using JSON & XML.
     *
     * Get a single Profile by sending a GET request to '/profiles/{UID}'.
     *
     * Profile GET requests support the following matrix parameters to modify the response:
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>categories - include the categories used by this profile's items.
     * </ul>
     */
    @Test
    void getSingleProfile() {
        versions.each { version -> getSingleProfile(version) }
    }

    def getSingleProfile(version) {
        if (version >= 3.6) {
            getSingleProfileJson(version)
            getSingleProfileXml(version)
        }
    }

    def getSingleProfileJson(version) {
        def response = client.get(
            path: "/${version}/profiles/${profileUids[0]};full",
            contentType: JSON)
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals profileUids[0], response.data.profile.uid
        assertEquals 1, response.data.profile.categories.size()
        assertEquals categoryNames[0], response.data.profile.categories[0].name
        assertEquals categoryWikiNames[0], response.data.profile.categories[0].wikiName
    }

    def getSingleProfileXml(version) {
        def response = client.get(
            path: "/${version}/profiles/${profileUids[1]};full",
            contentType: XML)
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals profileUids[1], response.data.Profile.@uid.text()
        def allCategories = response.data.Profile.Categories.Category
        assertEquals 1, allCategories.size()
        assertEquals categoryNames[1], allCategories[0].Name.text()
        assertEquals categoryWikiNames[1], allCategories[0].WikiName.text()
    }

    /**
     * Tests fetching a list of all Profiles for the current user JSON & XML.
     *
     * Profiles GET requests support the same matrix parameters as GETs for a single Profile.
     *
     * Profiles GET requests support the following query parameters to filter the results.
     *
     * <ul>
     *     <li>resultStart - Zero-based starting index offset to support result-set 'pagination'. Defaults to 0.
     *     <li>resultLimit - Limit the number of entries in the result-set. Defaults to 50 there is no max.
     * </ul>
     *
     * The order in which profiles are returned is undefined.
     *
     */
    @Test
    void getAllProfiles() {
        versions.each { version -> getAllProfiles(version) }
    }

    def getAllProfiles(version) {
        if (version >= 3.6) {
            getAllProfilesJson(version)
            getAllProfilesXml(version)
        }
    }

    def getAllProfilesJson(version) {
        def response = client.get(
            path: "/${version}/profiles;full",
            contentType: JSON)
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals profileUids.size(), response.data.profiles.size()
        assertEquals profileUids.sort(), response.data.profiles.collect { it.uid }.sort()

        def nameList = []
        response.data.profiles.each { profile -> profile.categories.each { nameList.add(it.name) } }
        assertEquals categoryNames.sort(), nameList.sort()
        def wikiNameList = []
        response.data.profiles.each { profile -> profile.categories.each { wikiNameList.add(it.wikiName) } }
        assertEquals categoryWikiNames.sort(), wikiNameList.sort()
    }

    def getAllProfilesXml(version) {
        def response = client.get(
            path: "/${version}/profiles;full",
            contentType: XML)
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        def allProfiles = response.data.Profiles.Profile
        assertEquals profileUids.size(), allProfiles.size()
        assertEquals profileUids.sort(), allProfiles.@uid*.text().sort()

        def nameList = []
        allProfiles.each { profile -> profile.Categories.Category.each { nameList.add(it.Name.text()) } }
        assertEquals nameList.sort(), categoryNames.sort()
    }

    /**
     * Tests fetching a subset of profiles by using the resultStart and resultLimit parameters.
     *
     */
    @Test
    void getSomeProfiles() {
        versions.each { version -> getSomeProfiles(version) }
    }

    def getSomeProfiles(version) {
        if (version >= 3.6) {
            getSomeProfilesJson(version)
            getSomeProfilesXml(version)
        }
    }

    def getSomeProfilesJson(version) {
        def response = client.get(
            path: "/${version}/profiles",
            query: [resultStart:'1', resultLimit: '1'],
            contentType: JSON)
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals 1, response.data.profiles.size()
    }

    def getSomeProfilesXml(version) {
        def response = client.get(
            path: "/${version}/profiles",
            query: [resultStart:'1', resultLimit: '1'],
            contentType: XML)
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        def profiles = response.data.Profiles.Profile
        assertEquals 1, profiles.size()
    }

    /**
     * Tests getting a single profile the user is not authorised for.
     */
    @Test
    void getSingleProfileUnauthorised() {
        versions.each { version -> getSingleProfileUnauthorised(version) }
    }

    def getSingleProfileUnauthorised(version) {
        if (version >= 3.6) {

            // We just use the ecoinvent user because it is a different user.
            setEcoinventUser()
            try {
                client.get(path: "/${version}/profiles/UCP4SKANF6CS", contentType: JSON)
                fail 'Expected 403'
            } catch (HttpResponseException e) {
                def response = e.response
                assertEquals 403, response.status
                assertEquals 403, response.data.status.code
                assertEquals 'Forbidden', response.data.status.name
            }
        }
    }

}
