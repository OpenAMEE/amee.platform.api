package com.amee.persist;

import com.amee.base.transaction.TransactionEvent;
import com.amee.base.transaction.TransactionEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DummyAMEETransactionListener implements ApplicationListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DummyEntityDAO dao;

    // A list of transaction event types to be checked in tests.
    private List<TransactionEventType> transactionEventTypes = new ArrayList<TransactionEventType>();

    public void onApplicationEvent(ApplicationEvent e) {
        if (e instanceof TransactionEvent) {
            TransactionEvent te = (TransactionEvent) e;
            switch (te.getType()) {
                case BEFORE_BEGIN:
                    log.debug("onApplicationEvent() BEFORE_BEGIN");
                    checkTransactionIsNotActive();
                    transactionEventTypes.clear();
                    transactionEventTypes.add(TransactionEventType.BEFORE_BEGIN);
                    break;
                case ROLLBACK:
                    log.debug("onApplicationEvent() ROLLBACK");
                    checkTransactionIsNotActive();
                    transactionEventTypes.add(TransactionEventType.ROLLBACK);
                    break;
                case COMMIT:
                    log.debug("onApplicationEvent() COMMIT");
                    checkTransactionIsNotActive();
                    transactionEventTypes.add(TransactionEventType.COMMIT);
                    break;
                case END:
                    log.debug("onApplicationEvent() END");
                    checkTransactionIsNotActive();
                    transactionEventTypes.add(TransactionEventType.END);
                    break;
                default:
                    throw new IllegalStateException("Event not trapped: " + te.toString());
            }
        }
    }

    public List<TransactionEventType> getTransactionEventTypes() {
        return Collections.unmodifiableList(transactionEventTypes);
    }

    public void reset() {
        transactionEventTypes.clear();
    }

    private void checkTransactionIsNotActive() {
        if (dao.isTransactionActive()) {
            throw new IllegalStateException("Should NOT have a transaction.");
        }
    }

    private void checkTransactionIsActive() {
        if (!dao.isTransactionActive()) {
            throw new IllegalStateException("Should have a transaction.");
        }
    }
}
