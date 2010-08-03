import org.junit.Ignore
import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.*

class CategoryIT extends BaseApiTest {

    def uids = ['CD310BEBAC52', 'BBA3AC3E795E', '427DFCC65E52', '3FE23FDC8CEA', 'F27BF795BB04',
            '54C8A44254AA', '75AD9B83B7BF', '319DDB5EC18E', '4BD595E1873A', '3C03A03B5F3A']

    def names = ['Root', 'Home', 'Appliances', 'Computers', 'Generic',
            'Cooking', 'Entertainment', 'Generic', 'Kitchen', 'Generic']

    def wikiNames = ['Root', 'Home', 'Appliances', 'Computers', 'Computers_generic',
            'Cooking', 'Entertainment', 'Entertainment_generic', 'Kitchen', 'Kitchen_generic']

    @Test
    @Ignore("POST not implemented in API")
    void createCategory() {
        client.contentType = JSON
        def reponse = client.post(
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
        assertEquals 10, response.data.categories.size()
        assert uids == response.data.categories.collect {it.uid}
        assert names == response.data.categories.collect {it.name}
        assert wikiNames == response.data.categories.collect {it.wikiName}
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
        assertEquals 10, allCategories.size()
        assert uids == allCategories.@uid*.text()
        assert names == allCategories.Name*.text()
        assert wikiNames == allCategories.WikiName*.text()
    }

    @Test
    void filterByAuthorityJson() {

    }

    @Test
    void filterByAuthorityXml() {
        
    }

    @Test
    void filterByTagsJson() {

    }

    @Test
    void filterByTagsXml() {
        
    }

    @Test
    void filterBySearchJson() {

    }

    @Test
    void filterBySearchXml() {
        
    }

    @Test
    void filterByFullPathJson() {

    }

    @Test
    void filterByFullPathXml() {

    }
}
