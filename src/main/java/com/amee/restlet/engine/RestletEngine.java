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

    public RestletEngine(String appName, String serverName, String instanceName) {
        super(instanceName, serverName, instanceName);
    }

    public static void main(String[] args) {
        start(new RestletEngine(), args);
    }

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
            log.fatal("onStart() Caught Exception: " + e);
            e.printStackTrace();
            return false;
        } catch (Throwable e) {
            log.fatal("onStart() Caught Throwable: " + e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected boolean onShutdown() {
        // Stop Restlet Container. Wait 500ms.
        if (container != null) {
            try {
                container.stop();
                container = null;
                Thread.sleep(500);
            } catch (Exception e) {
                log.fatal("onStart() Caught Exception: " + e);
                e.printStackTrace();
                return false;
            } catch (Throwable e) {
                log.fatal("onStart() Caught Throwable: " + e);
                e.printStackTrace();
                return false;
            }
        }
        // Stop Spring.
        return super.onShutdown();
    }

    public Component getContainer() {
        return container;
    }
}
