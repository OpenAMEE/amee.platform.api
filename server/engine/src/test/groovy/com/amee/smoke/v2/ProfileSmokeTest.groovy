package com.amee.smoke.v2

import com.amee.smoke.BaseSmokeTest
import org.junit.Test
import static groovyx.net.http.ContentType.URLENC
import static org.junit.Assert.*

class ProfileSmokeTest extends BaseSmokeTest {

    static def profileUid

    @Test
    void crud() {

        // Create a profile
        def response = client.post(
            path: "/profiles",
            body: ["profile": "true"],
            requestContentType: URLENC)

        // This post returns a 200 instead of 201.
        assertResponseOk(response)
        def profileUid = response.data.profile.uid
        assertTrue profileUid != null;

        // Create a profile item
        response = client.post(
            path: "/profiles/${profileUid}/transport/plane/specific/military/ipcc",
            body: ["dataItemUid": config.uid.item.IPCC_military_aircraft.a10, "flightDuration": 1],
            requestContentType: URLENC)

        assertEquals 201, response.status
        assertTrue response.headers['Location'] != null;
        assertTrue response.headers['Location'].value != null;
        def location = response.headers['Location'].value
        def profileItemUid = location.split('/')[10];
        assertTrue profileItemUid != null;

        // Get the created profile item
        response = client.get(path: "/profiles/${profileUid}/transport/plane/specific/military/ipcc/${profileItemUid}")
        assertResponseOk(response)

        // Delete the profile
        response = client.delete(path: "/profiles/${profileUid}")
        assertResponseOk(response)

        // Make sure it really is gone
        try {
            client.get(path: "profiles/${profileUid}")
            fail('Expected exception')
        } catch (ex) {
            assertEquals 404, ex.response.status
        }
    }
}
