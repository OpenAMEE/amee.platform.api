package com.amee.base.engine;

import java.util.TimeZone;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

import com.amee.base.transaction.TransactionController;

/**
 * The main 'Engine' class that bootstraps the application. This implements WrapperListener from Tanuki.
 * <p/>
 * See: http://wrapper.tanukisoftware.org/jdoc/org/tanukisoftware/wrapper/WrapperListener.html
 */
public class Engine implements WrapperListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private ClassPathXmlApplicationContext springContext;
    private TransactionController transactionController;

    // These are used to determine the PID of the instance in the init script.
    private String appName = "amee";
    private String serverName = "localhost";
    private String instanceName = "live";

    public Engine() {
        super();
    }

    public Engine(String appName, String serverName, String instanceName) {
        this();
        this.appName = appName;
        this.serverName = serverName;
        this.instanceName = instanceName;
    }

    /**
     * A main method which can be used to start an {@link Engine}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        start(new Engine(), args);
    }

    /**
     * Start the application via {@link WrapperManager}.
     *
     * @param wrapperListener to use when starting the application.
     * @param args            command line arguments
     */
    protected static void start(WrapperListener wrapperListener, String[] args) {
        WrapperManager.start(wrapperListener, args);
    }

    /**
     * Start the application. This implements the start method of {@link WrapperListener}.
     *
     * @param args command line arguments
     * @return error code
     */
    @Override
    public Integer start(String[] args) {

        // Redirect JDK logging to slf4j Logging
        // TODO: Should we use jul-to-slf4j? http://www.slf4j.org/legacy.html (http://stackoverflow.com/a/9117188)
        JavaLoggingToSlf4jRedirector.activate();

        parseOptions(args);

        log.debug("Starting Engine...");

        // Initialise Spring ApplicationContext.
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
     * Parse the command line options and store in private properties.
     *
     * @param args the argument list
     */
    protected void parseOptions(String[] args) {

        CommandLine line = null;
        CommandLineParser parser = new GnuParser();
        Options options = new Options();

        // Define appName option.
        Option appNameOpt = OptionBuilder.withArgName("appName")
                .hasArg()
                .withDescription("The app name")
                .create("appName");
        appNameOpt.setRequired(true);
        options.addOption(appNameOpt);

        // Define serverName option.
        Option serverNameOpt = OptionBuilder.withArgName("serverName")
                .hasArg()
                .withDescription("The server name")
                .create("serverName");
        serverNameOpt.setRequired(true);
        options.addOption(serverNameOpt);

        // Define instanceName option.
        Option instanceNameOpt = OptionBuilder.withArgName("instanceName")
                .hasArg()
                .withDescription("The instance name")
                .create("instanceName");
        instanceNameOpt.setRequired(true);
        options.addOption(instanceNameOpt);

        // Define timeZone option.
        Option timeZoneOpt = OptionBuilder.withArgName("timeZone")
                .hasArg()
                .withDescription("The time zone")
                .create("timeZone");
        timeZoneOpt.setRequired(false);
        options.addOption(timeZoneOpt);

        // Parse the options.
        try {
            line = parser.parse(options, args);
        } catch (ParseException exp) {
            new HelpFormatter().printHelp("java " + this.getClass().getName(), options);
            System.exit(-1);
        }

        // Handle appName.
        if (line.hasOption(appNameOpt.getOpt())) {
            appName = line.getOptionValue(appNameOpt.getOpt());
        }

        // Handle serverName.
        if (line.hasOption(serverNameOpt.getOpt())) {
            serverName = line.getOptionValue(serverNameOpt.getOpt());
        }

        // Handle instanceName.
        if (line.hasOption(instanceNameOpt.getOpt())) {
            instanceName = line.getOptionValue(instanceNameOpt.getOpt());
        }

        // Handle timeZone.
        if (line.hasOption(timeZoneOpt.getOpt())) {
            String timeZoneStr = line.getOptionValue(timeZoneOpt.getOpt());
            if (!StringUtils.isBlank(timeZoneStr)) {
                TimeZone timeZone = TimeZone.getTimeZone(timeZoneStr);
                if (timeZone != null) {
                    TimeZone.setDefault(timeZone);
                    DateTimeZone.setDefault(DateTimeZone.forTimeZone(timeZone));
                }
            }
        }
        log.info("parseOptions() Time Zone is: " + TimeZone.getDefault().getDisplayName() + " (" + TimeZone.getDefault().getID() + ")");
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
    @Override
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
    @Override
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

    public String getAppName() {
        return appName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getInstanceName() {
        return instanceName;
    }
}
