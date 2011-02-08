package com.amee.persist;

import java.util.List;

public interface DummyEntityDAO {

    public List<DummyEntity> getDummyEntities();

    public DummyEntity getDummyEntityByUid(String uid);

    public void persist(DummyEntity dummyEntity);

    public void remove(DummyEntity dummyEntity);

    public boolean isTransactionActive();
}
