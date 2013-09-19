package com.amee.persist;

import com.amee.base.resource.TimedOutException;
import com.amee.base.transaction.AMEETransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DummyEntityService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DummyEntityDAO dao;

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<DummyEntity> getDummyEntities() {
        return dao.getDummyEntities();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public DummyEntity getDummyEntityByUid(String uid) {
        return dao.getDummyEntityByUid(uid);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void persist(DummyEntity dummyEntity) {
        dao.persist(dummyEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void persist(List<DummyEntity> dummyEntities) {
        for (DummyEntity dummyEntity : dummyEntities) {
            dao.persist(dummyEntity);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void remove(DummyEntity dummyEntity) {
        dao.remove(dummyEntity);
    }

    @AMEETransaction
    public void doNothingWithinAMEETransaction() {
        if (dao.isTransactionActive()) {
            throw new IllegalStateException("Should NOT have a transaction.");
        }
    }

    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void doSomethingWithinAMEETransactionAndDBTransaction() {
        if (!dao.isTransactionActive()) {
            throw new IllegalStateException("Should have a transaction.");
        }
        getDummyEntities();
    }

    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void doCauseRollbackWithinAMEETransactionAndDBTransaction() {
        persist(new DummyEntity("An illegal value."));
    }

    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public double persistSlowly(DummyEntity dummyEntity) {
        persist(dummyEntity);
        double result = dao.getResultSlowly();
        if (!Thread.currentThread().isInterrupted()) {
            log.debug("Returning result normally.");
            return result;
        } else {
            log.debug("Thread was interrupted, throw TimedOutException.");
            throw new TimedOutException();
        }
    }

    public boolean isTransactionActive() {
        return dao.isTransactionActive();
    }
}
