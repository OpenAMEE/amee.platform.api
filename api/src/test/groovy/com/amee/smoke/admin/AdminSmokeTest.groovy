package com.amee.smoke.admin

import com.amee.smoke.BaseSmokeTest
import org.junit.Test
import groovyx.net.http.HttpResponseException
import static org.junit.Assert.*
import org.junit.Ignore

class AdminSmokeTest extends BaseSmokeTest {

    @Test
    void viewUsers() {
        try {
            client.get(path: '/admin/users')
            fail 'Should have thrown exception'
        } catch (HttpResponseException e) {
            assertEquals 403, e.response.status
        }

        setAdminUser()
        def response = client.get(path: '/admin/users')
        assertResponseOk(response)
    }

    @Test
    void viewGroups() {
        try {
            client.get(path: '/admin/groups')
            fail 'Should have thrown exception'
        } catch (HttpResponseException e) {
            assertEquals 403, e.response.status
        }

        setAdminUser()
        def response = client.get(path: '/admin/groups')
        assertResponseOk(response)
    }

}
