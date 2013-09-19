package com.amee.base.transaction;

/**
 * An implementation of TransactionController that does nothing.
 */
public class NoOpTransactionController implements TransactionController {

    public void begin(boolean withTransaction) {
        // Do nothing.
    }

    public void end() {
        // Do nothing.
    }

    public void beforeHandle(boolean withTransaction) {
        // Do nothing.
    }

    public void afterHandle(boolean success) {
        // Do nothing.
    }

    public void afterCommit() {
        // Do nothing.
    }

    public void setRollbackOnly() {
        // Do nothing.
    }
}
