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
public class DummyEntityTest extends PersistTest {

    @Autowired
    private DummyEntityService dummyEntityService;

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
    public void shouldLoadExistingDummyEntity() {
        DummyEntity dummyEntity = dummyEntityService.getDummyEntityByUid("655B1AD17733");
        assertTrue("Should load existing DummyEntity.", dummyEntity != null);
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
}
