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
          'EA3E8C70DBFE',
          '000FD23CD3A2',
          '001FD23CD3A2']

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
          'ecoinvent',
          'inc_tag_1',
          'inc_tag_2'];

  def tagCounts = [1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 6, 4, 2];

  def incTagUids = [
          '932FD23CD3A2',
          '5708D3DBF601',
          '3A38136735C6',
          'D75DB884855F',
          '412E8C9F4C36',
          '000FD23CD3A2',
          '001FD23CD3A2']

  def incTagNames = [
          'actonco2',
          'deprecated',
          'domestic',
          'electrical',
          'entertainment',
          'inc_tag_1',
          'inc_tag_2']

  def incTagCounts = [1, 1, 1, 3, 1, 3, 2]

  def excTagUids = [
          'D75DB884855F',
          '16185B71DAF3',
          'A846C7FB8832',
          '26BFBD444E6B',
          '59ABE00632F7',
          '30D53C8213A2',
          'DE5E8C70DB60',
          'EA3E8C70DBFE',
          '001FD23CD3A2']

  def excTagNames = [
          'electrical',
          'computer',
          'GHGP',
          'country',
          'US',
          'waste',
          'electricity',
          'ecoinvent',
          'inc_tag_2'];

  def excTagCounts = [1, 1, 6, 2, 2, 2, 1, 1, 1];

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
    assertEquals tagCounts.sort(), response.data.tags.collect {it.count}.sort();
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
    assertEquals tagCounts.sort(), allTags.Count*.text().collect {it.toInteger()}.sort();
  }

  @Test
  void getAllIncTagsJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.2/tags',
            query: ['incTags': 'inc_tag_1,inc_tag_2'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals incTagUids.size(), response.data.tags.size()
    assertEquals incTagUids.sort(), response.data.tags.collect {it.uid}.sort();
    assertEquals incTagNames.sort(), response.data.tags.collect {it.tag}.sort();
    assertEquals incTagCounts.sort(), response.data.tags.collect {it.count}.sort();
  }

  @Test
  void getAllExcTagsJson() {
    client.contentType = JSON
    def response = client.get(path: '/3.2/tags',
            query: ['excTags': 'inc_tag_1'])
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals excTagUids.size(), response.data.tags.size()
    assertEquals excTagUids.sort(), response.data.tags.collect {it.uid}.sort();
    assertEquals excTagNames.sort(), response.data.tags.collect {it.tag}.sort();
    assertEquals excTagCounts.sort(), response.data.tags.collect {it.count}.sort();
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
  void getTagsForCategoryJson() {
    def uids = ['932FD23CD3A2', 'D75DB884855F', '3A38136735C6', '000FD23CD3A2'];
    def names = ['actonco2', 'electrical', 'domestic', 'inc_tag_1'];
    client.contentType = JSON
    def response = client.get(path: '/3.2/categories/Appliances/tags')
    assertEquals 200, response.status
    assertEquals 'application/json', response.contentType
    assertTrue response.data instanceof net.sf.json.JSON
    assertEquals 'OK', response.data.status
    assertEquals uids.size(), response.data.tags.size()
    assertEquals uids.sort(), response.data.tags.collect {it.uid}.sort();
    assertEquals names.sort(), response.data.tags.collect {it.tag}.sort();
  }

  @Test
  void createAndRemoveTagJson() {
    setAdminUser();
    client.contentType = JSON;
    // Create a new Tag.
    def responsePost = client.post(
            path: "/3.2/tags",
            body: [tag: 'tag_to_be_deleted'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePost.status;
    // Then delete the Tag.
    def responseDelete = client.delete(path: '/3.2/tags/tag_to_be_deleted');
    assertEquals 200, responseDelete.status;
    // We should get a 404 here.
    try {
      client.get(path: '/3.2/tags/tag_to_be_deleted');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }

  @Test
  void createAndRemoveEntityTagJson() {
    setAdminUser();
    client.contentType = JSON;
    // Create a new Tag & EntityTag on a DataCategory.
    def responsePost = client.post(
            path: '/3.2/categories/Kitchen_generic/tags',
            body: [tag: 'entity_tag_to_be_deleted'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePost.status;
    // The EntityTag should exist.
    def responseGet = client.get(path: '/3.2/categories/Kitchen_generic/entity_tag_to_be_deleted');
    assertEquals 200, responseGet.status;
    // Then delete the EntityTag.
    def responseDelete = client.delete(path: '/3.2/categories/Kitchen_generic/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseDelete.status;
    // We should get a 404 here for the EntityTag.
    try {
      client.get(path: '/3.2/categories/Kitchen_generic/tags/entity_tag_to_be_deleted');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
    // The Tag should still exist.
    responseGet = client.get(path: '/3.2/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseGet.status;
    // Now delete the Tag.
    responseDelete = client.delete(path: '/3.2/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseDelete.status;
    // We should get a 404 here for the Tag.
    try {
      client.get(path: '/3.2/tags/entity_tag_to_be_deleted');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }

  @Test
  void updateTagJson() {
    setAdminUser();
    // 1) Do the update.
    def responsePut = client.put(
            path: '/3.2/tags/002FD23CD3A2',
            body: ['tag': 'tag_updated'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePut.status;
    // 2) Check values have been updated.
    def responseGet = client.get(
            path: '/3.2/tags/002FD23CD3A2',
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'application/json', responseGet.contentType;
    assertTrue responseGet.data instanceof net.sf.json.JSON;
    assertEquals 'OK', responseGet.data.status;
    assertEquals 'tag_updated', responseGet.data.tag.tag;
  }


  @Test
  void updateInvalidTagJson() {
    setAdminUser();
    updateTagFieldJson('tag', 'empty', '');
    updateTagFieldJson('tag', 'short', 'a');
    updateTagFieldJson('tag', 'long', String.randomString(256));
    updateTagFieldJson('tag', 'format', 'n o t v a l i d');
    updateTagFieldJson('tag', 'duplicate', 'electricity');
  }

  void updateTagFieldJson(field, code, value) {
    try {
      // Create form body.
      def body = [:];
      body[field] = value;
      // Update Tag.
      client.put(
              path: '/3.2/tags/computer',
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
      assertTrue([value] == response.data.validationResult.errors.collect {it.value});
      assertTrue([code] == response.data.validationResult.errors.collect {it.code});
    }
  }
}