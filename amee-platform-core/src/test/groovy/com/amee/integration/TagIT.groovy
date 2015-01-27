package com.amee.integration

import com.amee.domain.tag.Tag
import groovyx.net.http.HttpResponseException
import org.junit.Test

import static groovyx.net.http.ContentType.*
import static org.junit.Assert.assertEquals
import static org.junit.Assert.fail
import static org.restlet.data.Status.*

/**
 * Tests for the Tag API.
 *
 */
class TagIT extends BaseApiTest {

    // Time in ms to wait for lucene index updates.
    public static final int SLEEP_TIME = 2000

    def tags = [
            [uid: '00XAWWLZ6DHG', tag: 'electricity', count: 4],
            [uid: 'WI6NFTH4FK77', tag: 'GHGP', count: 3],
            [uid: '1ECBCD4IQCJ2', tag: 'US', count: 1],
            [uid: 'A5WXB7KUROF9', tag: 'deprecated', count: 2],
            [uid: 'YMCINO1P5A3O', tag: 'country', count: 2],
            [uid: '7I97KPQ44JUF', tag: 'grid', count: 1],
            [uid: 'E4P9V1X6AGJL', tag: 'waste', count: 1],
            [uid: 'CIYS5JIJN0Z1', tag: 'domestic', count: 1],
            [uid: 'Q6N8GZEGY7IV', tag: 'actonco2', count: 1],
            [uid: 'O6N5Y0SDAMIV', tag: 'electrical', count: 1],
            [uid: 'GKMTHRTWKQ4K', tag: 'computer', count: 1],
            [uid: 'QDYFKC69S3FU', tag: 'entertainment', count: 1],
            [uid: '000FD23CD3A2', tag: 'inc_tag_1', count: 3],
            [uid: '001FD23CD3A2', tag: 'inc_tag_2', count: 2]]

    // Cats with tag inc_tag_1 or inc_tag_2:
    // 268 (inc_tag_1, inc_tag_2, deprecated)
    // 293 (inc_tag_1, entertainment)
    // 265 (inc_tag_1, domestic, actonco2, electrical)
    // 295 (inc_tag_2, electricity)
    def incTags = [
            [uid: '00XAWWLZ6DHG', tag: 'electricity', count: 1],
            [uid: 'A5WXB7KUROF9', tag: 'deprecated', count: 1],
            [uid: 'CIYS5JIJN0Z1', tag: 'domestic', count: 1],
            [uid: 'Q6N8GZEGY7IV', tag: 'actonco2', count: 1],
            [uid: 'O6N5Y0SDAMIV', tag: 'electrical', count: 1],
            [uid: 'QDYFKC69S3FU', tag: 'entertainment', count: 1],
            [uid: '000FD23CD3A2', tag: 'inc_tag_1', count: 3],
            [uid: '001FD23CD3A2', tag: 'inc_tag_2', count: 2]]

    // Cats without tag inc_tag_1
    // 35 (electricity, GHGP, deprecated, country)
    // 50 (electricity, GHGP, country, grid)
    // 86 (electricity, GHGP, US)
    // 197 (waste)
    // 295 (inc_tag_2, electricity)
    // 267 (computer)
    def excTags = [
            [uid: '00XAWWLZ6DHG', tag: 'electricity', count: 4],
            [uid: 'WI6NFTH4FK77', tag: 'GHGP', count: 3],
            [uid: 'A5WXB7KUROF9', tag: 'deprecated', count: 1],
            [uid: 'YMCINO1P5A3O', tag: 'country', count: 2],
            [uid: '7I97KPQ44JUF', tag: 'grid', count: 1],
            [uid: '1ECBCD4IQCJ2', tag: 'US', count: 1],
            [uid: 'E4P9V1X6AGJL', tag: 'waste', count: 1],
            [uid: '001FD23CD3A2', tag: 'inc_tag_2', count: 1],
            [uid: 'GKMTHRTWKQ4K', tag: 'computer', count: 1]]

    /**
     * Tests for creation, fetch and deletion of a Tag using JSON responses.
     *
     * Create a new Tag by POSTing to '/tags'
     *
     * Supported POST parameters are:
     *
     * <ul>
     * <li>tag
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Delete (TRASH) a Tag by sending a DELETE request to '/tags/{UID|tag}'.
     *
     */
    @Test
    void createAndRemoveTagJson() {
        versions.each { version -> createAndRemoveTagJson(version) }
    }

    def createAndRemoveTagJson(version) {
        if (version >= 3.2) {
            setAdminUser()
            client.contentType = JSON

            // Create a new Tag.
            def responsePost = client.post(
                    path: "/$version/tags",
                    body: [tag: 'tagtobedeleted'],
                    requestContentType: URLENC,
                    contentType: JSON)

            String location = responsePost.headers['Location'].value
            String uid = location.split('/')[5]
            assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

            // Then delete the Tag.
            def responseDelete = client.delete(path: "/$version/tags/tagtobedeleted")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // We should get a 404 here.
            try {
                client.get(path: "/$version/tags/tagtobedeleted")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    /**
     * Tests for creation, fetch and deletion of an Entity-Tag association using JSON responses.
     *
     * Add a new tag to a Category by POSTing to '/categories/{category}/tags'
     *
     * Supported POST parameters are:
     *
     * <ul>
     *     <li>tag</li>
     * </ul>
     *
     * NOTE: For detailed rules on these parameters see the validation tests below.
     *
     * Remove a Tag from a category by sending a DELETE request to '/categories/{category}/tags/{UID|tag}'.
     * Note this does not delete the actual tag itself, just the association.
     *
     */
    @Test
    void createAndRemoveEntityTagJson() {
        versions.each { version -> createAndRemoveEntityTagJson(version) }
    }

    def createAndRemoveEntityTagJson(version) {
        if (version >= 3.2) {
            setAdminUser()
            client.contentType = JSON

            // Check the category cannot be discovered via the tag.
            testFilterCategories([tags: 'entity_tag_to_be_deleted'], [], version)

            // Create a new Tag & EntityTag on a DataCategory.
            postTagToCategory('Kitchen_generic', 'entity_tag_to_be_deleted', version)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // The EntityTag should exist.
            def responseGet = client.get(path: "/$version/categories/Kitchen_generic/tags/entity_tag_to_be_deleted")
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.tag.tag == 'entity_tag_to_be_deleted'
            String uid = responseGet.data.tag.uid

            // Check the category can be discovered via the tag.
            testFilterCategories([tags: 'entity_tag_to_be_deleted'], ['Kitchen_generic'], version)

            // Then delete the EntityTag.
            def responseDelete = client.delete(path: "/$version/categories/Kitchen_generic/tags/entity_tag_to_be_deleted")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // We should get a 404 here for the EntityTag.
            try {
                client.get(path: "/$version/categories/Kitchen_generic/tags/entity_tag_to_be_deleted")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }

            // Check the category cannot be discovered via the tag.
            testFilterCategories([tags: 'entity_tag_to_be_deleted'], [], version)

            // Create another EntityTag on another DataCategory.
            postTagToCategory('Entertainment_generic', 'entity_tag_to_be_deleted', version)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // The EntityTag should exist.
            responseGet = client.get(path: "/$version/categories/Entertainment_generic/tags/entity_tag_to_be_deleted")
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.tag.tag == 'entity_tag_to_be_deleted'
            uid = responseGet.data.tag.uid

            // Check the category can be discovered via the tag.
            testFilterCategories([tags: 'entity_tag_to_be_deleted'], ['Entertainment_generic'], version)

            // Then delete the EntityTag.
            responseDelete = client.delete(path: "/$version/categories/Entertainment_generic/tags/entity_tag_to_be_deleted")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // We should get a 404 here for the EntityTag.
            try {
                client.get(path: "/$version/categories/Entertainment_generic/tags/entity_tag_to_be_deleted")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }

            // Check the category cannot be discovered via the tag.
            testFilterCategories([tags: 'entity_tag_to_be_deleted'], [], version)

            // The Tag should still exist.
            responseGet = client.get(path: "/$version/tags/entity_tag_to_be_deleted")
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.data.tag.tag == 'entity_tag_to_be_deleted'
            uid = responseGet.data.tag.uid

            // Now delete the Tag.
            responseDelete = client.delete(path: "/$version/tags/entity_tag_to_be_deleted")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // We should get a 404 here for the Tag.
            try {
                client.get(path: "/$version/tags/entity_tag_to_be_deleted")
                fail 'Should have thrown an exception'
            } catch (HttpResponseException e) {
                assert e.response.status == CLIENT_ERROR_NOT_FOUND.code
            }
        }
    }

    @Test
    void createAndRemoveMultipleEntityTagsJson() {
        versions.each { version -> createAndRemoveMultipleEntityTagsJson(version) }
    }

    def createAndRemoveMultipleEntityTagsJson(version) {
        if (version >= 3.2) {
            setAdminUser()

            // Check categories cannot be discovered via the tag.
            testFilterCategories(['tags': 'entity_tag_to_be_deleted'], [], version)

            // Create a new Tag & EntityTag on a DataCategory.
            def uid = postTagToCategory('Kitchen_generic', 'entity_tag_to_be_deleted', version)

            // Create a new EntityTag on another DataCategory.
            postTagToCategory('Entertainment_generic', 'entity_tag_to_be_deleted', version)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Check the categories can be discovered via the tag.
            testFilterCategories([tags: 'entity_tag_to_be_deleted'], ['Kitchen_generic', 'Entertainment_generic'], version)

            // Now delete the Tag.
            def responseDelete = client.delete(path: "/${version}/tags/entity_tag_to_be_deleted")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid)

            // Sleep a little to give the index a chance to be updated.
            sleep(SLEEP_TIME)

            // Check categories cannot be discovered via the tag.
            testFilterCategories([tags: 'entity_tag_to_be_deleted'], [], version)
        }
    }

    /**
     * Tests fetching a list of all tags using JSON.
     *
     * Tag GET requests support the following query parameters to filter the results.
     *
     * <ul>
     * <li>incTags - A comma separated list of tags. The tags in the response will be limited to tags for Data Categories that ARE tagged with at least one of the supplied tags.
     * <li>excTags - A comma separated list of tags. The tags in the response will be limited to tags for Data Categories that ARE NOT tagged with any of the supplied tags.
     * </ul>
     *
     * Tag GET requests have NO matrix parameters to alter the response.
     *
     * Tags are sorted by tag (the name).
     *
     */
    @Test
    void getAllTagsJson() {
        versions.each { version -> getAllTagsJson(version) }
    }

    def getAllTagsJson(version) {
        client.contentType = JSON
        def response = client.get(path: "/$version/tags")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.tags.size() == tags.size()
        if (version >= 3.2) {
            assert response.data.tags.collect { it.uid }.sort() == tags.collect { it.uid }.sort()
        }
        // Tags are sorted by tag
        assert response.data.tags.collect { it.tag } == tags.collect { it.tag }.sort { a, b -> a.compareToIgnoreCase(b) }
        assert response.data.tags.collect { it.count }.sort() == tags.collect { it.count }.sort()
    }

    /**
     * Tests fetching a list of all tags using XML.
     */
    @Test
    void getAllTagsXml() {
        versions.each { version -> getAllTagsXml(version) }
    }

    def getAllTagsXml(version) {
        client.contentType = XML
        def response = client.get(path: "/$version/tags")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        def allTags = response.data.Tags.children()
        assert allTags.size() == tags.size()
        if (version >= 3.2) {
            assert allTags.@uid*.text().sort() == tags.collect { it.uid }.sort()
        }
        // Tags are sorted by tag
        assert allTags.Tag*.text() == tags.collect { it.tag }.sort { a, b -> a.compareToIgnoreCase(b) }
        assert allTags.Count*.text().collect { it as int }.sort() == tags.collect { it.count }.sort()
    }

    /**
     * Tests getting a list of tags for Data Categories that ARE tagged with at least one of the supplied tags (JSON).
     */
    @Test
    void getAllIncTagsJson() {
        versions.each { version -> getAllIncTagsJson(version) }
    }

    def getAllIncTagsJson(version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(path: "/$version/tags", query: [incTags: 'inc_tag_1,inc_tag_2'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.tags.size() == incTags.size()
            assert response.data.tags.collect { it.uid }.sort() == incTags.collect { it.uid }.sort()
            assert response.data.tags.collect { it.tag }.sort() == incTags.collect { it.tag }.sort()
            assert response.data.tags.collect { it.count }.sort() == incTags.collect { it.count }.sort()
        }
    }

    /**
     * Tests getting a list of tags for Data Categories that ARE tagged with at least one of the supplied tags (XML).
     */
    @Test
    void getAllIncTagsXml() {
        versions.each { version -> getAllIncTagsXml(version) }
    }

    def getAllIncTagsXml(version) {
        if (version >= 3.2) {
            client.contentType = XML
            def response = client.get(path: "/$version/tags", query: [incTags: 'inc_tag_1,inc_tag_2'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.Tags.Tag.size() == incTags.size()
            assert response.data.Tags.Tag.collect { it.@uid.text() }.sort() == incTags.collect { it.uid }.sort()
            assert response.data.Tags.Tag.collect { it.Tag.text() }.sort() == incTags.collect { it.tag }.sort()
            assert response.data.Tags.Tag.collect { it.Count.text() as int }.sort() == incTags.collect { it.count }.sort()
        }
    }

    /**
     * Tests getting a list of tags for Data Categories that ARE NOT tagged with any of the supplied tags (JSON).
     */
    @Test
    void getAllExcTagsJson() {
        versions.each { version -> getAllExcTagsJson(version) }
    }

    def getAllExcTagsJson(version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(path: "/$version/tags", query: [excTags: 'inc_tag_1'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.tags.size() == excTags.size()
            assert response.data.tags.collect { it.uid }.sort() == excTags.collect { it.uid }.sort()
            assert response.data.tags.collect { it.tag } == excTags.collect { it.tag }.sort { a, b -> a.compareToIgnoreCase(b) }
            assert response.data.tags.collect { it.count }.sort() == excTags.collect { it.count }.sort()
        }
    }

    /**
     * Tests getting a list of tags for Data Categories that ARE NOT tagged with any of the supplied tags (XML).
     */
    @Test
    void getAllExcTagsXml() {
        versions.each { version -> getAllExcTagsXml(version) }
    }

    def getAllExcTagsXml(version) {
        if (version >= 3.2) {
            client.contentType = XML
            def response = client.get(path: "/$version/tags", query: [excTags: 'inc_tag_1'])
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.Tags.Tag.size() == excTags.size()
            assert response.data.Tags.Tag.collect { it.@uid.text() }.sort() == excTags.collect { it.uid }.sort()
            assert response.data.Tags.Tag.collect { it.Tag.text() } == excTags.collect { it.tag }.sort { a, b -> a.compareToIgnoreCase(b) }
            assert response.data.Tags.Tag.collect { it.Count.text() as int }.sort() == excTags.collect { it.count }.sort()
        }
    }

    /**
     * Tests the validation rules.
     *
     * Values supplied to incTags and excTags must be valid tag names.
     *
     * <ul>
     *     <li>incTags - alphanumerics & underscore, min: 2, max: 255</li>
     *     <li>excTags - alphanumerics & underscore, min: 2, max: 255</li>
     * </ul>
     */
    @Test
    void getInvalidTagsJson() {
        setAdminUser()
        getInvalidTagsFieldJson('incTags', 'short', 'foo,a,bar', 3.2)
        getInvalidTagsFieldJson('incTags', 'long', String.randomString(Tag.TAG_MAX_SIZE + 1), 3.2)
        getInvalidTagsFieldJson('incTags', 'format', 'foo,n o t v a l i d', 3.2)
        getInvalidTagsFieldJson('excTags', 'short', 'a,bar', 3.2)
        getInvalidTagsFieldJson('excTags', 'long', String.randomString(Tag.TAG_MAX_SIZE + 1) + ',wee', 3.2)
        getInvalidTagsFieldJson('excTags', 'format', 'moo,n o t v a l i d,boo', 3.2)
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
        versions.each { version -> getInvalidTagsFieldJson(field, code, value, since, version) }
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
                def query = [:]
                query[field] = value
                // Request Tags.
                client.contentType = JSON
                client.get(path: "/$version/tags", query: query)
                fail 'Response status code should have been 400 (' + field + ', ' + code + ').'
            } catch (HttpResponseException e) {
                // Handle error response containing a ValidationResult.
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'
                assert response.data.validationResult.errors.collect { it.field } == [field]
                assert response.data.validationResult.errors.collect { it.code } == [code]
            }
        }
    }

    /**
     * Tests getting a tag by tag name using JSON.
     */
    @Test
    void getTagByTagJson() {
        getTagByPathJson('electricity')
    }

    /**
     * Tests getting a tag by tag UID using JSON.
     */
    @Test
    void getTagByUidJson() {
        getTagByPathJson('00XAWWLZ6DHG')
    }

    def getTagByPathJson(path) {
        versions.each { version -> getTagByPathJson(path, version) }
    }

    def getTagByPathJson(path, version) {
        if (version >= 3.2) {
            client.contentType = JSON
            def response = client.get(path: "/$version/tags/$path")
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/json'
            assert response.data.status == 'OK'
            assert response.data.tag.tag == 'electricity'
            assert response.data.tag.uid == '00XAWWLZ6DHG'
        }
    }

    /**
     * Tests getting a tag by tag name using XML.
     */
    @Test
    void getTagByTagXml() {
        getTagByPathXml('electricity')
    }

    /**
     * Tests getting a tag by tag UID using XML.
     */
    @Test
    void getTagByUidXml() {
        getTagByPathXml('00XAWWLZ6DHG')
    }

    def getTagByPathXml(path) {
        versions.each { version -> getTagByPathXml(path, version) }
    }

    def getTagByPathXml(path, version) {
        if (version >= 3.2) {
            client.contentType = XML
            def response = client.get(path: "/$version/tags/$path")
            assert response.status == SUCCESS_OK.code
            assert response.contentType == 'application/xml'
            assert response.data.Status.text() == 'OK'
            assert response.data.Tag.@uid.text() == '00XAWWLZ6DHG'
            assert response.data.Tag.Tag.text() == 'electricity'
        }
    }

    /**
     * Tests getting the tags for a category using JSON.
     *
     * You can fetch all tags for a category by making a GET request to /categories/{CATEGORY_PATH}/tags
     *
     */
    @Test
    void getTagsForCategoryJson() {
        versions.each { version -> getTagsForCategoryJson(version) }
    }

    def getTagsForCategoryJson(version) {
        def appliancesTags = [
                [uid: 'CIYS5JIJN0Z1', tag: 'domestic'],
                [uid: 'Q6N8GZEGY7IV', tag: 'actonco2'],
                [uid: 'O6N5Y0SDAMIV', tag: 'electrical'],
                [uid: '000FD23CD3A2', tag: 'inc_tag_1']]
        client.contentType = JSON
        def response = client.get(path: "/$version/categories/Appliances/tags")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/json'
        assert response.data.status == 'OK'
        assert response.data.tags.size() == appliancesTags.size()
        if (version >= 3.2) {
            assert response.data.tags.collect { it.uid }.sort() == appliancesTags.collect { it.uid }.sort()
        }
        assert response.data.tags.collect { it.tag } == appliancesTags.collect { it.tag }.sort { a, b -> a.compareToIgnoreCase(b) }
    }

    /**
     * Tests getting the tags for a category using XML.
     */
    @Test
    void getTagsForCategoryXml() {
        versions.each { version -> getTagsForCategoryXml(version) }
    }

    def getTagsForCategoryXml(version) {
        def appliancesTags = [
                [uid: 'CIYS5JIJN0Z1', tag: 'domestic'],
                [uid: 'Q6N8GZEGY7IV', tag: 'actonco2'],
                [uid: 'O6N5Y0SDAMIV', tag: 'electrical'],
                [uid: '000FD23CD3A2', tag: 'inc_tag_1']]
        client.contentType = XML
        def response = client.get(path: "/$version/categories/Appliances/tags")
        assert response.status == SUCCESS_OK.code
        assert response.contentType == 'application/xml'
        assert response.data.Status.text() == 'OK'
        assert response.data.Tags.Tag.size() == appliancesTags.size()
        if (version >= 3.2) {
            assert response.data.Tags.Tag.collect { it.@uid.text() }.sort() == appliancesTags.collect { it.uid }.sort()
        }
        assert response.data.Tags.Tag.collect { it.Tag.text() } == appliancesTags.collect { it.tag }.sort { a, b -> a.compareToIgnoreCase(b) }
    }

    /**
     * Tests fetching categories with the given tags.
     *
     * Get a list of categories with the given tags by sending a GET request to: 'categories?tags={lucene query}'
     * Eg: '/categories?tags=about or acid'
     *
     * Tests searching for categories with (and without) the given tags.
     * Eg: '/search?q=kitchen&tags=energy_star
     *
     */
    @Test
    void filterOnMultipleTagsJson() {
        versions.each { version -> filterOnMultipleTagsJson(version) }
    }

    def filterOnMultipleTagsJson(version) {
        setAdminUser()

        // Tag DataCategories.
        String uid1 = postTagToCategory('Kitchen_generic', 'test_tag_1', version)
        postTagToCategory('Entertainment_generic', 'test_tag_1', version)
        String uid2 = postTagToCategory('Entertainment_generic', 'test_tag_2', version)
        String uid3 = postTagToCategory('Entertainment_generic', 'test_tag_3', version)
        postTagToCategory('Computers_generic', 'test_tag_3', version)

        // Sleep a little to give the index a chance to be updated.
        sleep(SLEEP_TIME)

        // Check the categories can be discovered.
        testFilterCategories([tags: 'test_tag_1'], ['Kitchen_generic', 'Entertainment_generic'], version)
        testFilterCategories([tags: 'test_tag_2'], ['Entertainment_generic'], version)
        testFilterCategories([tags: 'test_tag_3'], ['Entertainment_generic', 'Computers_generic'], version)
        testFilterCategories([tags: 'test_tag_1 OR test_tag_2 OR test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version)
        testFilterCategories([tags: 'test_tag_1 test_tag_2 test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version)
        testFilterCategories([tags: 'test_tag_1, test_tag_2, test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version)
        testFilterCategories([tags: 'test_tag_1,test_tag_2,test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version)
        testFilterCategories([tags: 'test_tag_3,test_tag_2,test_tag_1'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version)
        testFilterCategories([tags: 'test_tag_1 AND test_tag_2 AND test_tag_3'], ['Entertainment_generic'], version)
        testFilterCategories([tags: 'test_tag_2 OR test_tag_3', excTags: 'test_tag_1'], ['Computers_generic'], version)
        testFilterCategories([tags: '(test_tag_2 OR test_tag_3) NOT test_tag_1'], ['Computers_generic'], version)

        // Check the categories can be searched for.
        if (version >= 3.2) {
            testSearchForCategories([q: 'kitchen', tags: 'test_tag_1'], ['Kitchen_generic'], version)
            testSearchForCategories([q: 'kitchen', tags: '-test_tag_1'], [], version)
            testSearchForCategories([q: 'generic', tags: 'test_tag_3'], ['Entertainment_generic', 'Computers_generic'], version)
            testSearchForCategories([q: 'generic', tags: 'test_tag_1,test_tag_3'], ['Kitchen_generic', 'Entertainment_generic', 'Computers_generic'], version)
            testSearchForCategories([q: 'generic', tags: 'test_tag_1,test_tag_3', excTags: 'test_tag_2'], ['Kitchen_generic', 'Computers_generic'], version)
            testSearchForCategories([q: 'generic', tags: '(test_tag_1 OR test_tag_3) NOT test_tag_2'], ['Kitchen_generic', 'Computers_generic'], version)
            testSearchForCategories([q: 'blahblah', tags: 'test_tag_1'], [], version)
        }

        // Test tag counts.
        if (version >= 3.2) {
            testTags([incTags: 'test_tag_1'],
                    ['electricity', 'entertainment', 'inc_tag_1', 'inc_tag_2', 'test_tag_1', 'test_tag_2', 'test_tag_3'],
                    [1, 1, 1, 1, 2, 1, 1], version)
            testTags([incTags: 'test_tag_1, test_tag_2, test_tag_3'],
                    ['computer', 'entertainment', 'inc_tag_1', 'inc_tag_2', 'test_tag_1', 'test_tag_2', 'test_tag_3'],
                    [1, 1, 1, 1, 2, 1, 2], version)
            testTags([excTags: 'test_tag_1'],
                    ['actonco2', 'computer', 'country', 'deprecated', 'domestic', 'electrical', 'electricity', 'GHGP', 'inc_tag_1', 'inc_tag_2', 'test_tag_3', 'US', 'waste', 'grid'],
                    [1, 1, 2, 2, 1, 1, 3, 3, 2, 1, 1, 1, 1, 1], version)
        }

        // Now delete the Tags.
        // NOTE: For < 3.2 this leaves database in odd state.
        if (version >= 3.2) {
            def responseDelete = client.delete(path: "/$version/tags/test_tag_1")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid1)
            responseDelete = client.delete(path: "/$version/tags/test_tag_2")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid2)
            responseDelete = client.delete(path: "/$version/tags/test_tag_3")
            assertOkJson(responseDelete, SUCCESS_OK.code, uid3)
        }
    }

    def postTagToCategory(category, tag, version) {
        def responsePost = client.post(
                path: "/$version/categories/$category/tags",
                body: [tag: tag],
                requestContentType: URLENC,
                contentType: JSON)

        String location = responsePost.headers['Location'].value
        String uid = location.split('/')[5]
        assertOkJson(responsePost, SUCCESS_CREATED.code, uid)

        uid
    }

    def testFilterCategories(query, expected, version) {
        def responseGet = client.get(path: "/$version/categories", query: query, contentType: JSON)
        assert responseGet.status == SUCCESS_OK.code
        assertEquals('Unexpected result. Is RabbitMQ running?', expected.size(), responseGet.data.categories.size())
        assert responseGet.data.categories.collect { it.wikiName }.sort() == expected.sort()
    }

    def testSearchForCategories(query, expected, version) {
        query['types'] = 'DC'
        def responseGet = client.get(path: "/$version/search", query: query, contentType: JSON)
        assert responseGet.status == SUCCESS_OK.code
        assertEquals('Unexpected result. Is RabbitMQ running?', expected.size(), responseGet.data.results.size())
        assert responseGet.data.results.collect { it.wikiName }.sort() == expected.sort()
    }

    def testTags(query, expectedTags, expectedCounts, version) {
        def responseGet = client.get(path: "/$version/tags", query: query, contentType: JSON)
        assert responseGet.status == SUCCESS_OK.code
        assertEquals('Unexpected result. Is RabbitMQ running?', expectedTags.size(), responseGet.data.tags.size())
        assert responseGet.data.tags.collect { it.tag }.sort() == expectedTags.sort()
        assert responseGet.data.tags.collect { it.count }.sort() == expectedCounts.sort()
    }

    /**
     * Tests updating a tag.
     */
    @Test
    void updateTagJson() {
        versions.each { version -> updateTagJson(version) }
    }

    def updateTagJson(version) {
        if (version >= 3.2) {
            setAdminUser()

            // 1) Do the update.
            def responsePut = client.put(
                    path: "/$version/tags/002FD23CD3A2",
                    body: [tag: 'tag_updated'],
                    requestContentType: URLENC,
                    contentType: JSON)

            assertOkJson(responsePut, SUCCESS_OK.code, '002FD23CD3A2')

            // 2) Check values have been updated.
            def responseGet = client.get(path: "/$version/tags/002FD23CD3A2", contentType: JSON)
            assert responseGet.status == SUCCESS_OK.code
            assert responseGet.contentType == 'application/json'
            assert responseGet.data.status == 'OK'
            assert responseGet.data.tag.tag == 'tag_updated'
        }
    }

    /**
     * Tests the validation rules.
     *
     * <ul>
     *     <li>tag - non-empty, unique, alphanumerics & underscore, min: 2, max: 255</li>
     * </ul>
     */
    @Test
    void updateInvalidTagJson() {
        setAdminUser()
        updateTagFieldJson('tag', 'empty', '', 3.2)
        updateTagFieldJson('tag', 'short', 'a', 3.2)
        updateTagFieldJson('tag', 'long', String.randomString(Tag.TAG_MAX_SIZE + 1), 3.2)
        updateTagFieldJson('tag', 'format', 'n o t v a l i d', 3.2)
        updateTagFieldJson('tag', 'duplicate', 'electricity', 3.2)
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
        versions.each { version -> updateTagFieldJson(field, code, value, since, version) }
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
                def body = [:]
                body[field] = value

                // Update Tag.
                client.put(
                        path: "/$version/tags/computer",
                        body: body,
                        requestContentType: URLENC,
                        contentType: JSON)
                fail 'Response status code should have been 400 (' + field + ', ' + code + ').'
            } catch (HttpResponseException e) {

                // Handle error response containing a ValidationResult.
                def response = e.response
                assert response.status == CLIENT_ERROR_BAD_REQUEST.code
                assert response.contentType == 'application/json'
                assert response.data.status == 'INVALID'
                assert response.data.validationResult.errors.collect { it.field } == [field]
                assert response.data.validationResult.errors.collect { it.code } == [code]
            }
        }
    }
}