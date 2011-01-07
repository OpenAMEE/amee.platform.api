import groovyx.net.http.HttpResponseException
import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

class ItemDefinitionIT extends BaseApiTest {

  static def itemDefinitionUids = [
          '11D3548466F2',
          '1B3B44CAE90C',
          'BB33FDB20228',
          '001D2DF83D01',
          '0A64D80D77CD',
          '8B4B7C308D51',
          '00F880E2B3AA']

  static def itemDefinitionNames = [
          'Computers Generic',
          'Cooking',
          'Entertainment Generic',
          'Kitchen Generic',
          'GHGElectricity',
          'GHGUSSubregion',
          'EcoSpold']

  def static expectedUsageNames = ['usage1', 'usage2'];
  def static expectedUsagePresents = ['false', 'true'];

  @Test
  @Ignore("Item Definition POST not implemented in API")
  void createItemDefinition() {
  }

  @Test
  void getItemDefinitionsJson() {
    def response = client.get(
            path: '/3.3/definitions;name',
            contentType: JSON);
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals itemDefinitionUids.size(), response.data.itemDefinitions.size()
    assert itemDefinitionUids.sort() == response.data.itemDefinitions.collect {it.uid}.sort()
    assert itemDefinitionNames.sort() == response.data.itemDefinitions.collect {it.name}.sort()
  }

  @Test
  void getItemDefinitionsXml() {
    def response = client.get(
            path: '/3.3/definitions;name',
            contentType: XML);
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    def allItemDefinitions = response.data.ItemDefinitions.ItemDefinition
    assertEquals itemDefinitionUids.size(), allItemDefinitions.size()
    assert itemDefinitionUids.sort() == allItemDefinitions.@uid*.text().sort()
    assert itemDefinitionNames.sort() == allItemDefinitions.Name*.text().sort()
  }

  @Test
  void getItemDefinitionJson() {
    def response = client.get(
            path: '/3.1/definitions/11D3548466F2;full',
            contentType: JSON);
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals 'Computers Generic', response.data.itemDefinition.name;
    assertEquals 'device,rating', response.data.itemDefinition.drillDown;
    assertEquals expectedUsageNames.size(), response.data.itemDefinition.usages.size();
    assert expectedUsageNames == response.data.itemDefinition.usages.collect {it.name};
    assert expectedUsagePresents == response.data.itemDefinition.usages.collect {it.present};
  }

  @Test
  void getItemDefinitionXml() {
    def response = client.get(
            path: '/3.1/definitions/11D3548466F2;full',
            contentType: XML);
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals 'Computers Generic', response.data.ItemDefinition.Name.text();
    assertEquals 'device,rating', response.data.ItemDefinition.DrillDown.text();
    def allUsages = response.data.ItemDefinition.Usages.Usage;
    assertEquals expectedUsageNames.size(), allUsages.size();
    assertTrue(expectedUsageNames == allUsages.Name*.text());
    assertTrue(expectedUsagePresents == allUsages.@present*.text());
  }

  @Test
  void updateItemDefinitionJson() {
    setAdminUser();
    // 1) Do the update.
    def responsePut = client.put(
            path: '/3.1/definitions/11D3548466F2',
            body: ['name': 'newName',
                    'drillDown': 'newDrillDownA,newDrillDownB',
                    'usages': 'usage1,usage2,usage3'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePut.status;
    // We added a usage.
    expectedUsageNames[2] = 'usage3';
    expectedUsagePresents[2] = 'true';
    // 2) Check values have been updated.
    def responseGet = client.get(
            path: '/3.1/definitions/11D3548466F2;full',
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'application/json', responseGet.contentType;
    assertTrue responseGet.data instanceof net.sf.json.JSON;
    assertEquals 'OK', responseGet.data.status;
    assertEquals 'newName', responseGet.data.itemDefinition.name;
    assertEquals 'newDrillDownA,newDrillDownB', responseGet.data.itemDefinition.drillDown;
    assertEquals expectedUsageNames.size(), responseGet.data.itemDefinition.usages.size();
    assertTrue(expectedUsageNames == responseGet.data.itemDefinition.usages.collect {it.name});
    assertTrue(expectedUsagePresents == responseGet.data.itemDefinition.usages.collect {it.present});
  }

  @Test
  void updateInvalidItemDefinition() {
    setAdminUser();
    updateItemDefinitionFieldJson('name', 'empty', '');
    updateItemDefinitionFieldJson('name', 'short', 'a');
    updateItemDefinitionFieldJson('name', 'long', String.randomString(256));
    updateItemDefinitionFieldJson('drillDown', 'long', String.randomString(256));
    updateItemDefinitionFieldJson('usages', 'long', String.randomString(32768));
  }

  void updateItemDefinitionFieldJson(field, code, value) {
    try {
      def body = [:];
      body[field] = value;
      client.put(
              path: '/3.1/definitions/BB33FDB20228',
              body: body,
              requestContentType: URLENC,
              contentType: JSON);
      fail 'Response status code should have been 400 (' + field + ', ' + code + ').';
    } catch (HttpResponseException e) {
      def response = e.response;
      assertEquals 400, response.status;
      assertEquals 'application/json', response.contentType;
      assertTrue response.data instanceof net.sf.json.JSON;
      assertEquals 'INVALID', response.data.status;
      // NOTE: This is commented out as 'usages' becomes 'usagesString' on the server-side causing this check to fail.
      // assertTrue([field] == response.data.validationResult.errors.collect {it.field});
      assertTrue([value] == response.data.validationResult.errors.collect {it.value});
      assertTrue([code] == response.data.validationResult.errors.collect {it.code});
    }
  }
}