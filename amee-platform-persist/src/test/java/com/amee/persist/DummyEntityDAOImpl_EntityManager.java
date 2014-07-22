package com.amee.persist;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

public class DummyEntityDAOImpl_EntityManager implements DummyEntityDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<DummyEntity> getDummyEntities() {
        return entityManager.createQuery(
                "FROM DummyEntity", DummyEntity.class)
                .getResultList();
    }

    public DummyEntity getDummyEntityByUid(String uid) {
        try {
            return entityManager.createQuery(
                    "FROM DummyEntity " +
                            "WHERE uid = :uid", DummyEntity.class)
                    .setParameter("uid", uid)
                    .getSingleResult();
        } catch (NonUniqueResultException e) {
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public void persist(DummyEntity dummyEntity) {
        if (dummyEntity.getDummyText().equals("An illegal value.")) {
            throw new IllegalArgumentException("An illegal value was used.");
        }
        entityManager.persist(dummyEntity);
    }

    public void remove(DummyEntity dummyEntity) {
        entityManager.remove(dummyEntity);
    }

    public boolean isTransactionActive() {
        Session session = (Session) entityManager.getDelegate();
        return session.isConnected();
    }

    public double getResultSlowly() {
        return (Double) entityManager
                .createNativeQuery("select \"com.amee.persist.TransactionalTest.slow\"() from dual")
                        // .setHint("javax.persistence.query.timeout", 2000)
                .getSingleResult();
    }
}