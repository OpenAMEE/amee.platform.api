import groovyx.net.http.HttpResponseException
import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Item Value Definition API.
 *
 * TODO: Document Item Value Definition API fully here. See https://jira.amee.com/browse/PL-9549 to vote on this task.
 */
class ItemValueDefinitionIT extends BaseApiTest {

  // TODO: 3.0 tests.

    static def versions = [3.0, 3.1, 3.2, 3.3, 3.4]

  @Test
  @Ignore("Item Value Definition POST not implemented in API")
  void createItemValueDefinition() {
  }

  @Test
  void getItemValueDefinitionsJson() {
    def response = client.get(
            path: '/3.1/definitions/11D3548466F2/values;full',
            contentType: JSON);
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals 6, response.data.itemValueDefinitions.size();

    // Should be sorted by name
    assertTrue response.data.itemValueDefinitions.first().name.compareToIgnoreCase(response.data.itemValueDefinitions.last().name) < 0
  }

  @Test
  void getItemValueDefinitionsXml() {
    def response = client.get(
            path: '/3.1/definitions/11D3548466F2/values;full',
            contentType: XML);
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    def allItemValueDefinitions = response.data.ItemValueDefinitions.ItemValueDefinition;
    assertEquals 6, allItemValueDefinitions.size();

    // Should be sorted by name
    assertTrue allItemValueDefinitions[0].Name.text().compareToIgnoreCase(allItemValueDefinitions[-1].Name.text()) < 0
  }

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

  @Test
  void updateItemValueDefinitionJson() {
    setAdminUser();
    // 1) Do the update.
    def responsePut = client.put(
            path: '/3.1/definitions/11D3548466F2/values/64BC7A490F41',
            body: ['name': 'New Name',
                    'path': 'newPath',
                    'wikiDoc': 'New WikiDoc.'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePut.status;
    // 2) Check values have been updated.
    def responseGet = client.get(
            path: '/3.1/definitions/11D3548466F2/values/64BC7A490F41;full',
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'application/json', responseGet.contentType;
    assertTrue responseGet.data instanceof net.sf.json.JSON;
    assertEquals 'OK', responseGet.data.status;
    assertEquals 'New Name', responseGet.data.itemValueDefinition.name;
    assertEquals 'newPath', responseGet.data.itemValueDefinition.path;
    assertEquals 'New WikiDoc.', responseGet.data.itemValueDefinition.wikiDoc;
  }

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
  }

  void updateItemValueDefinitionFieldJson(field, code, value) {
    try {
      // Create form body.
      def body = [:];
      body[field] = value;
      // Update IVD (64BC7A490F41 / 'Number Owned' / 'numberOwned').
      client.put(
              path: '/3.1/definitions/11D3548466F2/values/64BC7A490F41',
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
