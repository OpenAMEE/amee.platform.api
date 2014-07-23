package com.amee.persist;

import com.amee.base.transaction.TransactionController;
import com.amee.base.transaction.TransactionEvent;
import com.amee.base.transaction.TransactionEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import java.util.Map;
import java.util.Properties;

/**
 * An implementation of {@link TransactionController} for custom database transaction lifecycle management.
 */
public class TransactionControllerImpl extends EntityManagerFactoryAccessor implements TransactionController, ApplicationContextAware {

    @Autowired
    private PlatformTransactionManager transactionManager;

    private boolean manageTransactions;
    private TransactionAttribute transactionAttribute = new DefaultTransactionAttribute();
    private ThreadLocal<TransactionStatus> transactionStatus = new ThreadLocal<TransactionStatus>();
    private ThreadLocal<Boolean> transactionRollback = new ThreadLocal<Boolean>();
    private ApplicationContext applicationContext;

    public TransactionControllerImpl() {
        super();
        manageTransactions = true;
    }

    @Autowired
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        super.setEntityManagerFactory(entityManagerFactory);
    }

    @Autowired(required = false)
    public void setJpaProperties(Properties jpaProperties) {
        super.setJpaProperties(jpaProperties);
    }

    @Autowired(required = false)
    public void setJpaPropertyMap(Map jpaProperties) {
        super.setJpaPropertyMap(jpaProperties);
    }

    /**
     * 1) Called by TransactionFilter before Filter.doHandle
     *
     * @param withTransaction specify whether a transaction should be used
     */
    public void beforeHandle(boolean withTransaction) {
        logger.debug("beforeHandle() >>> BEFORE HANDLE {withTransaction=" + withTransaction + "}");
        // Callback hook.
        onBeforeBegin();
        // Ensure any EntityManager associated with this thread is closed before handling this new request.
        ensureEntityManagerIsClosed();
        // Begin transaction if required.
        begin(withTransaction);
    }

    public void onBeforeBegin() {
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.BEFORE_BEGIN));
    }

    /**
     * 2) Called by TransactionFilter after Filter.doHandle
     *
     * @param success true or false
     */
    public void afterHandle(boolean success) {
        logger.debug("afterHandle() <<< AFTER HANDLE");
        if (!success) {
            transactionRollback.set(true);
        }
        commitOrRollbackTransaction();
    }

    /**
     * 3) Called by TransactionServerConverter after HttpServerConverter.commit
     */
    public void afterCommit() {
        logger.debug("afterCommit() <<< AFTER COMMIT");
        end();
    }

    public void begin(boolean withTransaction) {
        logger.debug("begin() >>> BEGIN");
        openEntityManager();
        if (withTransaction && manageTransactions) {
            beginTransaction();
        }
    }

    /**
     * Ends the the current transaction.
     */
    public void end() {
        onBeforeEnd();
        commitOrRollbackTransaction();
        ensureEntityManagerIsClosed();
        logger.debug("end() <<< END");
        onEnd();
    }

    /**
     * Sends a BEFORE_END event before a transaction ends.
     */
    public void onBeforeEnd() {
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.BEFORE_END));
    }

    /**
     * Sends an END event when a transaction has ended.
     */
    public void onEnd() {
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.END));
    }

    private void beginTransaction() {
        if (manageTransactions && (transactionStatus.get() == null)) {
            transactionStatus.set(transactionManager.getTransaction(transactionAttribute));
            if (!transactionStatus.get().isNewTransaction()) {
                logger.debug("beginTransaction() Transaction already open.");
            }
            logger.debug("beginTransaction() >>> TRANSACTION OPENED");
        }
    }

    private void commitOrRollbackTransaction() {
        if (manageTransactions && (transactionStatus.get() != null)) {
            if (!transactionStatus.get().isCompleted()) {
                if (transactionRollback.get() != null) {
                    // Roll back transaction.
                    transactionManager.rollback(transactionStatus.get());
                    // Callback hook.
                    onRollback();
                    logger.warn("commitOrRollbackTransaction() <<< TRANSACTION ROLLED BACK");
                } else {
                    // Commit the transaction.
                    transactionManager.commit(transactionStatus.get());
                    // Callback hook.
                    onCommit();
                    logger.debug("commitOrRollbackTransaction() <<< TRANSACTION COMMITTED");
                }
            } else {
                logger.warn("commitOrRollbackTransaction() <<< TRANSACTION ALREADY COMPLETED");
            }
            transactionStatus.set(null);
        }
        transactionRollback.set(null);
    }

    /**
     * Sends a ROLLBACK event when a transaction has rolled-back.
     */
    public void onRollback() {
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.ROLLBACK));
    }

    /**
     * Sends a COMMIT event when a transaction has been committed.
     */
    public void onCommit() {
        applicationContext.publishEvent(new TransactionEvent(this, TransactionEventType.COMMIT));
    }

    /**
     * Opens an {@link EntityManager} if one is not already open.
     */
    public void openEntityManager() {
        // Return if there is an EntityManager already associated with this request.
        if (TransactionSynchronizationManager.hasResource(getEntityManagerFactory()))
            return;

        logger.debug("openEntityManager() Opening JPA EntityManager in TransactionController");
        try {
            EntityManager em = createEntityManager();
            TransactionSynchronizationManager.bindResource(getEntityManagerFactory(), new EntityManagerHolder(em));
        } catch (PersistenceException ex) {
            throw new DataAccessResourceFailureException("Could not create JPA EntityManager", ex);
        }
    }

    /**
     * Ensures the currently active {@link EntityManager} is closed, if one exists.
     */
    public void ensureEntityManagerIsClosed() {
        // Return if there is no EntityManager already associated with this request.
        if (!TransactionSynchronizationManager.hasResource(getEntityManagerFactory()))
            return;

        logger.debug("ensureEntityManagerIsClosed() Closing JPA EntityManager in TransactionController");
        EntityManagerHolder emHolder =
                (EntityManagerHolder) TransactionSynchronizationManager.unbindResourceIfPossible(getEntityManagerFactory());
        if (emHolder != null) {
            EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
        }
    }

    /**
     * Set the current transaction to only rollback.
     */
    public void setRollbackOnly() {
        transactionRollback.set(true);
    }

    public boolean isManageTransactions() {
        return manageTransactions;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}