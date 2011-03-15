import com.amee.platform.search.SearchIndexerImpl
import groovyx.net.http.RESTClient
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.springframework.context.support.ClassPathXmlApplicationContext

/**
 * A base class for API integration tests.
 *
 * The Restlet container is started.
 */
abstract class BaseApiTest {

    static def versions = [3.0, 3.1, 3.2, 3.3, 3.4]
    static def config
    static def context
    static def container
    static def luceneService

    def client

    @BeforeClass
    static void start() {

        // Augment String with a random method.
        addRandomStringMethodToString();

        // Set the default timezone
        def timeZone = TimeZone.getTimeZone(System.getProperty("AMEE_TIME_ZONE", "UTC"))
        TimeZone.setDefault(timeZone)
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(timeZone))

        // Clear the SearchIndexer DataCategory count.
        SearchIndexerImpl.resetCount();

        // Spring application context.
        context = new ClassPathXmlApplicationContext("classpath*:applicationContext*.xml")

        // Load config.
        config = new ConfigSlurper().parse(context.getResource('classpath:api.properties').getURL());

        // Configure Restlet server (ajp, http, etc).
        def server = context.getBean("platformServer")
        def transactionController = context.getBean("transactionController")
        server.context.attributes.transactionController = transactionController
        server.context.attributes.springContext = context;

        // Start the Restlet container.
        println "Starting container..."
        container = context.getBean("platformContainer")
        container.start()

        // Wait to allow the search index to be built.
        // NOTE: The count figure below needs to be updated when more DataCategories are added to import.sql.
        // NOTE: Remember to exclude trashed and implicitly trashed categories in the count.
        println 'Waiting while the index is built...'
        int count = 0;
        while (SearchIndexerImpl.getCount() < (CategoryIT.categoryNames.size() - 1)) {
            sleep(1000);
            count++;
            println 'Waited ' + count + ' second(s) whilst the index is being built... (' + SearchIndexerImpl.getCount() + '/' + CategoryIT.categoryNames.size() + ')';
        }

        // Now the index has been built reset the clearIndex flag & ensure index reader is re-opened.
        luceneService = context.getBean("luceneService")
        luceneService.setClearIndex(new Boolean(false));
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
        client = new RESTClient("${config.api.protocol}://${config.api.host}:${config.api.port}");
        // Set standard user as default.
        setStandardUser();
    }

    void setStandardUser() {
        client.auth.basic config.api.standard.user, config.api.standard.password;
    }

    void setAdminUser() {
        client.auth.basic config.api.admin.user, config.api.admin.password;
    }

    void setRootUser() {
        client.auth.basic config.api.root.user, config.api.root.password;
    }

    void setEcoinventUser() {
        client.auth.basic config.api.ecoinvent.user, config.api.ecoinvent.password;
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

    /**
     * Returns true if d2 is near d1. Uses a delta of 500 milliseconds.
     *
     * @param d1 base date
     * @param d2 date to compare
     * @return true if d2 is near d1
     */
    boolean isNear(DateTime d1, DateTime d2) {
        return isNear(d1, d2, 500);
    }

    /**
     * Returns true if d2 is near d1.
     *
     * @param d1 base date
     * @param d2 date to compare
     * @param delta in milliseconds
     * @return true if d2 is near d1
     */
    boolean isNear(DateTime d1, DateTime d2, int delta) {
        return Math.abs(d2.millis - d1.millis) <= delta;
    }
}