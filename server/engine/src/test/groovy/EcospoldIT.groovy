import org.junit.Test
import static groovyx.net.http.ContentType.*
import static org.junit.Assert.*
import groovyx.net.http.Method
import groovyx.net.http.ContentType

class EcospoldIT extends BaseApiTest {

    @Test
    void getEcospoldCategory() {
        def response = client.get(path: '/3/categories/4C94508B25B5',
            contentType: XML,
            headers: [Accept: 'application/x.ecospold+xml'])

        assertEquals 200, response.status
        assertEquals 'application/x.ecospold+xml', response.contentType
        assertEquals '266', response.data.dataset.@number.text()

//        // Do it the hard way so we can override the accept parameter but still parse the response as XML.
//        client.request(Method.GET, ContentType.XML) { req ->
//            uri.path = '/3/categories/4C94508B25B5'
//            headers.Accept = 'application/x.ecospold+xml'
//
//            response.success = { resp, reader ->
//                println "Got response: ${resp.statusLine}"
//                println "Content-Type: ${resp.headers.'Content-Type'}"
//            }
//        }
    }
}
