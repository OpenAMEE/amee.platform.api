package com.amee.restlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Simple extension of Filter that adds request context & custom fault handling.
 */
public class BaseFilter extends Filter {

    private final Logger log = LoggerFactory.getLogger(getClass());

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

    /**
     * Get the current {@link RequestContext} object from the request attributes. Will throw
     * a {@link RuntimeException} if the {@link RequestContext} is not present.
     *
     * @param request the current {@link Request}
     * @return the current {@link RequestContext}
     */
    protected RequestContext getRequestContext(Request request) {
        RequestContext requestContext = (RequestContext) request.getAttributes().get("requestContext");
        if (requestContext == null) {
            throw new RuntimeException("RequestContext was null.");
        }
        return requestContext;
    }
}
