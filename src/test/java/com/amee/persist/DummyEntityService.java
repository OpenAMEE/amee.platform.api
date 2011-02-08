package com.amee.persist;

import com.amee.base.transaction.AMEETransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DummyEntityService {

    @Autowired
    private DummyEntityDAO dao;

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<DummyEntity> getDummyEntities() {
        return dao.getDummyEntities();
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public DummyEntity getDummyEntityByUid(String uid) {
        return dao.getDummyEntityByUid(uid);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = IllegalStateException.class)
    public void persist(DummyEntity dummyEntity) {
        dao.persist(dummyEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = IllegalStateException.class)
    public void persist(List<DummyEntity> dummyEntities) {
        for (DummyEntity dummyEntity : dummyEntities) {
            dao.persist(dummyEntity);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = IllegalStateException.class)
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
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public void doSomethingWithinAMEETransactionAndDBTransaction() {
        if (!dao.isTransactionActive()) {
            throw new IllegalStateException("Should have a transaction.");
        }
        getDummyEntities();
    }


    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = IllegalStateException.class)
    public void doCauseRollbackWithinAMEETransactionAndDBTransaction() {
        persist(new DummyEntity("An illegal value."));
    }

    public boolean isTransactionActive() {
        return dao.isTransactionActive();
    }
}