package com.amee.restlet;

import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;

/**
 * Filter to manage {@link RequestContext}s and {@link com.amee.base.transaction.AMEETransaction}s. Will
 * create the {@link RequestContext} for the transaction log.
 */
public class AMEERequestFilter extends Filter {

    protected int doHandle(Request request, Response response) {
        RequestContext requestContext = new RequestContext();
        try {
            // Create the RequestContext.
            requestContext.setRequest(request);
            request.getAttributes().put("requestContext", requestContext);
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
        }
    }
}