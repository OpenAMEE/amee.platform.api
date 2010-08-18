import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class ReturnValueDefinitionIT extends BaseApiTest {

  // TODO: Remover.

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
}
