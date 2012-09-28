package com.amee.restlet;

import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.slf4j.MDC;

/**
 * Filter to manage {@link RequestContext}s. Will create the {@link RequestContext} for the transaction log.
 */
public class AMEERequestFilter extends Filter {

    protected int doHandle(Request request, Response response) {
        RequestContext requestContext = new RequestContext();
        try {

            // Create the RequestContext.
            requestContext.setRequest(request);
            request.getAttributes().put("requestContext", requestContext);

            // Add the client IP address for logging.
            MDC.put("ipAddress", request.getClientInfo().getAddress());

            // Delegate request handling to the super class.
            return super.doHandle(request, response);
        } catch (Throwable t) {

            // Record the exception in the RequestContext.
            requestContext.setError(t.getMessage());
            requestContext.error();

            // Wrap and re-throw the exception.
            throw new RuntimeException(t);
        } finally {

            // Update and record the RequestContext at the end of the request.
            requestContext.setStatus(response.getStatus());
            requestContext.record();
            MDC.remove("ipAddress");

            // User UID is added in CollectCredentialsGuard.
            MDC.remove("userUid");
        }
    }
}