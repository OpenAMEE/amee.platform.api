import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Data Item API.
 *
 * TODO: Document Data Item API fully here. See https://jira.amee.com/browse/PL-9547 to vote on this task.
 */
class DataItemIT extends BaseApiTest {

    static def versions = [3.0, 3.1, 3.2]

    static def dataItemUids = [
            'B6419AFB7114',
            'B1EF6970E87C',
            '004CF30590A5',
            'E011530FFEDC',
            '471F553DF10A',
            '9DD165D3AFC9'
    ].sort()

    static def oneGasItemValueValues = [
            '1',
            '188',
            'BRE/MTP/dgen/defra 2007',
            'Gas']

    static def oneGasItemValuePaths = [
            'numberOfPeople',
            'kgCO2PerYear',
            'source',
            'fuel']

    static def tenElectricItemValueValues = [
            '10',
            '620',
            'BRE/MTP/dgen/defra 2007',
            'Electric']

    static def tenElectricItemValuePaths = [
            'numberOfPeople',
            'kgCO2PerYear',
            'source',
            'fuel']

    /**
     * Tests for creation, fetch and deletion of a Data Item using JSON responses.
     *
     * Create a new Data Item by POSTing to '/categories/{UID|wikiName}' (since 3.4.0).
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>path
     * <li>wikiDoc
     * <li>provenance
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Data Item by sending a DELETE request to '/categories/{UID|wikiName}/items/{UID|path}' (since 3.4.0).
     */
    @Test
    void createDataItemJson() {
        setAdminUser();
        // Create a DataItem.
        def responsePost = client.post(
                path: '/3.4/categories/Cooking/items',
                body: [
                        wikiDoc: 'Test WikiDoc.'],
                requestContentType: URLENC,
                contentType: JSON);
        assertEquals 201, responsePost.status
        // Is Location available?
        assertTrue responsePost.headers['Location'] != null;
        assertTrue responsePost.headers['Location'].value != null;
        def location = responsePost.headers['Location'].value;
        assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")
        // Get new DataItem UID.
        def uid = location.split('/')[7];
        assertTrue uid != null;
        // Sleep a little to give the index a chance to be updated.
        sleep(1000);
        // Get the new DataItem.
        def responseGet = client.get(
                path: '/3.4/categories/Cooking/items/' + uid + ';full',
                contentType: JSON);
        assertEquals 200, responseGet.status;
        assertEquals 'application/json', responseGet.contentType;
        assertTrue responseGet.data instanceof net.sf.json.JSON;
        assertEquals 'OK', responseGet.data.status;
        assertEquals 'Test WikiDoc.', responseGet.data.item.wikiDoc
        // Then delete it.
        def responseDelete = client.delete(path: '/3.4/categories/Cooking/items/' + uid);
        assertEquals 200, responseDelete.status;
        // Sleep a little to give the index a chance to be updated.
        sleep(1000);
        // We should get a 404 here.
        try {
            client.get(path: '/3.4/categories/Cooking/items/' + uid);
            fail 'Should have thrown an exception';
        } catch (HttpResponseException e) {
            assertEquals 404, e.response.status;
        }
    }

    // TODO: createDataItemXml

    @Test
    void createDuplicateDataItemJson() {
        setAdminUser();
        // Create a DataItem.
        def responsePost = client.post(
                path: '/3.4/categories/Cooking/items',
                body: [path: 'testPath'],
                requestContentType: URLENC,
                contentType: JSON);
        // Should have been created.
        assertEquals 201, responsePost.status
        try {
            // Create a DataItem.
            client.post(
                    path: '/3.4/categories/Cooking/items',
                    body: [path: 'testPath'],
                    requestContentType: URLENC,
                    contentType: JSON);
        } catch (HttpResponseException e) {
            // Should have been rejected.
            assertEquals 400, e.response.status;
        }
        // Then delete it.
        def responseDelete = client.delete(path: '/3.4/categories/Cooking/items/testPath');
        // Should have been deleted.
        assertEquals 200, responseDelete.status;
    }

    @Test
    void getDataItemsJson() {
        versions.each { version -> getDataItemsJson(version) }
    }

    def getDataItemsJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/Cooking/items;full",
                query: ['resultLimit': '6'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertTrue response.data.resultsTruncated
        assertEquals dataItemUids.size(), response.data.items.size()
        def responseUids = response.data.items.collect { it.uid }.sort()
        assert dataItemUids == responseUids

        // Should  be sorted by label
        if (version >= 3.2) {
            assertTrue response.data.items.first().label.compareToIgnoreCase(response.data.items.last().label) < 0
        }
    }

    @Test
    void getFilteredDataItemsJson() {
        versions.each { version -> getFilteredDataItemsJson(version) }
    }

    def getFilteredDataItemsJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/Cooking/items;full",
                query: ['numberOfPeople': '1'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertFalse response.data.resultsTruncated
        assertEquals 5, response.data.items.size()

        // Should not be sorted by label
        if (version >= 3.2) {
            assertTrue response.data.items.first().label.compareToIgnoreCase(response.data.items.last().label) > 0
        }
    }

    @Test
    void getDataItemsXml() {
        versions.each { version -> getDataItemsXml(version) }
    }

    def getDataItemsXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/categories/Cooking/items;full",
                query: ['resultLimit': '6'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals 'true', response.data.Items.@truncated.text()
        def allDataItems = response.data.Items.Item
        assertEquals dataItemUids.size(), allDataItems.size()
        assert dataItemUids == allDataItems.@uid*.text().sort()

        // Should  be sorted by label
        if (version >= 3.2) {
            assertTrue allDataItems[0].Label.text().compareToIgnoreCase(allDataItems[-1].Label.text()) < 0
        }
    }

    @Test
    void getDataItemOneGasJson() {
        versions.each { version -> getDataItemOneGasJson(version) }
    }

    def getDataItemOneGasJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/Cooking/items/004CF30590A5;full");
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        if (version >= 3.2) {
            assertEquals '1, Gas', response.data.item.label;
        }
        assertEquals '54C8A44254AA', response.data.item.categoryUid;
        assertEquals 'Cooking', response.data.item.categoryWikiName;
        assertEquals 'Cooking', response.data.item.itemDefinition.name;
        assertEquals '/home/appliances/cooking/004CF30590A5', response.data.item.fullPath;
        assertEquals oneGasItemValueValues.size(), response.data.item.values.size();
        assertTrue(oneGasItemValueValues == response.data.item.values.collect {it.value});
        assertTrue(oneGasItemValuePaths == response.data.item.values.collect {it.path});
    }

    @Test
    void getDataItemOneGasXml() {
        versions.each { version -> getDataItemOneGasXml(version) }
    }

    def getDataItemOneGasXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/categories/Cooking/items/004CF30590A5;full");
        assertEquals 200, response.status;
        assertEquals 'application/xml', response.contentType;
        assertEquals 'OK', response.data.Status.text();
        if (version >= 3.2) {
            assertEquals '1, Gas', response.data.Item.Label.text();
        }
        assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text();
        assertEquals 'Cooking', response.data.Item.CategoryWikiName.text();
        assertEquals 'Cooking', response.data.Item.ItemDefinition.Name.text();
        assertEquals '/home/appliances/cooking/004CF30590A5', response.data.Item.FullPath.text();
        def allValues = response.data.Item.Values.Value;
        assertEquals oneGasItemValueValues.size(), allValues.size();
        assertTrue(oneGasItemValueValues == allValues.Value*.text());
        assertTrue(oneGasItemValuePaths == allValues.Path*.text());
    }

    @Test
    void getDataItemTenElectricJson() {
        versions.each { version -> getDataItemTenElectricJson(version) }
    }

    def getDataItemTenElectricJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/Cooking/items/9DD165D3AFC9;full");
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        if (version >= 3.2) {
            assertEquals '10, Electric', response.data.item.label;
        }
        assertEquals '54C8A44254AA', response.data.item.categoryUid;
        assertEquals 'Cooking', response.data.item.categoryWikiName;
        assertEquals 'Cooking', response.data.item.itemDefinition.name;
        assertEquals '/home/appliances/cooking/9DD165D3AFC9', response.data.item.fullPath;
        assertEquals tenElectricItemValueValues.size(), response.data.item.values.size();
        assertTrue(tenElectricItemValueValues == response.data.item.values.collect {it.value});
        assertTrue(tenElectricItemValuePaths == response.data.item.values.collect {it.path});
    }

    @Test
    void getDataItemTenElectricXml() {
        versions.each { version -> getDataItemTenElectricXml(version) }
    }

    def getDataItemTenElectricXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/categories/Cooking/items/9DD165D3AFC9;full");
        assertEquals 200, response.status;
        assertEquals 'application/xml', response.contentType;
        assertEquals 'OK', response.data.Status.text();
        if (version >= 3.2) {
            assertEquals '10, Electric', response.data.Item.Label.text();
        }
        assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text();
        assertEquals 'Cooking', response.data.Item.CategoryWikiName.text();
        assertEquals 'Cooking', response.data.Item.ItemDefinition.Name.text();
        assertEquals '/home/appliances/cooking/9DD165D3AFC9', response.data.Item.FullPath.text();
        def allValues = response.data.Item.Values.Value;
        assertEquals tenElectricItemValueValues.size(), allValues.size();
        assertTrue(tenElectricItemValueValues == allValues.Value*.text());
        assertTrue(tenElectricItemValuePaths == allValues.Path*.text());
    }

    @Test
    void updateDataItemJson() {
        setAdminUser();
        def responsePut = client.put(
                path: '/3.1/categories/Cooking/items/9DD165D3AFC9',
                body: ['name': 'newName',
                        'wikiDoc': 'wd',
                        'path': 'np',
                        'provenance': 'prov'],
                requestContentType: URLENC,
                contentType: JSON);
        assertEquals 201, responsePut.status;
        def responseGet = client.get(
                path: '/3.1/categories/Cooking/items/9DD165D3AFC9;full',
                contentType: JSON);
        assertEquals 200, responseGet.status;
        println responseGet.data;
        assertEquals 'newName', responseGet.data.item.name;
        assertEquals 'wd', responseGet.data.item.wikiDoc;
        assertEquals 'np', responseGet.data.item.path;
        assertEquals 'prov', responseGet.data.item.provenance;
        // Sleep a little to give the index a chance to be updated.
        sleep(1000);
    }

    @Test
    void updateDataItemUnauthorizedJson() {
        try {
            client.put(
                    path: '/3.1/categories/Cooking/items/9DD165D3AFC9',
                    body: ['name': 'newName'],
                    requestContentType: URLENC,
                    contentType: JSON);
            fail 'Expected 403'
        } catch (HttpResponseException e) {
            def response = e.response;
            assertEquals 403, response.status;
            assertEquals 403, response.data.status.code;
            assertEquals 'Forbidden', response.data.status.name;
        }
    }

    /**
     * The amount value below is not the same as an API result as the algorithm has been simplified for testing.
     */
    @Test
    void getDataItemCalculationJson() {
        client.contentType = JSON
        def response = client.get(
                path: "/3.5/categories/Cooking/items/004CF30590A5/calculation;full");
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        assertEquals 1, response.data.amounts.size();
        def amount = response.data.amounts[0];
        assertEquals 'year', amount.perUnit;
        assertEquals 'kg', amount.unit;
        assertEquals true, amount.default;
        assertEquals("", 233.3, amount.value, 0.5);
        assertEquals 'CO2', amount.type;
    }
}