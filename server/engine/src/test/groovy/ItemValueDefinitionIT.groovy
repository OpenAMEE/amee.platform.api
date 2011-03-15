import groovyx.net.http.HttpResponseException
import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

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
     * <li>wikioc
     * <li>value
     * <li>choices
     * <li>fromProfile
     * <li>fromData
     * <li>allowedRoles
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
    void createDeleteItemValueDefinition() {
        versions.each { version -> createDeleteItemValueDefinition(version) }
    }

    def createDeleteItemValueDefinition(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a new ItemValueDefinition
            def responsePost = client.post(
                    path: "/${version}/definitions/11D3548466F2/values",
                    body: ['valueDefinition': '45433E48B39F',
                            'name': 'test',
                            'path': 'foo',
                            'value': 'true',
                            'choices': 'true,false',
                            'fromProfile': 'true',
                            'fromData': 'true',
                            'unit': 'kg',
                            'perUnit': 'month',
                            'apiVersions': '1.0,2.0'],
                    requestContentType: URLENC,
                    contentType: JSON)
            assertEquals 201, responsePost.status
            def location = responsePost.headers['Location'].value;
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Get the new ItemValueDefinition
            def responseGet = client.get(
                    path: "${location};full",
                    contentType: JSON)
            assertEquals 200, responseGet.status
            assertEquals 'application/json', responseGet.contentType
            assertTrue responseGet.data instanceof net.sf.json.JSON
            assertEquals 'OK', responseGet.data.status
            assertEquals 'test', responseGet.data.itemValueDefinition.name
            assertEquals 'foo', responseGet.data.itemValueDefinition.path
            assertEquals 'true', responseGet.data.itemValueDefinition.value
            assertEquals 'true,false', responseGet.data.itemValueDefinition.choices
            assertTrue responseGet.data.itemValueDefinition.fromProfile
            assertTrue responseGet.data.itemValueDefinition.fromData
            assertEquals 'kg', responseGet.data.itemValueDefinition.unit
            assertEquals 'month', responseGet.data.itemValueDefinition.perUnit
            assertEquals 2, responseGet.data.itemValueDefinition.versions.size()
            assertEquals '1.0', responseGet.data.itemValueDefinition.versions[0].version
            assertEquals '2.0', responseGet.data.itemValueDefinition.versions[1].version

            // Delete it
            def responseDelete = client.delete(path: location)
            assertEquals 200, responseDelete.status

            // Should get a 404 here
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status
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
    @Ignore("PL-10487")
    void createDeleteItemValueDefinitionXml() {
        versions.each { version -> createDeleteItemValueDefinitionXml(version) }
    }

    def createDeleteItemValueDefinitionXml(version) {
        if (version >= 3.4) {
            setAdminUser()

            // Create a new ItemValueDefinition
            def responsePost = client.post(
                    path: "/${version}/definitions/11D3548466F2/values",
                    body: itemValueDefinitionNewXml(),
                    requestContentType: XML,
                    contentType: XML)
            assertEquals 201, responsePost.status
            def location = responsePost.headers['Location'].value;
            assertTrue location.startsWith("${config.api.protocol}://${config.api.host}")

            // Get the new ItemValueDefinition
            def responseGet = client.get(
                    path: "${location};full",
                    contentType: XML)
            assertEquals 200, responseGet.status
            assertEquals 'application/xml', responseGet.contentType
            assertEquals 'OK', responseGet.data.Status.text()
            assertEquals 'test', responseGet.data.ItemValueDefinition.Name.text()
            assertEquals 'foo', responseGet.data.ItemValueDefinition.Path.text()
            assertEquals '11D3548466F2', responseGet.data.ItemValueDefinition.ItemDefinition.@uid.text();
            assertEquals 'true', responseGet.data.ItemValueDefinition.Value.text()
            assertEquals 'true,false', responseGet.data.ItemValueDefinition.Choices.text()
            assertTrue responseGet.data.ItemValueDefinition.FromProfile.text()
            assertTrue responseGet.data.ItemValueDefinition.FromData.text()
            assertEquals 'kg', responseGet.data.ItemValueDefinition.Unit.text()
            assertEquals 'month', responseGet.data.ItemValueDefinition.PerUnit.text()
            assertEquals 2, responseGet.data.ItemValueDefinition.Versions.Version.size()
            assertEquals '1.0', responseGet.data.ItemValueDefinition.Versions.Version[0].text()
            assertEquals '2.0', responseGet.data.ItemValueDefinition.Versions.Version[1].text()
            def allUsages = responseGet.data.ItemValueDefinition.Usages.Usage;
            assertEquals 2, allUsages.size();
            assert ['usage2', 'usage3'] == allUsages.Name*.text();
            assert ['OPTIONAL', 'COMPULSORY'] == allUsages.Type*.text();
            assert ['true', 'false'] == allUsages.@active*.text();

            // Delete it
            def responseDelete = client.delete(path: location)
            assertEquals 200, responseDelete.status

            // Should get a 404 here
            try {
                client.get(path: location)
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status
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
        versions.each { version -> getItemValueDefinitionsJson(version) };
    }

    def getItemValueDefinitionsJson(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2/values;full",
                    contentType: JSON);
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals 6, response.data.itemValueDefinitions.size();

            // Should be sorted by name
            assertTrue response.data.itemValueDefinitions.first().name.compareToIgnoreCase(response.data.itemValueDefinitions.last().name) < 0
        }
    }

    /**
     * Test fetching a number of Item Value Definitions with XML response.
     */
    @Test
    void getItemValueDefinitionsXml() {
        versions.each { version -> getItemValueDefinitionsXml(version) };
    }

    def getItemValueDefinitionsXml(version) {
        if (version >= 3.1) {
            def response = client.get(
                    path: "/${version}/definitions/11D3548466F2/values;full",
                    contentType: XML);
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            def allItemValueDefinitions = response.data.ItemValueDefinitions.ItemValueDefinition;
            assertEquals 6, allItemValueDefinitions.size();

            // Should be sorted by name
            assertTrue allItemValueDefinitions[0].Name.text().compareToIgnoreCase(allItemValueDefinitions[-1].Name.text()) < 0
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
        def response = client.get(
                path: "/${version}/definitions/11D3548466F2/values/7B8149D9ADE7;full",
                contentType: JSON);
        assertEquals 200, response.status;
        assertEquals 'application/json', response.contentType;
        assertTrue response.data instanceof net.sf.json.JSON;
        assertEquals 'OK', response.data.status;
        assertEquals 'KWh Per Year', response.data.itemValueDefinition.name;
        assertEquals 'kWhPerYear', response.data.itemValueDefinition.path;
        assertEquals '11D3548466F2', response.data.itemValueDefinition.itemDefinition.uid;
        assertEquals 'Computers Generic', response.data.itemValueDefinition.itemDefinition.name;
        if (version >= 3.1) {
            assertEquals '013466CB8A7D', response.data.itemValueDefinition.valueDefinition.uid;
            assertEquals 'kWhPerYear', response.data.itemValueDefinition.valueDefinition.name;
            assertEquals false, response.data.itemValueDefinition.fromProfile;
            assertEquals true, response.data.itemValueDefinition.fromData;
            assertEquals '', response.data.itemValueDefinition.choices;
            assertEquals 2, response.data.itemValueDefinition.usages.size();
            assert ['usage2', 'usage3'] == response.data.itemValueDefinition.usages.collect {it.name};
            assert ['OPTIONAL', 'COMPULSORY'] == response.data.itemValueDefinition.usages.collect {it.type};
            assert ['true', 'false'] == response.data.itemValueDefinition.usages.collect {it.active};
            if (version >= 3.4) {
                assertEquals 'DOUBLE', response.data.itemValueDefinition.valueDefinition.valueType;
            } else {
                assertEquals 'DECIMAL', response.data.itemValueDefinition.valueDefinition.valueType;
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
        def response = client.get(
                path: "/${version}/definitions/11D3548466F2/values/7B8149D9ADE7;full",
                contentType: XML);
        assertEquals 200, response.status;
        assertEquals 'application/xml', response.contentType;
        assertEquals 'OK', response.data.Status.text();
        assertEquals 'KWh Per Year', response.data.ItemValueDefinition.Name.text();
        assertEquals 'kWhPerYear', response.data.ItemValueDefinition.Path.text();
        assertEquals '11D3548466F2', response.data.ItemValueDefinition.ItemDefinition.@uid.text();
        assertEquals 'Computers Generic', response.data.ItemValueDefinition.ItemDefinition.Name.text();
        if (version >= 3.1) {
            assertEquals '013466CB8A7D', response.data.ItemValueDefinition.ValueDefinition.@uid.text();
            assertEquals 'kWhPerYear', response.data.ItemValueDefinition.ValueDefinition.Name.text();
            assertEquals 'false', response.data.ItemValueDefinition.FromProfile.text();
            assertEquals 'true', response.data.ItemValueDefinition.FromData.text();
            assertEquals '', response.data.ItemValueDefinition.Choices.text();
            def allUsages = response.data.ItemValueDefinition.Usages.Usage;
            assertEquals 2, allUsages.size();
            assert ['usage2', 'usage3'] == allUsages.Name*.text();
            assert ['OPTIONAL', 'COMPULSORY'] == allUsages.Type*.text();
            assert ['true', 'false'] == allUsages.@active*.text();
            if (version >= 3.4) {
                assertEquals 'DOUBLE', response.data.ItemValueDefinition.ValueDefinition.ValueType.text();
            } else {
                assertEquals 'DECIMAL', response.data.ItemValueDefinition.ValueDefinition.ValueType.text();
            }
        }
    }

    /**
     * Tests updating an Item Value Definition using form parameters.
     */
    @Test
    void updateItemValueDefinitionJson() {
        versions.each { version -> updateItemValueDefinitionJson(version) };
    }

    def updateItemValueDefinitionJson(version) {
        setAdminUser();

        // 1) Do the update.
        def responsePut = client.put(
                path: "/${version}/definitions/11D3548466F2/values/64BC7A490F41",
                body: ['name': 'New Name',
                        'path': 'newPath',
                        'wikiDoc': 'New WikiDoc.'],
                requestContentType: URLENC,
                contentType: JSON);
        assertEquals 204, responsePut.status;

        // 2) Check values have been updated.
        def responseGet = client.get(
                path: "/${version}/definitions/11D3548466F2/values/64BC7A490F41;full",
                contentType: JSON);
        assertEquals 200, responseGet.status;
        assertEquals 'application/json', responseGet.contentType;
        assertTrue responseGet.data instanceof net.sf.json.JSON;
        assertEquals 'OK', responseGet.data.status;
        assertEquals 'New Name', responseGet.data.itemValueDefinition.name;
        assertEquals 'newPath', responseGet.data.itemValueDefinition.path;
        assertEquals 'New WikiDoc.', responseGet.data.itemValueDefinition.wikiDoc;
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
                    path: "/${version}/definitions/11D3548466F2/values/64BC7A490F41",
                    body: itemValueDefinitionUpdateXml(),
                    requestContentType: XML,
                    contentType: XML)
            assertEquals 204, responsePut.status

            def responseGet = client.get(
                    path: "/${version}/definitions/11D3548466F2/values/64BC7A490F41;full",
                    contentType: XML)
            assertEquals 200, responseGet.status
            assertEquals 'application/xml', responseGet.contentType
            assertEquals 'OK', responseGet.data.Status.text()
            assertEquals 'New Name XML', responseGet.data.ItemValueDefinition.Name.text()
            assertEquals 'newPathXml', responseGet.data.ItemValueDefinition.Path.text()
            assertEquals 'New WikiDoc XML', responseGet.data.ItemValueDefinition.WikiDoc.text()
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
        setAdminUser();
        updateItemValueDefinitionFieldJson('name', 'empty', '');
        updateItemValueDefinitionFieldJson('name', 'short', 'a');
        updateItemValueDefinitionFieldJson('name', 'long', String.randomString(256));
        updateItemValueDefinitionFieldJson('path', 'empty', '');
        updateItemValueDefinitionFieldJson('path', 'short', 'a');
        updateItemValueDefinitionFieldJson('path', 'long', String.randomString(256));
        updateItemValueDefinitionFieldJson('path', 'format', 'n o t v a l i d');
        updateItemValueDefinitionFieldJson('path', 'duplicate', 'onStandby');
        updateItemValueDefinitionFieldJson('wikiDoc', 'long', String.randomString(32768));
        updateItemValueDefinitionFieldJson('value', 'long', String.randomString(256));
        updateItemValueDefinitionFieldJson('choices', 'long', String.randomString(256));
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
        versions.each { version -> updateItemValueDefinitionFieldJson(field, code, value, since, version) };
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
                // Update IVD (64BC7A490F41 / 'Number Owned' / 'numberOwned').
                client.put(
                        path: "/${version}/definitions/11D3548466F2/values/64BC7A490F41",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail "Response status code should have been 400 (${field}, ${code})."
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assertEquals 400, response.status
                assertEquals 'application/json', response.contentType
                assertTrue response.data instanceof net.sf.json.JSON
                assertEquals 'INVALID', response.data.status
                assertTrue([field] == response.data.validationResult.errors.collect {it.field})
                assertTrue([code] == response.data.validationResult.errors.collect {it.code})
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
            <ValueDefinition>45433E48B39F</ValueDefinition>
            <Usages>
              <Usage active="true">
                <Name>byResponsibleArea</Name>
                <Type>COMPULSORY</Type>
              </Usage>
              <Usage active="true">
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
