package com.amee.persist;

import com.amee.base.transaction.TransactionEventType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionalTest extends BaseTest {

    @Autowired
    private DummyEntityService dummyEntityService;

    @Autowired
    private DummyAMEETransactionListener dummyAMEETransactionListener;

    @Before
    public void before() {
        dummyAMEETransactionListener.reset();
    }

    @After
    public void after() {
        dummyAMEETransactionListener.reset();
    }

    @Test
    public void shouldHaveDummyEntityService() {
        assertTrue("Should have a DummyEntityService.", dummyEntityService != null);
    }

    @Test
    public void shouldHaveSomeDummyEntities() {
        // We do NOT expect a transaction here.
        assertFalse("Should not have a transaction", dummyEntityService.isTransactionActive());
        // Fetch existing entities.
        List<DummyEntity> dummyEntities = dummyEntityService.getDummyEntities();
        assertTrue("Should have some DummyEntities.", !dummyEntities.isEmpty());
    }

    @Test
    public void shouldFetchAnExistingDummyEntity() {
        // We do NOT expect a transaction here.
        assertFalse("Should not have a transaction", dummyEntityService.isTransactionActive());
        // Fetch existing entity;
        DummyEntity dummyEntity = dummyEntityService.getDummyEntityByUid("655B1AD17733");
        assertTrue("Should fetch an existing DummyEntity.", dummyEntity != null);
    }

    @Test
    public void shouldCreateADummyEntity() {
        // We do NOT expect a transaction here.
        assertFalse("Should not have a transaction", dummyEntityService.isTransactionActive());
        // Create an entity.
        DummyEntity newDummyEntity = new DummyEntity();
        newDummyEntity.setDummyText("Dummy Text.");
        dummyEntityService.persist(newDummyEntity);
        // We still do NOT expect a transaction here.
        assertFalse("Should not have a transaction", dummyEntityService.isTransactionActive());
        // Fetch the entity.
        DummyEntity fetchedDummyEntity = dummyEntityService.getDummyEntityByUid(newDummyEntity.getUid());
        assertTrue("Should be able to fetch the DummyEntity.", fetchedDummyEntity != null);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void shouldCreateAndRemoveADummyEntity() {
        // We do expect a transaction here.
        assertTrue("Should have a transaction", dummyEntityService.isTransactionActive());
        // Create an entity.
        DummyEntity newDummyEntity = new DummyEntity();
        newDummyEntity.setDummyText("Dummy Text.");
        dummyEntityService.persist(newDummyEntity);
        // Fetch the entity.
        DummyEntity fetchedDummyEntity = dummyEntityService.getDummyEntityByUid(newDummyEntity.getUid());
        assertTrue("Should be able to fetch the DummyEntity.", fetchedDummyEntity != null);
        // Remove the entity.
        dummyEntityService.remove(newDummyEntity);
        // Fetch the entity, but should be null.
        DummyEntity removedDummyEntity = dummyEntityService.getDummyEntityByUid(fetchedDummyEntity.getUid());
        assertTrue("Should NOT be able to fetch the DummyEntity.", removedDummyEntity == null);
    }

    @Test
    public void shouldNotCreateAnInvalidDummyEntity() {
        // We do NOT expect a transaction here.
        assertFalse("Should not have a transaction", dummyEntityService.isTransactionActive());
        List<DummyEntity> dummyEntities = new ArrayList<DummyEntity>();
        // Create a new valid entity.
        DummyEntity newDummyEntityOne = new DummyEntity();
        newDummyEntityOne.setDummyText("An valid value.");
        dummyEntities.add(newDummyEntityOne);
        // Create a new invalid entity.
        DummyEntity newDummyEntityTwo = new DummyEntity();
        newDummyEntityTwo.setDummyText("An illegal value.");
        dummyEntities.add(newDummyEntityTwo);
        // Attempt to persist both entities.
        try {
            dummyEntityService.persist(dummyEntities);
            fail("Should have thrown an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // Swallow
        }
        // The first entity should not be persisted (rollback).
        newDummyEntityOne = dummyEntityService.getDummyEntityByUid(newDummyEntityOne.getUid());
        assertTrue("Should NOT be able to fetch the first DummyEntity.", newDummyEntityOne == null);
        // The second entity should not be persisted (invalid).
        newDummyEntityTwo = dummyEntityService.getDummyEntityByUid(newDummyEntityTwo.getUid());
        assertTrue("Should NOT be able to fetch the second DummyEntity.", newDummyEntityTwo == null);
    }

    @Test
    public void shouldListenToEvents() {
        assertFalse("Should not have a transaction", dummyEntityService.isTransactionActive());
        dummyEntityService.doNothingWithinAMEETransaction();
        assertTrue("Should have three events", dummyAMEETransactionListener.getTransactionEventTypes().size() == 3);
        assertTrue("Should have BEFORE_BEGIN event.", dummyAMEETransactionListener.getTransactionEventTypes().get(0).equals(TransactionEventType.BEFORE_BEGIN));
        assertTrue("Should have COMMIT event.", dummyAMEETransactionListener.getTransactionEventTypes().get(1).equals(TransactionEventType.COMMIT));
        assertTrue("Should have END event.", dummyAMEETransactionListener.getTransactionEventTypes().get(2).equals(TransactionEventType.END));
        assertFalse("Should still not have a transaction", dummyEntityService.isTransactionActive());
    }

    @Test
    public void shouldListenToEventsWithTransaction() {
        assertFalse("Should not have a transaction", dummyEntityService.isTransactionActive());
        dummyEntityService.doSomethingWithinAMEETransactionAndDBTransaction();
        assertTrue("Should have three events", dummyAMEETransactionListener.getTransactionEventTypes().size() == 3);
        assertTrue("Should have BEFORE_BEGIN event.", dummyAMEETransactionListener.getTransactionEventTypes().get(0).equals(TransactionEventType.BEFORE_BEGIN));
        assertTrue("Should have COMMIT event.", dummyAMEETransactionListener.getTransactionEventTypes().get(1).equals(TransactionEventType.COMMIT));
        assertTrue("Should have END event.", dummyAMEETransactionListener.getTransactionEventTypes().get(2).equals(TransactionEventType.END));
        assertFalse("Should still not have a transaction", dummyEntityService.isTransactionActive());
    }

    @Test
    public void shouldSeeRollbackEvent() {
        try {
            dummyEntityService.doCauseRollbackWithinAMEETransactionAndDBTransaction();
            fail("Should have thrown an IllegalArgumentException.");
        } catch (IllegalArgumentException e) {
            // Swallow
        }
        assertTrue("Should have three events", dummyAMEETransactionListener.getTransactionEventTypes().size() == 3);
        assertTrue("Should have BEFORE_BEGIN event.", dummyAMEETransactionListener.getTransactionEventTypes().get(0).equals(TransactionEventType.BEFORE_BEGIN));
        assertTrue("Should have ROLLBACK event.", dummyAMEETransactionListener.getTransactionEventTypes().get(1).equals(TransactionEventType.ROLLBACK));
        assertTrue("Should have END event.", dummyAMEETransactionListener.getTransactionEventTypes().get(2).equals(TransactionEventType.END));
    }
}
