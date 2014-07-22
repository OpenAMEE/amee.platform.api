package com.amee.base.transaction;

import org.springframework.context.ApplicationEvent;

public class TransactionEvent extends ApplicationEvent {

    private TransactionEventType type;

    public TransactionEvent(Object source) {
        super(source);
    }

    public TransactionEvent(Object source, TransactionEventType type) {
        super(source);
        this.type = type;
    }

    public TransactionEventType getType() {
        return type;
    }

    public void setType(TransactionEventType type) {
        this.type = type;
    }
}
