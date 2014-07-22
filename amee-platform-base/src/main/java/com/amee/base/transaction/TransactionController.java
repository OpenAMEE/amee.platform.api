package com.amee.base.transaction;

/**
 * An interface defining life-cycle events for AMEE managed transactions. Implementations of this interface
 * are primarily useful as part of an open-session-in-view configuration.
 */
public interface TransactionController {

    /**
     * Begin a read-only transaction. Upgrade transaction to write if withTransaction is true.
     *
     * @param withTransaction if true, will upgrade transaction to write
     */
    void begin(boolean withTransaction);

    /**
     * End the current transaction.
     */
    void end();

    /**
     * A callback indicating that a 'request' is about to be handled and if a write transaction is required.
     *
     * @param withTransaction if true, will upgrade transaction to write
     */
    void beforeHandle(boolean withTransaction);

    /**
     * A callback to indicate that the processing of a 'request' is complete.
     *
     * @param success true if the request was a success
     */
    void afterHandle(boolean success);

    /**
     * A callback to indicate that the request and transaction have been commited.
     */
    void afterCommit();

    /**
     * Mark the transaction to only end in a rollback.
     */
    void setRollbackOnly();
}
