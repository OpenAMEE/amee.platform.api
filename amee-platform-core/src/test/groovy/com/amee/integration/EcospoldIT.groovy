package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

/**
 * Tests for the Data Category API for ecoinvent data.
 */
@Ignore
class EcospoldIT extends BaseApiTest {

    @Test
    void getEcospoldCategory() {
        versions.each { version -> getEcospoldCategory(version) }
    }

    def getEcospoldCategory(version) {
        if (version >= 3.2) {

            setEcoinventUser()

            // We parse the response as XML but request x.ecospold+xml
            def response = client.get(path: "/${version}/categories/Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant_UPR_RER_kg",
                    contentType: XML,
                    headers: [Accept: 'application/x.ecospold+xml'])

            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/x.ecospold+xml', response.contentType

            assertEquals '2010-06-16T17:02:39', response.data.dataset.@timestamp.text()
            assertEquals '266', response.data.dataset.@number.text()

            assertEquals 'chemicals', response.data.dataset.metaInformation.processInformation.referenceFunction.@category.text()
            assertEquals 'inorganics', response.data.dataset.metaInformation.processInformation.referenceFunction.@subCategory.text()
            assertEquals 'chlorine, gaseous, diaphragm cell, at plant',
                    response.data.dataset.metaInformation.processInformation.referenceFunction.@name.text()
            assertEquals 'kg', response.data.dataset.metaInformation.processInformation.referenceFunction.@unit.text()

            assertEquals '2000-01', response.data.dataset.metaInformation.processInformation.timePeriod.startYearMonth.text()
            assertEquals 'de', response.data.dataset.metaInformation.processInformation.dataSetInformation.@localLanguageCode.text()

            assertEquals 'unknown',
                    response.data.dataset.metaInformation.modellingAndValidation.representativeness.@productionVolume.text()
            assertEquals 'Life Cycle Inventories of Chemicals',
                    response.data.dataset.metaInformation.modellingAndValidation.source.@title.text()
            assertEquals 'Passed',
                    response.data.dataset.metaInformation.modellingAndValidation.validation.@proofReadingDetails.text()

            assertEquals 2, response.data.dataset.metaInformation.administrativeInformation.person.size()

            assertEquals 31, response.data.dataset.flowData.exchange.size()
            assertEquals '007732-18-5', response.data.dataset.flowData.exchange[0].@CASNumber.text()
            assertEquals '0.0008816', response.data.dataset.flowData.exchange[0].@meanValue.text()
            assertEquals '4', response.data.dataset.flowData.exchange[0].inputGroup.text()
            assertEquals 'Should not have outputGroup element', '', response.data.dataset.flowData.exchange[0].outpuGroup.text()
            assertEquals 'Should not have outuputGroup attribute', '', response.data.dataset.flowData.exchange[0].@outpuGroup.text()

            assertEquals '4', response.data.dataset.flowData.exchange[15].outputGroup.text()
            assertEquals 'Should not have inputGroup element', '', response.data.dataset.flowData.exchange[15].inputGroup.text()
            assertEquals 'Should not have inputGroup attribute', '', response.data.dataset.flowData.exchange[15].@inputGroup.text()
        }
    }

    @Test
    void getNonEcospoldCategory() {
        versions.each { version -> getNonEcospoldCategory(version) }
    }

    def getNonEcospoldCategory(version) {
        if (version >= 3.3) {
            try {
                client.get(path: "/${version}/categories/F27BF795BB04",
                        contentType: XML,
                        headers: [Accept: 'application/x.ecospold+xml'])
                fail 'Expected 415'
            } catch (HttpResponseException e) {
                def response = e.response
                assertEquals CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE.code, response.status
            }
        }
    }

    @Test
    void getEcospoldCategoryNotAuthorized() {
        versions.each { version -> getEcospoldCategoryNotAuthorized(version) }
    }

    def getEcospoldCategoryNotAuthorized(version) {
        if (version >= 3.3) {
            try {
                client.get(path: "/${version}/categories/Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant_UPR_RER_kg",
                        contentType: XML,
                        headers: [Accept: 'application/x.ecospold+xml'])
                fail 'Expected 403'
            } catch (HttpResponseException e) {
                def response = e.response
                assertEquals CLIENT_ERROR_FORBIDDEN.code, response.status
            }
        }
    }
}