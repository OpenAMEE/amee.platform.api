package com.amee.service.definition;

import com.amee.base.domain.ResultsWrapper;
import com.amee.base.transaction.AMEETransaction;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;
import com.amee.domain.Pager;
import com.amee.domain.ValueDefinition;
import com.amee.domain.algorithm.AbstractAlgorithm;
import com.amee.domain.algorithm.Algorithm;
import com.amee.domain.algorithm.AlgorithmContext;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.search.ItemDefinitionsFilter;
import com.amee.service.data.DataService;
import com.amee.service.invalidation.InvalidationMessage;
import com.amee.service.invalidation.InvalidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("definitionService")
public class DefinitionServiceImpl implements DefinitionService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private InvalidationService invalidationService;

    @Autowired
    private DataService dataService;

    @Autowired
    private DefinitionServiceDAO dao;

    // Events

    @Override
    @AMEETransaction
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public void onApplicationEvent(InvalidationMessage invalidationMessage) {
        if ((invalidationMessage.isLocal() || invalidationMessage.isFromOtherInstance()) &&
                invalidationMessage.getObjectType().equals(ObjectType.ID)) {
            log.trace("onApplicationEvent() Handling InvalidationMessage.");
            ItemDefinition itemDefinition = getItemDefinitionByUid(invalidationMessage.getEntityUid());
            if (itemDefinition != null) {
                clearCaches(itemDefinition);
            }
        }
    }

    // Algorithms

    @Override
    public Algorithm getAlgorithmByUid(ItemDefinition itemDefinition, String uid) {
        Algorithm algorithm = getAlgorithmByUid(uid);
        if (algorithm != null && algorithm.getItemDefinition().equals(itemDefinition)) {
            return algorithm;
        } else {
            return null;
        }
    }

    @Override
    public Algorithm getAlgorithmByUid(String uid) {
        return dao.getAlgorithmByUid(uid);
    }

    @Override
    public List<AlgorithmContext> getAlgorithmContexts() {
        return dao.getAlgorithmContexts();
    }

    @Override
    public AlgorithmContext getAlgorithmContextByUid(String uid) {
        AlgorithmContext algorithmContext = dao.getAlgorithmContextByUid(uid);
        return algorithmContext;
    }

    @Override
    public void persist(AbstractAlgorithm algorithm) {
        dao.persist(algorithm);
    }

    @Override
    public void remove(AbstractAlgorithm algorithm) {
        dao.remove(algorithm);
    }

    // ItemDefinition

    /**
     * Returns the ItemDefinition for the ItemDefinition UID specified. Returns null
     * if the ItemDefinition could not be found.
     *
     * @param uid of the ItemDefinition to fetch
     * @return the ItemDefinition matching the ItemDefinition UID specified
     */
    @Override
    public ItemDefinition getItemDefinitionByUid(String uid) {
        return getItemDefinitionByUid(uid, false);
    }

    /**
     * Returns the ItemDefinition for the ItemDefinition UID specified. Returns null
     * if the ItemDefinition could not be found.
     *
     * @param uid          of the ItemDefinition to fetch
     * @param includeTrash if true will include trashed ItemDefinitions
     * @return the ItemDefinition matching the ItemDefinition UID specified
     */
    @Override
    public ItemDefinition getItemDefinitionByUid(String uid, boolean includeTrash) {
        return dao.getItemDefinitionByUid(uid, includeTrash);
    }

    @Override
    public List<ItemDefinition> getItemDefinitions() {
        ItemDefinitionsFilter filter = new ItemDefinitionsFilter();
        filter.setResultLimit(0);
        filter.setResultStart(0);
        return getItemDefinitions(filter).getResults();
    }

    @Override
    public ResultsWrapper<ItemDefinition> getItemDefinitions(ItemDefinitionsFilter filter) {
        return dao.getItemDefinitions(filter);
    }

    @Override
    public List<ItemDefinition> getItemDefinitions(Pager pager) {
        return dao.getItemDefinitions(pager);
    }

    @Override
    public void persist(ItemDefinition itemDefinition) {
        dao.persist(itemDefinition);
    }

    @Override
    public void remove(ItemDefinition itemDefinition) {
        dao.remove(itemDefinition);
    }

    /**
     * Invalidate an ItemDefinition. This will send an invalidation message via the
     * InvalidationService and clear the local caches.
     *
     * @param itemDefinition to invalidate
     */
    @Override
    public void invalidate(ItemDefinition itemDefinition) {
        log.info("invalidate() itemDefinition: " + itemDefinition.getUid());
        invalidationService.add(itemDefinition);
        for (IAMEEEntityReference ref : dataService.getDataCategoryReferences(itemDefinition)) {
            invalidationService.add(ref, "indexDataItems");
        }
    }

    /**
     * Clears all caches related to the supplied ItemDefinition.
     *
     * @param itemDefinition to clear caches for
     */
    @Override
    public void clearCaches(ItemDefinition itemDefinition) {
        log.info("clearCaches() itemDefinition: " + itemDefinition.getUid());
        // Invalidate the ItemDefinition itself.
        dao.invalidate(itemDefinition);
        // Clear all ItemValueDefinitions from the cache.
        for (ItemValueDefinition itemValueDefinition : itemDefinition.getItemValueDefinitions()) {
            dao.invalidate(itemValueDefinition);
        }
        // Clear all ReturnValueDefinitions from the cache.
        for (ReturnValueDefinition returnValueDefinition : itemDefinition.getReturnValueDefinitions()) {
            dao.invalidate(returnValueDefinition);
        }
        // TODO: Algorithms.
        // TODO: ItemDefinition Metadata.
        // TODO: ItemDefinition Locales.
        // TODO: What else? Anything in the index?
    }

    // ItemValueDefinitions

    @Override
    public ItemValueDefinition getItemValueDefinitionByUid(ItemDefinition itemDefinition, String uid) {
        ItemValueDefinition itemValueDefinition = getItemValueDefinitionByUid(uid);
        if (itemValueDefinition != null && itemValueDefinition.getItemDefinition().equals(itemDefinition)) {
            return itemValueDefinition;
        } else {
            return null;
        }
    }

    @Override
    public ItemValueDefinition getItemValueDefinitionByUid(String uid) {
        return dao.getItemValueDefinitionByUid(uid);
    }

    @Override
    public void persist(ItemValueDefinition itemValueDefinition) {
        dao.persist(itemValueDefinition);
    }

    @Override
    public void remove(ItemValueDefinition itemValueDefinition) {
        dao.remove(itemValueDefinition);
    }

    // ReturnValueDefinitions

    @Override
    public ReturnValueDefinition getReturnValueDefinitionByUid(ItemDefinition itemDefinition, String uid) {
        ReturnValueDefinition returnValueDefinition = getReturnValueDefinitionByUid(uid);
        if (returnValueDefinition != null && returnValueDefinition.getItemDefinition().equals(itemDefinition)) {
            return returnValueDefinition;
        } else {
            return null;
        }
    }

    @Override
    public ReturnValueDefinition getReturnValueDefinitionByUid(String uid) {
        return dao.getReturnValueDefinitionByUid(uid);
    }

    @Override
    public void persist(ReturnValueDefinition returnValueDefinition) {
        dao.persist(returnValueDefinition);
    }

    /**
     * Set all sibling ReturnValueDefinition defaultType values to false.
     *
     * @param returnValueDefinition the ReturnValueDefinition that is the new default type.
     */
    @Override
    public void unsetDefaultTypes(ReturnValueDefinition returnValueDefinition) {
        if (returnValueDefinition.isDefaultType()) {
            for (ReturnValueDefinition rvd : returnValueDefinition.getItemDefinition().getReturnValueDefinitions()) {
                if (!rvd.equals(returnValueDefinition)) {
                    rvd.setDefaultType(false);
                }
            }
        }
    }

    @Override
    public void remove(ReturnValueDefinition returnValueDefinition) {
        dao.remove(returnValueDefinition);
    }

    // ValueDefinitions

    @Override
    public List<ValueDefinition> getValueDefinitions() {
        return dao.getValueDefinitions();
    }

    @Override
    public List<ValueDefinition> getValueDefinitions(Pager pager) {
        return dao.getValueDefinitions(pager);
    }

    @Override
    public ValueDefinition getValueDefinition(String uid) {
        ValueDefinition valueDefinition = dao.getValueDefinitionByUid(uid);
        return valueDefinition;
    }

    @Override
    public void persist(ValueDefinition valueDefinition) {
        dao.persist(valueDefinition);
    }

    @Override
    public void remove(ValueDefinition valueDefinition) {
        dao.remove(valueDefinition);
    }
}
