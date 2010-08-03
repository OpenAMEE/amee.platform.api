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

    def client

    @BeforeClass
    static void start() {

        // Spring application context
        // TODO: Use spring annotation to load this?
        context = new ClassPathXmlApplicationContext("classpath*:applicationContext*.xml")

        // Configure Restlet server (ajp, http, etc).
        // TODO: Try and do this in Spring XML config.
        def server = context.getBean("platformServer")
        def transactionController = context.getBean("transactionController")
        server.context.attributes.transactionController = transactionController // used in TransactionServerConverter

        // TODO: Start this before all integration tests. exec-maven-plugin?
        // Start the restlet container
        container = context.getBean("platformContainer")
        container.start()
    }

    @AfterClass
    static void stop() {
        try {
            container.stop()
        } catch (e) {
            // Do nothing
        }
    }

    @Before
    void setUp() {

        // Get the HTTP client
        def config = new ConfigSlurper().parse(context.getResource('classpath:api.properties').getURL())
        client = new RESTClient("http://${config.api.host}:${config.api.port}", 'application/json')
        client.auth.basic config.api.user, config.api.password
    }
}