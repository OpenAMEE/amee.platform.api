import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class DataItemIT extends BaseApiTest {

  static def dataItemUids = [
          '004CF30590A5',
          '897513300787',
          'ZZ7513300787',
          '6C663D2B8681',
          '9EFA0CE242D0',
          'A81FD238C501'
  ].sort()

  static def oneGasItemValueValues = [
          '1',
          '188',
          'BRE/MTP/dgen/defra 2007',
          'Gas']

  static def oneGasItemValuePaths = [
          'numberOfPeople',
          'kgCO2PerYear',
          'source',
          'fuel']

  static def twoGasItemValueValues = [
          '2',
          '205',
          'BRE/MTP/dgen/defra 2007',
          'Gas']

  static def twoGasItemValuePaths = [
          'numberOfPeople',
          'kgCO2PerYear',
          'source',
          'fuel']

  @Test
  void getDataItemsJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/Cooking/items;full',
            query: ['resultLimit': '6'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertTrue response.data.resultsTruncated
    assertEquals dataItemUids.size(), response.data.items.size()
    def responseUids = response.data.items.collect { it.uid }.sort()
    assert dataItemUids == responseUids
  }

  @Test
  void getDataItemsXml() {
    client.contentType = XML
    def response = client.get(path: '/3.1/categories/Cooking/items;full',
            query: ['resultLimit': '6'])
    assertEquals 200, response.status
    assertEquals 'application/xml', response.contentType
    assertEquals 'OK', response.data.Status.text()
    assertEquals 'true', response.data.Items.@truncated.text()
    def allDataItems = response.data.Items.Item
    assertEquals dataItemUids.size(), allDataItems.size()
    assert dataItemUids == allDataItems.@uid*.text().sort()
  }

  @Test
  void getDataItemOneGasJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/Cooking/items/004CF30590A5;full');
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals '1, Gas', response.data.item.label;
    assertEquals '54C8A44254AA', response.data.item.categoryUid;
    assertEquals 'Cooking', response.data.item.categoryWikiName;
    assertEquals 'Cooking', response.data.item.itemDefinition.name;
    assertEquals '/home/appliances/cooking/004CF30590A5', response.data.item.fullPath;
    assertEquals oneGasItemValueValues.size(), response.data.item.values.size();
    assertTrue(oneGasItemValueValues == response.data.item.values.collect {it.value});
    assertTrue(oneGasItemValuePaths == response.data.item.values.collect {it.path});
  }

  @Test
  void getDataItemOneGasXml() {
    client.contentType = XML
    def response = client.get(path: '/3.1/categories/Cooking/items/004CF30590A5;full');
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals '1, Gas', response.data.Item.Label.text();
    assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text();
    assertEquals 'Cooking', response.data.Item.CategoryWikiName.text();
    assertEquals 'Cooking', response.data.Item.ItemDefinition.Name.text();
    assertEquals '/home/appliances/cooking/004CF30590A5', response.data.Item.FullPath.text();
    def allValues = response.data.Item.Values.Value;
    assertEquals oneGasItemValueValues.size(), allValues.size();
    assertTrue(oneGasItemValueValues == allValues.Value*.text());
    assertTrue(oneGasItemValuePaths == allValues.Path*.text());
  }

  @Test
  void getDataItemTwoGasJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/Cooking/items/897513300787;full');
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals '2, Gas', response.data.item.label;
    assertEquals '54C8A44254AA', response.data.item.categoryUid;
    assertEquals 'Cooking', response.data.item.categoryWikiName;
    assertEquals 'Cooking', response.data.item.itemDefinition.name;
    assertEquals '/home/appliances/cooking/897513300787', response.data.item.fullPath;
    assertEquals twoGasItemValueValues.size(), response.data.item.values.size();
    assertTrue(twoGasItemValueValues == response.data.item.values.collect {it.value});
    assertTrue(twoGasItemValuePaths == response.data.item.values.collect {it.path});
  }

  @Test
  void getDataItemTwoGasXml() {
    client.contentType = XML
    def response = client.get(path: '/3.1/categories/Cooking/items/897513300787;full');
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals '2, Gas', response.data.Item.Label.text();
    assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text();
    assertEquals 'Cooking', response.data.Item.CategoryWikiName.text();
    assertEquals 'Cooking', response.data.Item.ItemDefinition.Name.text();
    assertEquals '/home/appliances/cooking/897513300787', response.data.Item.FullPath.text();
    def allValues = response.data.Item.Values.Value;
    assertEquals twoGasItemValueValues.size(), allValues.size();
    assertTrue(twoGasItemValueValues == allValues.Value*.text());
    assertTrue(twoGasItemValuePaths == allValues.Path*.text());
  }

  @Test
  void updateDataItemJson() {
    setAdminUser();
    def responsePut = client.put(
            path: '/3.1/categories/Cooking/items/897513300787',
            body: ['name': 'newName',
                    'wikiDoc': 'wd',
                    'path': 'np',
                    'provenance': 'prov'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePut.status;
    def responseGet = client.get(
            path: '/3.1/categories/Cooking/items/897513300787;full',
            contentType: JSON);
    assertEquals 200, responseGet.status;
    println responseGet.data;
    assertEquals 'newName', responseGet.data.item.name;
    assertEquals 'wd', responseGet.data.item.wikiDoc;
    assertEquals 'np', responseGet.data.item.path;
    assertEquals 'prov', responseGet.data.item.provenance;
  }
}