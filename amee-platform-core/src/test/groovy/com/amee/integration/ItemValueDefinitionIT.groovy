package com.amee.integration

import com.amee.domain.data.ItemValueDefinition
import groovyx.net.http.HttpResponseException
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

/**
 * Tests for the Item Value Definition API.
 *
 */
class ItemValueDefinitionIT extends BaseApiTest {

    /**
     * Tests for creation, fetch and deletion of an Item Value Definition using JSON responses.
     *
     * Create a new Item Value Definition by POSTing to '/definitions/{UID}/values'
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>name
     * <li>path
     * <li>wikidoc
     * <li>value
     * <li>choices
     * <li>fromProfile
     * <li>fromData
     * <li>unit
     * <li>perUnit
     * <li>valueDefinition
     * <li>apiVersions
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) an Item Value Definition by sending a DELETE request to '/definitions/{UID}/values/{UID}'.
     */
    @Test
    void createDeleteItemValueDefinitionJson() {
        versions.each { version -> createDeleteItemValueDefinitionJson(version) }
    }

    def createDeleteItemValueDefinitionJson(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a new ItemValueDefinition
            def responsePost = client.post(
                    path: "/$version/definitions/65RC86G6KMRA/values",
                    body: [valueDefinition: 'OMU53CZCY970',
                            name: 'test',
                            path: 'foo',
                            value: 1,
                            choices: 'true,false',
                            fromProfile: 'true',
                            fromData: 'true',
                            unit: 'kg',
                            perUnit: 'month',
                            apiVersions: '1.0,2.0'],
                    requestContentType: URLENC,
                    contentType: JSON)
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")
            String uid = location.split('/')[7]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Get the new ItemValueDefinition
            def responseGet = client.get(path: "$location;full", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.itemValueDefinition.name == 'test'
            assert responseGet.data.itemValueDefinition.path == 'foo'
            assert responseGet.data.itemValueDefinition.value == 1
            assert responseGet.data.itemValueDefinition.choices == 'true,false'
            assert responseGet.data.itemValueDefinition.fromProfile
            assert responseGet.data.itemValueDefinition.fromData
            assert responseGet.data.itemValueDefinition.unit == 'kg/month'
            assert responseGet.data.itemValueDefinition.versions.size() == 2
            assert responseGet.data.itemValueDefinition.versions[0].version == '1.0'
            assert responseGet.data.itemValueDefinition.versions[1].version == '2.0'

            // Delete it
            def responseDelete = client.delete(path: location)
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Should get a 404 here
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals(CLIENT_ERROR_NOT_FOUND.code, e.response.status)
            }
        }
    }

    /**
     * Tests creating an Item Value Definition using XML.
     * XML must be used to create usages.
     *
     * @param version
     * @return
     */
    @Test
    void createDeleteItemValueDefinitionXml() {
        versions.each { version -> createDeleteItemValueDefinitionXml(version) }
    }

    def createDeleteItemValueDefinitionXml(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a new ItemValueDefinition
            def responsePost = client.post(
                    path: "/$version/definitions/65RC86G6KMRA/values",
                    body: itemValueDefinitionNewXml(),
                    requestContentType: XML,
                    contentType: XML)
            assert responsePost.status == 201
            String location = responsePost.headers['Location'].value
            assert location.startsWith("$config.api.protocol://$config.api.host")
            String uid = location.split('/')[7]
            assertOkXml(responsePost, SUCCESS_CREATED.code, uid)

            // Get the new ItemValueDefinition
            def responseGet = client.get(path: "$location;full", contentType: XML)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/xml'
            assert responseGet.data.Status.text() == 'OK'
            assert responseGet.data.ItemValueDefinition.Name.text() == 'test'
            assert responseGet.data.ItemValueDefinition.Path.text() == 'foo'
            assert responseGet.data.ItemValueDefinition.ItemDefinition.@uid.text() == '65RC86G6KMRA'
            assert responseGet.data.ItemValueDefinition.Value.text() == 'true'
            assert responseGet.data.ItemValueDefinition.Choices.text() == 'true,false'
            assert responseGet.data.ItemValueDefinition.FromProfile.text() == 'true'
            assert responseGet.data.ItemValueDefinition.FromData.text() == 'true'
            assert responseGet.data.ItemValueDefinition.Unit.text() == 'kg/month'
            def allVersions = responseGet.data.ItemValueDefinition.Versions.Version
            assert allVersions.size() == 2
            assert allVersions[0].text() == '1.0'
            assert allVersions[1].text() == '2.0'
            def allUsages = responseGet.data.ItemValueDefinition.Usages.Usage
            assert allUsages.size() == 2
            assert allUsages.Name*.text() == ['byResponsibleArea', 'totalEmissions']
            assert allUsages.Type*.text() == ['COMPULSORY', 'COMPULSORY']
            assert allUsages.@active*.text() == ['false', 'false']

            // Delete it
            def responseDelete = client.delete(path: location, contentType: XML)
            assertOkXml(responseDelete, SUCCESS_OK.code, uid)

            // Should get a 404 here
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    /**
     * Tests fetching a list of Item Value Definitions for an Item Definition with JSON response.
     *
     * Item Value Definition GET requests support the following matrix parameters to modify the response.
     *
     * <ul>
     * <li>full - include all values.
     * <li>name - include the name value.
     * <li>path - include the path value.
     * <li>value - include the value.
     * <li>audit - include the status, created and modified values.
     * <li>wikiDoc - include the wikiDoc value.
     * <li>itemDefinition - include the ItemDefinition UID and name values.
     * <li>valueDefinition - include the ValueDefinition UID, name and type values.
     * <li>usages - include the usages data.
     * <li>choices - include the choices values.
     * <li>units - include the unit and perUnit values.
     * <li>flags - include the drilldown, fromData and fromProfile flags.
     * <li>versions - include the version values.
     * </ul>
     *
     * Item Value Definitions are sorted by name.
     */
    @Test
    void getItemValueDefinitionsJson() {
        versions.each { version -> getItemValueDefinitionsJson(version) }
    }

    def getItemValueDefinitionsJson(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA/values;full", contentType: JSON)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.itemValueDefinitions.size() == 6

            // Should be sorted by name
            assert response.data.itemValueDefinitions.first().name.compareToIgnoreCase(response.data.itemValueDefinitions.last().name) < 0
        }
    }

    /**
     * Test fetching a number of Item Value Definitions with XML response.
     */
    @Test
    void getItemValueDefinitionsXml() {
        versions.each { version -> getItemValueDefinitionsXml(version) }
    }

    def getItemValueDefinitionsXml(version) {
        if (version >= 3.1) {
            def response = client.get(path: "/$version/definitions/65RC86G6KMRA/values;full", contentType: XML)
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            def allItemValueDefinitions = response.data.ItemValueDefinitions.ItemValueDefinition
            assert allItemValueDefinitions.size() == 6

            // Should be sorted by name
            assert allItemValueDefinitions[0].Name.text().compareToIgnoreCase(allItemValueDefinitions[-1].Name.text()) < 0
        }
    }

    /**
     * Test fetching a single Item Value Definition using JSON.
     */
    @Test
    void getItemValueDefinitionJson() {
        versions.each { version -> getItemValueDefinitionJson(version) }
    }

    def getItemValueDefinitionJson(version) {
        def response = client.get(path: "/$version/definitions/65RC86G6KMRA/values/9OWHW3DE3ZJF;full", contentType: JSON)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.itemValueDefinition.name == 'KWh Per Year'
        assert response.data.itemValueDefinition.path == 'kWhPerYear'
        assert response.data.itemValueDefinition.itemDefinition.uid == '65RC86G6KMRA'
        assert response.data.itemValueDefinition.itemDefinition.name == 'Computers Generic'
        if (version >= 3.1) {
            assert response.data.itemValueDefinition.valueDefinition.uid == 'OMU53CZCY970'
            assert response.data.itemValueDefinition.valueDefinition.name == 'amount'
            assert !response.data.itemValueDefinition.fromProfile
            assert response.data.itemValueDefinition.fromData
            assert response.data.itemValueDefinition.choices == ''
            assert response.data.itemValueDefinition.usages.size() == 2
            assert response.data.itemValueDefinition.usages.collect { it.name } == ['usage2', 'usage3']
            assert response.data.itemValueDefinition.usages.collect { it.type } == ['OPTIONAL', 'COMPULSORY']
            assert response.data.itemValueDefinition.usages.collect { it.active } == ['true', 'false']
            if (version >= 3.4) {
                assert response.data.itemValueDefinition.valueDefinition.valueType == 'DOUBLE'
            } else {
                assert response.data.itemValueDefinition.valueDefinition.valueType == 'DECIMAL'
            }
        }
    }

    /**
     * Test fetching a single Item Value Definition using JSON.
     */
    @Test
    void getItemValueDefinitionXml() {
        versions.each { version -> getItemValueDefinitionXml(version) }
    }

    def getItemValueDefinitionXml(version) {
        def response = client.get(path: "/$version/definitions/65RC86G6KMRA/values/9OWHW3DE3ZJF;full", contentType: XML)
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.ItemValueDefinition.Name.text() == 'KWh Per Year'
        assert response.data.ItemValueDefinition.Path.text() == 'kWhPerYear'
        assert response.data.ItemValueDefinition.ItemDefinition.@uid.text() == '65RC86G6KMRA'
        assert response.data.ItemValueDefinition.ItemDefinition.Name.text() == 'Computers Generic'
        if (version >= 3.1) {
            assert response.data.ItemValueDefinition.ValueDefinition.@uid.text() == 'OMU53CZCY970'
            assert response.data.ItemValueDefinition.ValueDefinition.Name.text() == 'amount'
            assert response.data.ItemValueDefinition.FromProfile.text() == 'false'
            assert response.data.ItemValueDefinition.FromData.text() == 'true'
            assert response.data.ItemValueDefinition.Choices.text() == ''
            def allUsages = response.data.ItemValueDefinition.Usages.Usage
            assert allUsages.size() == 2
            assert allUsages.Name*.text() == ['usage2', 'usage3']
            assert allUsages.Type*.text() == ['OPTIONAL', 'COMPULSORY']
            assert allUsages.@active*.text() == ['true', 'false']
            if (version >= 3.4) {
                assert response.data.ItemValueDefinition.ValueDefinition.ValueType.text() == 'DOUBLE'
            } else {
                assert response.data.ItemValueDefinition.ValueDefinition.ValueType.text() == 'DECIMAL'
            }
        }
    }

    /**
     * Tests updating an Item Value Definition using form parameters.
     */
    @Test
    void updateItemValueDefinitionJson() {
        versions.each { version -> updateItemValueDefinitionJson(version) }
    }

    def updateItemValueDefinitionJson(version) {
        setAdminUser()

        // 1) Do the update.
        def responsePut = client.put(
                path: "/$version/definitions/65RC86G6KMRA/values/9OWHW3DE3ZJF",
                body: [name: 'New Name', path: 'newPath', wikiDoc: 'New WikiDoc.'],
                requestContentType: URLENC,
                contentType: JSON)
        assertOkJson(responsePut, SUCCESS_OK.code, '9OWHW3DE3ZJF')

        // 2) Check values have been updated.
        def responseGet = client.get(path: "/$version/definitions/65RC86G6KMRA/values/9OWHW3DE3ZJF;full", contentType: JSON)
        assert responseGet.status == SUCCESS_OK.code
        assert responseGet.contentType == 'application/json'
        assert responseGet.data.status == 'OK'
        assert responseGet.data.itemValueDefinition.name == 'New Name'
        assert responseGet.data.itemValueDefinition.path == 'newPath'
        assert responseGet.data.itemValueDefinition.wikiDoc == 'New WikiDoc.'
    }

    /**
     * Tests updating an Item Value Definition using XML.
     */
    @Test
    void updateItemValueDefinitionXml() {
        versions.each { version -> updateItemValueDefinitionXml(version) }
    }

    def updateItemValueDefinitionXml(version) {
        if (version >= 3.1) {
            setAdminUser()

            def responsePut = client.put(
                    path: "/$version/definitions/65RC86G6KMRA/values/9OWHW3DE3ZJF",
                    body: itemValueDefinitionUpdateXml(),
                    requestContentType: XML,
                    contentType: XML)
            assertOkXml(responsePut, SUCCESS_OK.code, '9OWHW3DE3ZJF')

            def responseGet = client.get(path: "/$version/definitions/65RC86G6KMRA/values/9OWHW3DE3ZJF;full", contentType: XML)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/xml'
            assert responseGet.data.Status.text() == 'OK'
            assert responseGet.data.ItemValueDefinition.Name.text() == 'New Name XML'
            assert responseGet.data.ItemValueDefinition.Path.text() == 'newPathXml'
            assert responseGet.data.ItemValueDefinition.WikiDoc.text() == 'New WikiDoc XML'
        }
    }

    /**
     * Tests validation rules.
     *
     * <ul>
     *     <li>name - non-empty, min: 2, max: 255</li>
     *     <li>path - unique, alpha-numeric & underscore, non-empty, min: 2, max: 255</li>
     *     <li>wikiDoc - max: 32767</li>
     *     <li>values - max: 255</li>
     *     <li>choices - max: 255</li>
     * </ul>
     */
    @Test
    void updateInvalidItemValueDefinition() {
        setAdminUser()
        updateItemValueDefinitionFieldJson('name', 'empty', '')
        updateItemValueDefinitionFieldJson('name', 'short', 'a')
        updateItemValueDefinitionFieldJson('name', 'long', String.randomString(ItemValueDefinition.NAME_MAX_SIZE + 1))
        updateItemValueDefinitionFieldJson('path', 'empty', '')
        updateItemValueDefinitionFieldJson('path', 'short', 'a')
        updateItemValueDefinitionFieldJson('path', 'long', String.randomString(ItemValueDefinition.PATH_MAX_SIZE + 1))
        updateItemValueDefinitionFieldJson('path', 'format', 'n o t v a l i d')
        updateItemValueDefinitionFieldJson('path', 'duplicate', 'onStandby')

        // Long strings cause memory issues on CI build
//        updateItemValueDefinitionFieldJson('wikiDoc', 'long', String.randomString(ItemValueDefinition.WIKI_DOC_MAX_SIZE + 1))
        updateItemValueDefinitionFieldJson('value', 'long', String.randomString(ItemValueDefinition.VALUE_MAX_SIZE + 1))
        updateItemValueDefinitionFieldJson('choices', 'long', String.randomString(ItemValueDefinition.CHOICES_MAX_SIZE + 1))
    }

    /**
     * Submits a single Item Value Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateItemValueDefinitionFieldJson(field, code, value) {
        updateItemValueDefinitionFieldJson(field, code, value, 3.0)
    }

    /**
     * Submits a single Item Value Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateItemValueDefinitionFieldJson(field, code, value, since) {
        versions.each { version -> updateItemValueDefinitionFieldJson(field, code, value, since, version) }
    }

    /**
     * Submits a single Item Value Definition field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    void updateItemValueDefinitionFieldJson(field, code, value, since, version) {
        if (version >= since) {
            try {
                // Create form body.
                def body = [(field): value]
                // Update IVD (EEQ72W0K4WSK / 'Number Owned' / 'numberOwned').
                client.put(
                        path: "/$version/definitions/65RC86G6KMRA/values/EEQ72W0K4WSK",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail("Response status code should have been 400 (${field}, ${code}).")
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'
                assert response.data.validationResult.errors.collect { it.field } == [field]
                assert response.data.validationResult.errors.collect { it.code } == [code]
            }
        }
    }

    def itemValueDefinitionUpdateXml() {
        def ivd = '''\
            <ItemValueDefinition>
                <Name>New Name XML</Name>
                <Path>newPathXml</Path>
                <WikiDoc>New WikiDoc XML</WikiDoc>
            </ItemValueDefinition>
        '''
    }

    def itemValueDefinitionNewXml() {
        def ivd = '''\
        <ItemValueDefinition>
            <Name>test</Name>
            <Path>foo</Path>
            <Value>true</Value>
            <ValueDefinition>OMU53CZCY970</ValueDefinition>
            <Usages>
              <Usage>
                <Name>byResponsibleArea</Name>
                <Type>COMPULSORY</Type>
              </Usage>
              <Usage>
                <Name>totalEmissions</Name>
                <Type>COMPULSORY</Type>
              </Usage>
            </Usages>
            <Choices>true,false</Choices>
            <Unit>kg</Unit>
            <PerUnit>month</PerUnit>
            <FromData>true</FromData>
            <FromProfile>true</FromProfile>
            <Versions>1.0,2.0</Versions>
        </ItemValueDefinition>
        '''
    }
}
