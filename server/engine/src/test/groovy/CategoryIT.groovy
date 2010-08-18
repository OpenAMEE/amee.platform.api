import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.*

class CategoryIT extends BaseApiTest {

    @Test
    @Ignore("POST not implemented in API")
    void createCategory() {
        client.contentType = JSON
        def response = client.post(
            path: '/3/categories/CATEGORY1',
            body: [wikiName: 'testWikiName'],
            requestContentType: URLENC)

        assertEquals 201, response.status
    }

    @Test
    void getCategoriesJson() {
        client.contentType = JSON
        def response = client.get(path: '/3/categories')
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertFalse response.data.resultsTruncated
        assertEquals categoryUids.size(), response.data.categories.size()
        assert categoryUids == response.data.categories.collect {it.uid}
        assert categoryNames == response.data.categories.collect {it.name}
        assert categoryWikiNames == response.data.categories.collect {it.wikiName}
    }

    @Test
    void getCategoriesXml() {
        client.contentType = XML
        def response = client.get(path: '/3/categories')
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()
        assertEquals 'false', response.data.Categories.@truncated.text()

        def allCategories = response.data.Categories.Category
        assertEquals categoryUids.size(), allCategories.size()
        assert categoryUids == allCategories.@uid*.text()
        assert categoryNames == allCategories.Name*.text()
        assert categoryWikiNames == allCategories.WikiName*.text()
    }

    @Test
    void filterByAuthorityJson() {
        client.contentType = JSON
        def response = client.get(path: '/3/categories',
            query: ['authority': 'enterprise'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals 8, response.data.categories.size()
    }

    @Test
    void filterByAuthorityXml() {
        client.contentType = XML
        def response = client.get(path: '/3/categories',
            query: ['authority': 'enterprise'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()

        def allCategories = response.data.Categories.Category
        assertEquals 8, allCategories.size()
    }

    @Test
    void filterByTagsJson() {
        client.contentType = JSON
        def response = client.get(path: '/3/categories',
            query: ['tags': 'electrical'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals 4, response.data.categories.size()
    }

    @Test
    void filterByTagsXml() {
        client.contentType = XML
        def response = client.get(path: '/3/categories',
            query: ['tags': 'electrical'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()

        def allCategories = response.data.Categories.Category
        assertEquals 4, allCategories.size()
    }

    @Test
    void filterByPathJson() {
        client.contentType = JSON
        def response = client.get(path: '/3/categories',
            query: ['fullPath': '/home/appliances/*'])
        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals 7, response.data.categories.size()
    }

    @Test
    void filterByPathXml() {
        client.contentType = XML
        def response = client.get(path: '/3/categories',
            query: ['fullPath': '/home/appliances/*'])
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()

        def allCategories = response.data.Categories.Category
        assertEquals 7, allCategories.size()
    }
}
