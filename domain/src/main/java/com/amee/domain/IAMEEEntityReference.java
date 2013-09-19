package com.amee.domain;

import com.amee.domain.auth.AccessSpecification;

/**
 * Defines an interface exposing the properties required to identify (refer-to) an AMEEEntity.
 */
public interface IAMEEEntityReference {

    /**
     * Fetches the ID of the referenced entity.
     *
     * @return the entity id
     */
    Long getEntityId();

    /**
     * Fetches the UID of the referenced entity.
     *
     * @return entity UID
     */
    String getEntityUid();

    /**
     * Fetches the ObjectTyoe of the referenced entity.
     *
     * @return the ObjectType
     */
    ObjectType getObjectType();

    /**
     * Returns the transient AccessSpecification for this entity. This will only be present if
     * an AccessSpecification for this entity has been created in the current thread.
     *
     * @return the AccessSpecification for this entity in the current thread
     */
    AccessSpecification getAccessSpecification();

    /**
     * Sets the AccessSpecification for this entity.
     *
     * @param accessSpecification for this entity
     */
    void setAccessSpecification(AccessSpecification accessSpecification);

    IAMEEEntity getEntity();

    void setEntity(IAMEEEntity entity);
}