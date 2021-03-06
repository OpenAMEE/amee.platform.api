package com.amee.service.metadata;

import com.amee.domain.AMEEStatus;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.Metadata;
import com.amee.domain.ObjectType;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class MetadataServiceDAOImpl implements MetadataServiceDAO {

    private static final String CACHE_REGION = "query.metadataService";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @SuppressWarnings(value = "unchecked")
    public Metadata getMetadataForEntity(IAMEEEntityReference entity, String name) {
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(Metadata.class);
        criteria.add(Restrictions.eq("entityReference.entityUid", entity.getEntityUid()));
        criteria.add(Restrictions.eq("entityReference.entityType", entity.getObjectType().getName()));
        criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
        criteria.add(Restrictions.eq("name", name));
        criteria.setCacheable(true);
        criteria.setCacheRegion(CACHE_REGION);
        criteria.setFlushMode(FlushMode.MANUAL);
        try {
            return (Metadata) criteria.uniqueResult();
        } catch (HibernateException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<Metadata> getMetadatas(IAMEEEntityReference entity) {
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(Metadata.class);
        criteria.add(Restrictions.eq("entityReference.entityUid", entity.getEntityUid()));
        criteria.add(Restrictions.eq("entityReference.entityType", entity.getObjectType().getName()));
        criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
        criteria.setFlushMode(FlushMode.MANUAL);
        return criteria.list();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<Metadata> getMetadatas(ObjectType objectType, Collection<IAMEEEntityReference> entities) {
        Set<Long> entityIds = new HashSet<Long>();
        entityIds.add(0L);
        for (IAMEEEntityReference entity : entities) {
            entityIds.add(entity.getEntityId());
        }
        Session session = (Session) entityManager.getDelegate();
        Criteria criteria = session.createCriteria(Metadata.class);
        criteria.add(Restrictions.in("entityReference.entityId", entityIds));
        criteria.add(Restrictions.eq("entityReference.entityType", objectType.getName()));
        criteria.add(Restrictions.ne("status", AMEEStatus.TRASH));
        criteria.setFlushMode(FlushMode.MANUAL);
        return criteria.list();
    }

    @Override
    public void persist(Metadata metadata) {
        entityManager.persist(metadata);
    }

    @Override
    public void remove(Metadata metadata) {
        metadata.setStatus(AMEEStatus.TRASH);
    }
}
