import groovyx.net.http.HttpResponseException
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
          '245CBD734419', '245CBD734420', '15AC6CF74915', 'DF717F2CB5CB']

  static def categoryUidsExcEcoinvent = [
          'CD310BEBAC52', 'BBA3AC3E795E', '427DFCC65E52', '3FE23FDC8CEA', 'F27BF795BB04', '54C8A44254AA',
          '75AD9B83B7BF', '319DDB5EC18E', '4BD595E1873A', '3C03A03B5F3A', '99B121BB416C', '066196F049DD',
          'E71CA2FCFFEA', 'AA59F9613F2A', 'D9289C55E595', '3035D381872B', '588527FFBC5F', '245CBD734418',
          '245CBD734419', '245CBD734420', '15AC6CF74915', 'DF717F2CB5CB']

  static def categoryNames = [
          'Root', 'Home', 'Appliances', 'Computers', 'Generic', 'Cooking', 'Entertainment',
          'Generic', 'Kitchen', 'Generic', 'Business', 'Energy', 'Electricity', 'US', 'Subregion', 'Waste',
          'LCA', 'Ecoinvent', 'chemicals', 'inorganics', 'chlorine, gaseous, diaphragm cell, at plant',
          'chlorine, gaseous, diaphragm cell, at plant', 'Benchmark', 'CO2 Benchmark', 'CO2 Benchmark Two', 'CO2 Benchmark Child', 'Embodied', 'Clm']

  static def categoryNamesExcEcoinvent = [
          'Root', 'Home', 'Appliances', 'Computers', 'Generic', 'Cooking',
          'Entertainment', 'Generic', 'Kitchen', 'Generic', 'Business', 'Energy',
          'Electricity', 'US', 'Subregion', 'Waste',
          'Benchmark', 'CO2 Benchmark', 'CO2 Benchmark Two', 'CO2 Benchmark Child', 'Embodied', 'Clm']

  static def categoryWikiNames = [
          'Root', 'Home', 'Appliances', 'Computers', 'Computers_generic', 'Cooking', 'Entertainment',
          'Entertainment_generic', 'Kitchen', 'Kitchen_generic',
          'Business', 'Business_energy', 'Electricity_by_Country', 'Energy_US', 'US_Egrid', 'Waste',
          'LCA', 'Ecoinvent', 'Ecoinvent_chemicals', 'Ecoinvent_chemicals_inorganics',
          'Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant',
          'Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant_UPR_RER_kg',
          'Benchmarking', 'CO2_Benchmark', 'CO2_Benchmark_Two', 'CO2_Benchmark_Child', 'Embodied', 'CLM_food_life_cycle_database']

  static def categoryWikiNamesExcEcoinvent = [
          'Root', 'Home', 'Appliances', 'Computers', 'Computers_generic', 'Cooking',
          'Entertainment', 'Entertainment_generic', 'Kitchen', 'Kitchen_generic',
          'Business', 'Business_energy', 'Electricity_by_Country', 'Energy_US', 'US_Egrid', 'Waste',
          'Benchmarking', 'CO2_Benchmark', 'CO2_Benchmark_Two', 'CO2_Benchmark_Child', 'Embodied', 'CLM_food_life_cycle_database']

  /**
   * Tests for creation, fetch and deletion of a Data Category using JSON responses.
   *
   * Create a new Data Category by POSTing to '/categories'.
   *
   * Supported parameters are:
   *
   * <ul>
   * <li>name
   * <li>path
   * <li>wikiName
   * <li>wikiDoc
   * <li>provenance
   * <li>authority
   * <li>history - since 3.3.0.
   * <li>dataCategory - since 3.3.0.
   * </ul>
   *
   * NOTE: For detailed rules on these parameters see the validation tests below.
   *
   * Delete (TRASH) a Data Category by sending a DELETE request to '/categories/{UID|wikiName}' (since 3.3.0).
   */
  @Test
  void createCategoryJson() {
    setAdminUser();
    // Create a DataCategory.
    def responsePost = client.post(
            path: '/3.3/categories',
            body: [
                    dataCategory: 'Root',
                    path: 'testPath',
                    name: 'Test Name',
                    wikiName: 'Test_Wiki_Name'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePost.status
    // Get the new DataCategory.
    def responseGet = client.get(
            path: '/3.3/categories/Test_Wiki_Name',
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'application/json', responseGet.contentType;
    assertTrue responseGet.data instanceof net.sf.json.JSON;
    assertEquals 'OK', responseGet.data.status;
    assertEquals "Test Name", responseGet.data.category.name
    assertEquals "Test_Wiki_Name", responseGet.data.category.wikiName
    // Then delete it
    def responseDelete = client.delete(path: '/3.3/categories/Test_Wiki_Name');
    assertEquals 200, responseDelete.status;
    // We should get a 404 here
    try {
      client.get(path: '/3.3/categories/Test_Wiki_Name');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }

  /**
   * Tests fetching a Data Category by wikiName using JSON responses.
   *
   * Fetch a Data Category by sending a GET request to '/categories/{UID|wikiName}'.
   *
   * Data Category GET requests support the following query parameters to filter the results.
   *
   * <ul>
   * <li>uid -
   * <li>name - Include
   * <li>path
   * <li>fullPath
   * <li>wikiName
   * <li>wikiDoc
   * <li>provenance
   * <li>authority
   * <li>parentUid - Include Data Categories whose parent matches the UID.
   * <li>parentWikiName - Include Data Categories whose parent matches the wikiName.
   * <li>itemDefinitionUid - Include Data Categories associated with matching Item Definitions by UID.
   * <li>itemDefinitionName - Include Data Categories associated with matching Item Definitions by name.
   * <li>tags - Include Data Categories tagged with tags in this expression.
   * <li>excTags - Don't include Data Categories tagged with tags in this expression.
   * <li>resultStart - Zero-based starting index offset to support result-set 'pagination'.
   * <li>resultLimit - Limit the number of entries in the result-set.
   * </ul>
   *
   * Data Category GET requests support the following matrix parameters to modify the response.
   *
   * <ul>
   * <li>full - include all values.
   * <li>audit - include the status, created and modified values.
   * <li>path - include the path and fullPath values.
   * <li>parent - include the parentUid and parentWikiName values.
   * <li>authority - include the authority value.
   * <li>history - include the history value (since 3.3.0).
   * <li>wikiDoc - include the wikiDoc value.
   * <li>provenance - include the provenance value.
   * <li>itemDefinition - include the UID and name of the associated Item Definition (if linked).
   * <li>tags - include a collection of associated tags.
   * </ul>
   */
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

  /**
   * Tests fetching a Data Category by UID using JSON responses.
   */
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

  /**
   * Tests fetching a previously deleted Data Category by wikiName using JSON responses.
   */
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

  /**
   * Tests fetching a previously deleted Data Category by UID using JSON responses.
   */
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

  /**
   * Tests fetching a Data Category, whose parent has been deleted, by UID using JSON responses.
   */
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

  /**
   * Tests fetching a non-existent Data Category by wikiName.
   */
  @Test
  void getMissingCategoryByWikiName() {
    try {
      client.get(path: '/3.1/categories/Wibble')
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }

  /**
   * Tests getting a list of categories using JSON responses.
   *
   * The same matrix parameters described above are supported.
   */
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

  /**
   * Tests getting a list of categories using JSON responses with some categories excluded by tag.
   */
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

  /**
   * Tests getting a list of categories using XML responses.
   */
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

  /**
   * Tests that a Data Category can be updated with valid values.
   *
   * Update a Data Category by sending a PUT request to '/categories/{UID|wikiName}'.
   *
   * NOTE: For detailed rules on these parameters see the validation tests below.
   */
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
                    'history': 'New History.',
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
    assertEquals 'New History.', responseGet.data.category.history;
    assertEquals 'New WikiDoc.', responseGet.data.category.wikiDoc;
  }

  /**
   * Tests that multiple invalid Data Categories fields cause the expected 400 invalid response. Tests the
   * transaction rollback capability.
   */
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

  /**
   * Tests the validation rules for the Data Category name field.
   *
   * The rules are as follows:
   *
   * <ul>
   * <li>Mandatory.
   * <li>Duplicates are allowed.
   * <li>No longer than 255 chars.
   * <li>Must not be empty.
   * </ul>
   */
  @Test
  void updateWithInvalidName() {
    setAdminUser();
    updateCategoryFieldJson('name', 'empty', '');
    updateCategoryFieldJson('name', 'short', 'a');
    updateCategoryFieldJson('name', 'long', String.randomString(256));
  }

  /**
   * Tests the validation rules for the Data Category path field.
   *
   * The rules are as follows:
   *
   * <ul>
   * <li>Mandatory.
   * <li>Unique on lower case of entire string amongst peer Data Categories (those at same level in hierarchy).
   * <li>Intended to look like this: 'a_data_category_path' or 'apath' or 'anumberpath3'.
   * <li>Must match this regular expression: "^[a-zA-Z0-9_\\-]*$"
   * <li>Numbers and letters only, any case.
   * <li>No special character except underscores ('_') and hyphens ('-').
   * <li>No white space.
   * <li>No longer than 255 characters.
   * </ul>
   *
   * TODO: Although it is legal for a Data Category to have a path that is empty in practice this is not allowed as
   * the root Data Category is the only one with an empty path. See PL-9542.
   */
  @Test
  void updateWithInvalidPath() {
    updateCategoryFieldJson('path', 'long', String.randomString(256));
    updateCategoryFieldJson('path', 'format', 'n o t v a l i d');
    updateCategoryFieldJson('path', 'duplicate', 'co2benchmark2');
  }

  /**
   * Tests the validation rules for the Data Category wikiName field.
   *
   * The rules are as follows:
   *
   * <ul>
   * <li>Mandatory.
   * <li>Unique on lower case of entire string amongst *all* Data Categories.
   * <li>Intended to look like this: 'Light_Goods_Freight_Defra'
   * <li>Must match this regular expression: "^[a-zA-Z0-9_\\-]*$"
   * <li>Numbers and letters only, any case.
   * <li>No special character except underscores ('_') and hyphens ('-').
   * <li>No white space.
   * <li>No longer than 255 characters.
   * <li>No shorter than 3 characters.
   * </ul>
   */
  @Test
  void updateWithInvalidWikiName() {
    updateCategoryFieldJson('wikiName', 'empty', '');
    updateCategoryFieldJson('wikiName', 'short', '12');
    updateCategoryFieldJson('wikiName', 'long', String.randomString(256));
    updateCategoryFieldJson('wikiName', 'duplicate', 'CLM_food_life_cycle_database');
  }

  /**
   * Tests the validation rules for the Data Category metadata field (wikiDoc, provenance, authority, history).
   *
   * The rules are as follows:
   *
   * <ul>
   * <li>All are optional.
   * <li>wikiDoc and history (since 3.3.0) must be no longer than 32767 characters.
   * <li>provenance and authority must be no longer than 255 characters.
   * </ul>
   */
  @Test
  void updateWithInvalidMetadata() {
    updateCategoryFieldJson('wikiDoc', 'long', String.randomString(32768));
    updateCategoryFieldJson('provenance', 'long', String.randomString(256));
    updateCategoryFieldJson('authority', 'long', String.randomString(256));
    updateCategoryFieldJson('history', 'long', String.randomString(32768));
  }

  /**
   * Tests the validation rules for setting the parent Data Category of a Data Category.
   *
   * The rules are as follows:
   *
   * <ul>
   * <li>The parent Data Category must not be the same as the target Data Category.
   * <li>The parent Data Category must not be a child of the target Data Category in the hierarchy.
   * <li>All Data Categories must have a parent Data Category (except the root Data Category).
   * <li>The value can be either a UID or a wikiName. Either must be of a valid format.
   * </ul>
   *
   * TODO: Rules handling updates to the root Data Category have not yet been coded up. See PL-9542.
   */
  @Test
  void updateWithInvalidParentCategory() {
    updateCategoryFieldJson('dataCategory', 'same', '245CBD734418');
    updateCategoryFieldJson('dataCategory', 'child', 'CO2_Benchmark_Child');
    updateCategoryFieldJson('dataCategory', 'empty', 'XXX');
  }

  /**
   * Submits a single Data Category field value and tests the result. An error is expected.
   *
   * @param field that is being updated
   * @param code expected upon error
   * @param value to submit
   */
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
      assertTrue([code] == response.data.validationResult.errors.collect {it.code});
    }
  }
}