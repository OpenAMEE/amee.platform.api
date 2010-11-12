import com.amee.platform.search.SearchIndexer
import groovyx.net.http.RESTClient
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * A base class for API integration tests.
 *
 * The restlet container is started.
 */
abstract class BaseApiTest {

  static def context
  static def container
  static def luceneService

  def client

  @BeforeClass
  static void start() {

    // Augment String with a random method.
    addRandomStringMethodToString();

    // Spring application context.
    // TODO: Use spring annotation to load this?
    context = new ClassPathXmlApplicationContext("classpath*:applicationContext*.xml")

    // Clear the SearchIndexer DataCategory count (paranoid).
    SearchIndexer.resetCount();

    // Configure Restlet server (ajp, http, etc).
    // TODO: Try and do this in Spring XML config.
    def server = context.getBean("platformServer")
    def transactionController = context.getBean("transactionController")
    server.context.attributes.transactionController = transactionController
    server.context.attributes.springContext = context;

    // TODO: Start this before all integration tests. exec-maven-plugin?
    // Start the restlet container.
    container = context.getBean("platformContainer")

    // We're off!
    println "Starting container..."
    container.start()

    // Wait to allow the search index to be built.
    // NOTE: The count figure below needs to be updated when more DataCategories are added to import.sql.
    // NOTE: Remember to exclude trashed and implicitly trashed categories in the count.
    println 'Waiting while the index is built...'
    int count = 0;
    while (SearchIndexer.getCount() < CategoryIT.categoryUids.size()) {
      sleep(1000)
      count++;
      println 'Waited ' + count + ' second(s) whilst the index is being built...'
    }
    // Wait another 2 seconds just in case.
    sleep(2000)

    // Ensure index reader is re-opened.
    luceneService = context.getBean("luceneService")
    luceneService.checkSearcher();
  }

  @AfterClass
  static void stop() {
    try {
      println "Stopping container..."
      luceneService.closeEverything();
      container.stop()
      context.close();
    } catch (e) {
      // Do nothing.
    }
  }

  @Before
  void setUp() {

    // Get the HTTP client
    def config = new ConfigSlurper().parse(context.getResource('classpath:api.properties').getURL())
    client = new RESTClient("http://${config.api.host}:${config.api.port}")
    client.auth.basic config.api.user, config.api.password
  }

  // Add a random character generator to the String class.

  static void addRandomStringMethodToString() {
    String.metaClass.'static'.randomString = { length ->
      // The chars used for the random string.
      def list = ('a'..'z') + ('A'..'Z') + ('0'..'9');
      // Make sure the list is long enough.
      list = list * (1 + length / list.size());
      // Shuffle it up good.
      Collections.shuffle(list);
      length > 0 ? list[0..length - 1].join() : '';
    }
  }
}