import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

class ItemDefinitionIT extends BaseApiTest {

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
    client.contentType = JSON;
    def response = client.get(path: '/3.1/definitions/11D3548466F2;full');
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals 'Computers Generic', response.data.itemDefinition.name;
    assertEquals 'device,rating', response.data.itemDefinition.drillDown;
    assertEquals 2, response.data.itemDefinition.usages.size();
    assert ['usage1', 'usage2'] == response.data.itemDefinition.usages.collect {it.name};
    assert ['false', 'true'] == response.data.itemDefinition.usages.collect {it.present};
  }

  @Test
  void getItemDefinitionXml() {
    client.contentType = XML
    def response = client.get(path: '/3.1/definitions/11D3548466F2;full');
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals 'Computers Generic', response.data.ItemDefinition.Name.text();
    assertEquals 'device,rating', response.data.ItemDefinition.DrillDown.text();
    def allUsages = response.data.ItemDefinition.Usages.Usage;
    assertEquals 2, allUsages.size();
    assert ['usage1', 'usage2'] == allUsages.Name*.text();
    assert ['false', 'true'] == allUsages.@present*.text();
  }
}
