package com.amee.integration

import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import static org.restlet.data.Status.*

/**
 * Tests for the Data Item API.
 */
class DataItemIT extends BaseApiTest {

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
     * Create a new Data Item by POSTing to '/categories/{UID|wikiName}/items' (since 3.4.0).
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>path
     * <li>wikiDoc
     * <li>provenance
     * <li>values.{path}*
     * </ul>
     *
     * The 'values.{path}' parameter can be used to set Data Item Values that are drill downs
     * or the first in a historical series.
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Data Item by sending a DELETE request to '/categories/{UID|wikiName}/items/{UID|path}' (since 3.4.0).
     */
    @Test
    void createDataItemJson() {
        versions.each { version -> createDataItemJson(version) }
    }

    def createDataItemJson(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a DataItem.
            def responsePost = client.post(
                    path: "/${version}/categories/Cooking/items",
                    body: [
                            'wikiDoc': 'Test WikiDoc.',
                            'values.numberOfPeople': 10,
                            'values.fuel': 'Methane',
                            'values.kgCO2PerYear': 200],
                    requestContentType: URLENC,
                    contentType: JSON)

            // Is Location available?
            assertTrue responsePost.headers['Location'] != null
            assertTrue responsePost.headers['Location'].value != null
            def location = responsePost.headers['Location'].value
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Get new DataItem UID.
            def uid = location.split('/')[7]
            assertTrue uid != null

            // Success response
            assertOkJson responsePost, SUCCESS_CREATED.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)

            // Get the new DataItem.
            def responseGet = client.get(
                    path: "/${version}/categories/Cooking/items/${uid};full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseGet.status
            assertEquals 'application/json', responseGet.contentType
            assertTrue responseGet.data instanceof net.sf.json.JSON
            assertEquals 'OK', responseGet.data.status
            assertEquals 'Test WikiDoc.', responseGet.data.item.wikiDoc
            assertEquals 4, responseGet.data.item.values.size()
            assertTrue(['10', 'Methane', '', '200'].sort() == responseGet.data.item.values.collect {it.value}.sort())
            assertTrue(['numberOfPeople', 'fuel', 'source', 'kgCO2PerYear'].sort() == responseGet.data.item.values.collect {it.path}.sort())

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)

            // Then delete it.
            def responseDelete = client.delete(path: "/${version}/categories/Cooking/items/${uid}")
            assertOkJson responseDelete, SUCCESS_OK.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)

            // We should get a 404 here.
            try {
                client.get(path: "/${version}/categories/Cooking/items/${uid}")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals CLIENT_ERROR_NOT_FOUND.code, e.response.status
            }
        }
    }

    /**
     * Tests creation of two new DataItems with the same category and drill down values.
     * The second DataItem should fail to be created.
     */
    @Test
    void createDuplicateDataItemJson() {
        versions.each { version -> createDuplicateDataItemJson(version) }
    }

    def createDuplicateDataItemJson(version) {
        if (version >= 3.6) {
            setAdminUser()

            // Create a DataItem
            def responsePost = client.post(
                path: "/${version}/categories/Cooking/items",
                body: ['values.numberOfPeople': 100, 'values.fuel': 'foo'],
                requestContentType: URLENC,
                contentType: JSON)

            // Get the UID
            def location = responsePost.headers['Location'].value
            def uid = location.split('/')[7]
            assertNotNull uid

            // Should have been created
            assertOkJson responsePost, SUCCESS_CREATED.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)

            // Try to create another data item with the same drilldown values.
            try {
                client.post(
                    path: "/${version}/categories/Cooking/items",
                    body: ['values.numberOfPeople': 100, 'values.fuel': 'foo'],
                    requestContentType: URLENC,
                    contentType: JSON)
            } catch (HttpResponseException e) {

                // Should have been rejected.
                assertEquals CLIENT_ERROR_BAD_REQUEST.code, e.response.status
            }

            // Delete it
            def responseDelete = client.delete(path: "/${version}/categories/Cooking/items/${uid}")
            assertOkJson responseDelete, SUCCESS_OK.code, uid

            // Create another data item with different value for one drilldown.
            responsePost = client.post(
                path: "/${version}/categories/Cooking/items",
                body: ['values.numberOfPeople': 100, 'values.fuel': 'bar'],
                requestContentType: URLENC,
                contentType: JSON)

            // Get the UID
            location = responsePost.headers['Location'].value
            uid = location.split('/')[7]
            assertNotNull uid

            // Should have been created
            assertOkJson responsePost, SUCCESS_CREATED.code, uid

            // Delete it
            responseDelete = client.delete(path: "/${version}/categories/Cooking/items/${uid}")
            assertOkJson responseDelete, SUCCESS_OK.code, uid
        }
    }

    /**
     * Test creation of two new DataItems with the same path. The second DataItem should fail to be created because
     * duplicate paths are not allowed.
     */
    @Test
    void createDuplicatePathJson() {
        versions.each { version -> createDuplicatePathJson(version) }
    }

    def createDuplicatePathJson(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a DataItem.
            def responsePost = client.post(
                    path: "/${version}/categories/Cooking/items",
                    body: ['path': 'testPath'],
                    requestContentType: URLENC,
                    contentType: JSON)

            def location = responsePost.headers['Location'].value
            def uid = location.split('/')[7]
            assertOkJson responsePost, SUCCESS_CREATED.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)
            try {

                // Create a DataItem.
                client.post(
                        path: "/${version}/categories/Cooking/items",
                        body: ['path': 'testPath'],
                        requestContentType: URLENC,
                        contentType: JSON)
                fail 'Should have been rejected'
            } catch (HttpResponseException e) {

                // Should have been rejected.
                assertEquals CLIENT_ERROR_BAD_REQUEST.code, e.response.status
            }

            // Then delete it.
            def responseDelete = client.delete(path: "/${version}/categories/Cooking/items/testPath")

            // Should have been deleted.
            assertOkJson responseDelete, SUCCESS_OK.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)
        }
    }

    /**
     * Test fetching a number of DataItems with JSON responses.
     *
     * This resource supports the 'startDate' parameter which behaves identically to the
     * DataItem values resource detailed and tested in com.amee.integration.DataItemValueIT.
     *
     * Data Item GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>audit - include the status, created and modified values.
     * <li>name - include the Data Item name.
     * <li>label - include the Data Item label.
     * <li>path - include the full Data Item path.
     * <li>parent - include the parent Category UID and wikiName values.
     * <li>wikiDoc - include the Data Item wikiDoc.
     * <li>provenance - include the Data Item provenance value.
     * <li>itemDefinition - include the ItemDefinition UID and name values
     * <li>values - include the Data Item Values' path and value.
     * </ul>
     *
     * The label field is only supported since version 3.2.
     */
    @Test
    void getDataItemsJson() {
        versions.each { version -> getDataItemsJson(version) }
    }

    def getDataItemsJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/Cooking/items;full",
                query: [resultLimit: '6'])
        assertEquals SUCCESS_OK.code, response.status
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

    /**
     * Test fetching a number of DataItems filtered to match a single value with JSON responses.
     *
     * The DataItem label field is only supported since version 3.2.
     */
    @Test
    void getFilteredDataItemsJson() {
        versions.each { version -> getFilteredDataItemsJson(version) }
    }

    def getFilteredDataItemsJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/Cooking/items;full",
                query: [numberOfPeople: '1'])
        assertEquals SUCCESS_OK.code, response.status
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

    /**
     * Test fetching a number of DataItems with XML responses.
     *
     * The DataItem label field is only supported since version 3.2.
     */
    @Test
    void getDataItemsXml() {
        versions.each { version -> getDataItemsXml(version) }
    }

    def getDataItemsXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/categories/Cooking/items;full",
                query: [resultLimit: '6'])
        assertEquals SUCCESS_OK.code, response.status
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

    /**
     * Test fetching a number of DataItems filtered to match a single value with XML responses.
     *
     * The DataItem label field is only supported since version 3.2.
     */
    @Test
    void getFilteredDataItemsXml() {
        versions.each { version -> getFilteredDataItemsXml(version) }
    }

    def getFilteredDataItemsXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/categories/Cooking/items;full",
                query: [numberOfPeople: '1'])
        assertEquals SUCCESS_OK.code, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals 'false', response.data.Items.@truncated.text()
        assertEquals 5, response.data.Items.Item.size()

        // Should not be sorted by label
        if (version >= 3.2) {
            assertTrue response.data.Items.Item[0].Label.text().compareToIgnoreCase(response.data.Items.Item[-1].Label.text()) > 0
        }
    }

    /**
     * Tests fetching a single DataItem with JSON response.
     *
     * The DataItem label field is only supported since version 3.2.
     */
    @Test
    void getDataItemOneGasJson() {
        versions.each { version -> getDataItemOneGasJson(version) }
    }

    def getDataItemOneGasJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/${version}/categories/Cooking/items/004CF30590A5;full")
        assertEquals SUCCESS_OK.code, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        if (version >= 3.2) {
            assertEquals '1, Gas', response.data.item.label
        }
        assertEquals '54C8A44254AA', response.data.item.categoryUid
        assertEquals 'Cooking', response.data.item.categoryWikiName
        assertEquals 'Cooking', response.data.item.itemDefinition.name
        assertEquals '/home/appliances/cooking/004CF30590A5', response.data.item.fullPath
        assertEquals oneGasItemValueValues.size(), response.data.item.values.size()
        assertTrue(oneGasItemValueValues == response.data.item.values.collect {it.value})
        assertTrue(oneGasItemValuePaths == response.data.item.values.collect {it.path})
    }

    /**
     * Tests fetching a single DataItem with XML response.
     *
     * The DataItem label field is only supported since version 3.2.
     */
    @Test
    void getDataItemOneGasXml() {
        versions.each { version -> getDataItemOneGasXml(version) }
    }

    def getDataItemOneGasXml(version) {
        client.contentType = XML
        def response = client.get(path: "/${version}/categories/Cooking/items/004CF30590A5;full")
        assertEquals SUCCESS_OK.code, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        if (version >= 3.2) {
            assertEquals '1, Gas', response.data.Item.Label.text()
        }
        assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text()
        assertEquals 'Cooking', response.data.Item.CategoryWikiName.text()
        assertEquals 'Cooking', response.data.Item.ItemDefinition.Name.text()
        assertEquals '/home/appliances/cooking/004CF30590A5', response.data.Item.FullPath.text()
        def allValues = response.data.Item.Values.Value
        assertEquals oneGasItemValueValues.size(), allValues.size()
        assertTrue(oneGasItemValueValues == allValues.Value*.text())
        assertTrue(oneGasItemValuePaths == allValues.Path*.text())
    }

    /**
     * Tests fetching a single DataItem with JSON response.
     *
     * The DataItem label field is only supported since version 3.2.
     */
    @Test
    void getDataItemTenElectricJson() {
        versions.each { version -> getDataItemTenElectricJson(version) }
    }

    def getDataItemTenElectricJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/${version}/categories/Cooking/items/9DD165D3AFC9;full")
        assertEquals SUCCESS_OK.code, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        if (version >= 3.2) {
            assertEquals '10, Electric', response.data.item.label
        }
        assertEquals '54C8A44254AA', response.data.item.categoryUid
        assertEquals 'Cooking', response.data.item.categoryWikiName
        assertEquals 'Cooking', response.data.item.itemDefinition.name
        assertEquals '/home/appliances/cooking/9DD165D3AFC9', response.data.item.fullPath
        assertEquals tenElectricItemValueValues.size(), response.data.item.values.size()
        assertTrue(tenElectricItemValueValues == response.data.item.values.collect {it.value})
        assertTrue(tenElectricItemValuePaths == response.data.item.values.collect {it.path})
    }

    /**
     * Tests fetching a single DataItem with XML response.
     *
     * The DataItem label field is only supported since version 3.2.
     */
    @Test
    void getDataItemTenElectricXml() {
        versions.each { version -> getDataItemTenElectricXml(version) }
    }

    def getDataItemTenElectricXml(version) {
        client.contentType = XML
        def response = client.get(path: "/${version}/categories/Cooking/items/9DD165D3AFC9;full")
        assertEquals SUCCESS_OK.code, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        if (version >= 3.2) {
            assertEquals '10, Electric', response.data.Item.Label.text()
        }
        assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text()
        assertEquals 'Cooking', response.data.Item.CategoryWikiName.text()
        assertEquals 'Cooking', response.data.Item.ItemDefinition.Name.text()
        assertEquals '/home/appliances/cooking/9DD165D3AFC9', response.data.Item.FullPath.text()
        def allValues = response.data.Item.Values.Value
        assertEquals tenElectricItemValueValues.size(), allValues.size()
        assertTrue(tenElectricItemValueValues == allValues.Value*.text())
        assertTrue(tenElectricItemValuePaths == allValues.Path*.text())
    }

    /**
     * Tests updating of a Data Item. Creates an empty Data Item, updates it then deletes it.
     *
     * The PUT parameters are the same as those for creating a Data Item (see above).
     */
    @Test
    void updateDataItemJson() {
        versions.each { version -> updateDataItemJson(version) }
    }

    /**
     * Although DataItem update has been supported since version 3.0 this test is limited to versions 3.4+ as
     * it depends on creating and removing a test DataItem.
     *
     * @param version to test
     */
    def updateDataItemJson(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a DataItem.
            def responsePost = client.post(
                    path: "/${version}/categories/Cooking/items",
                    body: ['path': 'aTestDataItem'],
                    requestContentType: URLENC,
                    contentType: JSON)

            def location = responsePost.headers['Location'].value
            def uid = location.split('/')[7]
            assertOkJson responsePost, SUCCESS_CREATED.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)

            // Update the DataItem.
            def responsePut = client.put(
                    path: "/${version}/categories/Cooking/items/aTestDataItem",
                    body: ['name': 'Test Name',
                            'wikiDoc': 'Test WikiDoc.',
                            'provenance': 'Test Provenance',
                            'values.numberOfPeople': 20,
                            'values.fuel': 'Petrol',
                            'values.kgCO2PerYear': '123'],
                    requestContentType: URLENC,
                    contentType: JSON)

            // Should have been updated.
            assertOkJson responsePut, SUCCESS_OK.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)

            // Get the DataItem and check values.
            def responseGet = client.get(
                    path: "/${version}/categories/Cooking/items/aTestDataItem;full",
                    contentType: JSON)
            assertEquals SUCCESS_OK.code, responseGet.status
            println responseGet.data
            assertEquals 'Test Name', responseGet.data.item.name
            assertEquals 'Test WikiDoc.', responseGet.data.item.wikiDoc
            assertEquals 'Test Provenance', responseGet.data.item.provenance
            assertEquals 4, responseGet.data.item.values.size()
            assertTrue(['20', 'Petrol', '', '123'].sort() == responseGet.data.item.values.collect {it.value}.sort())
            assertTrue(['numberOfPeople', 'fuel', 'source', 'kgCO2PerYear'].sort() == responseGet.data.item.values.collect {it.path}.sort())

            // Then delete it.
            def responseDelete = client.delete(path: "/${version}/categories/Cooking/items/aTestDataItem")

            // Should have been deleted.
            assertOkJson responseDelete, SUCCESS_OK.code, uid

            // Sleep a little to give the index a chance to be updated.
            sleep(2000)
        }
    }

    /**
     * Test that a DataItem cannot be updated if the user is not authorized.
     */
    @Test
    void updateDataItemUnauthorizedJson() {
        versions.each { version -> updateDataItemUnauthorizedJson(version) }
    }

    def updateDataItemUnauthorizedJson(version) {
        try {
            client.put(
                    path: "/${version}/categories/Cooking/items/9DD165D3AFC9",
                    body: ['name': 'newName'],
                    requestContentType: URLENC,
                    contentType: JSON)
            fail 'Expected 403'
        } catch (HttpResponseException e) {
            def response = e.response
            assertEquals CLIENT_ERROR_FORBIDDEN.code, response.status
            assertEquals CLIENT_ERROR_FORBIDDEN.code, response.data.status.code
            assertEquals 'Forbidden', response.data.status.name
        }
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
     * '/categories/{UID|wikiName}/items/{UID}/calculation' (since 3.4.0).
     *
     * Supply the input values with the values.{PATH} query params, eg values.energyPerTime=10.
     * Supply the input units with the units.{PATH} query params, eg units.energyPerTime=MWh.
     * Supply the input perUnits with the perUnits.{PATH} query params, eg perUnits.energyPerTime=month.
     */
    @Test
    void getDataItemCalculationDefaultUnitsJson() {
        versions.each { version -> getDataItemCalculationDefaultUnitsJson(version) }
    }

    def getDataItemCalculationDefaultUnitsJson(version) {
        if (version >= 3.4) {
            client.contentType = JSON

            // Default units
            def response = client.get(
                path: "/${version}/categories/Electricity_by_Country/items/963A90C107FA/calculation;full",
                query: ['values.energyPerTime': '10'])
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status

            // Output amounts
            assertEquals 1, response.data.amounts.size()
            def amount = response.data.amounts[0]
            assertEquals 'CO2', amount.type
            assertEquals 'kg', amount.unit
            assertEquals 'year', amount.perUnit
            assertEquals true, amount.default
            assertEquals "", 20.0, amount.value, 0.000001

            // Notes
            assertEquals 1, response.data.notes.size()
            assertEquals 'comment', response.data.notes[0].type
            assertEquals 'This is a comment', response.data.notes[0].value

            // Input values
            assertEquals 3, response.data.values.size()
            def itemValue = response.data.values.find { it.name == 'energyPerTime' }
            assertNotNull itemValue
            assertEquals '10', itemValue.value
            assertEquals 'kWh', itemValue.unit
            assertEquals 'year', itemValue.perUnit
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
    void getDataItemCalculationDefaultUnitsXml() {
        versions.each { version -> getDataItemCalculationDefaultUnitsXml(version) }
    }

    def getDataItemCalculationDefaultUnitsXml(version) {
        if (version >= 3.4) {
            client.contentType = XML
            def response = client.get(
                path: "/${version}/categories/Electricity_by_Country/items/963A90C107FA/calculation;full",
                query: ['values.energyPerTime': '10'])
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()

            // Output amounts
            assertEquals 1, response.data.Amounts.Amount.size()
            def amount = response.data.Amounts.Amount[0]
            assertEquals 'CO2', amount.@type.text()
            assertEquals 'kg', amount.@unit.text()
            assertEquals 'year', amount.@perUnit.text()
            assertEquals 'true', amount.@default.text()
            assertEquals 20.0, Double.parseDouble(amount.text()), 0.000001

            // Notes
            assertEquals 1, response.data.Notes.size()
            assertEquals 'comment', response.data.Notes.Note[0].@type.text()
            assertEquals 'This is a comment', response.data.Notes.Note[0].text()

            // Input values
            assertEquals 3, response.data.Values.Value.size()
            def itemValue = response.data.Values.Value.find { it.@name == 'energyPerTime' }
            assertNotNull itemValue
            assertEquals 10.0, Double.parseDouble(itemValue.text()), 0.000001
            assertEquals 'kWh', itemValue.@unit.text()
            assertEquals 'year', itemValue.@perUnit.text()
        }
    }

    /**
     * Tests a calculation using custom units and perUnits.
     */
    @Test
    void getDataItemCalculationCustomUnitsJson() {
        versions.each { version -> getDataItemCalculationCustomUnitsJson(version) }
    }

    def getDataItemCalculationCustomUnitsJson(version) {
        if (version >= 3.4) {
            client.contentType = JSON

            // Default units
            def response = client.get(
                path: "/${version}/categories/Electricity_by_Country/items/963A90C107FA/calculation;full",
                query: ['values.energyPerTime': '10', 'units.energyPerTime': 'MWh', 'perUnits.energyPerTime': 'month'])
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status

            // Output amounts
            assertEquals 1, response.data.amounts.size()
            def amount = response.data.amounts[0]
            assertEquals 'CO2', amount.type
            assertEquals 'kg', amount.unit
            assertEquals 'year', amount.perUnit
            assertEquals true, amount.default
            assertEquals "", 240000.0, amount.value, 0.000001

            // Notes
            assertEquals 1, response.data.notes.size()
            assertEquals 'comment', response.data.notes[0].type
            assertEquals 'This is a comment', response.data.notes[0].value

            // Input values
            assertEquals 3, response.data.values.size()
            def itemValue = response.data.values.find { it.name == 'energyPerTime' }
            assertNotNull itemValue
            assertEquals '10', itemValue.value
            assertEquals 'MWh', itemValue.unit
            assertEquals 'month', itemValue.perUnit
        }
    }

    @Test
    void getDataItemCalculationCustomUnitsXml() {
        versions.each { version -> getDataItemCalculationCustomUnitsXml(version) }
    }

    def getDataItemCalculationCustomUnitsXml(version) {
        if (version >= 3.4) {
            client.contentType = XML
            def response = client.get(
                path: "/${version}/categories/Electricity_by_Country/items/963A90C107FA/calculation;full",
                query: ['values.energyPerTime': '10', 'units.energyPerTime': 'MWh', 'perUnits.energyPerTime': 'month'])
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/xml', response.contentType
            assertEquals 'OK', response.data.Status.text()

            // Output amounts
            assertEquals 1, response.data.Amounts.Amount.size()
            def amount = response.data.Amounts.Amount[0]
            assertEquals 'CO2', amount.@type.text()
            assertEquals 'kg', amount.@unit.text()
            assertEquals 'year', amount.@perUnit.text()
            assertEquals 'true', amount.@default.text()
            assertEquals 240000.0, Double.parseDouble(amount.text()), 0.000001

            // Notes
            assertEquals 1, response.data.Notes.size()
            assertEquals 'comment', response.data.Notes.Note[0].@type.text()
            assertEquals 'This is a comment', response.data.Notes.Note[0].text()

            // Input values
            assertEquals 3, response.data.Values.Value.size()
            def itemValue = response.data.Values.Value.find { it.@name == 'energyPerTime' }
            assertNotNull itemValue
            assertEquals 10.0, Double.parseDouble(itemValue.text()), 0.000001
            assertEquals 'MWh', itemValue.@unit.text()
            assertEquals 'month', itemValue.@perUnit.text()
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
    void getDataItemCalculationInfinityAndNanJson() {
        versions.each { version -> getDataItemCalculationInfinityAndNanJson(version)}
    }

    def getDataItemCalculationInfinityAndNanJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON
            def response = client.get(path: "/${version}/categories/Computers_generic/items/651B5AE27940/calculation;full")
            assertEquals SUCCESS_OK.code, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals 2, response.data.amounts.size()
            assertTrue "Should have Infinity and NaN", hasInfinityAndNan(response.data.amounts)
        }
    }

    def hasInfinityAndNan(amounts) {
        def hasInfinity = false
        def hasNan = false

        amounts.each {
            if (it.type == 'infinity' && it.value == 'Infinity') {
                hasInfinity = true
            }
            if (it.type == 'nan' && it.value == 'NaN') {
                hasNan = true
            }
        }
        return hasInfinity && hasNan
    }

    /**
     * Tests the validation rules for the Data Item name field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Optional.
     * <li>Duplicates are allowed.
     * <li>No longer than 255 chars.
     * </ul>
     */
    @Test
    void updateWithInvalidName() {
        setAdminUser()
        updateDataItemFieldJson('name', 'long', String.randomString(256))
    }

    /**
     * Tests the validation rules for the Data Item path field.
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>Optional.
     * <li>Unique on lower case of entire string amongst peer Data Items (those belonging to the same Data Category).
     * <li>Intended to look like this: 'a_data_item_path' or 'apath' or 'anumberpath3'.
     * <li>Must match this regular expression: "^[a-zA-Z0-9_\\-]*$"
     * <li>Numbers and letters only, any case.
     * <li>No special character except underscores ('_') and hyphens ('-').
     * <li>No white space.
     * <li>No longer than 255 characters.
     * </ul>
     *
     * NOTE: The test for duplicates below depends on the DataItem with UID 'AE884BA62089' having the same path set.
     */
    @Test
    void updateWithInvalidPath() {
        setAdminUser()
        updateDataItemFieldJson('path', 'long', String.randomString(256))
        updateDataItemFieldJson('path', 'format', 'n o t v a l i d')
        updateDataItemFieldJson('path', 'duplicate', 'pathForUniqueTest')
    }

    /**
     * Tests the validation rules for the Data Item metadata field (wikiDoc, provenance).
     *
     * The rules are as follows:
     *
     * <ul>
     * <li>All are optional.
     * <li>wikiDoc must be no longer than 32767 characters.
     * <li>provenance must be no longer than 255 characters.
     * </ul>
     */
    @Test
    void updateWithInvalidMetadata() {
        setAdminUser()
        updateDataItemFieldJson('wikiDoc', 'long', String.randomString(32768))
        updateDataItemFieldJson('provenance', 'long', String.randomString(256))
    }

    /**
     * Tests the validation rules for the Data Item values.
     *
     * The rules depend on the Item Value Definition for the value being updated.
     *
     * <ul>
     * <li>integer values must not be empty and must be a Java integer.
     * <li>double values must not be empty and must be a Java double.
     * <li>text values are optional and must be no longer than 32767 characters.
     * </ul>
     */
    @Test
    void updateWithValues() {
        setAdminUser()
        updateDataItemFieldJson('values.numberOfPeople', 'typeMismatch', 'not_an_integer', 3.4)
        updateDataItemFieldJson('values.numberOfPeople', 'typeMismatch', '1.1', 3.4); // Not an integer either.
        updateDataItemFieldJson('values.numberOfPeople', 'typeMismatch', '', 3.4)
        updateDataItemFieldJson('values.kgCO2PerYear', 'typeMismatch', 'not_a_double', 3.4)
        updateDataItemFieldJson('values.kgCO2PerYear', 'typeMismatch', '', 3.4)
        updateDataItemFieldJson('values.fuel', 'long', String.randomString(32768), 3.4)
        updateDataItemFieldJson('values.source', 'long', String.randomString(32768), 3.4)
    }

    /**
     * Submits a single Data Item field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateDataItemFieldJson(field, code, value) {
        updateDataItemFieldJson(field, code, value, 3.0)
    }

    /**
     * Submits a single Data Item field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateDataItemFieldJson(field, code, value, since) {
        versions.each { version -> updateDataItemFieldJson(field, code, value, since, version) }
    }

    /**
     * Submits a single Data Item field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    void updateDataItemFieldJson(field, code, value, since, version) {
        if (version >= since) {
            try {
                // Create form body.
                def body = [:]
                body[field] = value
                // Update DataItem.
                client.put(
                        path: "/${version}/categories/Cooking/items/AE884BA62089",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail 'Response status code should have been 400 (' + field + ', ' + code + ').'
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assertEquals CLIENT_ERROR_BAD_REQUEST.code, response.status
                assertEquals 'application/json', response.contentType
                assertTrue response.data instanceof net.sf.json.JSON
                assertEquals 'INVALID', response.data.status
                assertTrue([field] == response.data.validationResult.errors.collect {it.field})
                assertTrue([code] == response.data.validationResult.errors.collect {it.code})
            }
        }
    }
}