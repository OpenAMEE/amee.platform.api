package com.amee.integration

import com.amee.domain.data.DataCategory
import groovyx.net.http.HttpResponseException
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import static org.restlet.data.Status.*

/**
 * Tests for the Data Category API.
 */
class CategoryIT extends BaseApiTest {

    // NOTE: Keep these lists up to date if you add new categories to import.sql.

    static def categoryNames = [
        'Root',
        'Home', 'Appliances', 'Computers', 'Generic', 'Cooking', 'Entertainment', 'Generic', 'Kitchen', 'Generic',
        'Business', 'Energy', 'Electricity', 'US', 'Greenhouse Gas Protocol methodology for US grid electricity', 'Waste',
        'Embodied', 'ICE Building Materials LCA', 'V2', 'Inventory of Carbon & Energy methodology for materials by mass',
        'Transport', 'Plane', 'Specific', 'Military', 'Ipcc',
        'Integration', 'Api', 'Item history test', 'Item history dimless test',
        'Greenhouse Gas Protocol methodology for international grid electricity']

    static def categoryWikiNames = [
        'Root',
        'Home', 'Appliances', 'Computers', 'Computers_generic', 'Cooking', 'Entertainment', 'Entertainment_generic', 'Kitchen', 'Kitchen_generic',
        'Business', 'Business_energy', 'Electricity_by_Country', 'Energy_US', 'US_Subregion_Electricity', 'Waste',
        'Embodied', 'ICE_Building_Materials_LCA', 'ICE_v2', 'ICE_v2_by_mass',
        'Transport', 'Plane', 'Specific_plane_transport', 'Specific_military_aircraft', 'IPCC_military_aircraft',
        'Integration', 'Api', 'Item_history_test', 'Item_history_dimless_test',
        'Greenhouse_Gas_Protocol_international_electricity']

    /**
     * Tests for creation, fetch and deletion of a Data Category using JSON responses.
     *
     * Create a new Data Category by POSTing to '/categories' (since 3.3.0).
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>path
     * <li>wikiName
     * <li>wikiDoc
     * <li>provenance
     * <li>authority
     * <li>history.
     * <li>dataCategory.
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Data Category by sending a DELETE request to '/categories/{UID|wikiName}' (since 3.3.0).
     */
    @Test
    void createCategoryJson() {
        versions.each { version -> createCategoryJson(version) }
    }

    def createCategoryJson(version) {
        if (version >= 3.3) {
            setAdminUser()

            // Create a DataCategory.
            def responsePost = client.post(
                    path: "/$version/categories",
                    body: [dataCategory: 'Root', path: 'testPath', name: 'Test Name', wikiName: 'Test_Wiki_Name'],
                    requestContentType: URLENC,
                    contentType: JSON)
            String location = responsePost.headers['Location'].value
            assert location.startsWith("${config.api.protocol}://${config.api.host}")
            String uid = location.split('/')[5]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Get the new DataCategory.
            def responseGet = client.get(
                    path: "/$version/categories/Test_Wiki_Name",
                    contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.category.wikiName == "Test_Wiki_Name"

            // Then delete it.
            def responseDelete = client.delete(path: "/$version/categories/Test_Wiki_Name")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // We should get a 404 here.
            try {
                client.get(path: "/$version/categories/Test_Wiki_Name")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
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
     * <li>uid, name, path, fullPath, wikiName, wikiDoc, provenance, authority, parentUid,
     *     parentWikiName, itemDefinitionUid, itemDefinitionName - Include Data Categories that match one or more of
     *     these fields. Query values can be Lucene expressions.
     * <li>tags - Include Data Categories tagged with tags matching this expression. This can be a comma separated
     *     list of tags or a full Lucene expression.
     * <li>excTags - Exclude Data Categories tagged with tags matching this expression. Same rules as 'tags' above.
     * <li>resultStart - Zero-based starting index offset to support result-set 'pagination'. Defaults to 0.
     * <li>resultLimit - Limit the number of entries in the result-set. Defaults to 50 with a max of 100.
     * </ul>
     *
     * Results will be sorted by wikiName unless one or more of the following query parameters are supplied (in which
     * case results will be sorted by relevance): name, wikiName, wikiDoc, provenance, authority, parentWikiName,
     * itemDefinitionName, tags, excTags.
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
        versions.each { version -> getCategoryByWikiNameJson(version) }
    }

    def getCategoryByWikiNameJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/$version/categories/Kitchen_generic;audit")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.category.uid == 'AZCW0FZTL52Y'
        assert response.data.category.status == 'ACTIVE'
        assert response.data.category.wikiName == 'Kitchen_generic'
    }

    /**
     * Tests fetching a Data Category by UID using JSON responses.
     */
    @Test
    void getCategoryByUidJson() {
        versions.each { version -> getCategoryByUidJson(version) }
    }

    def getCategoryByUidJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/$version/categories/AZCW0FZTL52Y;audit")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.category.uid == 'AZCW0FZTL52Y'
        assert response.data.category.status == 'ACTIVE'
        assert response.data.category.wikiName == 'Kitchen_generic'
    }

    /**
     * Tests fetching a previously deleted Data Category by wikiName using JSON responses.
     * If more than one Data Category is found (may happen when searching by wikiName and status),
     * the most recently modified category is returned.
     */
    @Test
    void getTrashedCategoryByWikiNameJson() {
        versions.each { version -> getTrashedCategoryByWikiNameJson(version) }
    }

    def getTrashedCategoryByWikiNameJson(version) {
        setRootUser()
        client.contentType = JSON
        def response = client.get(
                path: "/$version/categories/Kitchen_generic;audit",
                query: [status: 'trash'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.category.uid == 'AZCW0FZTL56Y'
        assert response.data.category.status == 'TRASH'
        assert response.data.category.wikiName == 'Kitchen_generic'
    }

    /**
     * Tests fetching a previously deleted Data Category by UID using JSON responses.
     */
    @Test
    void getTrashedCategoryByUidJson() {
        versions.each { version -> getTrashedCategoryByUidJson(version) }
    }

    def getTrashedCategoryByUidJson(version) {
        setRootUser()
        client.contentType = JSON
        def response = client.get(
                path: "/$version/categories/AZCW0FZTL54Y;audit",
                query: [status: 'trash'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.category.uid == 'AZCW0FZTL54Y'
        assert response.data.category.status == 'TRASH'
        assert response.data.category.wikiName == 'Kitchen_generic'
    }

    /**
     * Tests fetching a Data Category, whose parent has been deleted, by UID using JSON responses.
     */
    @Test
    void getInferredTrashedCategoryByUidJson() {
        versions.each { version -> getInferredTrashedCategoryByUidJson(version) }
    }

    def getInferredTrashedCategoryByUidJson(version) {
        setRootUser()
        client.contentType = JSON
        def response = client.get(
                path: "/$version/categories/AZCW0FZTL58Y;audit",
                query: [status: 'trash'])
        assert SUCCESS_OK.code, response.status
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.category.uid == 'AZCW0FZTL58Y'
        assert response.data.category.status == 'ACTIVE'
        assert response.data.category.wikiName == 'Kitchen_generic_child'
    }

    /**
     * Tests fetching a non-existent Data Category by wikiName.
     */
    @Test
    void getMissingCategoryByWikiName() {
        versions.each { version -> getMissingCategoryByWikiName(version) }
    }

    def getMissingCategoryByWikiName(version) {
        try {
            client.get(path: "/$version/categories/Wibble")
            fail 'Should have thrown an exception'
        } catch (HttpResponseException e) {
            assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
        }
    }

    /**
     * Tests getting a list of categories using JSON responses.
     *
     * The same matrix parameters described above are supported.
     */
    @Test
    void getCategoriesJson() {
        versions.each { version -> getCategoriesJson(version) }
    }

    def getCategoriesJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/$version/categories")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.resultsTruncated == false
        assert response.data.categories.size() == categoryNames.size()

        // Results are sorted by wikiName
        assert response.data.categories.collect { it.wikiName } == categoryWikiNames.sort { a, b -> a.compareToIgnoreCase(b) }
    }
    
    /**
     * Tests getting a list of categories with names included, specified using a matrix parameter
     */
    @Test
    void getCategoriesWithNamesJson() {
        versions.each { version -> getCategoriesWithNamesJson(version) }
    }
    
    def getCategoriesWithNamesJson(version) {
        if (version >= 3.3) {
            client.contentType = JSON
            def response = client.get(path: "/$version/categories;name")
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.resultsTruncated == false
            assert response.data.categories.size() == categoryNames.size()
            assert response.data.categories.collect { it.name }.sort() == categoryNames.sort()
    
            // Results are sorted by wikiName
            assert response.data.categories.collect { it.wikiName } == categoryWikiNames.sort { a, b -> a.compareToIgnoreCase(b) }
        }
    }

    /**
     * Tests getting a list of categories using XML responses.
     */
    @Test
    void getCategoriesXml() {
        versions.each { version -> getCategoriesXml(version) }
    }

    def getCategoriesXml(version) {
        client.contentType = XML
        def response = client.get(path: "/$version/categories")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.Categories.@truncated.text() == 'false'
        def allCategories = response.data.Categories.Category
        assert allCategories.size() == categoryNames.size()

        // Should be sorted by wikiName
        assert allCategories.WikiName*.text() == categoryWikiNames.sort { a, b -> a.compareToIgnoreCase(b) }
    }

    /**
    * Tests getting a list of categories with names using XML responses.
    */
   @Test
   void getCategoriesWithNamesXml() {
       versions.each { version -> getCategoriesWithNamesXml(version) }
   }

   def getCategoriesWithNamesXml(version) {
       if (version >= 3.3) {
           client.contentType = XML
           def response = client.get(path: "/$version/categories;name")
           assert response.status == SUCCESS_OK.code
           assert response.contentType == 'application/xml'
           assert response.data.Status.text() == 'OK'
           assert response.data.Categories.@truncated.text() == 'false'
           def allCategories = response.data.Categories.Category
           assert allCategories.size() == categoryNames.size()
           assert allCategories.Name*.text().sort() == categoryNames.sort()
    
           // Should be sorted by wikiName
           assert allCategories.WikiName*.text() == categoryWikiNames.sort { a, b -> a.compareToIgnoreCase(b) }
       }
   }
    
    /**
     * Tests getting a list of categories filtered by authority using JSON responses.
     */
    @Test
    void filterByAuthorityJson() {
        versions.each { version -> filterByAuthorityJson(version) }
    }

    def filterByAuthorityJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/$version/categories",
                query: [authority: 'enterprise'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.categories.size() == 9
    }

    /**
     * Tests getting a list of categories filtered by authority using XML responses.
     */
    @Test
    void filterByAuthorityXml() {
        versions.each { version -> filterByAuthorityXml(version) }
    }

    def filterByAuthorityXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/$version/categories",
                query: [authority: 'enterprise'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        def allCategories = response.data.Categories.Category
        assert allCategories.size() == 9
    }

    /**
     * Tests getting a list of categories filtered by tags using JSON responses.
     */
    @Test
    void filterByTagsJson() {
        versions.each { version -> filterByTagsJson(version) }
    }

    def filterByTagsJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/$version/categories",
                query: [tags: 'electricity'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.categories.size() == 4
    }

    /**
     * Tests getting a list of categories filtered by tags using XML responses.
     */
    @Test
    void filterByTagsXml() {
        versions.each { version -> filterByTagsXml(version) }
    }

    def filterByTagsXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/$version/categories",
                query: [tags: 'electricity'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        def allCategories = response.data.Categories.Category
        assert allCategories.size() == 4
    }

    /**
     * Tests getting a list of categories using JSON responses with some categories excluded by tag.
     */
    @Test
    void filterByTagsExcludedJson() {
        versions.each { version -> getCategoriesWithTagsExcludedJson(version) }
    }

    def getCategoriesWithTagsExcludedJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/$version/categories", query: [excTags: 'electricity'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.resultsTruncated == false

        def electricityCategoryWikiNames = ['Electricity_by_Country', 'Greenhouse_Gas_Protocol_international_electricity',
                                            'US_Subregion_Electricity', 'Kitchen_generic']
        def categoryWikiNamesExcElectricity = categoryWikiNames - electricityCategoryWikiNames

        assert response.data.categories.size() == categoryWikiNamesExcElectricity.size()
    }

    /**
     * Tests getting a list of categories filtered by fullPath using JSON responses.
     */
    @Test
    void filterByFullPathJson() {
        versions.each { version -> filterByFullPathJson(version) }
    }

    def filterByFullPathJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories",
                query: [fullPath: '/home/appliances/*'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        def allCategories = response.data.categories
        assert allCategories.size() == 7

        // Should be sorted
        assert response.data.categories.first().wikiName.compareToIgnoreCase(response.data.categories.last().wikiName) < 0
    }

    /**
     * Tests getting a list of categories filtered by fullPath using XML responses.
     */
    @Test
    void filterByPathXml() {
        versions.each { version -> filterByPathXml(version) }
    }

    def filterByPathXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/$version/categories",
                query: [fullPath: '/home/appliances/*'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        def allCategories = response.data.Categories.Category
        assert allCategories.size() == 7

        // Should be sorted
        assertTrue allCategories[0].WikiName.text().compareToIgnoreCase(allCategories[-1].WikiName.text()) < 0
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
        versions.each { version -> updateCategoryJson(version) }
    }

    def updateCategoryJson(version) {
        setAdminUser()

        // 1) Do the update (Integration).
        def responsePut = client.put(
                path: "/$version/categories/6153F468BE05",
                body: [path: 'newPath',
                       name: 'New Name',
                       wikiName: 'New_Wiki_Name',
                       provenance: 'New Provenance.',
                       authority: 'New Authority.',
                       history: 'New History.', // This parameter will be ignored pre version 3.3.
                       wikiDoc: 'New WikiDoc.'],
                requestContentType: URLENC,
                contentType: JSON)
        assertOkJson(responsePut, SUCCESS_OK.code, '6153F468BE05')

        // 2) Check values have been updated (Integration).
        def responseGet = client.get(
                path: "/${version}/categories/6153F468BE05;full",
                contentType: JSON)
        assert responseGet.status == SUCCESS_OK.code
        assert responseGet.contentType == 'application/json'
        assert responseGet.data.status == 'OK'
        assert responseGet.data.category.path == 'newPath'
        assert responseGet.data.category.name == 'New Name'
        assert responseGet.data.category.wikiName == 'New_Wiki_Name'
        assert responseGet.data.category.provenance == 'New Provenance.'
        assert responseGet.data.category.authority == 'New Authority.'
        assert responseGet.data.category.wikiDoc == 'New WikiDoc.'
        if (version >= 3.3) {
            assert responseGet.data.category.history == 'New History.'
        }
    }

    /**
     * Tests that multiple invalid Data Categories fields cause the expected 400 invalid response. Tests the
     * transaction rollback capability.
     */
    @Test
    void updateInvalidCategoryJson() {
        versions.each { version -> updateInvalidCategoryJson(version) }
    }

    def updateInvalidCategoryJson(version) {
        setAdminUser()
        try {
            client.put(
                    path: "/${version}/categories/6153F468BE05",
                    body: [wikiName: 'business', // duplicate
                           provenance: String.randomString(256), // too long
                           wikiDoc: String.randomString(32768)], // too long
                    requestContentType: URLENC,
                    contentType: JSON)
            fail 'Response status code should have been 400.'
        } catch (HttpResponseException e) {
            def response = e.response
            assert response.status == CLIENT_ERROR_BAD_REQUEST.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'INVALID'
        }
    }

    /**
     * Tests that the root Data Category cannot be updated via the API.
     */
    @Test
    void updateRootCategoryJson() {
        versions.each { version -> updateRootCategoryJson(version) }
    }

    def updateRootCategoryJson(version) {
        setAdminUser()
        try {

            // Should not be allowed to update the root Data Category.
            client.put(
                    path: "/$version/categories/Root",
                    body: [path: 'bad', provenance: 'bad', wikiDoc: 'bad'],
                    requestContentType: URLENC,
                    contentType: JSON)
            fail 'Response status code should have been 403'
        } catch (HttpResponseException e) {

            // Expect a 403.
            def response = e.response
            assert response.status == CLIENT_ERROR_FORBIDDEN.code
        }
    }

    /**
     * Tests that a Data Category is still available following an Item Definition being trashed.
     */
    @Test
    void shouldBehaveWhenItemDefinitionIsTrashed() {
        versions.each { version -> shouldBehaveWhenItemDefinitionIsTrashed(version) }
    }

    def shouldBehaveWhenItemDefinitionIsTrashed(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create Item Definition.
            def itemDefinitionPost = client.post(
                    path: "/$version/definitions",
                    body: [name: 'Test Item Definition'],
                    requestContentType: URLENC,
                    contentType: JSON)
            String itemDefinitionLocation = itemDefinitionPost.headers['Location'].value
            String itemDefinitionUid = itemDefinitionLocation.split('/')[5]
            assertOkJson(itemDefinitionPost, SUCCESS_CREATED.code, itemDefinitionUid)

            // Create Item Value Definition.
            def itemValueDefinitionPost = client.post(
                    path: "/$version/definitions/$itemDefinitionUid/values",
                    body: [valueDefinition: 'OMU53CZCY970',
                           name: 'Test Item Value Definition',
                           path: 'test_item_value_definition',
                           value: 'true',
                           fromProfile: 'false',
                           fromData: 'true',
                           unit: 'kg',
                           perUnit: 'month',
                           apiVersions: '2.0'],
                    requestContentType: URLENC,
                    contentType: JSON)
            String itemValueDefinitionLocation = itemValueDefinitionPost.headers['Location'].value
            String itemValueDefinitionUid = itemValueDefinitionLocation.split('/')[7]
            assertOkJson(itemValueDefinitionPost, SUCCESS_CREATED.code, itemValueDefinitionUid)

            // Create Data Category.
            def dataCategoryPost = client.post(
                    path: "/$version/categories",
                    body: [dataCategory: 'Root',
                           itemDefinition: itemDefinitionUid,
                           path: 'testPath',
                           name: 'Test Name',
                           wikiName: 'Test_Wiki_Name'],
                    requestContentType: URLENC,
                    contentType: JSON)
            String dataCategoryLocation = dataCategoryPost.headers['Location'].value
            String dataCategoryUid = dataCategoryLocation.split('/')[5]
            assertOkJson(dataCategoryPost, SUCCESS_CREATED.code, dataCategoryUid)

            // Check Data Category is available.
            def dataCategoryGet1 = client.get(path: "$dataCategoryLocation;full", contentType: JSON)
            assert dataCategoryGet1.status == SUCCESS_OK.code
            assert dataCategoryGet1.data.category.itemDefinition.name == "Test Item Definition"

            // Create Data Item.
            def dataItemPost = client.post(
                    path: "$dataCategoryLocation/items",
                    body: ['values.test_item_value_definition': 10],
                    requestContentType: URLENC,
                    contentType: JSON)
            String dataItemLocation = dataItemPost.headers['Location'].value
            String dataItemUid = dataItemLocation.split('/')[7]
            assertOkJson dataItemPost, SUCCESS_CREATED.code, dataItemUid

            // Check Data Item is available.
            def dataItemGet = client.get(path: "$dataItemLocation;full", contentType: JSON)
            assert dataItemGet.status == SUCCESS_OK.code
            assert dataItemGet.data.item.values.size() == 1
            assert dataItemGet.data.item.values.collect { it.value } == [10]
            assert dataItemGet.data.item.values.collect { it.path } == ['test_item_value_definition']

            // Delete Item Definition.
            def itemDefinitionDelete = client.delete(path: itemDefinitionLocation)
            assertOkJson(itemDefinitionDelete, SUCCESS_OK.code, itemDefinitionUid)
            try {
                client.get(path: itemDefinitionLocation)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }

            // Check Data Category is available and there is no Item Definition.
            def dataCategoryGet2 = client.get(path: "${dataCategoryLocation};full", contentType: JSON)
            assert dataCategoryGet2.status == SUCCESS_OK.code
            assert dataCategoryGet2.data.category.itemDefinition == null

            // Check Data Item is not available.
            try {
                client.get(path: dataItemLocation)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }

            // Delete Data Category.
            def dataCategoryDelete = client.delete(path: dataCategoryLocation)
            assert dataCategoryDelete.status == SUCCESS_OK.code
            try {
                client.get(path: itemDefinitionLocation)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
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
        setAdminUser()
        updateCategoryFieldJson('name', 'empty', '')
        updateCategoryFieldJson('name', 'short', 'a')
        updateCategoryFieldJson('name', 'long', String.randomString(256))
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
        setAdminUser()
        updateCategoryFieldJson('path', 'long', String.randomString(256))
        updateCategoryFieldJson('path', 'format', 'n o t v a l i d')
        updateCategoryFieldJson('path', 'duplicate', 'business')
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
        setAdminUser()
        updateCategoryFieldJson('wikiName', 'empty', '')
        updateCategoryFieldJson('wikiName', 'short', '12')
        updateCategoryFieldJson('wikiName', 'long', String.randomString(256))
        updateCategoryFieldJson('wikiName', 'duplicate', 'Business')
    }

    /**
     * Tests the validation rules for the Data Category metadata field (wikiDoc, provenance, authority, history).
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>All are optional.
     * <li>wikiDoc and history (since 3.3.0) must be no longer than 32767?? characters.
     * <li>provenance and authority must be no longer than 255 characters.
     * </ul>
     */
    @Test
    void updateWithInvalidMetadata() {
        setAdminUser()
        updateCategoryFieldJson('wikiDoc', 'long', String.randomString(DataCategory.WIKI_DOC_MAX_SIZE + 1))
        updateCategoryFieldJson('provenance', 'long', String.randomString(DataCategory.PROVENANCE_MAX_SIZE + 1))
        updateCategoryFieldJson('authority', 'long', String.randomString(DataCategory.AUTHORITY_MAX_SIZE + 1))
        updateCategoryFieldJson('history', 'long', String.randomString(DataCategory.HISTORY_MAX_SIZE + 1), 3.3)
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
        setAdminUser()
        updateCategoryFieldJson('dataCategory', 'same', '6153F468BE05', 3.3)
        updateCategoryFieldJson('dataCategory', 'child', 'Item_history_test', 3.3)
        updateCategoryFieldJson('dataCategory', 'empty', 'XXX', 3.3)
    }

    /**
     * Submits a single Data Category field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateCategoryFieldJson(field, code, value) {
        updateCategoryFieldJson(field, code, value, 3.0)
    }

    /**
     * Submits a single Data Category field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateCategoryFieldJson(field, code, value, since) {
        updateInvalidFieldJson('/categories/6153F468BE05', field, code, value, since)
    }

    /**
     * Tests an algorithm is applied to calculate a result with JSON response.
     *
     * The default units and perUnits are used.
     *
     * NB: The amount calculated is not the same as for a real API result as the algorithm has been
     * simplified for testing.
     *
     * Perform a data or 'profileless' calculation by sending a GET request to:
     * '/categories/{UID|wikiName}/calculation' (since 3.6.0).
     *
     * Supply the drill down values to select a data item using drill paths, eg type=A-10A
     * Supply the input values with the values.{PATH} query params, eg values.energyPerTime=10.
     * Supply the input units with the units.{PATH} query params, eg units.energyPerTime=MWh.
     * Supply the input perUnits with the perUnits.{PATH} query params, eg perUnits.energyPerTime=month.
     *
     * See {@link DataItemIT} for examples of performing calculations using drills to select the data item.
     */
    @Test
    void getCategoryCalculationDefaultUnitsJson() {
        versions.each { version -> getCategoryCalculationDefaultUnitsJson(version) }
    }

    def getCategoryCalculationDefaultUnitsJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON

            // Default units
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/calculation;full",
                query: [country: 'Albania', 'values.energyPerTime': '10'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'

            // Output amounts
            assert response.data.output.amounts.size() == 1
            def amount = response.data.output.amounts[0]
            assert amount.type == 'CO2'
            assert amount.unit == 'kg/year'
            assert amount.default == true
            assertEquals(20.0, amount.value as double, 0.000001)

            // Notes
            assert response.data.output.notes.size() == 1
            assert response.data.output.notes[0].type == 'comment'
            assert response.data.output.notes[0].value == 'This is a comment'

            // User input values
            assert response.data.input.values.size() == 8
            def userItemValue = response.data.input.values.find { it.name == 'energyPerTime' }
            assert userItemValue != null
            assert userItemValue.source == 'user'
            assert userItemValue.value == 10
            assert userItemValue.unit == 'kWh'
            assert userItemValue.perUnit == 'year'

            // Data item input values
            def dataItemValue
            dataItemValue = response.data.input.values.find { it.name == 'massCO2PerEnergy' }
            assert dataItemValue != null
            assertEquals(0.0324402, dataItemValue.value as double, 0.000001)
            assert dataItemValue.source == 'amee'
            assert dataItemValue.unit == 'kg/(kW·h)'
        }
    }

    /**
     * Tests an algorithm is applied to calculate a result with XML response.
     *
     * The default units and perUnits are used.
     *
     * NB: The amount calculated is not the same as for a real API result as the algorithm has been
     * simplified for testing.
     */
    @Test
    void getCategoryCalculationDefaultUnitsXml() {
        versions.each { version -> getCategoryCalculationDefaultUnitsXml(version) }
    }

    def getCategoryCalculationDefaultUnitsXml(version) {
        if (version >= 3.6) {
            client.contentType = XML
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/calculation;full",
                query: [country: 'Albania', 'values.energyPerTime': '10'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'

            // Output amounts
            assert response.data.Output.Amounts.Amount.size() == 1
            def amount = response.data.Output.Amounts.Amount[0]
            assert amount.@type.text() == 'CO2'
            assert amount.@unit.text() == 'kg/year'
            assert amount.@default.text() == 'true'
            assertEquals(20.0, amount.text() as double, 0.000001)

            // Notes
            assert response.data.Output.Notes.size() == 1
            assert response.data.Output.Notes.Note[0].@type.text() == 'comment'
            assert response.data.Output.Notes.Note[0].text() == 'This is a comment'

            // User input values
            assert response.data.Input.Values.Value.size() == 8
            def userItemValue = response.data.Input.Values.Value.find { it.@name == 'energyPerTime' }
            assert userItemValue != null
            assert userItemValue.@source.text() == 'user'
            assertEquals(10.0, userItemValue.text() as double, 0.000001)
            assert userItemValue.@unit.text() == 'kWh'
            assert userItemValue.@perUnit.text() == 'year'

            // Data item input values
            def dataItemValue
            if (version >= 3.6) {
                dataItemValue = response.data.Input.Values.Value.find { it.@name == 'massCO2PerEnergy' }
                assert dataItemValue != null
                assert dataItemValue.@source.text() == 'amee'
                assertEquals(0.0324402, dataItemValue.text() as double, 0.000001)
                assert dataItemValue.@unit.text() == 'kg/(kW·h)'
            }
        }
    }

    /**
     * Tests a calculation using custom units and perUnits.
     */
    @Test
    void getCategoryCalculationCustomUnitsJson() {
        versions.each { version -> getCategoryCalculationCustomUnitsJson(version) }
    }

    def getCategoryCalculationCustomUnitsJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON

            // Default units
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/calculation;full",
                query: [country: 'Albania', 'values.energyPerTime': '10', 'units.energyPerTime': 'MWh',
                        'perUnits.energyPerTime': 'month'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'

            // Output amounts
            assert response.data.output.amounts.size() == 1
            def amount = response.data.output.amounts[0]
            assert amount.type == 'CO2'
            assert amount.unit == 'kg/year'
            assert amount.default == true
            assertEquals(240000.0, amount.value as double, 0.000001)

            // Notes
            assert response.data.output.notes.size() == 1
            assert response.data.output.notes[0].type == 'comment'
            assert response.data.output.notes[0].value == 'This is a comment'

            // Input values
            assert response.data.input.values.size() == 8
            def itemValue = response.data.input.values.find { it.name == 'energyPerTime' }
            assert itemValue != null
            assert itemValue.source == 'user'
            assert itemValue.value == 10
            assert itemValue.unit == 'MWh'
            assert itemValue.perUnit == 'month'
        }
    }

    @Test
    void getCategoryCalculationCustomUnitsXml() {
        versions.each { version -> getCategoryCalculationCustomUnitsXml(version) }
    }

    def getCategoryCalculationCustomUnitsXml(version) {
        if (version >= 3.6) {
            client.contentType = XML
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/calculation;full",
                query: [country: 'Albania', 'values.energyPerTime': '10', 'units.energyPerTime': 'MWh',
                        'perUnits.energyPerTime': 'month'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'

            // Output amounts
            assert response.data.Output.Amounts.Amount.size() == 1
            def amount = response.data.Output.Amounts.Amount[0]
            assert amount.@type.text() == 'CO2'
            assert amount.@unit.text() == 'kg/year'
            assert amount.@default.text() == 'true'
            assertEquals(240000.0, amount.text() as double, 0.000001)

            // Notes
            assert response.data.Output.Notes.size() == 1
            assert response.data.Output.Notes.Note[0].@type.text() == 'comment'
            assert response.data.Output.Notes.Note[0].text() == 'This is a comment'

            // Input values
            assert response.data.Input.Values.Value.size() == 8
            def itemValue = response.data.Input.Values.Value.find { it.@name == 'energyPerTime' }
            assert itemValue != null
            assert itemValue.@source.text() == 'user'
            assertEquals(10.0, itemValue.text() as double, 0.000001)
            assert itemValue.@unit.text() == 'MWh'
            assert itemValue.@perUnit.text() == 'month'
        }
    }

    /**
     * Tests a calculation using custom returnUnits and returnPerUnits.
     */
    @Test
    void getCategoryCalculationCustomReturnUnitsJson() {
        versions.each { version -> getCategoryCalculationCustomReturnUnitsJson(version) }
    }

    def getCategoryCalculationCustomReturnUnitsJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/calculation",
                query: [country: 'Albania', 'values.energyPerTime': '10', 'returnUnits.CO2': 'g',
                        'returnPerUnits.CO2': 'month'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'

            // Output amounts
            def amount
            assert response.data.output.amounts.size() == 1
            amount = response.data.output.amounts[0]
            assert amount.unit == 'g/month'
            assert amount.type == 'CO2'
            assert amount.default == true
            assertEquals(1666.66666666667, amount.value as double, 0.000001)
        }
    }

    @Test
    void getCategoryCalculationCustomReturnUnitsXml() {
        versions.each { version -> getCategoryCalculationCustomReturnUnitsXml(version) }
    }

    def getCategoryCalculationCustomReturnUnitsXml(version) {
        if (version >= 3.6) {
            client.contentType = XML
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/calculation",
                query: [country: 'Albania', 'values.energyPerTime': '10', 'returnUnits.CO2': 'g',
                        'returnPerUnits.CO2': 'month'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'

            // Output amounts
            def amount
            assert response.data.Output.Amounts.Amount.size() == 1
            amount = response.data.Output.Amounts.Amount[0]
            assert amount.@unit.text() == 'g/month'
            assert amount.@type.text() == 'CO2'
            assert amount.@default.text() == 'true'
            assertEquals(1666.66666666667, amount.text() as double, 0.000001)
        }
    }


    /**
     * Tests an algorithm that returns Infinity or NaN return values.
     *
     * Note: The amount value below is not the same as for a real API result as the algorithm has been simplified for testing.
     * Algorithms should not normally return non-finite values however if they do the platform should handle them.
     * JSON does not allow non-finite numbers so we return them as strings.
     */
    @Test
    void getCategoryCalculationInfinityAndNanJson() {
        versions.each { version -> getCategoryCalculationInfinityAndNanJson(version)}
    }

    def getCategoryCalculationInfinityAndNanJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON
            def response = client.get(path: "/$version/categories/Computers_generic/calculation;full",
                query: [device: 'Personal Computers', rating: 'Desktop no monitor'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.output.amounts.size() == 2
            assertTrue "Should have Infinity and NaN", hasInfinityAndNan(response.data.output.amounts)
        }
    }

    /**
     * Tests a calculation that uses data item value histories (data series).
     * A startDate and endDate are supplied.
     */
    @Test
    void getCategoryCalculationHistoryJson() {
        versions.each { version -> getCategoryCalculationHistoryJson(version) }
    }

    def getCategoryCalculationHistoryJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON

            // Default units
            // Georgia
            def response = client.get(
                path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/39MEKTNO2AKJ/calculation;full",
                query: [country: 'Georgia', 'values.energyPerTime': '10', startDate: '2000-01-01T00:00:00Z',
                        endDate: '2004-01-01T00:00:00Z'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'

            // Output amounts
            def amount
            assert response.data.output.amounts.size() == 1
            amount = response.data.output.amounts[0]
            assert amount.unit == 'kg/year'
            assert amount.type == 'CO2'
            assert amount.default == true
            assertEquals(1.1839605065023955, amount.value as double, 0.000001)

            // User input values
            def userItemValue
            assertEquals 6, response.data.input.values.size()
            userItemValue = response.data.input.values.find { it.name == 'energyPerTime' }
            assert userItemValue != null
            assert userItemValue.value == 10
            assert userItemValue.source == 'user'
            assert userItemValue.unit == 'kWh'

            // Data item input values
            def dataItemValue = response.data.input.values.find { it.name == 'massCO2PerEnergy' }
            assert dataItemValue != null
            assert dataItemValue.source == 'amee'
            assert dataItemValue.value.size() == 5
            def firstValue = dataItemValue.value.first()
            assert firstValue.startDate == '1970-01-01T00:00:00Z'
            assert firstValue.unit == 'kg/(kW·h)'
            assertEquals(0.1449678, firstValue.value as double, 0.000001)
        }
    }

    @Test
    void getDataItemCalculationHistoryXml() {
        versions.each { version -> getDataItemCalculationHistoryXml(version) }
    }

    def getDataItemCalculationHistoryXml(version) {
        if (version >= 3.6) {
            client.contentType = XML
            def response = client.get(
                path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/39MEKTNO2AKJ/calculation;full",
                query: [country: 'Georgia', 'values.energyPerTime': '10', startDate: '2000-01-01T00:00:00Z',
                        endDate: '2004-01-01T00:00:00Z'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'

            // Output amounts
            def amount
            assert response.data.Output.Amounts.Amount.size() == 1
            amount = response.data.Output.Amounts.Amount[0]
            assert amount.@unit.text() == 'kg/year'
            assert amount.@type.text() == 'CO2'
            assert amount.@default.text() == 'true'
            assertEquals(1.1839605065023955, amount.text() as double, 0.000001)

            // User input values
            def userItemValue
            assert response.data.Input.Values.Value.size() == 6
            userItemValue = response.data.Input.Values.Value.find { it.@name == 'energyPerTime' }
            assert userItemValue != null
            assert userItemValue.@source.text() == 'user'
            assertEquals(10.0, userItemValue.text() as double, 0.000001)
            assert userItemValue.@unit.text() == 'kWh'

            // Data item input values
            def dataItemValue = response.data.Input.Values.Value.find { it.@name == 'massCO2PerEnergy' }
            assert dataItemValue != null
            assert dataItemValue.@source.text() == 'amee'
            assert dataItemValue.DataSeries.DataPoint.size() == 5
            def firstValue = dataItemValue.DataSeries.DataPoint[0]
            assert firstValue.@startDate == '1970-01-01T00:00:00Z'
            assert firstValue.@unit == 'kg/(kW·h)'
            assertEquals(0.1449678, firstValue.text() as double, 0.000001)
        }
    }
}