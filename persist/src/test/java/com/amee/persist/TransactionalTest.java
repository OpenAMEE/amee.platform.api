package com.amee.persist;

import com.amee.base.resource.LocalResourceHandler;
import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceHandler;
import com.amee.base.resource.TimedOutException;
import com.amee.base.transaction.TransactionEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DummyEntityService dummyEntityService;

    @Autowired
    private DummyAMEETransactionListener dummyAMEETransactionListener;

    boolean slowResourceHandlerCompleted = false;

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
        DummyEntity newDummyEntity = new DummyEntity("Dummy Text.");
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
        DummyEntity newDummyEntity = new DummyEntity("Dummy Text.");
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
        DummyEntity newDummyEntityOne = new DummyEntity("An valid value.");
        dummyEntities.add(newDummyEntityOne);
        // Create a new invalid entity.
        DummyEntity newDummyEntityTwo = new DummyEntity("An illegal value.");
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

    /**
     * Test that a slow running ResourceHandler which executes SQL can be stopped with a timeout.
     */
    @Test
    public void willTrapSlowSQLInResourceHandler() throws InterruptedException {
        String result = null;
        // Create an entity.
        final DummyEntity dummyEntity = new DummyEntity("Dummy Text.");
        // Create a LocalResourceHandler that will cancel the ResourceHandler after 1 second.
        LocalResourceHandler lrh = new LocalResourceHandler();
        lrh.setTimeout(1);
        try {
            // Invoke a ResourceHandler which takes 2 seconds to complete.
            result = (String) lrh.handleWithTimeout(new RequestWrapper(), new ResourceHandler() {
                @Override
                public Object handle(RequestWrapper requestWrapper) {
                    // This method call takes a long time...
                    double result = dummyEntityService.persistSlowly(dummyEntity);
                    // Only return a result if the thread was not interrupted.
                    if (!Thread.currentThread().isInterrupted()) {
                        log.debug("Not interrupted, returning result.");
                        slowResourceHandlerCompleted = true;
                        return Double.toString(result);
                    } else {
                        log.debug("Interrupted, returning null.");
                        return null;
                    }
                }
            });
            fail("Should have thrown a TimedOutException.");
        } catch (TimedOutException e) {
            // Let 20 seconds pass before testing (to allow ResourceHandler to complete).
            Thread.sleep(20 * 1000);
            assertNull("Result should be null.", result);
            assertFalse("Should not have completed.", slowResourceHandlerCompleted);
            assertTrue("Should have three events", dummyAMEETransactionListener.getTransactionEventTypes().size() == 3);
            assertTrue("Should have BEFORE_BEGIN event.", dummyAMEETransactionListener.getTransactionEventTypes().get(0).equals(TransactionEventType.BEFORE_BEGIN));
            assertTrue("Should have ROLLBACK event.", dummyAMEETransactionListener.getTransactionEventTypes().get(1).equals(TransactionEventType.ROLLBACK));
            assertTrue("Should have END event.", dummyAMEETransactionListener.getTransactionEventTypes().get(2).equals(TransactionEventType.END));
        }
        // Fetch the entity, but should be null.
        DummyEntity removedDummyEntity = dummyEntityService.getDummyEntityByUid(dummyEntity.getUid());
        assertTrue("Should NOT be able to fetch the DummyEntity.", removedDummyEntity == null);
    }

    /**
     * A slow method that can be called from hsqldb SQL. See implementations of doSomethingSlowly in {@link DummyEntityDAO}.
     * <p/>
     * This seems to take about 3 seconds on a mac. This doesn't use the sleep technique because that
     * can interfere with interrupt status.
     *
     * @return a silly value
     */
    public static double slow() {
        double value = 0;
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
            value += i;
        }
        return value;
    }
}
