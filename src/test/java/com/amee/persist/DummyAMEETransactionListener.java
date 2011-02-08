package com.amee.persist;

import com.amee.base.transaction.TransactionEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DummyAMEETransactionListener implements ApplicationListener {

    private final Log log = LogFactory.getLog(getClass());

    @Autowired
    private DummyEntityDAO dao;

    private List<String> events = new ArrayList<String>();

    public void onApplicationEvent(ApplicationEvent e) {
        if (e instanceof TransactionEvent) {
            TransactionEvent te = (TransactionEvent) e;
            switch (te.getType()) {
                case BEFORE_BEGIN:
                    log.debug("onApplicationEvent() BEFORE_BEGIN");
                    checkTransactionIsNotActive();
                    events.clear();
                    events.add("BEFORE_BEGIN");
                    break;
                case END:
                    log.debug("onApplicationEvent() END");
                    checkTransactionIsNotActive();
                    events.add("END");
                    break;
                default:
                    // Do nothing!
            }
        }
    }

    public List<String> getEvents() {
        return events;
    }

    private void checkTransactionIsNotActive() {
        if (dao.isTransactionActive()) {
            throw new IllegalStateException("Should NOT have a transaction.");
        }
    }
}
