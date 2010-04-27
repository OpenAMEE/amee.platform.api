package com.amee.base.transaction;

import java.io.Serializable;

public enum TransactionEventType implements Serializable {

    BEFORE_BEGIN("BEFORE_BEGIN", "Before Begin"),
    COMMIT("COMMIT", "Commit"),
    ROLLBACK("ROLLBACK", "Rollback"),
    BEFORE_END("BEFORE_END", "Before End"),
    END("END", "End");

    private final String name;
    private final String label;

    TransactionEventType(String name, String label) {
        this.name = name;
        this.label = label;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getLabel() {
        return label;
    }
}
