import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML

import static org.junit.Assert.*
import org.junit.Ignore

class ReturnValueDefinitionIT extends BaseApiTest {

    @Test
    @Ignore("Spoils count in getReturnValueDefinitions tests")
    void createReturnValueDefinition() {

        client.contentType = JSON
        def response = client.post(
            path: "/3/definitions/11D3548466F2/returnvalues",
            body: [type: 'CO2', unit: 'kg', perUnit: 'month', valueDefinition: '45433E48B39F'],
            requestContentType: URLENC)

        assertEquals 201, response.status
    }

    @Test
    void getReturnValueDefinitionsJson() {
        client.contentType = JSON
        def response = client.get(path: '/3/definitions/11D3548466F2/returnvalues')

        assertEquals 200, response.status
        assertEquals 'application/json', response.contentType
        assertTrue response.data instanceof net.sf.json.JSON
        assertEquals 'OK', response.data.status
        assertEquals returnValueDefinitionUids.size(), response.data.returnValueDefinitions.size()
        assertEquals returnValueDefinitionUids.sort(), response.data.returnValueDefinitions.collect {it.uid}.sort()
    }

    @Test
    void getReturnValueDefinitionsXml() {
        client.contentType = XML
        def response = client.get(path: '/3/definitions/11D3548466F2/returnvalues')
        assertEquals 200, response.status
        assertEquals 'application/xml', response.contentType
        assertEquals 'OK', response.data.Status.text()

        def allReturnValueDefinitions = response.data.ReturnValueDefinitions.ReturnValueDefinition
        assertEquals returnValueDefinitionUids.size(), allReturnValueDefinitions.size()
        assertEquals returnValueDefinitionUids.sort(), allReturnValueDefinitions.@uid*.text().sort()
    }
}
