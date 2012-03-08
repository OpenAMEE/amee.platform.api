package com.amee.smoke.v3

import static groovyx.net.http.ContentType.URLENC

import org.junit.Ignore
import org.junit.Test

import com.amee.smoke.BaseSmokeTest

class ProfileSmokeTest extends BaseSmokeTest {

    private static final String note = "A man, a plan, a canal - Panama!"

    @Ignore("v3 Profile functionality not yet live")
    @Test
    void testProfileRequests(){
        // Create a profile
        def response = client.post(
            path: "/3/profiles",
            body: ["profile": "true"],
            requestContentType: URLENC)

        assert response.status == 201
        assert response.data.status == 'OK'
        def profileUid = response.data.entity.uid
        assert profileUid != null
        assert response.data.location.split("/")[3] == profileUid

        // Get the profile
        response = client.get(path: "/3/profiles/${profileUid};full")
        assertResponseOk(response)
        assert response.data.profile.uid == profileUid

        // Create a profile item
        response = client.post(
            path: "/3/profiles/${profileUid}/items",
            body: [
                "dataItemUid": config.uid.item.IPCC_military_aircraft.a10,
                "flightDuration": 1,
                "note": note],
            requestContentType: URLENC)

        assert response.status == 201
        assert response.data.status == 'OK'
        assert response.headers['Location'] != null
        assert response.headers['Location'].value != null
        def location = response.headers['Location'].value
        def profileItemUid = location.split("/")[7]
        assert profileItemUid != null
        assert response.data.location.split("/")[5] == profileItemUid

        // Get the profile item
        response = client.get(path: "/3/profiles/${profileUid}/items/${profileItemUid};full")
        assertResponseOk(response)
        assert response.data.item.note == note

        // Update the profile item
        response = client.put(
            path: "/3/profiles/${profileUid}/items/${profileItemUid}",
            body: ["note" : ""],
            requestContentType: URLENC)

        assertResponseOk(response)
        assert response.data.entity.uid == profileItemUid

        // Check the updated profile item
        response = client.get(path: "/3/profiles/${profileUid}/items/${profileItemUid}")
        assertResponseOk(response)
        assert response.data.item.note != note

        // Delete the profile item
        response = client.delete(path: "/3/profiles/${profileUid}/items/${profileItemUid}")
        assertResponseOk(response)
        assert response.data.entity.uid == profileItemUid

        // Check the profile item has been deleted
        try{
            client.get(path: "/3/profiles/${profileUid}/items/${profileItemUid}")
            fail("Expected exception")
        }catch(e){
            assert e.response.status == 404
        }
    }
}