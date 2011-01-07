import groovyx.net.http.HttpResponseException
import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

class CategoryIT extends BaseApiTest {

  // See import.sql
  static def categoryUids = [
          'CD310BEBAC52', 'BBA3AC3E795E', '427DFCC65E52', '3FE23FDC8CEA', 'F27BF795BB04', '54C8A44254AA',
          '75AD9B83B7BF', '319DDB5EC18E', '4BD595E1873A', '3C03A03B5F3A', '99B121BB416C', '066196F049DD',
          'E71CA2FCFFEA', 'AA59F9613F2A', 'D9289C55E595', '3035D381872B', '25C9445D11E6', 'FD093356A2F9',
          '23B9564ED6AA', '77D7394D46E5', '00383F6EA807', '4304B67B1D19', '588527FFBC5F', '245CBD734418',
          '245CBD734419', '15AC6CF74915', 'DF717F2CB5CB']

  static def categoryUidsExcEcoinvent = [
          'CD310BEBAC52', 'BBA3AC3E795E', '427DFCC65E52', '3FE23FDC8CEA', 'F27BF795BB04', '54C8A44254AA',
          '75AD9B83B7BF', '319DDB5EC18E', '4BD595E1873A', '3C03A03B5F3A', '99B121BB416C', '066196F049DD',
          'E71CA2FCFFEA', 'AA59F9613F2A', 'D9289C55E595', '3035D381872B', '588527FFBC5F', '245CBD734418',
          '245CBD734419', '15AC6CF74915', 'DF717F2CB5CB']

  static def categoryNames = [
          'Root', 'Home', 'Appliances', 'Computers', 'Generic', 'Cooking', 'Entertainment',
          'Generic', 'Kitchen', 'Generic', 'Business', 'Energy', 'Electricity', 'US', 'Subregion', 'Waste',
          'LCA', 'Ecoinvent', 'chemicals', 'inorganics', 'chlorine, gaseous, diaphragm cell, at plant',
          'chlorine, gaseous, diaphragm cell, at plant', 'Benchmark', 'CO2 Benchmark', 'CO2 Benchmark Two', 'Embodied', 'Clm']

  static def categoryNamesExcEcoinvent = [
          'Root', 'Home', 'Appliances', 'Computers', 'Generic', 'Cooking',
          'Entertainment', 'Generic', 'Kitchen', 'Generic', 'Business', 'Energy',
          'Electricity', 'US', 'Subregion', 'Waste',
          'Benchmark', 'CO2 Benchmark', 'CO2 Benchmark Two', 'Embodied', 'Clm']

  static def categoryWikiNames = [
          'Root', 'Home', 'Appliances', 'Computers', 'Computers_generic', 'Cooking', 'Entertainment',
          'Entertainment_generic', 'Kitchen', 'Kitchen_generic',
          'Business', 'Business_energy', 'Electricity_by_Country', 'Energy_US', 'US_Egrid', 'Waste',
          'LCA', 'Ecoinvent', 'Ecoinvent_chemicals', 'Ecoinvent_chemicals_inorganics',
          'Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant',
          'Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant_UPR_RER_kg',
          'Benchmarking', 'CO2_Benchmark', 'CO2_Benchmark_Two', 'Embodied', 'CLM_food_life_cycle_database']

  static def categoryWikiNamesExcEcoinvent = [
          'Root', 'Home', 'Appliances', 'Computers', 'Computers_generic', 'Cooking',
          'Entertainment', 'Entertainment_generic', 'Kitchen', 'Kitchen_generic',
          'Business', 'Business_energy', 'Electricity_by_Country', 'Energy_US', 'US_Egrid', 'Waste',
          'Benchmarking', 'CO2_Benchmark', 'CO2_Benchmark_Two', 'Embodied', 'CLM_food_life_cycle_database']

  @Test
  @Ignore("POST not implemented in API")
  void createCategory() {
    client.contentType = JSON
    def response = client.post(
            path: '/3.0/categories/CATEGORY1',
            body: [wikiName: 'testWikiName'],
            requestContentType: URLENC)
    assertEquals 201, response.status
  }

  @Test
  void getCategoryByWikiNameJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/Kitchen_generic;audit')
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals "3C03A03B5F3A", response.data.category.uid
    assertEquals "ACTIVE", response.data.category.status
    assertEquals "Generic", response.data.category.name
    assertEquals "Kitchen_generic", response.data.category.wikiName
  }

  @Test
  void getCategoryByUidJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/3C03A03B5F3A;audit')
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals "3C03A03B5F3A", response.data.category.uid
    assertEquals "ACTIVE", response.data.category.status
    assertEquals "Generic", response.data.category.name
    assertEquals "Kitchen_generic", response.data.category.wikiName
  }

  @Test
  void getTrashedCategoryByWikiNameJson() {
    setRootUser();
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/Kitchen_generic;audit',
            query: ['status': 'trash'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals "3C03A03B5F2A", response.data.category.uid
    assertEquals "TRASH", response.data.category.status
    assertEquals "Generic", response.data.category.name
    assertEquals "Kitchen_generic", response.data.category.wikiName
  }

  @Test
  void getMissingCategoryByWikiName() {
    try {
      client.get(path: '/3.1/categories/Wibble')
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }

  @Test
  void getTrashedCategoryByUidJson() {
    setRootUser();
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/3C03A03B5F1A;audit',
            query: ['status': 'trash'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals "3C03A03B5F1A", response.data.category.uid
    assertEquals "TRASH", response.data.category.status
    assertEquals "Generic", response.data.category.name
    assertEquals "Kitchen_generic", response.data.category.wikiName
  }

  @Test
  void getInferredTrashedCategoryByUidJson() {
    setRootUser();
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/3C03A03B5F4A;audit',
            query: ['status': 'trash'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals "3C03A03B5F4A", response.data.category.uid
    assertEquals "ACTIVE", response.data.category.status
    assertEquals "Child", response.data.category.name
    assertEquals "Kitchen_generic_child", response.data.category.wikiName
  }

  @Test
  void getCategoriesJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.0/categories')
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertFalse response.data.resultsTruncated
    assertEquals categoryUids.size(), response.data.categories.size()
    assert categoryUids.sort() == response.data.categories.collect {it.uid}.sort()
    assert categoryNames.sort() == response.data.categories.collect {it.name}.sort()
    assert categoryWikiNames.sort() == response.data.categories.collect {it.wikiName}.sort()
  }

  @Test
  void getCategoriesWithTagsExcludedJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.0/categories', query: ['excTags': 'ecoinvent'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertFalse response.data.resultsTruncated
    assertEquals categoryUidsExcEcoinvent.size(), response.data.categories.size()
    assert categoryUidsExcEcoinvent.sort() == response.data.categories.collect {it.uid}.sort()
    assert categoryNamesExcEcoinvent.sort() == response.data.categories.collect {it.name}.sort()
    assert categoryWikiNamesExcEcoinvent.sort() == response.data.categories.collect {it.wikiName}.sort()
  }

  @Test
  void getCategoriesXml() {
    client.contentType = XML
    def response = client.get(path: '/3.0/categories')
    assertEquals 200, response.status
    assertEquals 'application/xml', response.contentType
    assertEquals 'OK', response.data.Status.text()
    assertEquals 'false', response.data.Categories.@truncated.text()
    def allCategories = response.data.Categories.Category
    assertEquals categoryUids.size(), allCategories.size()
    assert categoryUids.sort() == allCategories.@uid*.text().sort()
    assert categoryNames.sort() == allCategories.Name*.text().sort()
    assert categoryWikiNames.sort() == allCategories.WikiName*.text().sort()
  }

  @Test
  void filterByAuthorityJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.0/categories',
            query: ['authority': 'enterprise'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals 14, response.data.categories.size()
  }

  @Test
  void filterByAuthorityXml() {
    client.contentType = XML
    def response = client.get(path: '/3.0/categories',
            query: ['authority': 'enterprise'])
    assertEquals 200, response.status
    assertEquals 'application/xml', response.contentType
    assertEquals 'OK', response.data.Status.text()
    def allCategories = response.data.Categories.Category
    assertEquals 14, allCategories.size()
  }

  @Test
  void filterByTagsJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.0/categories',
            query: ['tags': 'electrical'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals 4, response.data.categories.size()
  }

  @Test
  void filterByTagsXml() {
    client.contentType = XML
    def response = client.get(path: '/3.0/categories',
            query: ['tags': 'electrical'])
    assertEquals 200, response.status
    assertEquals 'application/xml', response.contentType
    assertEquals 'OK', response.data.Status.text()

    def allCategories = response.data.Categories.Category
    assertEquals 4, allCategories.size()
  }

  @Test
  void filterByPathJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.0/categories',
            query: ['fullPath': '/home/appliances/*'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals 7, response.data.categories.size()
  }

  @Test
  void filterByPathXml() {
    client.contentType = XML
    def response = client.get(path: '/3.0/categories',
            query: ['fullPath': '/home/appliances/*'])
    assertEquals 200, response.status
    assertEquals 'application/xml', response.contentType
    assertEquals 'OK', response.data.Status.text()
    def allCategories = response.data.Categories.Category
    assertEquals 7, allCategories.size()
  }

  @Test
  void updateCategoryJson() {
    setAdminUser();
    // 1) Do the update (CO2_Benchmark).
    def responsePut = client.put(
            path: '/3.2/categories/245CBD734418',
            body: [
                    'path': 'newPath',
                    'name': 'New Name',
                    'wikiName': 'New_Wiki_Name',
                    'provenance': 'New Provenance.',
                    'authority': 'New Authority.',
                    'wikiDoc': 'New WikiDoc.'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePut.status;
    // 2) Check values have been updated (CO2_Benchmark).
    def responseGet = client.get(
            path: '/3.2/categories/245CBD734418;full',
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'application/json', responseGet.contentType;
    assertTrue responseGet.data instanceof net.sf.json.JSON;
    assertEquals 'OK', responseGet.data.status;
    assertEquals 'newPath', responseGet.data.category.path;
    assertEquals 'New Name', responseGet.data.category.name;
    assertEquals 'New_Wiki_Name', responseGet.data.category.wikiName;
    assertEquals 'New Provenance.', responseGet.data.category.provenance;
    assertEquals 'New Authority.', responseGet.data.category.authority;
    assertEquals 'New WikiDoc.', responseGet.data.category.wikiDoc;
  }

  @Test
  void updateInvalidCategoryJson() {
    setAdminUser();
    try {
      // 1) Do the update (CO2_Benchmark).
      client.put(
              path: '/3.2/categories/245CBD734418',
              body: [
                      'wikiName': 'CLM_food_life_cycle_database', // duplicate
                      'provenance': String.randomString(256), // too long
                      'wikiDoc': String.randomString(32768)], // too long
              requestContentType: URLENC,
              contentType: JSON);
      fail 'Response status code should have been 400 (' + field + ', ' + code + ').';
    } catch (HttpResponseException e) {
      // Handle error response containing a ValidationResult.
      def response = e.response;
      assertEquals 400, response.status;
      assertEquals 'application/json', response.contentType;
      assertTrue response.data instanceof net.sf.json.JSON;
      assertEquals 'INVALID', response.data.status;
    }
  }

  @Test
  void updateInvalidCategoryFields() {
    setAdminUser();
    updateCategoryFieldJson('name', 'empty', '');
    updateCategoryFieldJson('name', 'short', 'a');
    updateCategoryFieldJson('name', 'long', String.randomString(256));
    updateCategoryFieldJson('path', 'long', String.randomString(256));
    updateCategoryFieldJson('path', 'format', 'n o t v a l i d');
    updateCategoryFieldJson('path', 'duplicate', 'co2benchmark2');
    updateCategoryFieldJson('wikiName', 'empty', '');
    updateCategoryFieldJson('wikiName', 'short', 'a');
    updateCategoryFieldJson('wikiName', 'long', String.randomString(256));
    updateCategoryFieldJson('wikiName', 'duplicate', 'CLM_food_life_cycle_database');
    updateCategoryFieldJson('wikiDoc', 'long', String.randomString(32768));
    updateCategoryFieldJson('provenance', 'long', String.randomString(256));
    updateCategoryFieldJson('authority', 'long', String.randomString(256));
  }

  void updateCategoryFieldJson(field, code, value) {
    try {
      // Create form body.
      def body = [:];
      body[field] = value;
      // Update Category (CO2_Benchmark).
      client.put(
              path: '/3.2/categories/245CBD734418',
              body: body,
              requestContentType: URLENC,
              contentType: JSON);
      fail 'Response status code should have been 400 (' + field + ', ' + code + ').';
    } catch (HttpResponseException e) {
      // Handle error response containing a ValidationResult.
      def response = e.response;
      assertEquals 400, response.status;
      assertEquals 'application/json', response.contentType;
      assertTrue response.data instanceof net.sf.json.JSON;
      assertEquals 'INVALID', response.data.status;
      assertTrue([field] == response.data.validationResult.errors.collect {it.field});
      assertTrue([value] == response.data.validationResult.errors.collect {it.value});
      assertTrue([code] == response.data.validationResult.errors.collect {it.code});
    }
  }
}