import org.junit.Test
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

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
}