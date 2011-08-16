package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Data Category API.
 */
class CategoryIT extends BaseApiTest {

    static def categoryNames = [
            'Root', 'Home', 'Appliances', 'Computers', 'Generic', 'Cooking', 'Entertainment',
            'Generic', 'Kitchen', 'Generic', 'Business', 'Energy', 'Electricity', 'US', 'Subregion', 'Waste',
            'LCA', 'Ecoinvent', 'chemicals', 'inorganics', 'chlorine, gaseous, diaphragm cell, at plant',
            'chlorine, gaseous, diaphragm cell, at plant', 'Benchmark', 'CO2 Benchmark', 'CO2 Benchmark Two',
            'CO2 Benchmark Child', 'Embodied', 'Clm', 'Grid']

    static def categoryNamesExcEcoinvent = [
            'Root', 'Home', 'Appliances', 'Computers', 'Generic', 'Cooking',
            'Entertainment', 'Generic', 'Kitchen', 'Generic', 'Business', 'Energy',
            'Electricity', 'US', 'Subregion', 'Waste',
            'Benchmark', 'CO2 Benchmark', 'CO2 Benchmark Two', 'CO2 Benchmark Child', 'Embodied', 'Clm', 'LCA',
            'Grid']

    static def categoryWikiNames = [
            'Root', 'Home', 'Appliances', 'Computers', 'Computers_generic', 'Cooking', 'Entertainment',
            'Entertainment_generic', 'Kitchen', 'Kitchen_generic',
            'Business', 'Business_energy', 'Electricity_by_Country', 'Energy_US', 'US_Egrid', 'Waste',
            'LCA', 'Ecoinvent', 'Ecoinvent_chemicals', 'Ecoinvent_chemicals_inorganics',
            'Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant',
            'Ecoinvent_chemicals_inorganics_chlorine_gaseous_diaphragm_cell_at_plant_UPR_RER_kg',
            'Benchmarking', 'CO2_Benchmark', 'CO2_Benchmark_Two', 'CO2_Benchmark_Child', 'Embodied',
            'CLM_food_life_cycle_database', 'Greenhouse_Gas_Protocol_international_electricity']

    static def categoryWikiNamesExcEcoinvent = [
            'Root', 'Home', 'Appliances', 'Computers', 'Computers_generic', 'Cooking',
            'Entertainment', 'Entertainment_generic', 'Kitchen', 'Kitchen_generic',
            'Business', 'Business_energy', 'Electricity_by_Country', 'Energy_US', 'US_Egrid', 'Waste',
            'Benchmarking', 'CO2_Benchmark', 'CO2_Benchmark_Two', 'CO2_Benchmark_Child', 'Embodied',
            'CLM_food_life_cycle_database', 'LCA', 'Greenhouse_Gas_Protocol_international_electricity']

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
        versions.each { version -> createCategoryJson(version) };
    }

    def createCategoryJson(version) {
        if (version >= 3.3) {
            setAdminUser();

            // Create a DataCategory.
            def responsePost = client.post(
                    path: "/${version}/categories",
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
                    path: "/${version}/categories/Test_Wiki_Name",
                    contentType: JSON);
            assertEquals 200, responseGet.status;
            assertEquals 'application/json', responseGet.contentType;
            assertTrue responseGet.data instanceof net.sf.json.JSON;
            assertEquals 'OK', responseGet.data.status;
            assertEquals "Test Name", responseGet.data.category.name
            assertEquals "Test_Wiki_Name", responseGet.data.category.wikiName

            // Then delete it.
            def responseDelete = client.delete(path: "/${version}/categories/Test_Wiki_Name");
            assertEquals 200, responseDelete.status;

            // We should get a 404 here.
            try {
                client.get(path: "/${version}/categories/Test_Wiki_Name");
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
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
        versions.each { version -> getCategoryByWikiNameJson(version) };
    }

    def getCategoryByWikiNameJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/${version}/categories/Kitchen_generic;audit")
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
        versions.each { version -> getCategoryByUidJson(version) };
    }

    def getCategoryByUidJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/${version}/categories/3C03A03B5F3A;audit")
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
        versions.each { version -> getTrashedCategoryByWikiNameJson(version) };
    }

    def getTrashedCategoryByWikiNameJson(version) {
        setRootUser();
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/Kitchen_generic;audit",
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
        versions.each { version -> getTrashedCategoryByUidJson(version) };
    }

    def getTrashedCategoryByUidJson(version) {
        setRootUser();
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/3C03A03B5F1A;audit",
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
        versions.each { version -> getInferredTrashedCategoryByUidJson(version) };
    }

    def getInferredTrashedCategoryByUidJson(version) {
        setRootUser();
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/3C03A03B5F4A;audit",
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
        versions.each { version -> getMissingCategoryByWikiName(version) };
    }

    def getMissingCategoryByWikiName(version) {
        try {
            client.get(path: "/${version}/categories/Wibble")
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
        versions.each { version -> getCategoriesJson(version) };
    }

    def getCategoriesJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/${version}/categories")
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertFalse response.data.resultsTruncated
        assertEquals categoryNames.size(), response.data.categories.size()
        assert categoryNames.sort() == response.data.categories.collect {it.name}.sort()
        // Results are sorted by wikiName
        assertEquals categoryWikiNames.sort { a, b -> a.compareToIgnoreCase(b) }, response.data.categories.collect {it.wikiName}
    }

    /**
     * Tests getting a list of categories using JSON responses with some categories excluded by tag.
     */
    @Test
    void getCategoriesWithTagsExcludedJson() {
        versions.each { version -> getCategoriesWithTagsExcludedJson(version) };
    }

    def getCategoriesWithTagsExcludedJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories",
                query: ['excTags': 'ecoinvent'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertFalse response.data.resultsTruncated
        assertEquals categoryNamesExcEcoinvent.size(), response.data.categories.size()
        assert categoryNamesExcEcoinvent.sort() == response.data.categories.collect {it.name}.sort()
        // Results should NOT be sorted
        assert categoryWikiNamesExcEcoinvent.sort { a, b -> a.compareToIgnoreCase(b) } != response.data.categories.collect {it.wikiName}
        assert categoryWikiNamesExcEcoinvent.sort() == response.data.categories.collect {it.wikiName}.sort()
    }

    /**
     * Tests getting a list of categories using XML responses.
     */
    @Test
    void getCategoriesXml() {
        versions.each { version -> getCategoriesXml(version) };
    }

    def getCategoriesXml(version) {
        client.contentType = XML
        def response = client.get(path: "/${version}/categories");
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals 'false', response.data.Categories.@truncated.text()
        def allCategories = response.data.Categories.Category
        assertEquals categoryNames.size(), allCategories.size()
        assert categoryNames.sort() == allCategories.Name*.text().sort()
        // Should be sorted by wikiName
        assertEquals categoryWikiNames.sort { a, b -> a.compareToIgnoreCase(b) }, allCategories.WikiName*.text()
    }

    /**
     * Tests getting a list of categories filtered by authority using JSON responses.
     */
    @Test
    void filterByAuthorityJson() {
        versions.each { version -> filterByAuthorityJson(version) };
    }

    def filterByAuthorityJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories",
                query: ['authority': 'enterprise'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals 9, response.data.categories.size()
        // Should NOT be sorted
        assertTrue response.data.categories.first().wikiName.compareToIgnoreCase(response.data.categories.last().wikiName) > 0
    }

    /**
     * Tests getting a list of categories filtered by authority using XML responses.
     */
    @Test
    void filterByAuthorityXml() {
        versions.each { version -> filterByAuthorityXml(version) };
    }

    def filterByAuthorityXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/categories",
                query: ['authority': 'enterprise'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        def allCategories = response.data.Categories.Category
        assertEquals 9, allCategories.size()
        // Should NOT be sorted
        assertTrue allCategories[0].WikiName.text().compareToIgnoreCase(allCategories[-1].WikiName.text()) > 0
    }

    /**
     * Tests getting a list of categories filtered by tags using JSON responses.
     */
    @Test
    void filterByTagsJson() {
        versions.each { version -> filterByTagsJson(version) };
    }

    def filterByTagsJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories",
                query: ['tags': 'electrical'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals 4, response.data.categories.size()
        // Should NOT be sorted
        assertTrue response.data.categories.first().wikiName.compareToIgnoreCase(response.data.categories.last().wikiName) > 0
    }

    /**
     * Tests getting a list of categories filtered by tags using XML responses.
     */
    @Test
    void filterByTagsXml() {
        versions.each { version -> filterByTagsXml(version) };
    }

    def filterByTagsXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/categories",
                query: ['tags': 'electrical'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        def allCategories = response.data.Categories.Category
        assertEquals 4, allCategories.size()
        // Should NOT be sorted
        assertTrue allCategories[0].WikiName.text().compareToIgnoreCase(allCategories[-1].WikiName.text()) > 0
    }

    /**
     * Tests getting a list of categories filtered by fullPath using JSON responses.
     */
    @Test
    void filterByFullPathJson() {
        versions.each { version -> filterByFullPathJson(version) };
    }

    def filterByFullPathJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories",
                query: ['fullPath': '/home/appliances/*'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        def allCategories = response.data.categories
        assertEquals 7, allCategories.size()
        // Should be sorted
        assertTrue response.data.categories.first().wikiName.compareToIgnoreCase(response.data.categories.last().wikiName) < 0
    }

    /**
     * Tests getting a list of categories filtered by fullPath using XML responses.
     */
    @Test
    void filterByPathXml() {
        versions.each { version -> filterByPathXml(version) };
    }

    def filterByPathXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/categories",
                query: ['fullPath': '/home/appliances/*'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        def allCategories = response.data.Categories.Category
        assertEquals 7, allCategories.size()
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
        versions.each { version -> updateCategoryJson(version) };
    }

    def updateCategoryJson(version) {
        setAdminUser();
        // 1) Do the update (CO2_Benchmark).
        def responsePut = client.put(
                path: "/${version}/categories/245CBD734418",
                body: [
                        'path': 'newPath',
                        'name': 'New Name',
                        'wikiName': 'New_Wiki_Name',
                        'provenance': 'New Provenance.',
                        'authority': 'New Authority.',
                        'history': 'New History.', // This parameter will be ignored pre version 3.3.
                        'wikiDoc': 'New WikiDoc.'],
                requestContentType: URLENC,
                contentType: JSON);
        assertEquals 204, responsePut.status;
        // 2) Check values have been updated (CO2_Benchmark).
        def responseGet = client.get(
                path: "/${version}/categories/245CBD734418;full",
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
        if (version >= 3.3) {
            assertEquals 'New History.', responseGet.data.category.history;
        }
    }

    /**
     * Tests that multiple invalid Data Categories fields cause the expected 400 invalid response. Tests the
     * transaction rollback capability.
     */
    @Test
    void updateInvalidCategoryJson() {
        versions.each { version -> updateInvalidCategoryJson(version) };
    }

    def updateInvalidCategoryJson(version) {
        setAdminUser();
        try {
            // 1) Do the update (CO2_Benchmark).
            client.put(
                    path: "/${version}/categories/245CBD734418",
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
     * Tests that the root Data Category cannot be updated via the API.
     */
    @Test
    void updateRootCategoryJson() {
        versions.each { version -> updateRootCategoryJson(version) };
    }

    def updateRootCategoryJson(version) {
        setAdminUser();
        try {
            // Should not be allowed to update the root Data Category.
            client.put(
                    path: "/${version}/categories/Root",
                    body: [
                            'path': 'bad',
                            'provenance': 'bad',
                            'wikiDoc': 'bad'],
                    requestContentType: URLENC,
                    contentType: JSON);
            fail 'Response status code should have been 403';
        } catch (HttpResponseException e) {
            // Expect a 403.
            def response = e.response;
            assertEquals 403, response.status;
        }
    }

    /**
     * Tests that a Data Category is still available following an Item Definition being trashed.
     */
    @Test
    void shouldBehaveWhenItemDefinitionIsTrashed() {
        versions.each { version -> shouldBehaveWhenItemDefinitionIsTrashed(version) };
    }

    def shouldBehaveWhenItemDefinitionIsTrashed(version) {
        if (version >= 3.4) {
            setAdminUser();

            // Create Item Definition.
            def itemDefinitionPost = client.post(
                    path: "/${version}/definitions",
                    body: ['name': 'Test Item Definition'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, itemDefinitionPost.status;
            def itemDefinitionLocation = itemDefinitionPost.headers['Location'].value;
            def itemDefinitionUid = itemDefinitionLocation.split('/')[5];

            // Create Item Value Definition.
            def itemValueDefinitionPost = client.post(
                    path: "/${version}/definitions/${itemDefinitionUid}/values",
                    body: ['valueDefinition': '45433E48B39F',
                            'name': 'Test Item Value Definition',
                            'path': 'test_item_value_definition',
                            'value': 'true',
                            'fromProfile': 'false',
                            'fromData': 'true',
                            'unit': 'kg',
                            'perUnit': 'month',
                            'apiVersions': '2.0'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, itemValueDefinitionPost.status;

            // Create Data Category.
            def dataCategoryPost = client.post(
                    path: "/${version}/categories",
                    body: [
                            dataCategory: 'Root',
                            itemDefinition: itemDefinitionUid,
                            path: 'testPath',
                            name: 'Test Name',
                            wikiName: 'Test_Wiki_Name'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, dataCategoryPost.status;
            def dataCategoryLocation = dataCategoryPost.headers['Location'].value;

            // Check Data Category is available.
            def dataCategoryGet1 = client.get(
                    path: "${dataCategoryLocation};full",
                    contentType: JSON);
            assertEquals 200, dataCategoryGet1.status;
            assertEquals "Test Item Definition", dataCategoryGet1.data.category.itemDefinition.name;

            // Create Data Item.
            def dataItemPost = client.post(
                    path: "${dataCategoryLocation}/items",
                    body: ['values.test_item_value_definition': 10],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, dataItemPost.status;
            def dataItemLocation = dataItemPost.headers['Location'].value;

            // Check Data Item is available.
            def dataItemGet = client.get(
                    path: "${dataItemLocation};full",
                    contentType: JSON);
            assertEquals 200, dataItemGet.status;
            assertEquals 1, dataItemGet.data.item.values.size();
            assertTrue(['10'].sort() == dataItemGet.data.item.values.collect {it.value}.sort());
            assertTrue(['test_item_value_definition'].sort() == dataItemGet.data.item.values.collect {it.path}.sort());

            // Delete Item Definition.
            def itemDefinitionDelete = client.delete(path: itemDefinitionLocation);
            assertEquals 200, itemDefinitionDelete.status;
            try {
                client.get(path: itemDefinitionLocation);
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }

            // Check Data Category is available and there is no Item Definition.
            def dataCategoryGet2 = client.get(
                    path: "${dataCategoryLocation};full",
                    contentType: JSON);
            assertEquals 200, dataCategoryGet2.status;
            assertNull dataCategoryGet2.data.category.itemDefinition;

            // Check Data Item is not available.
            try {
                client.get(path: dataItemLocation);
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }

            // Delete Data Category.
            def dataCategoryDelete = client.delete(path: dataCategoryLocation);
            assertEquals 200, dataCategoryDelete.status;
            try {
                client.get(path: itemDefinitionLocation);
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
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
        setAdminUser();
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
        setAdminUser();
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
        setAdminUser();
        updateCategoryFieldJson('wikiDoc', 'long', String.randomString(32768));
        updateCategoryFieldJson('provenance', 'long', String.randomString(256));
        updateCategoryFieldJson('authority', 'long', String.randomString(256));
        updateCategoryFieldJson('history', 'long', String.randomString(32768), 3.3);
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
        setAdminUser();
        updateCategoryFieldJson('dataCategory', 'same', '245CBD734418', 3.3);
        updateCategoryFieldJson('dataCategory', 'child', 'CO2_Benchmark_Child', 3.3);
        updateCategoryFieldJson('dataCategory', 'empty', 'XXX', 3.3);
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
        updateInvalidFieldJson("/categories/245CBD734418", field, code, value, since)
    }
}