package com.amee.persist;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static junit.framework.Assert.assertTrue;

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
    public void shouldListenToAMEETransactionEvents() {
        dummyEntityService.doNothingWithinAMEETransaction();
        assertTrue("Should have two AMEETransaction events", dummyAMEETransactionListener.getEvents().size() == 2);
    }
}
