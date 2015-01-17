package com.amee.integration

import com.amee.base.utils.UidGen
import groovyx.net.http.HttpResponseException
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

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
            path: "/$version/profiles",
            body: [profile: "true"],
            requestContentType: URLENC,
            contentType: JSON)

        // Get and check the Location.
        String profileLocation = responsePost.headers['Location'].value
        String profileUid = profileLocation.split('/')[5]
        assert UidGen.INSTANCE_12.isValid(profileUid)
        assertOkJson(responsePost, SUCCESS_CREATED.code, profileUid)

        // Fetch the Profile.
        def responseGet = client.get(path: "$profileLocation;full", contentType: JSON)
        assert responseGet.status == SUCCESS_OK.code
        assert responseGet.contentType == 'application/json'
        assert responseGet.data.status == 'OK'
        assert responseGet.data.profile.uid == profileUid
        assert responseGet.data.profile.categories.isEmpty()

        // Delete it
        def responseDelete = client.delete(path: profileLocation)
        assertOkJson(responseDelete, SUCCESS_OK.code, profileUid)

        // Check it has been deleted
        try {
            client.get(path: profileLocation)
            fail 'Should have thrown Exception'
        } catch (HttpResponseException e) {
            assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
        }
    }

    def createAndRemoveProfileXml(version) {

        // Create a new Profile.
        def responsePost = client.post(
            path: "/$version/profiles",
            body: [profile: "true"],
            requestContentType: URLENC,
            contentType: XML)

        // Get and check the Location.
        String profileLocation = responsePost.headers['Location'].value
        String profileUid = profileLocation.split('/')[5]
        assert UidGen.INSTANCE_12.isValid(profileUid)
        assertOkXml(responsePost, SUCCESS_CREATED.code, profileUid)

        // Fetch the Profile.
        def responseGet = client.get(path: "$profileLocation;full", contentType: XML)
        assert responseGet.status == SUCCESS_OK.code
        assert responseGet.contentType == 'application/xml'
        assert responseGet.data.Status.text() == 'OK'
        assert responseGet.data.Profile.@uid.text() == profileUid
        assert responseGet.data.Profile.Categories.Category.isEmpty()

        // Delete it
        def responseDelete = client.delete(path: profileLocation, contentType: XML)
        assertOkXml(responseDelete, SUCCESS_OK.code, profileUid)

        // Check it has been deleted
        try {
            client.get(path: profileLocation)
            fail 'Should have thrown Exception'
        } catch (HttpResponseException e) {
            assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
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
        def response = client.get(path: "/$version/profiles/${profileUids[0]};full", contentType: JSON)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.profile.uid == profileUids[0]
        assert response.data.profile.categories.size() == 1
        assert response.data.profile.categories[0].name == categoryNames[0]
        assert response.data.profile.categories[0].wikiName == categoryWikiNames[0]
    }

    def getSingleProfileXml(version) {
        def response = client.get(path: "/$version/profiles/${profileUids[1]};full", contentType: XML)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.Profile.@uid.text() == profileUids[1]
        def allCategories = response.data.Profile.Categories.Category
        assert allCategories.size() == 1
        assert allCategories[0].Name.text() == categoryNames[1]
        assert allCategories[0].WikiName.text() == categoryWikiNames[1]
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
        def response = client.get(path: "/$version/profiles;full", contentType: JSON)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.profiles.size() == profileUids.size()
        assert response.data.profiles.collect { it.uid }.sort() == profileUids.sort()

        def nameList = []
        def wikiNameList = []
        response.data.profiles.each { profile -> profile.categories.each { nameList.add(it.name); wikiNameList.add(it.wikiName) } }
        assert nameList.sort() == categoryNames.sort()
        assert wikiNameList.sort() == categoryWikiNames.sort()
    }

    def getAllProfilesXml(version) {
        def response = client.get(path: "/$version/profiles;full", contentType: XML)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        def allProfiles = response.data.Profiles.Profile
        assert allProfiles.size() == profileUids.size()
        assert allProfiles.@uid*.text().sort() == profileUids.sort()

        def nameList = []
        def wikiNameList = []
        allProfiles.each { profile -> profile.Categories.Category.each { nameList.add(it.Name.text()); wikiNameList.add(it.WikiName.text()) } }
        assert categoryNames.sort() == nameList.sort()
        assert wikiNameList.sort() == categoryWikiNames.sort()
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
        def response = client.get(path: "/$version/profiles", query: [resultStart:'1', resultLimit: '1'], contentType: JSON)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.profiles.size() == 1
    }

    def getSomeProfilesXml(version) {
        def response = client.get(path: "/$version/profiles", query: [resultStart:'1', resultLimit: '1'], contentType: XML)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        def profiles = response.data.Profiles.Profile
        assert profiles.size() == 1
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
            setOtherUser()
            try {
                client.get(path: "/$version/profiles/UCP4SKANF6CS", contentType: JSON)
                fail 'Expected 403'
            } catch (HttpResponseException e) {
                def response = e.response
                assert response.status == CLIENT_ERROR_FORBIDDEN.code
                assert response.data.status.code == CLIENT_ERROR_FORBIDDEN.code
                assert response.data.status.name == 'Forbidden'
            }
        }
    }
}
