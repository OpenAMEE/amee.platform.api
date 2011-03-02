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

    static def versions = [3.0, 3.2]

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
            '1B29E8DC98F0',
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
            'grid',
            'inc_tag_1',
            'inc_tag_2'];

    def tagCounts = [1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 3, 3, 4, 6, 7];

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
            '1B29E8DC98F0',
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
            'grid',
            'inc_tag_2'];

    def excTagCounts = [1, 1, 1, 1, 1, 2, 2, 3, 3, 6, 7];

    @Test
    void getAllTagsJson() {
        versions.each { version -> getAllTagsJson(version) }
    }

    def getAllTagsJson(version) {
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/tags")
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals tagNames.size(), response.data.tags.size()
        if (version >= 3.2) {
            assertEquals tagUids.sort(), response.data.tags.collect {it.uid}.sort();
        }
        // Tags are sorted by tag
        assertEquals tagNames.sort { a, b -> a.compareToIgnoreCase(b) }, response.data.tags.collect {it.tag};
        assertEquals tagCounts.sort(), response.data.tags.collect {it.count}.sort();
    }

    @Test
    void getAllTagsXml() {
        versions.each { version -> getAllTagsXml(version) }
    }

    def getAllTagsXml(version) {
        client.contentType = XML
        def response = client.get(
                path: "/${version}/tags")
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        def allTags = response.data.Tags.children()
        assertEquals tagNames.size(), allTags.size()
        if (version >= 3.2) {
            assertEquals tagUids.sort(), allTags.@uid*.text().sort();
        }
        // Tags are sorted by tag
        assertEquals tagNames.sort { a, b -> a.compareToIgnoreCase(b) }, allTags.Tag*.text();
        assertEquals tagCounts.sort(), allTags.Count*.text().collect {it.toInteger()}.sort();
    }

    @Test
    void getAllIncTagsJson() {
        versions.each { version -> getAllIncTagsJson(version) }
    }

    def getAllIncTagsJson(version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(
                    path: "/${version}/tags",
                    query: ['incTags': 'inc_tag_1,inc_tag_2'])
            assertEquals 200, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals incTagUids.size(), response.data.tags.size()
            assertEquals incTagUids.sort(), response.data.tags.collect {it.uid}.sort();
            assertEquals incTagNames.sort(), response.data.tags.collect {it.tag};
            assertEquals incTagCounts.sort(), response.data.tags.collect {it.count}.sort();
        }
    }

    @Test
    void getAllExcTagsJson() {
        versions.each { version -> getAllExcTagsJson(version) }
    }

    def getAllExcTagsJson(version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(
                    path: "/${version}/tags",
                    query: ['excTags': 'inc_tag_1'])
            assertEquals 200, response.status
            assertEquals 'application/json', response.contentType
            assertTrue response.data instanceof net.sf.json.JSON
            assertEquals 'OK', response.data.status
            assertEquals excTagUids.size(), response.data.tags.size()
            assertEquals excTagUids.sort(), response.data.tags.collect {it.uid}.sort();
            assertEquals excTagNames.sort { a, b -> a.compareToIgnoreCase(b) }, response.data.tags.collect {it.tag};
            assertEquals excTagCounts.sort(), response.data.tags.collect {it.count}.sort();
        }
    }

    @Test
    void getInvalidTagsJson() {
        setAdminUser();
        getInvalidTagsFieldJson('incTags', 'short', 'foo,a,bar', 3.2);
        getInvalidTagsFieldJson('incTags', 'long', String.randomString(256), 3.2);
        getInvalidTagsFieldJson('incTags', 'format', 'foo,n o t v a l i d', 3.2);
        getInvalidTagsFieldJson('excTags', 'short', 'a,bar', 3.2);
        getInvalidTagsFieldJson('excTags', 'long', String.randomString(256) + ',wee', 3.2);
        getInvalidTagsFieldJson('excTags', 'format', 'moo,n o t v a l i d,boo', 3.2);
    }

    /**
     * Test parameter validation. An error is expected.
     *
     * @param field that is being sent
     * @param code expected upon error
     * @param value to submit
     */
    def getInvalidTagsFieldJson(field, code, value) {
        getInvalidTagsFieldJson(field, code, value, 3.0)
    }

    /**
     * Test parameter validation. An error is expected.
     *
     * @param field that is being sent
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def getInvalidTagsFieldJson(field, code, value, since) {
        versions.each { version -> getInvalidTagsFieldJson(field, code, value, since, version) };
    }

    /**
     * Test parameter validation. An error is expected.
     *
     * @param field that is being sent
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    def getInvalidTagsFieldJson(field, code, value, since, version) {
        if (version >= since) {
            try {
                // Create query.
                def query = [:];
                query[field] = value;
                // Request Tags.
                client.contentType = JSON
                client.get(
                        path: "/${version}/tags",
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
        versions.each { version -> getTagByPathJson(path, version) }
    }

    def getTagByPathJson(path, version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(
                    path: "/${version}/tags/${path}");
            assertEquals 200, response.status;
            assertEquals 'application/json', response.contentType;
            assertTrue response.data instanceof net.sf.json.JSON;
            assertEquals 'OK', response.data.status;
            assertEquals 'ZBDV9V20SI2C', response.data.tag.uid;
            assertEquals 'Ecoinvent', response.data.tag.tag;
        }
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
        versions.each { version -> getTagByPathXml(path, version) }
    }

    def getTagByPathXml(path, version) {
        if (version >= 3.2) {
            client.contentType = XML
            def response = client.get(
                    path: "/${version}/tags/${path}");
            assertEquals 200, response.status;
            assertEquals 'application/xml', response.contentType;
            assertEquals 'OK', response.data.Status.text();
            assertEquals 'ZBDV9V20SI2C', response.data.Tag.@uid.text();
            assertEquals 'Ecoinvent', response.data.Tag.Tag.text();
        }
    }

    @Test
    void getTagsForCategoryJson() {
        versions.each { version -> getTagsForCategoryJson(version) }
    }

    def getTagsForCategoryJson(version) {
        def uids = ['932FD23CD3A2', 'D75DB884855F', '3A38136735C6', '000FD23CD3A2'];
        def names = ['actonco2', 'electrical', 'domestic', 'inc_tag_1'];
        client.contentType = JSON
        def response = client.get(
                path: "/${version}/categories/Appliances/tags")
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        if (version >= 3.2) {
            assertEquals uids.size(), response.data.tags.size()
            assertEquals uids.sort(), response.data.tags.collect {it.uid}.sort();
        }
        assertEquals names.sort { a, b -> a.compareToIgnoreCase(b) }, response.data.tags.collect {it.tag};
    }

    @Test
    void createAndRemoveTagJson() {
        versions.each { version -> createAndRemoveTagJson(version) }
    }

    def createAndRemoveTagJson(version) {
        if (version >= 3.2) {
            setAdminUser();
            client.contentType = JSON;
            // Create a new Tag.
            def responsePost = client.post(
                    path: "/${version}/tags",
                    body: [tag: 'tagtobedeleted'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, responsePost.status;
            // Then delete the Tag.
            def responseDelete = client.delete(path: "/${version}/tags/tagtobedeleted");
            assertEquals 200, responseDelete.status;
            // We should get a 404 here.
            try {
                client.get(path: "/${version}/tags/tagtobedeleted");
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
        }
    }

    @Test
    void createAndRemoveEntityTagJson() {
        versions.each { version -> createAndRemoveEntityTagJson(version) }
    }

    def createAndRemoveEntityTagJson(version) {
        if (version >= 3.2) {
            setAdminUser();
            client.contentType = JSON;
            // Check the category cannot be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], [], version);
            // Create a new Tag & EntityTag on a DataCategory.
            postTagToCategory('Kitchen_generic', 'entity_tag_to_be_deleted', version);
            // Sleep a little to give the index a chance to be updated.
            sleep(1000);
            // The EntityTag should exist.
            def responseGet = client.get(path: "/${version}/categories/Kitchen_generic/tags/entity_tag_to_be_deleted");
            assertEquals 200, responseGet.status;
            assertEquals 'entity_tag_to_be_deleted', responseGet.data.tag.tag;
            // Check the category can be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], ['Kitchen_generic'], version);
            // Then delete the EntityTag.
            def responseDelete = client.delete(path: "/${version}/categories/Kitchen_generic/tags/entity_tag_to_be_deleted");
            assertEquals 200, responseDelete.status;
            // Sleep a little to give the index a chance to be updated.
            sleep(1000);
            // We should get a 404 here for the EntityTag.
            try {
                client.get(path: "/${version}/categories/Kitchen_generic/tags/entity_tag_to_be_deleted");
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
            // Check the category cannot be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], [], version);
            // Create another EntityTag on another DataCategory.
            postTagToCategory('Entertainment_generic', 'entity_tag_to_be_deleted', version);
            // Sleep a little to give the index a chance to be updated.
            sleep(1000);
            // The EntityTag should exist.
            responseGet = client.get(path: "/${version}/categories/Entertainment_generic/tags/entity_tag_to_be_deleted");
            assertEquals 200, responseGet.status;
            assertEquals 'entity_tag_to_be_deleted', responseGet.data.tag.tag;
            // Check the category can be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], ['Entertainment_generic'], version);
            // Then delete the EntityTag.
            responseDelete = client.delete(path: "/${version}/categories/Entertainment_generic/tags/entity_tag_to_be_deleted");
            assertEquals 200, responseDelete.status;
            // Sleep a little to give the index a chance to be updated.
            sleep(1000);
            // We should get a 404 here for the EntityTag.
            try {
                client.get(path: "/${version}/categories/Entertainment_generic/tags/entity_tag_to_be_deleted");
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
            // Check the category cannot be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], [], version);
            // The Tag should still exist.
            responseGet = client.get(path: "/${version}/tags/entity_tag_to_be_deleted");
            assertEquals 200, responseGet.status;
            assertEquals 'entity_tag_to_be_deleted', responseGet.data.tag.tag;
            // Now delete the Tag.
            responseDelete = client.delete(path: "/${version}/tags/entity_tag_to_be_deleted");
            assertEquals 200, responseDelete.status;
            // We should get a 404 here for the Tag.
            try {
                client.get(path: "/${version}/tags/entity_tag_to_be_deleted");
                fail 'Should have thrown an exception';
            } catch (HttpResponseException e) {
                assertEquals 404, e.response.status;
            }
        }
    }

    @Test
    void createAndRemoveMultipleEntityTagsJson() {
        versions.each { version -> createAndRemoveMultipleEntityTagsJson(version) }
    }

    def createAndRemoveMultipleEntityTagsJson(version) {
        if (version >= 3.2) {
            setAdminUser();
            // Check categories cannot be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], [], version);
            // Create a new Tag & EntityTag on a DataCategory.
            postTagToCategory('Kitchen_generic', 'entity_tag_to_be_deleted', version);
            // Create a new EntityTag on another DataCategory.
            postTagToCategory('Entertainment_generic', 'entity_tag_to_be_deleted', version);
            // Sleep a little to give the index a chance to be updated.
            sleep(1000);
            // Check the categories can be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], ['Kitchen_generic', 'Entertainment_generic'], version);
            // Now delete the Tag.
            def responseDelete = client.delete(path: "/${version}/tags/entity_tag_to_be_deleted");
            assertEquals 200, responseDelete.status;
            // Sleep a little to give the index a chance to be updated.
            sleep(1000);
            // Check categories cannot be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], [], version);
        }
    }

    @Test
    void filterOnMultipleTagsJson() {
        versions.each { version -> filterOnMultipleTagsJson(version) }
    }

    def filterOnMultipleTagsJson(version) {
        setAdminUser();
        // Tag DataCategories.
        postTagToCategory('Kitchen_generic', 'test_tag_1', version);
        postTagToCategory('Entertainment_generic', 'test_tag_1', version);
        postTagToCategory('Entertainment_generic', 'test_tag_2', version);
        postTagToCategory('Entertainment_generic', 'test_tag_3', version);
        postTagToCategory('Computers_generic', 'test_tag_3', version);
        // Sleep a little to give the index a chance to be updated.
        sleep(1000);
        // Check the categories can be discovered.
        testFilterCategories(['tags': 'test_tag_1'], ['Kitchen_generic', 'Entertainment_generic'], version);
        testFilterCategories(['tags': 'test_tag_2'], ['Entertainment_generic'], version);
        testFilterCategories(['tags': 'test_tag_3'], ['Entertainment_generic', 'Computers_generic'], version);
        testFilterCategories(['tags': 'test_tag_1 OR test_tag_2 OR test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version);
        testFilterCategories(['tags': 'test_tag_1 test_tag_2 test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version);
        testFilterCategories(['tags': 'test_tag_1, test_tag_2, test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version);
        testFilterCategories(['tags': 'test_tag_1,test_tag_2,test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version);
        testFilterCategories(['tags': 'test_tag_3,test_tag_2,test_tag_1'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version);
        testFilterCategories(['tags': 'test_tag_1 AND test_tag_2 AND test_tag_3'], ['Entertainment_generic'], version);
        testFilterCategories(['tags': 'test_tag_2 OR test_tag_3', 'excTags': 'test_tag_1'], ['Computers_generic'], version);
        testFilterCategories(['tags': '(test_tag_2 OR test_tag_3) NOT test_tag_1'], ['Computers_generic'], version);
        // Check the categories can be searched for.
        if (version >= 3.2) {
            testSearchForCategories(['q': 'kitchen', 'tags': 'test_tag_1'], ['Kitchen_generic'], version);
            testSearchForCategories(['q': 'kitchen', 'tags': '-test_tag_1'], [], version);
            testSearchForCategories(['q': 'generic', 'tags': 'test_tag_3'], ['Entertainment_generic', 'Computers_generic'], version);
            testSearchForCategories(['q': 'generic', 'tags': 'test_tag_1,test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version);
            testSearchForCategories(['q': 'generic', 'tags': 'test_tag_1,test_tag_3', 'excTags': 'test_tag_2'], ['Kitchen_generic', 'Computers_generic'], version);
            testSearchForCategories(['q': 'generic', 'tags': '(test_tag_1 OR test_tag_3) NOT test_tag_2'], ['Kitchen_generic', 'Computers_generic'], version);
            testSearchForCategories(['q': 'blahblah', 'tags': 'test_tag_1'], [], version);
        }
        // Test tag counts.
        if (version >= 3.2) {
            testTags(['incTags': 'test_tag_1'],
                    ['electrical', 'entertainment', 'inc_tag_1', 'inc_tag_2', 'test_tag_1', 'test_tag_2', 'test_tag_3'],
                    [2, 1, 1, 1, 2, 1, 1], version);
            testTags(['incTags': 'test_tag_1, test_tag_2, test_tag_3'],
                    ['computer', 'electrical', 'entertainment', 'inc_tag_1', 'inc_tag_2', 'test_tag_1', 'test_tag_2', 'test_tag_3'],
                    [1, 3, 1, 1, 1, 2, 1, 2], version);
            testTags(['excTags': 'test_tag_1'],
                    ['actonco2', 'computer', 'country', 'deprecated', 'domestic', 'Ecoinvent', 'LCA', 'electrical', 'electricity', 'GHGP', 'inc_tag_1', 'inc_tag_2', 'test_tag_3', 'US', 'waste', 'grid'],
                    [1, 1, 2, 1, 1, 6, 2, 3, 3, 1, 2, 1, 7, 1, 1, 1], version);
        }
        // Now delete the Tags.
        // NOTE: For < 3.2 this leaves database in odd state.
        if (version >= 3.2) {
            def responseDelete = client.delete(path: "/${version}/tags/test_tag_1");
            assertEquals 200, responseDelete.status;
            responseDelete = client.delete(path: "/${version}/tags/test_tag_2");
            assertEquals 200, responseDelete.status;
            responseDelete = client.delete(path: "/${version}/tags/test_tag_3");
            assertEquals 200, responseDelete.status;
        }
    }

    private def postTagToCategory(category, tag, version) {
        def responsePost = client.post(
                path: "/${version}/categories/${category}/tags",
                body: [tag: tag],
                requestContentType: URLENC,
                contentType: JSON);
        assertEquals 201, responsePost.status
    }

    private def testFilterCategories(query, expected, version) {
        def responseGet = client.get(
                path: "/${version}/categories",
                query: query,
                contentType: JSON);
        assertEquals 200, responseGet.status;
        assertEquals 'Unexpected result. Is RabbitMQ running?', expected.size(), responseGet.data.categories.size();
        assert expected.sort() == responseGet.data.categories.collect {it.wikiName}.sort();
    }

    private def testSearchForCategories(query, expected, version) {
        query['types'] = 'DC';
        def responseGet = client.get(
                path: "/${version}/search",
                query: query,
                contentType: JSON);
        assertEquals 200, responseGet.status;
        assertEquals 'Unexpected result. Is RabbitMQ running?', expected.size(), responseGet.data.results.size();
        assert expected.sort() == responseGet.data.results.collect {it.wikiName}.sort();
    }

    private def testTags(query, expectedTags, expectedCounts, version) {
        def responseGet = client.get(
                path: "/${version}/tags",
                query: query,
                contentType: JSON);
        assertEquals 200, responseGet.status;
        assertEquals 'Unexpected result. Is RabbitMQ running?', expectedTags.size(), responseGet.data.tags.size();
        assert expectedTags.sort() == responseGet.data.tags.collect {it.tag}.sort();
        assert expectedCounts.sort() == responseGet.data.tags.collect {it.count}.sort();
    }

    @Test
    void updateTagJson() {
        versions.each { version -> updateTagJson(version) }
    }

    def updateTagJson(version) {
        if (version >= 3.2) {
            setAdminUser();
            // 1) Do the update.
            def responsePut = client.put(
                    path: "/${version}/tags/002FD23CD3A2",
                    body: ['tag': 'tag_updated'],
                    requestContentType: URLENC,
                    contentType: JSON);
            assertEquals 201, responsePut.status;
            // 2) Check values have been updated.
            def responseGet = client.get(
                    path: "/${version}/tags/002FD23CD3A2",
                    contentType: JSON);
            assertEquals 200, responseGet.status;
            assertEquals 'application/json', responseGet.contentType;
            assertTrue responseGet.data instanceof net.sf.json.JSON;
            assertEquals 'OK', responseGet.data.status;
            assertEquals 'tag_updated', responseGet.data.tag.tag;
        }
    }

    @Test
    void updateInvalidTagJson() {
        setAdminUser();
        updateTagFieldJson('tag', 'empty', '', 3.2);
        updateTagFieldJson('tag', 'short', 'a', 3.2);
        updateTagFieldJson('tag', 'long', String.randomString(256), 3.2);
        updateTagFieldJson('tag', 'format', 'n o t v a l i d', 3.2);
        updateTagFieldJson('tag', 'duplicate', 'electricity', 3.2);
    }

    /**
     * Submits a single Tag field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     */
    def updateTagFieldJson(field, code, value) {
        updateTagFieldJson(field, code, value, 3.0)
    }

    /**
     * Submits a single Tag field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     */
    def updateTagFieldJson(field, code, value, since) {
        versions.each { version -> updateTagFieldJson(field, code, value, since, version) };
    }

    /**
     * Submits a single Tag field value and tests the result. An error is expected.
     *
     * @param field that is being updated
     * @param code expected upon error
     * @param value to submit
     * @param since only to versions on or after this since value
     * @param version version to test
     */
    void updateTagFieldJson(field, code, value, since, version) {
        if (version >= since) {
            try {
                // Create form body.
                def body = [:];
                body[field] = value;
                // Update Tag.
                client.put(
                        path: "/${version}/tags/computer",
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
}