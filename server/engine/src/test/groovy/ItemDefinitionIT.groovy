import groovyx.net.http.HttpResponseException
import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

class ItemDefinitionIT extends BaseApiTest {

  // This starts off as 2 but will change in updateItemDefinition.
  def expectedUsagesCount = 2;
  def expectedUsageNames = ['usage1', 'usage2'];
  def expectedUsagePresents = ['false', 'true'];

  @Test
  @Ignore("Item Definition POST not implemented in API")
  void createItemDefinition() {
  }

  @Test
  @Ignore("Item Definition list GET not implemented in API")
  void getItemDefinitionsJson() {
  }

  @Test
  @Ignore("Item Definition list GET not implemented in API")
  void getItemDefinitionsXml() {
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
    assertEquals expectedUsagesCount, response.data.itemDefinition.usages.size();
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
    assertEquals expectedUsagesCount, allUsages.size();
    assertTrue(expectedUsageNames == allUsages.Name*.text());
    assertTrue(expectedUsagePresents == allUsages.@present*.text());
  }

  @Test
  void updateItemDefinition() {
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
    expectedUsagesCount++;
    expectedUsageNames = ['usage1', 'usage2', 'usage3'];
    expectedUsagePresents = ['false', 'true', 'true'];
    // 2) Check values have been updated.
    client.contentType = JSON;
    def responseGet = client.get(path: '/3.1/definitions/11D3548466F2;full');
    assertEquals 200, responseGet.status;
    assertEquals 'application/json', responseGet.contentType;
    assertTrue responseGet.data instanceof net.sf.json.JSON;
    assertEquals 'OK', responseGet.data.status;
    assertEquals 'newName', responseGet.data.itemDefinition.name;
    assertEquals 'newDrillDownA,newDrillDownB', responseGet.data.itemDefinition.drillDown;
    assertEquals expectedUsagesCount, responseGet.data.itemDefinition.usages.size();
    assertTrue(expectedUsageNames == responseGet.data.itemDefinition.usages.collect {it.name});
    assertTrue(expectedUsagePresents == responseGet.data.itemDefinition.usages.collect {it.present});
  }

  @Test
  void updateItemDefinitionNameEmpty() {
    updateItemDefinitionField('name', 'empty', '');
  }

  @Test
  void updateItemDefinitionNameShort() {
    updateItemDefinitionField('name', 'short', 'a');
  }

  @Test
  void updateItemDefinitionNameLong() {
    updateItemDefinitionField('name', 'long', String.randomString(256));
  }

  void updateItemDefinitionField(field, code, value) {
    try {
      def body = [:];
      body[field] = value;
      client.put(
              path: '/3.1/definitions/BB33FDB20228',
              body: body,
              requestContentType: URLENC,
              contentType: JSON);
      fail 'Response status code should have been 400.';
    } catch (HttpResponseException e) {
      def response = e.response;
      assertEquals 400, response.status;
      assertEquals 'application/json', response.contentType;
      assertTrue response.data instanceof net.sf.json.JSON;
      assertEquals 'INVALID', response.data.status;
      assertTrue([field] == response.data.validationResult.errors.collect {it.field});
      assertTrue([value] == response.data.validationResult.errors.collect {it.value});
      assertTrue([code] == response.data.validationResult.errors.collect {it.code});
    }
  }
}