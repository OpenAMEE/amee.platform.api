package com.amee.engine;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Extends {@link Engine} to bootstrap an application based on Spring and Restlet with a Jetty web server.
 */
public class RestletEngine extends Engine {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Component container;

    public RestletEngine() {
        super();
    }

    /**
     * A main method which can be used to start a {@link RestletEngine}.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        start(new RestletEngine(), args);
    }

    /**
     * A callback for when the application has started. Will start the Restlet container and Jetty.
     *
     * @return returns true if the application started successfully
     */
    @Override
    protected boolean onStart() {
        // Obtain the Restlet container.
        container = ((Component) getSpringContext().getBean("platformContainer"));

        // Configure Restlet server (ajp, http, etc).
        // TODO: Try and do this in Spring XML config.  
        Server server = ((Server) getSpringContext().getBean("platformServer"));
        server.getContext().getAttributes().put("transactionController", getTransactionController());
        server.getContext().getAttributes().put("springContext", getSpringContext());
        Server secureServer = ((Server) getSpringContext().getBean("platformSecureServer"));
        secureServer.getContext().getAttributes().put("transactionController", getTransactionController());
        secureServer.getContext().getAttributes().put("springContext", getSpringContext());

        // Configure Restlet logging to log on a single line.
        LogService logService = container.getLogService();
        logService.setLogFormat("[IP:{cia}] [M:{m}] [S:{S}] [PATH:{rp}] [UA:{cig}] [REF:{fp}]");

        // Get things going.
        try {
            // Start the Restlet container.
            container.start();
            // Optionally start the Servlet container.
            // TODO: Make this a start-up argument instead.
            String startServletContext = System.getenv("START_SERVLET_CONTEXT");
            if (Boolean.parseBoolean(startServletContext)) {
                org.mortbay.jetty.Server servletServer = (org.mortbay.jetty.Server) getSpringContext().getBean("servletServer");
                servletServer.start();
                servletServer.join();
            }
            return true;
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

    /**
     * A callback for when the application has shutdown. Will shutdown the Restlet Container and then
     * call onShutdown in {@link Engine} to stop Spring.
     *
     * @return returns true if the application has shutdown successfully
     */
    @Override
    protected boolean onShutdown() {
        // Stop Restlet Container. Wait 500ms.
        if (container != null) {
            try {
                container.stop();
                container = null;
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
        // Stop Spring.
        return super.onShutdown();
    }

    /**
     * Get the Restlet container {@link Component}.
     *
     * @return the Restlet container {@link Component}
     */
    public Component getContainer() {
        return container;
    }
}
