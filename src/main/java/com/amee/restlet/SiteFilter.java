package com.amee.restlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.springframework.beans.factory.annotation.Autowired;

public class SiteFilter extends BaseFilter {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private Component ameeContainer;

    @Override
    protected int doHandle(Request request, Response response) {
        request.getAttributes().put("activeServer", getServer());

        return super.doHandle(request, response);
    }

    private Server getServer() {
        Server currentServer = null;

        // Use the port of the current request to match.
        int currentPort = Response.getCurrent().getServerInfo().getPort();
        for (Server s : ameeContainer.getServers()) {
            if (s.getPort() == currentPort) {
                currentServer = s;
            }
        }

        return currentServer;
    }
}