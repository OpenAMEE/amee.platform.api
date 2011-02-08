package com.amee.persist;

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

    @Transactional(propagation = Propagation.REQUIRED)
    public void persist(DummyEntity dummyEntity) {
        dao.persist(dummyEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void remove(DummyEntity dummyEntity) {
        dao.remove(dummyEntity);
    }
}