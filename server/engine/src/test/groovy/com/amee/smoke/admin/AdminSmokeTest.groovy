package com.amee.smoke.admin

import com.amee.smoke.BaseSmokeTest
import org.junit.Test
import groovyx.net.http.HttpResponseException
import static org.junit.Assert.*
import org.junit.Ignore

class AdminSmokeTest extends BaseSmokeTest {

    @Ignore("Ignore until 2.18.1 is deployed.")
    @Test
    void viewUsers() {
        try {
            client.get(path: '/admin/users')
            fail 'Should have thrown exception'
        } catch (HttpResponseException e) {
            assertEquals 403, e.response.status
        }

        setRootUser()
        def response = client.get(path: '/admin/users')
        assertResponseOk(response)
    }

    @Ignore("Ignore until 2.18.1 is deployed.")
    @Test
    void viewGroups() {
        try {
            client.get(path: '/admin/groups')
            fail 'Should have thrown exception'
        } catch (HttpResponseException e) {
            assertEquals 403, e.response.status
        }

        setRootUser()
        def response = client.get(path: '/admin/groups')
        assertResponseOk(response)
    }

}
