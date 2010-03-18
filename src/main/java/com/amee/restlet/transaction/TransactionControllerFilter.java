package com.amee.restlet.transaction;

import com.amee.base.transaction.TransactionController;
import com.amee.restlet.RequestContext;
import org.restlet.Filter;
import org.restlet.data.Method;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Filter to allow the TransactionController to commit or rollback a transaction depending on
 * the error state of the response or if a Throwable is caught.
 */
public class TransactionControllerFilter extends Filter {

    @Autowired
    private TransactionController transactionController;

    protected int doHandle(Request request, Response response) {
        boolean success = true;
        RequestContext requestContext = new RequestContext();
        requestContext.setRequest(request);
        request.getAttributes().put("requestContext", requestContext);
        try {
            transactionController.beforeHandle(!request.getMethod().equals(Method.GET));
            return super.doHandle(request, response);
        } catch (Throwable t) {
            success = false;
            requestContext.setError(t.getMessage());
            requestContext.error();
            throw new RuntimeException(t);
        } finally {
            requestContext.record();
            transactionController.afterHandle(success && !response.getStatus().isError());
        }
    }
}