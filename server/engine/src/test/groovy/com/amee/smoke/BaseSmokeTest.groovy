package com.amee.smoke

import groovyx.net.http.RESTClient
import org.junit.Before
import static groovyx.net.http.ContentType.JSON
import static org.junit.Assert.assertEquals

/**
 * A base class for API integration tests.
 *
 * The Restlet container is started.
 */
abstract class BaseSmokeTest {
    public static final double DELTA = 0.000001
    def config
    def client
    def apiVersion

    @Before
    void setUp() {

        // Get the HTTP client
        config = new ConfigSlurper().parse(getClass().getResource("/smoke.properties"))

        // V2 or V3 API?
        def packageName = this.getClass().getPackage().name.tokenize(".").last()
        switch (packageName) {
            case "v2":
                apiVersion = 2
                client = new RESTClient("${config.api.protocol}://${config.api.host.v2}:${config.api.port}")

                // Can't use the built-in auth handling as we don't send the WWW-Authenticate header in v2
                def auth = 'Basic ' + (config.api.standard.user + ':' + config.api.standard.password).bytes.encodeBase64().toString()
                client.headers.Authorization = auth
                break
            case "v3":
                apiVersion = 3
                client = new RESTClient("${config.api.protocol}://${config.api.host.v3}:${config.api.port}")
                break
        }



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

        if (apiVersion == 3) {
            assertEquals 'OK', response.data.status
        }
    }

}