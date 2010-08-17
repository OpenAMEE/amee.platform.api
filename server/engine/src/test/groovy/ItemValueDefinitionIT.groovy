import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class ItemValueDefinitionIT extends BaseApiTest {

  @Test
  @Ignore("Item Value Definition POST not implemented in API")
  void createItemValueDefinition() {
  }

  @Test
  void getItemValueDefinitionsJson() {
    client.contentType = JSON;
    def response = client.get(path: '/3.1/definitions/11D3548466F2/values;full');
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals 6, response.data.itemValueDefinitions.size();
  }

  @Test
  void getItemValueDefinitionsXml() {
    client.contentType = XML
    def response = client.get(path: '/3.1/definitions/11D3548466F2/values;full');
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    def allItemValueDefinitions = response.data.ItemValueDefinitions.ItemValueDefinition;
    assertEquals 6, allItemValueDefinitions.size();
  }

  @Test
  void getItemValueDefinitionJson() {
    client.contentType = JSON;
    def response = client.get(path: '/3.1/definitions/11D3548466F2/values/7B8149D9ADE7;full');
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals 'KWh Per Year', response.data.itemValueDefinition.name;
    assertEquals 'kWhPerYear', response.data.itemValueDefinition.path;
    assertEquals '', response.data.itemValueDefinition.choices;
    assertEquals false, response.data.itemValueDefinition.fromProfile;
    assertEquals true, response.data.itemValueDefinition.fromData;
    assertEquals '11D3548466F2', response.data.itemValueDefinition.itemDefinition.uid;
    assertEquals 2, response.data.itemValueDefinition.usages.size();
    assert ['usage2', 'usage3'] == response.data.itemValueDefinition.usages.collect {it.name};
    assert ['OPTIONAL', 'COMPULSORY'] == response.data.itemValueDefinition.usages.collect {it.type};
    assert ['true', 'false'] == response.data.itemValueDefinition.usages.collect {it.active};

  }

  @Test
  void getItemValueDefinitionXml() {
    client.contentType = XML
    def response = client.get(path: '/3.1/definitions/11D3548466F2/values/7B8149D9ADE7;full');
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals 'KWh Per Year', response.data.ItemValueDefinition.Name.text();
    assertEquals 'kWhPerYear', response.data.ItemValueDefinition.Path.text();
    assertEquals '', response.data.ItemValueDefinition.Choices.text();
    assertEquals 'false', response.data.ItemValueDefinition.FromProfile.text();
    assertEquals 'true', response.data.ItemValueDefinition.FromData.text();
    assertEquals '11D3548466F2', response.data.ItemValueDefinition.ItemDefinition.@uid.text();
    def allUsages = response.data.ItemValueDefinition.Usages.Usage;
    assertEquals 2, allUsages.size();
    assert ['usage2', 'usage3'] == allUsages.Name*.text();
    assert ['OPTIONAL', 'COMPULSORY'] == allUsages.Type*.text();
    assert ['true', 'false'] == allUsages.@active*.text();
  }
}
