package com.amee.restlet.engine;

import com.amee.base.engine.Engine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.service.LogService;

public class RestletEngine extends Engine {

    private final Log log = LogFactory.getLog(getClass());

    private Component container;

    public RestletEngine() {
        super();
    }

    public RestletEngine(String instanceName) {
        super(instanceName);
    }

    public static void main(String[] args) {
        start(new RestletEngine(), args);
    }

    protected boolean onStart() {
        // Obtain the Restlet container.
        container = ((Component) getSpringContext().getBean("platformContainer"));

        // Configure Restlet server (ajp, http, etc).
        // TODO: Try and do this in Spring XML config.  
        Server ajpServer = ((Server) getSpringContext().getBean("platformServer"));
        ajpServer.getContext().getAttributes()
                .put("transactionController", getTransactionController()); // used in TransactionServerConverter

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
                org.mortbay.jetty.Server server = (org.mortbay.jetty.Server) getSpringContext().getBean("servletServer");
                server.start();
                server.join();
            }
            return true;
        } catch (Exception e) {
            log.fatal("Caught Exception: " + e);
            e.printStackTrace();
            return false;
        } catch (Throwable e) {
            log.fatal("Caught Throwable: " + e);
            e.printStackTrace();
            return false;
        }
    }

    public Component getContainer() {
        return container;
    }
}
