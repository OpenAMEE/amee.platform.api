package com.amee.base.transaction;

import java.io.Serializable;

/**
 * Defines a set of life-cycle events that occur in AMEE API transactions.
 *
 * @see AMEETransaction
 * @see AMEETransactionAspect
 */
public enum TransactionEventType implements Serializable {

    /**
     * Event sent before a transaction starts.
     */
    BEFORE_BEGIN("BEFORE_BEGIN", "Before Begin"),

    /**
     * Event sent once a transaction has been commited.
     */
    COMMIT("COMMIT", "Commit"),

    /**
     * Event sent when a transaction has been rolled back.
     */
    ROLLBACK("ROLLBACK", "Rollback"),

    /**
     * Event sent before a transaction ends.
     */
    BEFORE_END("BEFORE_END", "Before End"),

    /**
     * Event sent once a transaction has ended.
     */
    END("END", "End");

    private final String name;
    private final String label;

    /**
     * Construct a new TransactionEventType.
     *
     * @param name  the name of the TransactionEventType
     * @param label a label for the TransactionEventType
     */
    TransactionEventType(String name, String label) {
        this.name = name;
        this.label = label;
    }

    /**
     * Returns the TransactionEventType name for the toString.
     *
     * @return the TransactionEventType name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Get the TransactionEventType name.
     *
     * @return the TransactionEventType name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the TransactionEventType label.
     *
     * @return the TransactionEventType label.
     */
    public String getLabel() {
        return label;
    }
}
