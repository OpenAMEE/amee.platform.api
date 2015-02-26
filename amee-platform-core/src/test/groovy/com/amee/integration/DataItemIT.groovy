package com.amee.integration

import com.amee.domain.item.data.BaseDataItemTextValue
import com.amee.domain.item.data.DataItem

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import static org.restlet.data.Status.*
import groovyx.net.http.HttpResponseException

import org.junit.Test

/**
 * Tests for the Data Item API.
 */
class DataItemIT extends BaseApiTest {

    // Time in ms to wait for lucene index updates.
    public static final int SLEEP_TIME = 2000

    static def dataItemUids = ['1THYA02WQG6E', 'A3VIO0PCX3EC', 'GT8V0NZMCX9C', 'J8JH4QE9VRBI', 'R0H6PGN4YHQH', 'UFUBP8SU13AE']

    static def oneGasItemValueValues = [1, 188, 'BRE/MTP/dgen/defra 2007', 'Gas']

    static def oneGasItemValuePaths = ['numberOfPeople', 'kgCO2PerYear', 'source', 'fuel']

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
     * <li>units.{path}*
     * <li>perUnits.{path}*
     * </ul>
     *
     * The 'values.{path}', 'units.{path}' and 'perUnits.{path}' parameters can be used to set Data Item Values that
     * are drill downs or the first in a historical series.
     *
     * To set historical data item values, POST to /categories/{UID|wikiName}/items/{UID|path}/values/{path}.
     * See {@code DataItemValueIT} for examples.
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
                    path: "/$version/categories/Cooking/items",
                    body: [wikiDoc: 'Test WikiDoc.', 'values.numberOfPeople': 10, 'values.fuel': 'Methane',
                           'values.kgCO2PerYear': 200],
                    requestContentType: URLENC,
                    contentType: JSON)

            // Is Location available?
            assert responsePost.headers['Location'] != null
            assert responsePost.headers['Location'].value != null
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")

            // Get new DataItem UID.
            String uid = location.split('/')[7]
            assert uid != null

            // Success response
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Get the new DataItem.
            def responseGet = client.get(path: "$location;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.item.wikiDoc == 'Test WikiDoc.'
            assert responseGet.data.item.values.size() == 4
            assert responseGet.data.item.values.collect { it.value }.containsAll(['', 'Methane', 10, 200])
            assert responseGet.data.item.values.collect { it.path }.containsAll(['fuel', 'kgCO2PerYear', 'numberOfPeople', 'source'])

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Then delete it.
            def responseDelete = client.delete(path: "/$version/categories/Cooking/items/$uid")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // We should get a 404 here.
            try {
                client.get(path: "/$version/categories/Cooking/items/$uid")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
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
                path: "/$version/categories/Cooking/items",
                body: ['values.numberOfPeople': '100', 'values.fuel': 'foo'],
                requestContentType: URLENC,
                contentType: JSON)

            // Get the UID
            String location = responsePost.headers['Location'].value
            String uid = location.split('/')[7]

            // Should have been created
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Try to create another data item with the same drilldown values.
            try {
                client.post(
                    path: "/$version/categories/Cooking/items",
                    body: ['values.numberOfPeople': '100', 'values.fuel': 'foo'],
                    requestContentType: URLENC,
                    contentType: JSON)
            } catch (HttpResponseException e) {

                // Should have been rejected.
                assert e.response.status == CLIENT_ERROR_BAD_REQUEST.code
            }

            // Delete it
            def responseDelete = client.delete(path: location)
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)
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
                    path: "/$version/categories/Cooking/items",
                    body: [path: 'testPath'],
                    requestContentType: URLENC,
                    contentType: JSON)

            String location = responsePost.headers['Location'].value
            String uid = location.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)
            try {

                // Create a DataItem.
                client.post(
                        path: "/$version/categories/Cooking/items",
                        body: [path: 'testPath'],
                        requestContentType: URLENC,
                        contentType: JSON)
                fail 'Should have been rejected'
            } catch (HttpResponseException e) {

                // Should have been rejected.
                assert e.response.status == CLIENT_ERROR_BAD_REQUEST.code
            }

            // Then delete it.
            def responseDelete = client.delete(path: "/$version/categories/Cooking/items/testPath")

            // Should have been deleted.
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)
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
        def response = client.get(path: "/$version/categories/Cooking/items;full", query: [resultLimit: '6'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.resultsTruncated
        assert response.data.items.size() == dataItemUids.size()
        def responseUids = response.data.items.collect { it.uid }
        assert responseUids.sort() == dataItemUids.sort()

        // Should  be sorted by label
        if (version >= 3.2) {
            assert response.data.items.first().label.compareToIgnoreCase(response.data.items.last().label) < 0
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
        def response = client.get(path: "/$version/categories/Cooking/items;full", query: [numberOfPeople: '1'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.resultsTruncated == false
        assert response.data.items.size() == 5
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
        def response = client.get(path: "/$version/categories/Cooking/items;full", query: [resultLimit: '6'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.Items.@truncated.text() == 'true'
        def allDataItems = response.data.Items.Item
        assert allDataItems.size() == dataItemUids.size()
        assert allDataItems.@uid*.text().sort() == dataItemUids.sort()

        // Should  be sorted by label
        if (version >= 3.2) {
            assert allDataItems[0].Label.text().compareToIgnoreCase(allDataItems[-1].Label.text()) < 0
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
        def response = client.get(path: "/$version/categories/Cooking/items;full", query: [numberOfPeople: '1'])
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.Items.@truncated.text() == 'false'
        assert response.data.Items.Item.size() == 5
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
        def response = client.get(path: "/$version/categories/Cooking/items/8KV038HODIQ9;full")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        if (version >= 3.2) {
            assert response.data.item.label == 'Gas, 1'
        }
        assert response.data.item.categoryUid == '1H5QPB38KZ8Z'
        assert response.data.item.categoryWikiName == 'Cooking'
        assert response.data.item.itemDefinition.name == 'Cooking'
        assert response.data.item.fullPath == '/home/appliances/cooking/8KV038HODIQ9'
        assert response.data.item.values.size() == oneGasItemValueValues.size()
        if (version >= 3.1) {
			assert response.data.item.values.collect { it.value } == oneGasItemValueValues
		} else {
			// Versions before 3.1 expect all JSON values to be Strings, so a conversion is needed for non-string types
			assert response.data.item.values.collect { it.value }[0] == oneGasItemValueValues[0].toString()
			assert response.data.item.values.collect { it.value }[1] == oneGasItemValueValues[1].toString()
			assert response.data.item.values.collect { it.value }[2] == oneGasItemValueValues[2]
			assert response.data.item.values.collect { it.value }[3] == oneGasItemValueValues[3]
		}
        assert response.data.item.values.collect { it.path } == oneGasItemValuePaths
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
        def response = client.get(path: "/$version/categories/Cooking/items/8KV038HODIQ9;full")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        if (version >= 3.2) {
            assert response.data.Item.Label.text() == 'Gas, 1'
        }
        assert response.data.Item.CategoryUid.text() == '1H5QPB38KZ8Z'
        assert response.data.Item.CategoryWikiName.text() == 'Cooking'
        assert response.data.Item.ItemDefinition.Name.text() == 'Cooking'
        assert response.data.Item.FullPath.text() == '/home/appliances/cooking/8KV038HODIQ9'
        def allValues = response.data.Item.Values.Value
        assert allValues.size() == oneGasItemValueValues.size()
		// XML is all text, but oneGasItemValueValues contains integers for testing json datatypes
		assert allValues.Value[0].text() == oneGasItemValueValues[0].toString()
		assert allValues.Value[1].text() == oneGasItemValueValues[1].toString()
		assert allValues.Value[2].text() == oneGasItemValueValues[2]
		assert allValues.Value[3].text() == oneGasItemValueValues[3]
        assert allValues.Path*.text() == oneGasItemValuePaths
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
                    path: "/$version/categories/Cooking/items",
                    body: [path: 'aTestDataItem'],
                    requestContentType: URLENC,
                    contentType: JSON)

            String location = responsePost.headers['Location'].value
            String uid = location.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Update the DataItem.
            def responsePut = client.put(
                    path: "/$version/categories/Cooking/items/aTestDataItem",
                    body: [name: 'Test Name',
                           wikiDoc: 'Test WikiDoc.',
                           provenance: 'Test Provenance',
                           'values.numberOfPeople': 20,
                           'values.fuel': 'Petrol',
                           'values.kgCO2PerYear': 123],
                    requestContentType: URLENC,
                    contentType: JSON)

            // Should have been updated.
            assertOkJson(responsePut, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Get the DataItem and check values.
            def responseGet = client.get(path: "/$version/categories/Cooking/items/aTestDataItem;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.item.name == 'Test Name'
            assert responseGet.data.item.wikiDoc == 'Test WikiDoc.'
            assert responseGet.data.item.provenance == 'Test Provenance'
            assert responseGet.data.item.values.size() == 4
			assert responseGet.data.item.values.collect { it.value }.sort() == ['', 'Petrol', 20, 123]
            assert responseGet.data.item.values.collect { it.path }.sort() == ['fuel', 'kgCO2PerYear', 'numberOfPeople', 'source']
			
            // Then delete it.
            def responseDelete = client.delete(path: "/$version/categories/Cooking/items/aTestDataItem")

            // Should have been deleted.
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)
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
                    path: "/$version/categories/Cooking/items/UFUBP8SU13AE",
                    body: [name: 'newName'],
                    requestContentType: URLENC,
                    contentType: JSON)
            fail 'Expected 403'
        } catch (HttpResponseException e) {
            def response = e.response
            assert response.status == CLIENT_ERROR_FORBIDDEN.code
            assert response.data.status.code == CLIENT_ERROR_FORBIDDEN.code
            assert response.data.status.name == 'Forbidden'
        }
    }

    /**
     * Tests overriding the units defined in the item value definition with units in the data item value.
     *
     * If a unit is defined for a data item value then that unit is used for display and calculation,
     * otherwise the unit from the item value definition is used.
     *
     * Values are always converted to the canonical 'internal value' with the units defined in the
     * item value definition before the calculation algorithm is run.
     */
    @Test
    void overrideUnitsJson() {
        versions.each { version -> overrideUnitsJson(version) }
    }

    def overrideUnitsJson(version) {
        if (version >= 3.6) {

            // Must be admin to update data items.
            setAdminUser()

            // Get the data item
            def responseGet = client.get(
                    path: "/$version/categories/IPCC_military_aircraft/items/ITBK7FUWNPJN/values/volumeFuelPerTime")
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.values[0].unit == "L/min"

            // Do a calculation
            responseGet = client.get(
                path: "/$version/categories/IPCC_military_aircraft/items/ITBK7FUWNPJN/calculation;full",
                query: ['values.flightDuration': 1])
            assert responseGet.status == SUCCESS_OK.code
            assertEquals(95.5576256544, responseGet.data.output.amounts[0].value, 0.000001)
            assert responseGet.data.output.amounts[0].unit == 'kg/year'
            
            // Update the unit and perUnit
            def responsePut = client.put(
                path: "/$version/categories/IPCC_military_aircraft/items/ITBK7FUWNPJN",
                body: ['units.volumeFuelPerTime': 'mL', 'perUnits.volumeFuelPerTime': 's'],
                requestContentType: URLENC,
                contentType: JSON)
            assertOkJson(responsePut, 200, 'ITBK7FUWNPJN')

            // The data item should show the overridden values.
            responseGet = client.get(
                path: "/$version/categories/IPCC_military_aircraft/items/ITBK7FUWNPJN/values/volumeFuelPerTime")
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.values[0].unit == "mL/s"

            // Do the calculation again
            // Algorithm: volumeFuelPerTime * flightDuration * fuelCO2Factor * RFI
            // 38.85 * 1 * (70.72 * 34.7802 / 1000) * 1
            // asInternalDecimal() label: IPCC military aircraft/volumeFuelPerTime,external: 38.85 mL/s,internal: 2.3310000000000004 L/min
            responseGet = client.get(
                path: "/$version/categories/IPCC_military_aircraft/items/ITBK7FUWNPJN/calculation;full",
                query: ['values.flightDuration': 1])
            assert responseGet.status == SUCCESS_OK.code
            assertEquals(5.733457539264001, responseGet.data.output.amounts[0].value, 0.000001)
            assert responseGet.data.output.amounts[0].unit == 'kg/year'
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
     * Supply the output units with the returnUnits.{TYPE} query params, eg returnUnits.CH4=g.
     * Supply the output perUnits with the returnPerUnits.{TYPE} query params, eg returnPerUnits.CH4=month.
     *
     * See {@link CategoryIT} for examples of performing calculations using drills to select the data item.
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
                path: "/$version/categories/Electricity_by_Country/items/HEDE07J83VR2/calculation;full",
                query: ['values.energyPerTime': 10])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'

            // Output amounts
            def amount
            if (version >= 3.6) {
                assert response.data.output.amounts.size() == 1
                amount = response.data.output.amounts[0]
                assert amount.unit == 'kg/year'
            } else {
                assert response.data.amounts.size() == 1
                amount = response.data.amounts[0]
                assert amount.unit == 'kg'
                assert amount.perUnit == 'year'
            }
            assert amount.type == 'CO2'
            assert amount.default == true
            assertEquals(20.0, amount.value, 0.000001)

            // Notes
            def note
            if (version >= 3.6) {
                assert response.data.output.notes.size() == 1
                note = response.data.output.notes[0]
            } else {
                assert response.data.notes.size() == 1
                note = response.data.notes[0]
            }
            assert note.type == 'comment'
            assert note.value == 'This is a comment'

            // User input values
            def userItemValue
            if (version >= 3.6) {
                assert response.data.input.values.size() == 8
                userItemValue = response.data.input.values.find { it.name == 'energyPerTime' }
                assert userItemValue.value == 10
                assert userItemValue.source == 'user'
            } else {
                assert response.data.values.size() == 3
                userItemValue = response.data.values.find { it.name == 'energyPerTime' }
                assert userItemValue.value == '10'
            }
            assert userItemValue.unit == 'kWh'
            assert userItemValue.perUnit == 'year'

            // Data item input values
            def dataItemValue
            if (version >= 3.6) {
                dataItemValue = response.data.input.values.find { it.name == 'massCO2PerEnergy' }
                assertEquals(0.0324402, dataItemValue.value, 0.000001)
                assert dataItemValue.source == 'amee'
                assert dataItemValue.unit == 'kg/(kW·h)'
            }
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
                path: "/$version/categories/Electricity_by_Country/items/HEDE07J83VR2/calculation;full",
                query: ['values.energyPerTime': 10])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'

            // Output amounts
            def amount
            if (version >= 3.6) {
                assert response.data.Output.Amounts.Amount.size() == 1
                amount = response.data.Output.Amounts.Amount[0]
                assert amount.@unit.text() == 'kg/year'
            } else {
                assert response.data.Amounts.Amount.size() == 1
                amount = response.data.Amounts.Amount[0]
                assert amount.@unit.text() == 'kg'
                assert amount.@perUnit.text() == 'year'
            }
            assert amount.@type.text() == 'CO2'
            assert amount.@default.text() == 'true'
            assertEquals(20.0, Double.parseDouble(amount.text()), 0.000001)

            // Notes
            def note
            if (version >= 3.6) {
                assert response.data.Output.Notes.size() == 1
                note = response.data.Output.Notes.Note[0]
            } else {
                assert response.data.Notes.size() == 1
                note = response.data.Notes.Note[0]
            }
            assert note.@type.text() == 'comment'
            assert note.text() == 'This is a comment'

            // User input values
            def userItemValue
            if (version >= 3.6) {
                assert response.data.Input.Values.Value.size() == 8
                userItemValue = response.data.Input.Values.Value.find { it.@name == 'energyPerTime' }
                assert userItemValue.@source.text() == 'user'
            } else {
                assert response.data.Values.Value.size() == 3
                userItemValue = response.data.Values.Value.find { it.@name == 'energyPerTime' }
                assert userItemValue != null
            }
            assertEquals(10.0, Double.parseDouble(userItemValue.text()), 0.000001)
            assert userItemValue.@unit.text() == 'kWh'
            assert userItemValue.@perUnit.text() == 'year'

            // Data item input values
            def dataItemValue
            if (version >= 3.6) {
                dataItemValue = response.data.Input.Values.Value.find { it.@name == 'massCO2PerEnergy' }
                assert dataItemValue.@source.text() == 'amee'
                assertEquals(0.0324402, Double.parseDouble(dataItemValue.text()), 0.000001)
                assert dataItemValue.@unit.text() == 'kg/(kW·h)'
            }
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
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/items/HEDE07J83VR2/calculation;full",
                query: ['values.energyPerTime': 10, 'units.energyPerTime': 'MWh', 'perUnits.energyPerTime': 'month'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'

            // Output amounts
            def amount
            if (version >= 3.6) {
                assert response.data.output.amounts.size() == 1
                amount = response.data.output.amounts[0]
                assert amount.unit == 'kg/year'
            } else {
                assert response.data.amounts.size() == 1
                amount = response.data.amounts[0]
                assert amount.unit == 'kg'
                assert amount.perUnit == 'year'
            }
            assert amount.type == 'CO2'
            assert amount.default == true
            assertEquals(240000.0, amount.value, 0.000001)

            // Notes
            def note
            if (version >= 3.6) {
                assert response.data.output.notes.size() == 1
                note = response.data.output.notes[0]
            } else {
                assert response.data.notes.size() == 1
                note = response.data.notes[0]
            }
            assert note.type == 'comment'
            assert note.value == 'This is a comment'

            // User input values
            def itemValue
            if (version >= 3.6) {
                assert response.data.input.values.size() == 8
                itemValue = response.data.input.values.find { it.name == 'energyPerTime' }
                assert itemValue.value == 10
                assert itemValue.source == 'user'
            } else {
                assert response.data.values.size() == 3
                itemValue = response.data.values.find { it.name == 'energyPerTime' }
                assert itemValue.value == '10'
            }
            assert itemValue.unit == 'MWh'
            assert itemValue.perUnit == 'month'
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
                path: "/$version/categories/Electricity_by_Country/items/HEDE07J83VR2/calculation;full",
                query: ['values.energyPerTime': 10, 'units.energyPerTime': 'MWh', 'perUnits.energyPerTime': 'month'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'

            // Output amounts
            def amount
            if (version >= 3.6) {
                assert response.data.Output.Amounts.Amount.size() == 1
                amount = response.data.Output.Amounts.Amount[0]
                assert amount.@unit.text() == 'kg/year'
            } else {
                assert response.data.Amounts.Amount.size() == 1
                amount = response.data.Amounts.Amount[0]
                assert amount.@unit.text() == 'kg'
                assert amount.@perUnit.text() == 'year'
            }
            assert amount.@type.text() == 'CO2'
            assert amount.@default.text() == 'true'
            assertEquals(240000.0, Double.parseDouble(amount.text()), 0.000001)

            // Notes
            def note
            if (version >= 3.6) {
                assert response.data.Output.Notes.size() == 1
                note = response.data.Output.Notes.Note[0]
            } else {
                assert response.data.Notes.size() == 1
                note = response.data.Notes.Note[0]
            }
            assert note.@type.text() == 'comment'
            assert note.text() == 'This is a comment'

            // Input values
            def itemValue
            if (version >= 3.6) {
                assert response.data.Input.Values.Value.size() == 8
                itemValue = response.data.Input.Values.Value.find { it.@name == 'energyPerTime' }
                assert itemValue.@source.text() == 'user'
            } else {
                assert response.data.Values.Value.size() == 3
                itemValue = response.data.Values.Value.find { it.@name == 'energyPerTime' }
                assert itemValue != null
            }
            assertEquals(10.0, Double.parseDouble(itemValue.text()), 0.000001)
            assert itemValue.@unit.text() == 'MWh'
            assert itemValue.@perUnit.text() == 'month'
        }
    }

    /**
     * Tests a calculation using custom returnUnits and returnPerUnits.
     */
    @Test
    void getDataItemCalculationCustomReturnUnitsJson() {
        versions.each { version -> getDataItemCalculationCustomReturnUnitsJson(version) }
    }

    def getDataItemCalculationCustomReturnUnitsJson(version) {
        if (version >= 3.4) {
            client.contentType = JSON
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/items/HEDE07J83VR2/calculation",
                query: ['values.energyPerTime': '10', 'returnUnits.CO2': 'g', 'returnPerUnits.CO2': 'month'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'

            // Output amounts
            def amount
            if (version >= 3.6) {
                assert response.data.output.amounts.size() == 1
                amount = response.data.output.amounts[0]
                assert amount.unit == 'g/month'
            } else {
                assert response.data.amounts.size() == 1
                amount = response.data.amounts[0]
                assert amount.unit == 'g'
                assert amount.perUnit == 'month'
            }
            assert amount.type == 'CO2'
            assert amount.default == true
            assertEquals(1666.66666666667, amount.value, 0.000001)
        }
    }

    @Test
    void getDataItemCalculationCustomReturnUnitsXml() {
        versions.each { version -> getDataItemCalculationCustomReturnUnitsXml(version) }
    }

    def getDataItemCalculationCustomReturnUnitsXml(version) {
        if (version >= 3.4) {
            client.contentType = XML
            def response = client.get(
                path: "/$version/categories/Electricity_by_Country/items/HEDE07J83VR2/calculation",
                query: ['values.energyPerTime': '10', 'returnUnits.CO2': 'g', 'returnPerUnits.CO2': 'month'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'

            // Output amounts
            def amount
            if (version >= 3.6) {
                assert response.data.Output.Amounts.Amount.size() == 1
                amount = response.data.Output.Amounts.Amount[0]
                assert amount.@unit.text() == 'g/month'
            } else {
                assert response.data.Amounts.Amount.size() == 1
                amount = response.data.Amounts.Amount[0]
                assert amount.@unit.text() == 'g'
                assert amount.@perUnit.text() == 'month'
            }
            assert amount.@type.text() == 'CO2'
            assert amount.@default.text() == 'true'
            assertEquals(1666.66666666667, Double.parseDouble(amount.text()), 0.000001)
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
        versions.each { version -> getDataItemCalculationInfinityAndNanJson(version) }
    }

    def getDataItemCalculationInfinityAndNanJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON
            def response = client.get(path: "/$version/categories/Computers_generic/items/4A1PR4YZSMIJ/calculation;full")
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            
            def amounts
            if (version >= 3.6) {
                amounts = response.data.output.amounts
            } else {
                amounts = response.data.amounts
            }
            assert amounts.size() == 2
            assertTrue("Should have Infinity and NaN", hasInfinityAndNan(amounts))
        }
    }

    /**
     * Tests a calculation that uses data item value histories (data series).
     * A startDate and endDate are supplied.
     */
    @Test
    void getDataItemCalculationHistoryJson() {
        versions.each { version -> getDataItemCalculationHistoryJson(version) }
    }
    
    def getDataItemCalculationHistoryJson(version) {
        if (version >= 3.6) {
            client.contentType = JSON

            // Default units
            // Georgia
            def response = client.get(
                path: "/$version/categories/Greenhouse_Gas_Protocol_international_electricity/items/39MEKTNO2AKJ/calculation;full",
                query: ['values.energyPerTime': '10', startDate: '2000-01-01T00:00:00Z', endDate: '2004-01-01T00:00:00Z'])
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
            assertEquals(1.1839605065023955, amount.value, 0.000001)

            // User input values
            def userItemValue
            assert response.data.input.values.size() == 6
            userItemValue = response.data.input.values.find { it.name == 'energyPerTime' }
            assert userItemValue.value == 10
            assert userItemValue.source == 'user'
            assert userItemValue.unit == 'kWh'

            // Data item input values
            def dataItemValue = response.data.input.values.find { it.name == 'massCO2PerEnergy' }
            assert dataItemValue.source == 'amee'
            assert dataItemValue.value.size() == 5
            def firstValue = dataItemValue.value.first()
            assert firstValue.startDate == '1970-01-01T00:00:00Z'
            assert firstValue.unit == 'kg/(kW·h)'
            assertEquals(0.1449678, firstValue.value, 0.000001)
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
                query: ['values.energyPerTime': '10', startDate: '2000-01-01T00:00:00Z', endDate: '2004-01-01T00:00:00Z'])
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
            assertEquals(1.1839605065023955, Double.parseDouble(amount.text()), 0.000001)

            // User input values
            def userItemValue
            assert response.data.Input.Values.Value.size() == 6
            userItemValue = response.data.Input.Values.Value.find { it.@name == 'energyPerTime' }
            assert userItemValue.@source.text() == 'user'
            assert Double.parseDouble(userItemValue.text()), 0.000001 == 10.0
            assert userItemValue.@unit.text() == 'kWh'

            // Data item input values
            def dataItemValue = response.data.Input.Values.Value.find { it.@name == 'massCO2PerEnergy' }
            assert dataItemValue.@source.text() == 'amee'
            assert dataItemValue.DataSeries.DataPoint.size() == 5
            def firstValue = dataItemValue.DataSeries.DataPoint[0]
            assert firstValue.@startDate == '1970-01-01T00:00:00Z'
            assert firstValue.@unit == 'kg/(kW·h)'
            assertEquals(0.1449678, Double.parseDouble(firstValue.text()), 0.000001)
        }
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
        updateDataItemFieldJson('name', 'long', String.randomString(DataItem.NAME_MAX_SIZE + 1))
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
     * NOTE: The test for duplicates below depends on the DataItem with UID 'A9290047911B' having the same path set.
     */
    @Test
    void updateWithInvalidPath() {
        setAdminUser()
        updateDataItemFieldJson('path', 'long', String.randomString(DataItem.PATH_MAX_SIZE + 1))
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
//        updateDataItemFieldJson('wikiDoc', 'long', String.randomString(DataItem.WIKI_DOC_MAX_SIZE + 1))
        updateDataItemFieldJson('provenance', 'long', String.randomString(DataItem.PROVENANCE_MAX_SIZE + 1))
    }

    /**
     * Tests the validation rules for the Data Item values.
     *
     * The rules depend on the Item Value Definition for the value being updated.
     *
     * <ul>
     * <li>integer values must not be empty and must be a Java integer.
     * <li>double values must not be empty and must be a Java double.
     * <li>text values are optional and must be no longer than 32767 characters. TODO: 255 chars?
     * </ul>
     */
    @Test
    void updateWithValues() {
        setAdminUser()
        updateDataItemFieldJson('values.description', 'long', String.randomString(BaseDataItemTextValue.VALUE_SIZE + 1), 3.4)
        updateDataItemFieldJson('values.volumeFuelPerTime', 'typeMismatch', 'not_a_double', 3.4)
        updateDataItemFieldJson('values.volumeFuelPerTime', 'typeMismatch', '', 3.4)

        updateDataItemFieldJson('units.volumeFuelPerTime', 'format', 'NOT_A_UNIT', 3.6)

        // Meter is not compatible with litre.
        updateDataItemFieldJson('units.volumeFuelPerTime', 'format', 'm', 3.6)
        updateDataItemFieldJson('perUnits.volumeFuelPerTime', 'format', 'NOT_A_UNIT', 3.6)

        // kg is not compatible with minute
        updateDataItemFieldJson('perUnits.volumeFuelPerTime', 'format', 'kg', 3.6)
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
                        path: "/$version/categories/IPCC_military_aircraft/items/ITBK7FUWNPJN",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail('Response status code should have been 400 (' + field + ', ' + code + ').')
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'
                assert [field] == response.data.validationResult.errors.collect { it.field }
                assert [code] == response.data.validationResult.errors.collect { it.code }
            }
        }
    }
}