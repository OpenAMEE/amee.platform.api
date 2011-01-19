import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail

/**
 * Tests for the Data Category API for ecoinvent data.
 */
class EcospoldIT extends BaseApiTest {

  @Test
  void getEcospoldCategory() {

    setEcoinventUser();

    // We parse the response as XML but request x.ecospold+xml
    def response = client.get(path: '/3/categories/Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant_UPR_RER_kg',
            contentType: XML,
            headers: [Accept: 'application/x.ecospold+xml'])

    assertEquals 200, response.status
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

  @Test
  void getNonEcospoldCategory() {
    try {
      client.get(path: '/3/categories/F27BF795BB04',
              contentType: XML,
              headers: [Accept: 'application/x.ecospold+xml'])
      fail 'Expected 415'
    } catch (HttpResponseException e) {
      def response = e.response;
      assertEquals 415, response.status
    }
  }

  @Test
  void getEcospoldCategoryNotAuthorized() {
    try {
      client.get(path: '/3/categories/Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant_UPR_RER_kg',
              contentType: XML,
              headers: [Accept: 'application/x.ecospold+xml'])
      fail 'Expected 403'
    } catch (HttpResponseException e) {
      def response = e.response;
      assertEquals 403, response.status;
    }
  }
}