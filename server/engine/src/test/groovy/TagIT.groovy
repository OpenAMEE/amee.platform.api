import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

class TagIT extends BaseApiTest {

  def tagUids = [
          '932FD23CD3A2',
          '5708D3DBF601',
          'D75DB884855F',
          '3A38136735C6',
          '412E8C9F4C36',
          '16185B71DAF3',
          'A846C7FB8832',
          '26BFBD444E6B',
          '59ABE00632F7',
          '30D53C8213A2',
          'DE5E8C70DB60',
          'EA3E8C70DBFE'];

  def tagNames = [
          'actonco2',
          'deprecated',
          'electrical',
          'domestic',
          'entertainment',
          'computer',
          'GHGP',
          'country',
          'US',
          'waste',
          'electricity',
          'ecoinvent'];

  @Test
  void getAllTagsJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.2/tags')
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals tagUids.size(), response.data.tags.size()
    assertEquals tagUids.sort(), response.data.tags.collect {it.uid}.sort();
    assertEquals tagNames.sort(), response.data.tags.collect {it.tag}.sort();
  }

  @Test
  void getAllTagsXml() {
    client.contentType = XML
    def response = client.get(path: '/3.2/tags')
    assertEquals 200, response.status
    assertEquals 'application/xml', response.contentType
    assertEquals 'OK', response.data.Status.text()
    def allTags = response.data.Tags.children()
    assertEquals tagUids.size(), allTags.size()
    assertEquals tagUids.sort(), allTags.@uid*.text().sort();
    assertEquals tagNames.sort(), allTags.Tag*.text().sort();
  }

  @Test
  void getTagByTagJson() {
    getTagByPathJson('ecoinvent');
  }

  @Test
  void getTagByUidJson() {
    getTagByPathJson('EA3E8C70DBFE');
  }

  void getTagByPathJson(path) {
    client.contentType = JSON
    def response = client.get(path: '/3.2/tags/' + path);
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals 'EA3E8C70DBFE', response.data.tag.uid;
    assertEquals 'ecoinvent', response.data.tag.tag;
  }

  @Test
  void getTagByTagXml() {
    getTagByPathXml('ecoinvent');
  }

  @Test
  void getTagByUidXml() {
    getTagByPathXml('EA3E8C70DBFE');
  }

  void getTagByPathXml(path) {
    client.contentType = XML
    def response = client.get(path: '/3.2/tags/' + path);
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals 'EA3E8C70DBFE', response.data.Tag.@uid.text();
    assertEquals 'ecoinvent', response.data.Tag.Tag.text();
  }

  @Test
  void removeTagJson() {
    setAdminUser();
    // Create a new tag.
    def responsePost = client.post(
            path: "/3.2/tags",
            body: [tag: 'tag_to_be_deleted'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePost.status;
    // Then delete it
    def responseDelete = client.delete(path: '/3.2/tags/tag_to_be_deleted');
    assertEquals 200, responseDelete.status;
    // We should get a 404 here
    try {
      client.get(path: '/3.2/tags/tag_to_be_deleted');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }
}