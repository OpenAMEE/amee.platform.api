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

    // See import.sql
    def categoryUids = ['CD310BEBAC52', 'BBA3AC3E795E', '427DFCC65E52', '3FE23FDC8CEA', 'F27BF795BB04', '54C8A44254AA', '75AD9B83B7BF', '319DDB5EC18E', '4BD595E1873A', '3C03A03B5F3A',
        '99B121BB416C', '066196F049DD', 'E71CA2FCFFEA', 'AA59F9613F2A', 'D9289C55E595', '3035D381872B']

    def categoryNames = ['Root', 'Home', 'Appliances', 'Computers', 'Generic', 'Cooking', 'Entertainment', 'Generic', 'Kitchen', 'Generic',
        'Business', 'Energy', 'Electricity', 'US', 'Subregion', 'Waste']

    def categoryWikiNames = ['Root', 'Home', 'Appliances', 'Computers', 'Computers_generic', 'Cooking', 'Entertainment', 'Entertainment_generic', 'Kitchen', 'Kitchen_generic',
        'Business', 'Business_energy', 'Electricity_by_Country', 'Energy_US', 'US_Egrid', 'Waste']

    def returnValueDefinitionUids = ['B0268549CD9C', '6008F958CE20']
    def returnValueDefinitionTypes = ['co2', 'co2e']

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

        println "Starting container..."
        container.start()

        // Wait 5 seconds to allow the search index to be built (there must be a better way to do this?)
        sleep(5000)
    }

    @AfterClass
    static void stop() {
        try {
            println "Stopping container..."
            container.stop()
        } catch (e) {
            // Do nothing
        }
    }

    @Before
    void setUp() {

        // Get the HTTP client
        def config = new ConfigSlurper().parse(context.getResource('classpath:api.properties').getURL())
        client = new RESTClient("http://${config.api.host}:${config.api.port}")
        client.auth.basic config.api.user, config.api.password
    }
}