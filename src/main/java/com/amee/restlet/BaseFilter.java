package com.amee.restlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Simple extension of Filter that adds request context & custom fault handling.
 */
public class BaseFilter extends Filter {

    private final Log log = LogFactory.getLog(getClass());

    /**
     * Sets the status of response with the status & message from fault.
     *
     * @param response to set status on
     * @param fault    to fetch status and message from
     */
    protected void setFault(Response response, Fault fault) {
        log.debug("setFault() - {status=" + fault.getStatus() + ", message=" + (!fault.toString().isEmpty() ? fault.toString() : "<EMPTY>}"));
        response.setStatus(fault.getStatus(), fault.getMessage());
    }

    protected RequestContext getRequestContext(Request request) {
        RequestContext requestContext = (RequestContext) request.getAttributes().get("requestContext");
        if (requestContext == null) {
            throw new RuntimeException("RequestContext was null.");
        }
        return requestContext;
    }
}
