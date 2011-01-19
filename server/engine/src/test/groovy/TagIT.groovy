import groovyx.net.http.HttpResponseException
import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*

/**
 * Tests for the Data Category API.
 *
 * TODO: Document Tags API fully here. See https://jira.amee.com/browse/PL-9546 to vote on this task.
 */
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
          'ZBDV9V20SI2C',
          '5CECA47185F8',
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
          'Ecoinvent',
          'LCA',
          'inc_tag_1',
          'inc_tag_2'];

  def tagCounts = [1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 3, 4, 6, 7];

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
          'ZBDV9V20SI2C',
          '5CECA47185F8',
          '001FD23CD3A2']

  def excTagNames = [
          'electrical',
          'computer',
          'GHGP',
          'country',
          'US',
          'waste',
          'electricity',
          'Ecoinvent',
          'LCA',
          'inc_tag_2'];

  def excTagCounts = [1, 1, 1, 1, 1, 2, 2, 2, 6, 7];

  @Test
  void getAllTagsJson() {
    client.contentType = JSON
    def response = client.get(
            path: '/3.2/tags')
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
    def response = client.get(
            path: '/3.2/tags')
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
    def response = client.get(
            path: '/3.2/tags',
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
    def response = client.get(
            path: '/3.2/tags',
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
  void getInvalidTagsJson() {
    setAdminUser();
    getInvalidTagsFieldJson('incTags', 'short', 'foo,a,bar');
    getInvalidTagsFieldJson('incTags', 'long', String.randomString(256));
    getInvalidTagsFieldJson('incTags', 'format', 'foo,n o t v a l i d');
    getInvalidTagsFieldJson('excTags', 'short', 'a,bar');
    getInvalidTagsFieldJson('excTags', 'long', String.randomString(256) + ',wee');
    getInvalidTagsFieldJson('excTags', 'format', 'moo,n o t v a l i d,boo');
  }

  void getInvalidTagsFieldJson(field, code, value) {
    try {
      // Create query.
      def query = [:];
      query[field] = value;
      // Request Tags.
      client.contentType = JSON
      client.get(
              path: '/3.2/tags',
              query: query);
      fail 'Response status code should have been 400 (' + field + ', ' + code + ').';
    } catch (HttpResponseException e) {
      // Handle error response containing a ValidationResult.
      def response = e.response;
      assertEquals 400, response.status;
      assertEquals 'application/json', response.contentType;
      assertTrue response.data instanceof net.sf.json.JSON;
      assertEquals 'INVALID', response.data.status;
      assertTrue([field] == response.data.validationResult.errors.collect {it.field});
      assertTrue([code] == response.data.validationResult.errors.collect {it.code});
    }
  }

  @Test
  void getTagByTagJson() {
    getTagByPathJson('Ecoinvent');
  }

  @Test
  void getTagByUidJson() {
    getTagByPathJson('ZBDV9V20SI2C');
  }

  void getTagByPathJson(path) {
    client.contentType = JSON
    def response = client.get(
            path: '/3.2/tags/' + path);
    assertEquals 200, response.status;
    assertEquals 'application/json', response.contentType;
    assertTrue response.data instanceof net.sf.json.JSON;
    assertEquals 'OK', response.data.status;
    assertEquals 'ZBDV9V20SI2C', response.data.tag.uid;
    assertEquals 'Ecoinvent', response.data.tag.tag;
  }

  @Test
  void getTagByTagXml() {
    getTagByPathXml('Ecoinvent');
  }

  @Test
  void getTagByUidXml() {
    getTagByPathXml('ZBDV9V20SI2C');
  }

  void getTagByPathXml(path) {
    client.contentType = XML
    def response = client.get(
            path: '/3.2/tags/' + path);
    assertEquals 200, response.status;
    assertEquals 'application/xml', response.contentType;
    assertEquals 'OK', response.data.Status.text();
    assertEquals 'ZBDV9V20SI2C', response.data.Tag.@uid.text();
    assertEquals 'Ecoinvent', response.data.Tag.Tag.text();
  }

  @Test
  void getTagsForCategoryJson() {
    def uids = ['932FD23CD3A2', 'D75DB884855F', '3A38136735C6', '000FD23CD3A2'];
    def names = ['actonco2', 'electrical', 'domestic', 'inc_tag_1'];
    client.contentType = JSON
    def response = client.get(
            path: '/3.2/categories/Appliances/tags')
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
            body: [tag: 'tagtobedeleted'],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePost.status;
    // Then delete the Tag.
    def responseDelete = client.delete(path: '/3.2/tags/tagtobedeleted');
    assertEquals 200, responseDelete.status;
    // We should get a 404 here.
    try {
      client.get(path: '/3.2/tags/tagtobedeleted');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }

  @Test
  void createAndRemoveEntityTagJson() {
    setAdminUser();
    client.contentType = JSON;
    // Check the category cannot be discovered via the tag.
    testFilterCategories(['tags': 'entity_tag_to_be_deleted'], []);
    // Create a new Tag & EntityTag on a DataCategory.
    postTagToCategory('Kitchen_generic', 'entity_tag_to_be_deleted');
    // Sleep a little to give the index a chance to be updated.
    sleep(1000);
    // The EntityTag should exist.
    def responseGet = client.get(path: '/3.3/categories/Kitchen_generic/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseGet.status;
    assertEquals 'entity_tag_to_be_deleted', responseGet.data.tag.tag;
    // Check the category can be discovered via the tag.
    testFilterCategories(['tags': 'entity_tag_to_be_deleted'], ['Kitchen_generic']);
    // Then delete the EntityTag.
    def responseDelete = client.delete(path: '/3.3/categories/Kitchen_generic/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseDelete.status;
    // Sleep a little to give the index a chance to be updated.
    sleep(1000);
    // We should get a 404 here for the EntityTag.
    try {
      client.get(path: '/3.3/categories/Kitchen_generic/tags/entity_tag_to_be_deleted');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
    // Check the category cannot be discovered via the tag.
    testFilterCategories(['tags': 'entity_tag_to_be_deleted'], []);
    // Create another EntityTag on another DataCategory.
    postTagToCategory('Entertainment_generic', 'entity_tag_to_be_deleted');
    // Sleep a little to give the index a chance to be updated.
    sleep(1000);
    // The EntityTag should exist.
    responseGet = client.get(path: '/3.3/categories/Entertainment_generic/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseGet.status;
    assertEquals 'entity_tag_to_be_deleted', responseGet.data.tag.tag;
    // Check the category can be discovered via the tag.
    testFilterCategories(['tags': 'entity_tag_to_be_deleted'], ['Entertainment_generic']);
    // Then delete the EntityTag.
    responseDelete = client.delete(path: '/3.3/categories/Entertainment_generic/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseDelete.status;
    // Sleep a little to give the index a chance to be updated.
    sleep(1000);
    // We should get a 404 here for the EntityTag.
    try {
      client.get(path: '/3.3/categories/Entertainment_generic/tags/entity_tag_to_be_deleted');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
    // Check the category cannot be discovered via the tag.
    testFilterCategories(['tags': 'entity_tag_to_be_deleted'], []);
    // The Tag should still exist.
    responseGet = client.get(path: '/3.3/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseGet.status;
    assertEquals 'entity_tag_to_be_deleted', responseGet.data.tag.tag;
    // Now delete the Tag.
    responseDelete = client.delete(path: '/3.3/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseDelete.status;
    // We should get a 404 here for the Tag.
    try {
      client.get(path: '/3.3/tags/entity_tag_to_be_deleted');
      fail 'Should have thrown an exception';
    } catch (HttpResponseException e) {
      assertEquals 404, e.response.status;
    }
  }

  @Test
  void createAndRemoveMultipleEntityTagsJson() {
    setAdminUser();
    // Check categories cannot be discovered via the tag.
    testFilterCategories(['tags': 'entity_tag_to_be_deleted'], []);
    // Create a new Tag & EntityTag on a DataCategory.
    postTagToCategory('Kitchen_generic', 'entity_tag_to_be_deleted');
    // Create a new EntityTag on another DataCategory.
    postTagToCategory('Entertainment_generic', 'entity_tag_to_be_deleted');
    // Sleep a little to give the index a chance to be updated.
    sleep(1000);
    // Check the categories can be discovered via the tag.
    testFilterCategories(['tags': 'entity_tag_to_be_deleted'], ['Kitchen_generic', 'Entertainment_generic']);
    // Now delete the Tag.
    def responseDelete = client.delete(path: '/3.3/tags/entity_tag_to_be_deleted');
    assertEquals 200, responseDelete.status;
    // Sleep a little to give the index a chance to be updated.
    sleep(1000);
    // Check categories cannot be discovered via the tag.
    testFilterCategories(['tags': 'entity_tag_to_be_deleted'], []);
  }

  @Test
  void filterOnMultipleTagsJson() {
    setAdminUser();
    // Tag DataCategories.
    postTagToCategory('Kitchen_generic', 'test_tag_1');
    postTagToCategory('Entertainment_generic', 'test_tag_1');
    postTagToCategory('Entertainment_generic', 'test_tag_2');
    postTagToCategory('Entertainment_generic', 'test_tag_3');
    postTagToCategory('Computers_generic', 'test_tag_3');
    // Sleep a little to give the index a chance to be updated.
    sleep(1000);
    // Check the categories can be discovered.
    testFilterCategories(['tags': 'test_tag_1'], ['Kitchen_generic', 'Entertainment_generic']);
    testFilterCategories(['tags': 'test_tag_2'], ['Entertainment_generic']);
    testFilterCategories(['tags': 'test_tag_3'], ['Entertainment_generic', 'Computers_generic']);
    testFilterCategories(['tags': 'test_tag_1 OR test_tag_2 OR test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic']);
    testFilterCategories(['tags': 'test_tag_1 test_tag_2 test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic']);
    testFilterCategories(['tags': 'test_tag_1, test_tag_2, test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic']);
    testFilterCategories(['tags': 'test_tag_1,test_tag_2,test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic']);
    testFilterCategories(['tags': 'test_tag_3,test_tag_2,test_tag_1'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic']);
    testFilterCategories(['tags': 'test_tag_1 AND test_tag_2 AND test_tag_3'], ['Entertainment_generic']);
    testFilterCategories(['tags': 'test_tag_2 OR test_tag_3', 'excTags': 'test_tag_1'], ['Computers_generic']);
    testFilterCategories(['tags': '(test_tag_2 OR test_tag_3) NOT test_tag_1'], ['Computers_generic']);
    // Check the categories can be searched for.
    testSearchForCategories(['q': 'kitchen', 'tags': 'test_tag_1'], ['Kitchen_generic']);
    testSearchForCategories(['q': 'kitchen', 'tags': '-test_tag_1'], []);
    testSearchForCategories(['q': 'generic', 'tags': 'test_tag_3'], ['Entertainment_generic', 'Computers_generic']);
    testSearchForCategories(['q': 'generic', 'tags': 'test_tag_1,test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic']);
    testSearchForCategories(['q': 'generic', 'tags': 'test_tag_1,test_tag_3', 'excTags': 'test_tag_2'], ['Kitchen_generic', 'Computers_generic']);
    testSearchForCategories(['q': 'generic', 'tags': '(test_tag_1 OR test_tag_3) NOT test_tag_2'], ['Kitchen_generic', 'Computers_generic']);
    testSearchForCategories(['q': 'blahblah', 'tags': 'test_tag_1'], []);
    // Test tag counts.
    testTags(['incTags': 'test_tag_1'],
            ['electrical', 'entertainment', 'inc_tag_1', 'inc_tag_2', 'test_tag_1', 'test_tag_2', 'test_tag_3'],
            [2, 1, 1, 1, 2, 1, 1]);
    testTags(['incTags': 'test_tag_1, test_tag_2, test_tag_3'],
            ['computer', 'electrical', 'entertainment', 'inc_tag_1', 'inc_tag_2', 'test_tag_1', 'test_tag_2', 'test_tag_3'],
            [1, 3, 1, 1, 1, 2, 1, 2]);
    testTags(['excTags': 'test_tag_1'],
            ['actonco2', 'computer', 'country', 'deprecated', 'domestic', 'Ecoinvent', 'LCA', 'electrical', 'electricity', 'GHGP', 'inc_tag_1', 'inc_tag_2', 'test_tag_3', 'US', 'waste'],
            [1, 1, 1, 1, 1, 6, 7, 2, 2, 2, 2, 1, 1, 1, 1]);
    // Now delete the Tags.
    def responseDelete = client.delete(path: '/3.3/tags/test_tag_1');
    assertEquals 200, responseDelete.status;
    responseDelete = client.delete(path: '/3.3/tags/test_tag_2');
    assertEquals 200, responseDelete.status;
    responseDelete = client.delete(path: '/3.3/tags/test_tag_3');
    assertEquals 200, responseDelete.status;
  }

  private def postTagToCategory(category, tag) {
    def responsePost = client.post(
            path: '/3.3/categories/' + category + '/tags',
            body: [tag: tag],
            requestContentType: URLENC,
            contentType: JSON);
    assertEquals 201, responsePost.status
  }

  private def testFilterCategories(query, expected) {
    def responseGet = client.get(path: '/3.3/categories',
            query: query,
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'Unexpected result. Is RabbitMQ running?', expected.size(), responseGet.data.categories.size();
    assert expected.sort() == responseGet.data.categories.collect {it.wikiName}.sort();
  }

  private def testSearchForCategories(query, expected) {
    query['types'] = 'DC';
    def responseGet = client.get(path: '/3.3/search',
            query: query,
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'Unexpected result. Is RabbitMQ running?', expected.size(), responseGet.data.results.size();
    assert expected.sort() == responseGet.data.results.collect {it.wikiName}.sort();
  }

  private def testTags(query, expectedTags, expectedCounts) {
    def responseGet = client.get(path: '/3.3/tags',
            query: query,
            contentType: JSON);
    assertEquals 200, responseGet.status;
    assertEquals 'Unexpected result. Is RabbitMQ running?', expectedTags.size(), responseGet.data.tags.size();
    assert expectedTags.sort() == responseGet.data.tags.collect {it.tag}.sort();
    assert expectedCounts.sort() == responseGet.data.tags.collect {it.count}.sort();
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
      assertTrue([code] == response.data.validationResult.errors.collect {it.code});
    }
  }
}