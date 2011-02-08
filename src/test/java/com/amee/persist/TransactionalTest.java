package com.amee.persist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionalTest extends BaseTest {

    @Autowired
    private DummyEntityService dummyEntityService;

    @Autowired
    private DummyAMEETransactionListener dummyAMEETransactionListener;

    @Test
    public void shouldHaveDummyEntityService() {
        assertTrue("Should have a DummyEntityService.", dummyEntityService != null);
    }

    @Test
    public void shouldHaveSomeDummyEntities() {
        List<DummyEntity> dummyEntities = dummyEntityService.getDummyEntities();
        assertTrue("Should have some DummyEntities.", !dummyEntities.isEmpty());
    }

    @Test
    public void shouldFetchAnExistingDummyEntity() {
        DummyEntity dummyEntity = dummyEntityService.getDummyEntityByUid("655B1AD17733");
        assertTrue("Should fetch an existing DummyEntity.", dummyEntity != null);
    }

    @Test
    public void shouldCreateADummyEntity() {
        // Create an entity.
        DummyEntity newDummyEntity = new DummyEntity();
        newDummyEntity.setDummyText("Dummy Text.");
        dummyEntityService.persist(newDummyEntity);
        // Fetch the entity.
        DummyEntity fetchedDummyEntity = dummyEntityService.getDummyEntityByUid(newDummyEntity.getUid());
        assertTrue("Should be able to fetch the DummyEntity.", fetchedDummyEntity != null);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRED)
    public void shouldCreateAndRemoveADummyEntity() {
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
    public void shouldListenToAMEETransactionEvents() {
        dummyEntityService.doNothingWithinAMEETransaction();
        assertTrue("Should have two AMEETransaction events", dummyAMEETransactionListener.getEvents().size() == 2);
    }
}
