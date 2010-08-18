import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

class ReturnValueDefinitionIT extends BaseApiTest {

  def static returnValueDefinitionUids = ['B0268549CD9C', '6008F958CE20'];
  def static returnValueDefinitionTypes = ['co2', 'co2e'];

  @Test
  void createReturnValueDefinition() {

    // Create a new RVD.
    def responsePost = client.post(
            path: "/3.1/definitions/11D3548466F2/returnvalues",
            body: [type: 'CO2', unit: 'kg', perUnit: 'month', valueDefinition: '45433E48B39F'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePost.status;
    assertTrue responsePost.headers['Location'] != null;
    assertTrue responsePost.headers['Location'].value != null;
    def location = responsePost.headers['Location'].value;
    // Add new RVD to local state.
    returnValueDefinitionUids[2] = location.split('/')[5];
    returnValueDefinitionTypes[2] = 'CO2';
    // Get the new RVD.
    def responseGet = client.get(
            path: location,
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'application/json', responseGet.contentType;
    assertTrue responseGet.data instanceof net.sf.json.JSON;
    assertEquals 'OK', responseGet.data.status;
  }

  @Test
  void removeReturnValueDefinitionJson() {

    // Create a new RVD.
    def responsePost = client.post(
            path: "/3.1/definitions/11D3548466F2/returnvalues",
            body: [type: 'CO2', unit: 'kg', perUnit: 'month', valueDefinition: '45433E48B39F'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePost.status;
    assertTrue responsePost.headers['Location'] != null;
    assertTrue responsePost.headers['Location'].value != null;
    def location = responsePost.headers['Location'].value;

    // Then delete it
    def responseDelete = client.delete(path: location);
    assertEquals 200, responseDelete.status;

    // We should get a 404 here
    try {
      client.get(path: location);
      fail 'Should have thrown exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }

  @Test
  void getReturnValueDefinitionJson() {
    def response = client.get(
            path: '/3.1/definitions/11D3548466F2/returnvalues/B0268549CD9C;full',
            contentType: JSON);
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals 'B0268549CD9C', response.data.returnValueDefinition.uid;
    assertEquals 'co2', response.data.returnValueDefinition.type;
    assertEquals 'kg', response.data.returnValueDefinition.unit;
    assertEquals 'month', response.data.returnValueDefinition.perUnit;
    assertEquals 'true', response.data.returnValueDefinition['default'];
    assertEquals '11D3548466F2', response.data.returnValueDefinition.itemDefinition.uid;
    assertEquals 'Computers Generic', response.data.returnValueDefinition.itemDefinition.name;
  }

  @Test
  void getReturnValueDefinitionXml() {
    def response = client.get(
            path: '/3.1/definitions/11D3548466F2/returnvalues/B0268549CD9C;full',
            contentType: XML);
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals 'B0268549CD9C', response.data.ReturnValueDefinition.@uid.text();
    assertEquals 'co2', response.data.ReturnValueDefinition.Type.text();
    assertEquals 'kg', response.data.ReturnValueDefinition.Unit.text();
    assertEquals 'month', response.data.ReturnValueDefinition.PerUnit.text();
    assertEquals 'true', response.data.ReturnValueDefinition.Default.text();
    assertEquals '11D3548466F2', response.data.ReturnValueDefinition.ItemDefinition.@uid.text();
    assertEquals 'Computers Generic', response.data.ReturnValueDefinition.ItemDefinition.Name.text();
  }

  @Test
  void getReturnValueDefinitionsJson() {
    def response = client.get(
            path: '/3.1/definitions/11D3548466F2/returnvalues',
            contentType: JSON);
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals returnValueDefinitionUids.size(), response.data.returnValueDefinitions.size();
    assertEquals returnValueDefinitionUids.sort(), response.data.returnValueDefinitions.collect {it.uid}.sort();
  }

  @Test
  void getReturnValueDefinitionsXml() {
    def response = client.get(
            path: '/3.1/definitions/11D3548466F2/returnvalues',
            contentType: XML);
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    def allReturnValueDefinitions = response.data.ReturnValueDefinitions.ReturnValueDefinition;
    assertEquals returnValueDefinitionUids.size(), allReturnValueDefinitions.size();
    assertEquals returnValueDefinitionUids.sort(), allReturnValueDefinitions.@uid*.text().sort();
  }

  @Test
  void updateReturnValueDefinitionJson() {
    // 1) Do the update.
    def responsePut = client.put(
            path: '/3.1/definitions/11D3548466F2/returnvalues/6008F958CE20',
            body: ['type': 'drink',
                    'unit': 'bbl',
                    'perUnit': 'day'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePut.status;
    // 2) Check values have been updated.
    def responseGet = client.get(
            path: '/3.1/definitions/11D3548466F2/returnvalues/6008F958CE20;full',
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'application/json', responseGet.contentType;
    assertTrue responseGet.data instanceof net.sf.json.JSON;
    assertEquals 'OK', responseGet.data.status;
    assertEquals 'drink', responseGet.data.returnValueDefinition.type;
    assertEquals 'bbl', responseGet.data.returnValueDefinition.unit;
    assertEquals 'day', responseGet.data.returnValueDefinition.perUnit;
  }

  @Test
  void updateInvalidReturnValueDefinition() {
    updateReturnValueDefinitionFieldJson('type', 'empty', '');
    updateReturnValueDefinitionFieldJson('type', 'long', String.randomString(256));
    updateReturnValueDefinitionFieldJson('unit', 'typeMismatch', 'not_a_unit');
    updateReturnValueDefinitionFieldJson('perUnit', 'typeMismatch', 'not_a_per_unit');
    updateReturnValueDefinitionFieldJson('valueDefinition', 'typeMismatch', 'AAAAAAAAAAAA');
  }

  void updateReturnValueDefinitionFieldJson(field, code, value) {
    try {
      def body = [:];
      body[field] = value;
      client.put(
              path: '/3.1/definitions/11D3548466F2/returnvalues/6008F958CE20',
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
      assertTrue([field] == response.data.validationResult.errors.collect {it.field});
      assertTrue([value] == response.data.validationResult.errors.collect {it.value});
      assertTrue([code] == response.data.validationResult.errors.collect {it.code});
    }
  }
}
