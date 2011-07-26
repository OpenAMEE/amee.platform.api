package com.amee.smoke

import groovyx.net.http.RESTClient
import org.junit.Before
import static groovyx.net.http.ContentType.JSON
import static org.junit.Assert.*

/**
 * A base class for API integration tests.
 *
 * The Restlet container is started.
 */
abstract class BaseSmokeTest {
    public static final double DELTA = 0.000001
    def config
    def client

    @Before
    void setUp() {

        // Get the HTTP client
        config = new ConfigSlurper().parse(getClass().getResource("/smoke.properties"))
        client = new RESTClient("${config.api.protocol}://${config.api.host}:${config.api.port}")

        // Use JSON for the smoke tests.
        client.setContentType JSON

        // Set standard user as default.
        setStandardUser();
    }

    def setStandardUser() {
        client.auth.basic config.api.standard.user, config.api.standard.password
    }

    def setAdminUser() {
        client.auth.basic config.api.admin.user, config.api.admin.password
    }

    def setRootUser() {
        client.auth.basic config.api.root.user, config.api.root.password
    }

    def setEcoinventUser() {
        client.auth.basic config.api.ecoinvent.user, config.api.ecoinvent.password
    }

    def assertResponseOk(response) {
        assertEquals 200, response.status
        assertEquals 'OK', response.data.status
    }
}