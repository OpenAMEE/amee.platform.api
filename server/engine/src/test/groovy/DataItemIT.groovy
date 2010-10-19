import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.*

class DataItemIT extends BaseApiTest {

  def dataItemUids = [
          '004CF30590A5',
          '897513300787',
          '6C663D2B8681',
          '9EFA0CE242D0']

  @Test
  void getDataItemsJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.1/categories/Cooking/items;full')
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertFalse response.data.resultsTruncated
    assertEquals dataItemUids.size(), response.data.items.size()
    assert dataItemUids == response.data.items.collect {it.uid}
  }

  @Test
  void getDataItemsXml() {
    client.contentType = XML
    def response = client.get(path: '/3.1/categories/Cooking/items;full')
    assertEquals 200, response.status
    assertEquals 'application/xml', response.contentType
    assertEquals 'OK', response.data.Status.text()
    assertEquals 'false', response.data.Items.@truncated.text()
    def allDataItems = response.data.Items.Item
    assertEquals dataItemUids.size(), allDataItems.size()
    assert dataItemUids == allDataItems.@uid*.text()
  }

  @Test
  void getDataItemJson() {
    def response = client.get(
            path: '/3.1/categories/Cooking/items/004CF30590A5;full',
            contentType: JSON);
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals '54C8A44254AA', response.data.item.categoryUid;
    assertEquals 'Cooking', response.data.item.categoryWikiName;
    assertEquals 'Cooking', response.data.item.itemDefinition.name;
    assertEquals '/home/appliances/cooking/004CF30590A5', response.data.item.fullPath;
  }

  @Test
  void getDataItemXml() {
    def response = client.get(
            path: '/3.1/categories/Cooking/items/004CF30590A5;full',
            contentType: XML);
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals '54C8A44254AA', response.data.Item.CategoryUid.text();
    assertEquals 'Cooking', response.data.Item.CategoryWikiName.text();
    assertEquals 'Cooking', response.data.Item.ItemDefinition.Name.text();
    assertEquals '/home/appliances/cooking/004CF30590A5', response.data.Item.FullPath.text();
  }
}
