package com.amee.service.definition;

import com.amee.base.domain.ResultsWrapper;
import com.amee.domain.AMEEStatus;
import com.amee.domain.Pager;
import com.amee.domain.ValueDefinition;
import com.amee.domain.algorithm.AbstractAlgorithm;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.algorithm.AlgorithmContext;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.search.ItemDefinitionsFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class DefinitionServiceDAO {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String CACHE_REGION = "query.definitionService";

    @PersistenceContext
    private EntityManager entityManager;

    // Algorithms & AlgorithmContexts
    @SuppressWarnings(value = "unchecked")
    public Algorithm getAlgorithmByUid(String uid) {
        Algorithm algorithm = null;
        if (!StringUtils.isBlank(uid)) {
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(Algorithm.class);
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            criteria.add(Restrictions.naturalId().set("uid", uid.toUpperCase()));
            criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
            criteria.setCacheable(true);
            criteria.setCacheRegion(CACHE_REGION);
            List<Algorithm> algorithms = criteria.list();
            if (algorithms.size() == 1) {
                log.debug("getAlgorithmByUid() found: {}", uid);
                algorithm = algorithms.get(0);
            } else {
                log.debug("getAlgorithmByUid() NOT found: {}", uid);
            }
        }
        return algorithm;
    }

    @SuppressWarnings(value = "unchecked")
    public List<AlgorithmContext> getAlgorithmContexts() {
        List<AlgorithmContext> algorithmContexts =
                entityManager.createQuery(
                        "FROM AlgorithmContext ac " +
                                "WHERE ac.status != :trash")
                        .setParameter("trash", AMEEStatus.TRASH)
                        .setHint("org.hibernate.cacheable", true)
                        .setHint("org.hibernate.cacheRegion", CACHE_REGION)
                        .getResultList();
        if (algorithmContexts.size() == 1) {
            log.debug("found AlgorithmContexts");
        } else {
            log.debug("AlgorithmContexts NOT found");
        }
        return algorithmContexts;
    }

    @SuppressWarnings(value = "unchecked")
    public AlgorithmContext getAlgorithmContextByUid(String uid) {
        AlgorithmContext algorithmContext = null;
        if (!StringUtils.isBlank(uid)) {
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(AlgorithmContext.class);
            criteria.add(Restrictions.naturalId().set("uid", uid.toUpperCase()));
            criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
            criteria.setCacheable(true);
            criteria.setCacheRegion(CACHE_REGION);
            List<AlgorithmContext> algorithmContexts = criteria.list();
            if (algorithmContexts.size() == 1) {
                log.debug("getAlgorithmContextByUid() found: {}", uid);
                algorithmContext = algorithmContexts.get(0);
            } else {
                log.debug("getAlgorithmContextByUid() NOT found: {}", uid);
            }
        }
        return algorithmContext;
    }

    public void persist(AbstractAlgorithm algorithm) {
        entityManager.persist(algorithm);
    }

    public void remove(AbstractAlgorithm algorithm) {
        algorithm.setStatus(AMEEStatus.TRASH);
    }

    // ItemDefinition

    public ItemDefinition getItemDefinitionByUid(String uid) {
        return getItemDefinitionByUid(uid, false);
    }

    @SuppressWarnings(value = "unchecked")
    public ItemDefinition getItemDefinitionByUid(String uid, boolean includeTrash) {
        ItemDefinition itemDefinition = null;
        if (!StringUtils.isBlank(uid)) {
            // See http://www.hibernate.org/117.html#A12 for notes on DISTINCT_ROOT_ENTITY.
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(ItemDefinition.class);
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            criteria.add(Restrictions.naturalId().set("uid", uid.toUpperCase()));
            if (!includeTrash) {
                criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
            }
            criteria.setFetchMode("itemValueDefinitions", FetchMode.JOIN);
            criteria.setCacheable(true);
            criteria.setCacheRegion(CACHE_REGION);
            List<ItemDefinition> itemDefinitions = criteria.list();
            if (itemDefinitions.size() == 1) {
                log.debug("getItemDefinitionByUid() found: {}", uid);
                itemDefinition = itemDefinitions.get(0);
            } else {
                log.debug("getItemDefinitionByUid() NOT found: {}", uid);
            }
        }
        return itemDefinition;
    }

    @SuppressWarnings(value = "unchecked")
    public ResultsWrapper<ItemDefinition> getItemDefinitions(ItemDefinitionsFilter filter) {
        // Create Criteria.
        // See http://www.hibernate.org/117.html#A12 for notes on DISTINCT_ROOT_ENTITY.
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(ItemDefinition.class);
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (!filter.getName().isEmpty()) {
            criteria.add(Restrictions.ilike("name", filter.getName(), MatchMode.EXACT));
        }
        criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
        criteria.addOrder(Order.asc("name").ignoreCase());
        criteria.setCacheable(true);
        criteria.setCacheRegion(CACHE_REGION);
        // Apply start and limit if relevant.
        if (filter.getResultStart() > 0) {
            criteria.setFirstResult(filter.getResultStart());
        }
        if (filter.getResultLimit() > 0) {
            criteria.setMaxResults(filter.getResultLimit() + 1);
        }
        // Get the results.
        List<ItemDefinition> itemDefinitions = (List<ItemDefinition>) criteria.list();
        // Did we limit the results?
        if (filter.getResultLimit() > 0) {
            // Results were limited, work out correct results and truncation state.
            return new ResultsWrapper<ItemDefinition>(
                    itemDefinitions.size() > filter.getResultLimit() ? itemDefinitions.subList(0, filter.getResultLimit()) : itemDefinitions,
                    itemDefinitions.size() > filter.getResultLimit());

        } else {
            // Results were not limited, no truncation.
            return new ResultsWrapper(itemDefinitions, false);
        }
    }

    @SuppressWarnings(value = "unchecked")
    public List<ItemDefinition> getItemDefinitions(Pager pager) {
        // first count all entities
        long count = (Long) entityManager.createQuery(
                "SELECT count(id) " +
                        "FROM ItemDefinition id " +
                        "WHERE id.status != :trash")
                .setParameter("trash", AMEEStatus.TRASH)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", CACHE_REGION)
                .getSingleResult();
        // tell pager how many entities there are and give it a chance to select the requested page again
        pager.setItems(count);
        pager.goRequestedPage();
        // now get the entities for the current page
        List<ItemDefinition> itemDefinitions = entityManager.createQuery(
                "SELECT id " +
                        "FROM ItemDefinition id " +
                        "WHERE id.status != :trash " +
                        "ORDER BY lower(id.name)")
                .setParameter("trash", AMEEStatus.TRASH)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", CACHE_REGION)
                .setMaxResults(pager.getItemsPerPage())
                .setFirstResult((int) pager.getStart())
                .getResultList();
        // update the pager
        pager.setItemsFound(itemDefinitions.size());
        return itemDefinitions;
    }

    public void persist(ItemDefinition itemDefinition) {
        entityManager.persist(itemDefinition);
    }

    public void remove(ItemDefinition itemDefinition) {
        itemDefinition.setStatus(AMEEStatus.TRASH);
    }

    protected void invalidate(ItemDefinition itemDefinition) {
        log.debug("invalidate() {}", itemDefinition.toString());
        ((Session) entityManager.getDelegate()).getSessionFactory().getCache().evictEntity(ItemDefinition.class, itemDefinition.getId());
    }

    // ItemValueDefinitions

    @SuppressWarnings(value = "unchecked")
    public ItemValueDefinition getItemValueDefinitionByUid(String uid) {
        ItemValueDefinition itemValueDefinition = null;
        if (!StringUtils.isBlank(uid)) {
            // See http://www.hibernate.org/117.html#A12 for notes on DISTINCT_ROOT_ENTITY.
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(ItemValueDefinition.class);
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            criteria.add(Restrictions.naturalId().set("uid", uid.toUpperCase()));
            criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
            criteria.setCacheable(true);
            criteria.setCacheRegion(CACHE_REGION);
            List<ItemValueDefinition> itemValueDefinitions = criteria.list();
            if (itemValueDefinitions.size() == 1) {
                log.debug("getItemValueDefinitionByUid() found: {}", uid);
                itemValueDefinition = itemValueDefinitions.get(0);
            } else {
                log.debug("getItemValueDefinitionByUid() NOT found: {}", uid);
            }
        }
        return itemValueDefinition;
    }

    public void persist(ItemValueDefinition itemValueDefinition) {
        entityManager.persist(itemValueDefinition);
    }

    public void remove(ItemValueDefinition itemValueDefinition) {
        itemValueDefinition.setStatus(AMEEStatus.TRASH);
    }

    public void invalidate(ItemValueDefinition itemValueDefinition) {
        log.debug("invalidate() {}", itemValueDefinition.toString());
        ((Session) entityManager.getDelegate()).getSessionFactory().getCache().evictEntity(ItemValueDefinition.class, itemValueDefinition.getId());
    }

    // ReturnValueDefinitions

    @SuppressWarnings(value = "unchecked")
    public ReturnValueDefinition getReturnValueDefinitionByUid(String uid) {
        ReturnValueDefinition returnValueDefinition = null;
        if (!StringUtils.isBlank(uid)) {
            // See http://www.hibernate.org/117.html#A12 for notes on DISTINCT_ROOT_ENTITY.
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(ReturnValueDefinition.class);
            criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
            criteria.add(Restrictions.naturalId().set("uid", uid.toUpperCase()));
            criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
            criteria.setCacheable(true);
            criteria.setCacheRegion(CACHE_REGION);
            List<ReturnValueDefinition> returnValueDefinitions = criteria.list();
            if (returnValueDefinitions.size() == 1) {
                log.debug("getReturnValueDefinitionByUid() found: {}", uid);
                returnValueDefinition = returnValueDefinitions.get(0);
            } else {
                log.debug("getReturnValueDefinitionByUid() NOT found: {}", uid);
            }
        }
        return returnValueDefinition;
    }

    public void persist(ReturnValueDefinition returnValueDefinition) {
        entityManager.persist(returnValueDefinition);
    }

    public void remove(ReturnValueDefinition returnValueDefinition) {
        returnValueDefinition.setStatus(AMEEStatus.TRASH);
    }

    public void invalidate(ReturnValueDefinition returnValueDefinition) {
        log.debug("invalidate() {}", returnValueDefinition.toString());
        ((Session) entityManager.getDelegate()).getSessionFactory().getCache().evictEntity(ReturnValueDefinition.class, returnValueDefinition.getId());
    }

    // ValueDefinitions

    @SuppressWarnings(value = "unchecked")
    public List<ValueDefinition> getValueDefinitions() {
        return entityManager.createQuery(
                "FROM ValueDefinition vd " +
                        "WHERE vd.status != :trash " +
                        "ORDER BY vd.name")
                .setParameter("trash", AMEEStatus.TRASH)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", CACHE_REGION)
                .getResultList();
    }

    @SuppressWarnings(value = "unchecked")
    public List<ValueDefinition> getValueDefinitions(Pager pager) {
        // first count all entities
        long count = (Long) entityManager.createQuery(
                "SELECT count(vd) " +
                        "FROM ValueDefinition vd " +
                        "WHERE vd.status != :trash")
                .setParameter("trash", AMEEStatus.TRASH)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", CACHE_REGION)
                .getSingleResult();
        // tell pager how many entities there are and give it a chance to select the requested page again
        pager.setItems(count);
        pager.goRequestedPage();
        // now get the entities for the current page
        List<ValueDefinition> valueDefinitions = entityManager.createQuery(
                "SELECT vd " +
                        "FROM ValueDefinition vd " +
                        "WHERE vd.status != :trash " +
                        "ORDER BY vd.name")
                .setParameter("trash", AMEEStatus.TRASH)
                .setHint("org.hibernate.cacheable", true)
                .setHint("org.hibernate.cacheRegion", CACHE_REGION)
                .setMaxResults(pager.getItemsPerPage())
                .setFirstResult((int) pager.getStart())
                .getResultList();
        // update the pager
        pager.setItemsFound(valueDefinitions.size());
        return valueDefinitions;
    }

    @SuppressWarnings(value = "unchecked")
    public ValueDefinition getValueDefinitionByUid(String uid) {
        ValueDefinition valueDefinition = null;
        if (!StringUtils.isBlank(uid)) {
            Session session = (Session) entityManager.getDelegate();
            Criteria criteria = session.createCriteria(ValueDefinition.class);
            criteria.add(Restrictions.naturalId().set("uid", uid.toUpperCase()));
            criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
            criteria.setCacheable(true);
            criteria.setCacheRegion(CACHE_REGION);
            criteria.setFlushMode(FlushMode.MANUAL);
            List<ValueDefinition> valueDefinitions = criteria.list();
            if (valueDefinitions.size() == 1) {
                log.debug("getValueDefinitionByUid() found: {}", uid);
                valueDefinition = valueDefinitions.get(0);
            } else {
                log.debug("getValueDefinitionByUid() NOT found: {}", uid);
            }
        }
        return valueDefinition;
    }

    public void persist(ValueDefinition valueDefinition) {
        entityManager.persist(valueDefinition);
    }

    public void remove(ValueDefinition valueDefinition) {
        valueDefinition.setStatus(AMEEStatus.TRASH);
    }

    /**
     * Set all sibling ReturnValueDefinition defaultType values to false.
     * <p/>
     * This method is deprecated as this logic is now performed in DefinitionService.unsetDefaultTypes.
     *
     * @param returnValueDefinition the ReturnValueDefinition that is the new default type.
     */
    @Deprecated
    public void unsetDefaultType(ReturnValueDefinition returnValueDefinition) {
        entityManager.createQuery(
                "UPDATE ReturnValueDefinition rvd " +
                        "SET rvd.defaultType = false " +
                        "WHERE rvd.itemDefinition.id = :itemDefinitionId " +
                        "AND rvd.id != :returnValueDefinitionId")
                .setParameter("itemDefinitionId", returnValueDefinition.getItemDefinition().getId())
                .setParameter("returnValueDefinitionId", returnValueDefinition.getId())
                .executeUpdate();
    }
}
