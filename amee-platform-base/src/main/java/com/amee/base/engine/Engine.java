package com.amee.base.engine;

import com.amee.base.transaction.TransactionController;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.TimeZone;

/**
 * The main 'Engine' class that bootstraps the application.
 */
public class Engine {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ClassPathXmlApplicationContext springContext;
    private TransactionController transactionController;

    public Engine() {
        super();
    }

    /**
     * A main method which can be used to start an {@link Engine}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
//        new Engine().start(args);
        start(new Engine(), args);
    }

    /**
     * Start the application with the given Engine.
     *
     * @param engine Engine to use when starting the application.
     * @param args command line arguments
     */
    protected static void start(Engine engine, String[] args) {
        engine.start(args);
    }

    /**
     * Start the application.
     *
     * @param args command line arguments
     * @return error code
     */
    public Integer start(String[] args) {

        // Redirect JDK logging to slf4j Logging
        // TODO: Should we use jul-to-slf4j? http://www.slf4j.org/legacy.html (http://stackoverflow.com/a/9117188)
        JavaLoggingToSlf4jRedirector.activate();

        // Set the JVM timezone
        String timeZoneStr = System.getProperty("amee.timezone", "UTC");
        if (!StringUtils.isBlank(timeZoneStr)) {
            TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
            if (timeZone != null) {
                TimeZone.setDefault(timeZone);
                DateTimeZone.setDefault(DateTimeZone.forTimeZone(timeZone));
            }
        }
        log.info("Time Zone is: " + TimeZone.getDefault().getDisplayName() + " (" + TimeZone.getDefault().getID() + ")");

        log.debug("Starting Engine...");

        // Initialise Spring ApplicationContext.
        // See http://docs.spring.io/spring/docs/current/spring-framework-reference/html/resources.html#resources-classpath-wildcards
        // for why the 'classpath*' pattern will not retrieve files from the root of jar files, hence the 'context' prefix.
        springContext = new ClassPathXmlApplicationContext("applicationContext*.xml", "classpath*:/context/applicationContext*.xml");

        // Initialise TransactionController (for controlling Spring).
        transactionController = (TransactionController) springContext.getBean("transactionController");

        // Do onStart callback wrapped in a transaction.
        boolean started;
        try {
            transactionController.begin(true);
            started = onStart();
            log.debug("...Engine started.");
        } finally {
            transactionController.end();
        }

        // Handle result.
        if (started) {
            return null;
        } else {
            // An arbitrary error code to indicate startup failure.
            return 1;
        }
    }

    /**
     * A callback for when the application has started.
     *
     * @return returns true if the application started successfully
     */
    protected boolean onStart() {
        // Do nothing.
        return true;
    }

    /**
     * A callback for when the application has shutdown.
     *
     * @return returns true if the application has shutdown successfully
     */
    protected boolean onShutdown() {
        // Stop Spring. Wait 500ms.
        if (springContext != null) {
            try {
                springContext.stop();
                springContext = null;
                Thread.sleep(500);
            } catch (Exception e) {
                Marker fatal = MarkerFactory.getMarker("FATAL");
                log.error(fatal, "onStart() Caught Exception: " + e);
                e.printStackTrace();
                return false;
            } catch (Throwable e) {
                Marker fatal = MarkerFactory.getMarker("FATAL");
                log.error(fatal, "onStart() Caught Throwable: " + e);
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Stop the application.
     *
     * @param exitCode system exit code
     * @return system exit code
     */
    public int stop(int exitCode) {
        try {
            log.debug("Stopping Engine...");
            onShutdown();
            log.debug("...Engine stopped.");
        } catch (Exception e) {
            log.error("Caught Exception: " + e, e);
        }
        return exitCode;
    }

    /**
     * Callback for handling system control events.
     *
     * @param event the event code
     */
    public void controlEvent(int event) {
        log.debug("controlEvent() {}", event);
        // Do nothing.
    }

    public ApplicationContext getSpringContext() {
        return springContext;
    }

    public TransactionController getTransactionController() {
        return transactionController;
    }
}