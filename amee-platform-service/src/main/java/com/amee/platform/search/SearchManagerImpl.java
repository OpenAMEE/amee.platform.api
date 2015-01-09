package com.amee.platform.search;

import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.ObjectType;
import com.amee.domain.data.DataCategory;
import com.amee.service.data.DataService;
import com.amee.service.invalidation.InvalidationMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SearchManagerImpl implements SearchManager, ApplicationContextAware {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private DataService dataService;

    @Autowired
    private LuceneService luceneService;

    /**
     * Is this instance the master index node? There can be only one!
     */
    private boolean masterIndex = false;

    /**
     * Should all Data Categories be checked on application start?
     */
    private boolean checkDataCategories = false;

    /**
     * Should all Data Categories be re-indexed on application start?
     */
    private boolean indexDataCategories = false;

    /**
     * Should all Data Items be re-indexed on application start?
     */
    private boolean indexDataItems = false;

    /**
     * The path prefix for Data Categories that should be indexed (e.g., '/lca/ecoinvent').
     */
    private String dataCategoryPathPrefix = null;

    /**
     * A {@link Queue} of {@link SearchIndexerContext}s waiting to be sent to a {@link SearchIndexer}. The
     * queue will only contain one {@link SearchIndexerContext} per Data Category.
     */
    private Queue<SearchIndexerContext> queue = new ConcurrentLinkedQueue<SearchIndexerContext>();

    /**
     * A {@link CountDownLatch} for managing the processing loop for the queue.
     * <p/>
     * The initial default latch has a countdown value of zero so it does not wait on first use.
     */
    private CountDownLatch queueLatch = new CountDownLatch(0);

    // Used to obtain SearchIndexer instances.
    private ApplicationContext applicationContext;

    // Events

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void onApplicationEvent(InvalidationMessage invalidationMessage) {
        if (masterIndex &&
                !invalidationMessage.isLocal() &&
                invalidationMessage.getObjectType().equals(ObjectType.DC) &&
                !invalidationMessage.hasOption("dataCategoryIndexed")) {
            log.trace("onApplicationEvent() Handling InvalidationMessage.");
            DataCategory dataCategory = dataService.getDataCategoryByUid(invalidationMessage.getEntityUid(), null);
            if (dataCategory != null) {
                SearchIndexerContext context = new SearchIndexerContext();
                context.dataCategoryUid = dataCategory.getUid();
                context.handleDataCategories = indexDataCategories;
                context.handleDataItems = invalidationMessage.hasOption("indexDataItems");
                context.checkDataItems = invalidationMessage.hasOption("checkDataItems");
                addSearchIndexerContext(context, true);
            }
        }
    }

    // Scheduled jobs.

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void update() {
        updateCategories();
        updateDataItems();
    }

    /**
     * Update all Data Categories in the search index which have been modified in
     * the last one hour segment.
     */
    private void updateCategories() {
        log.debug("updateCategories()");
        DateTime anHourAgoRoundedUp = DateTime.now().minusHours(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        List<DataCategory> dataCategories = dataService.getDataCategoriesModifiedWithin(
                anHourAgoRoundedUp.toDate(),
                anHourAgoRoundedUp.plusHours(1).toDate());
        for (DataCategory dataCategory : dataCategories) {
            SearchIndexerContext context = new SearchIndexerContext();
            context.dataCategoryUid = dataCategory.getUid();
            context.handleDataCategories = indexDataCategories;
            addSearchIndexerContext(context);
        }
    }

    /**
     * Update all Data Categories & Data Items in the search index where the
     * Data Items have been modified in the last one hour segment.
     */
    private void updateDataItems() {
        log.debug("updateDataItems()");
        DateTime anHourAgoRoundedUp = DateTime.now().minusHours(1).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
        List<DataCategory> dataCategories = dataService.getDataCategoriesForDataItemsModifiedWithin(
                anHourAgoRoundedUp.toDate(),
                anHourAgoRoundedUp.plusHours(1).toDate());
        for (DataCategory dataCategory : dataCategories) {
            SearchIndexerContext context = new SearchIndexerContext();
            context.dataCategoryUid = dataCategory.getUid();
            context.handleDataCategories = indexDataCategories;
            context.handleDataItems = true;
            addSearchIndexerContext(context);
        }
    }

    // Index & Document management.

    /**
     * Loops until the application stops (is interrupted). Calls consumeQueue, after
     * a 10 second sleep OR the queue latch has been signalled, to handle any waiting {@link SearchIndexerContext}s.
     */
    public void updateLoop() {
        log.info("updateLoop() Begin.");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                log.debug("updateLoop() Waiting.");
                // Wait until:
                //  * 10 seconds have elapsed OR
                //  * the queue latch reaches zero (this thread has been signalled).
                queueLatch.await(10, TimeUnit.SECONDS);
                // Consume the queue.
                consumeQueue();
                // Having processed the queue we can reset the queue latch.
                resetQueueLatch();
            } catch (InterruptedException e) {
                log.debug("updateLoop() Interrupted.");
                return;
            } catch (Exception e) {
                log.error("updateLoop() Caught Exception: " + e.getMessage(), e);
            } catch (Throwable t) {
                log.error("updateLoop() Caught Throwable: " + t.getMessage(), t);
            }
        }
        log.info("updateLoop() End.");
    }

    /**
     * Will update or create the whole search index.
     */
    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void updateAll() {
        // Clear the SearchIndexer DataCategory count.
        SearchIndexerImpl.resetCount();
        // Prepare the index; unlock it and potential clear it.
        luceneService.prepareIndex();
        // Check DataCategories?
        if (checkDataCategories) {
            buildDataCategories();
        }
    }

    /**
     * Add all DataCategories to the index.
     */
    private void buildDataCategories() {
        log.debug("handleDataCategories()");
        Set<String> dataCategoryUids = getDataCategoryUids();
        buildDataCategories(dataCategoryUids);
    }

    /**
     * Get a Set of Data Category UIDs for all.
     *
     * @return Set of Data Category UIDs
     */
    private Set<String> getDataCategoryUids() {
        log.debug("getDataCategoryUids()");
        // Iterate over all DataCategories and gather DataCategory UIDs.
        Set<String> dataCategoryUids = new HashSet<String>();
        for (DataCategory dataCategory : dataService.getDataCategories()) {
            // Don't index Data Categories whose path starts with '/test'.
            // Only index Data Categories whose path starts with dataCategoryPathPrefix (if set).
            if (!dataCategory.getFullPath().startsWith("/test") &&
                    (StringUtils.isBlank(dataCategoryPathPrefix) || dataCategory.getFullPath().startsWith(dataCategoryPathPrefix))) {
                dataCategoryUids.add(dataCategory.getUid());
            }
        }
        return dataCategoryUids;
    }

    /**
     * Add all DataCategories to the index.
     *
     * @param dataCategoryUids UIDs of Data Categories to index.
     */
    private void buildDataCategories(Set<String> dataCategoryUids) {
        log.debug("handleDataCategories()");
        for (String uid : dataCategoryUids) {
            buildDataCategory(uid);
        }
    }

    /**
     * Create a {@link SearchIndexerContext} for the supplied Data Category UID and submit this to the queue.
     *
     * @param dataCategoryUid Data Category UID
     */
    private void buildDataCategory(String dataCategoryUid) {
        log.debug("buildDataCategory()");
        SearchIndexerContext context = new SearchIndexerContext();
        context.dataCategoryUid = dataCategoryUid;
        context.handleDataCategories = indexDataCategories;
        context.handleDataItems = indexDataItems;
        addSearchIndexerContext(context);
    }

    // Task submission.


    /**
     * Add a {@link SearchIndexerContext} to the queue, but only if there is not an equivalent object already present.
     *
     * @param context {@link SearchIndexerContext} to add to the queue
     */
    private void addSearchIndexerContext(SearchIndexerContext context) {
        addSearchIndexerContext(context, false);
    }

    /**
     * Add a {@link SearchIndexerContext} to the queue, but only if there is not an equivalent object already present.
     *
     * @param context {@link SearchIndexerContext} to add to the queue
     * @param signal  should we signal for immediate processing?
     */
    private synchronized void addSearchIndexerContext(SearchIndexerContext context, boolean signal) {
        if (context != null) {
            // Never allow equivalent SearchIndexerContexts to exist in the queue.
            if (!queue.contains(context)) {
                log.debug("addSearchIndexerContext() Adding: {}", context.dataCategoryUid);
                queue.add(context);
                // Signal the queue loop thread to process the queue?
                if (signal) {
                    signalViaQueueLatch();
                }
            } else {
                log.debug("addSearchIndexerContext() Skipping: {}", context.dataCategoryUid);
            }
        }
    }

    /**
     * Loops over the queue and sends waiting {@link SearchIndexerContext}s to be
     * processed by {@link SearchIndexer}s. There are no items in the queue this will return immediately.
     */
    private void consumeQueue() {
        if (!queue.isEmpty()) {
            log.debug("consumeQueue() Consuming.");
            Iterator<SearchIndexerContext> iterator = queue.iterator();
            while (iterator.hasNext()) {
                SearchIndexerContext next = iterator.next();
                if (next != null) {
                    iterator.remove();
                    log.debug("consumeQueue() Removed: {}", next.dataCategoryUid);
                    if (!submitForExecution(next)) {
                        // Failed to submit task so break.
                        break;
                    }
                }
            }
        } else {
            log.debug("consumeQueue() Nothing to consume.");
        }
    }

    /**
     * Update the DataCategory in the index using a SearchIndexerRunner for the supplied SearchIndexerContext.
     *
     * @param context a context for the SearchIndexer
     * @return true if the task was submitted and the thread pool is not full
     */
    private boolean submitForExecution(SearchIndexerContext context) {
        // Create SearchIndexerRunner.
        SearchIndexerRunner searchIndexerRunner = applicationContext.getBean(SearchIndexerRunner.class);
        searchIndexerRunner.setSearchIndexerContext(context);
        // Attempt to execute the SearchIndexerRunner
        try {
            // The SearchIndexerRunner can be rejected if an equivalent SearchIndexerContext is
            // currently being processed.
            searchIndexerRunner.execute();
            // Managed to submit task.
            return true;
        } catch (SearchIndexerRunnerException e) {
            log.debug("submitForExecution() Task was rejected: {}", context.dataCategoryUid);
            // Failed to execute the SearchIndexerRunner as thread pool was full or this is a duplicate
            // category. Now we add the SearchIndexerContext back into the queue so it gets another
            // chance to be executed.
            addSearchIndexerContext(context);
            // Return false if the queue is full.
            return !e.isReasonFull();
        }
    }

    /**
     * Reset the queue latch with a new {@link CountDownLatch} with a countdown value of 1.
     */
    private synchronized void resetQueueLatch() {
        queueLatch = new CountDownLatch(1);
    }

    /**
     * Signal the queue looping thread via the {@link CountDownLatch}. This will trigger immediate
     * processing of the  queue.
     */
    private synchronized void signalViaQueueLatch() {
        queueLatch.countDown();
    }

    // Properties.

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Value("${MASTER_INDEX}")
    public void setMasterIndex(Boolean masterIndex) {
        this.masterIndex = masterIndex;
    }

    @Value("${CHECK_DATA_CATEGORIES}")
    public void setCheckDataCategories(Boolean checkDataCategories) {
        this.checkDataCategories = checkDataCategories;
    }

    @Value("${INDEX_DATA_CATEGORIES}")
    public void setIndexDataCategories(Boolean indexDataCategories) {
        this.indexDataCategories = indexDataCategories;
    }

    @Value("${INDEX_DATA_ITEMS}")
    public void setIndexDataItems(Boolean indexDataItems) {
        this.indexDataItems = indexDataItems;
    }

    @Value("${INDEX_DATA_CATEGORY_PATH_PREFIX:}")
    public void setDataCategoryPathPrefix(String dataCategoryPathPrefix) {
        this.dataCategoryPathPrefix = dataCategoryPathPrefix;
    }
}
