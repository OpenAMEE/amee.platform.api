package com.amee.integration

import org.junit.Test

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.restlet.data.Status.SUCCESS_OK

/**
 * Tests for the Data Category drill down API. This API has been available since version 3.3.
 */
class DrillDownIT extends BaseApiTest {

    /**
     * Tests drill down GETs against a Data Category using JSON responses.
     *
     * The parameters this resource supports are available in the representations as part of the
     * choices node in the representation.
     *
     * The purpose and behaviour of this resource is similar to the AMEE API V1 / V2 drill down resource. For
     * some background see http://my.amee.com/developers/wiki/DrillDown. The main difference is that the V3
     * implementation is quicker (backed by Lucene search) and the representations are tidier.
     */
    @Test
    void canDrillDownJson() {
        doDrillDownJson([nothing_to_see: 'here'], 5, 'fuel')
        doDrillDownJson([fuel: 'gas'], 21, 'numberOfPeople')
        doDrillDownJson([fuel: 'gas', numberOfPeople: '5'], 1, 'uid')
    }

    def doDrillDownJson(query, choicesSize, choicesName) {
        versions.each { version -> doDrillDownJson(query, choicesSize, choicesName, version) }
    }

    def doDrillDownJson(query, choicesSize, choicesName, version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/$version/categories/Cooking/drill",
                    query: query,
                    contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.drill.choices.values.size() == choicesSize
            assert response.data.drill.choices.name == choicesName
        }
    }

    /**
     * Tests drill down GETs against a Data Category using XML responses.
     *
     * See notes for canDrillDownJson above.
     */
    @Test
    void canDrillDownXml() {
        doDrillDownXml([nothing_to_see: 'here'], 5, 'fuel')
        doDrillDownXml([fuel: 'gas'], 21, 'numberOfPeople')
        doDrillDownXml([fuel: 'Gas', numberOfPeople: '5'], 1, 'uid')
    }

    def doDrillDownXml(query, choicesSize, choicesName) {
        versions.each { version -> doDrillDownXml(query, choicesSize, choicesName, version) }
    }

    def doDrillDownXml(query, choicesSize, choicesName, version) {
        if (version >= 3.3) {
            def response = client.get(
                    path: "/$version/categories/Cooking/drill",
                    query: query,
                    contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.Drill.Choices.Values.Value.size() == choicesSize
            assert response.data.Drill.Choices.Name.text() == choicesName
        }
    }

    /**
     * Tests a UID is retrieved when one or more of the drill downs has an empty value.
     * Should auto-select the next choice if there is only one.
     */
    @Test
    void emptyDrillDownValue() {
        versions.each { version -> emptyDrillDownValue(version) }
    }

    def emptyDrillDownValue(version) {
        if (version >= 3.3) {
            def response = client.get(
                path: "/$version/categories/ICE_v2_by_mass/drill",
                query: [type: 'General'],
                contentType: JSON);
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.drill.choices.values.size() == 1
            assert response.data.drill.choices.name == 'uid'
            assert response.data.drill.choices.values[0] == 'R80Z2HQRYRR6'
        }
    }
}
