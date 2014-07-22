package com.amee.restlet.transaction;

import com.amee.base.transaction.TransactionController;
import com.noelios.restlet.http.HttpResponse;
import com.noelios.restlet.http.HttpServerConverter;
import org.restlet.Context;

public class TransactionServerConverter extends HttpServerConverter {

    private TransactionController transactionController;

    public TransactionServerConverter(Context context) {
        super(context);
        transactionController = (TransactionController) context.getAttributes().get("transactionController");
    }

    public void commit(HttpResponse response) {
        // commit the response
        super.commit(response);
        // end transaction / entity manager
        transactionController.afterCommit();
    }
}