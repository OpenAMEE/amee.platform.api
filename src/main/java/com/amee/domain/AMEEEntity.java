/*
 * This file is part of AMEE.
 *
 * Copyright (c) 2007, 2008, 2009 AMEE UK LIMITED (help@amee.com).
 *
 * AMEE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * AMEE is free software and is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Created by http://www.dgen.net.
 * Website http://www.amee.cc
 */
package com.amee.domain;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.auth.AccessSpecification;
import com.amee.domain.auth.AuthorizationContext;
import com.amee.domain.auth.Permission;
import com.amee.persist.BaseEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Extends BaseEntity to add state (status) and permissions.
 */
@MappedSuperclass
public abstract class AMEEEntity extends BaseEntity implements IAMEEEntity {

    /**
     * Represents the state of the entity.
     */
    @Column(name = "STATUS")
    protected AMEEStatus status = AMEEStatus.ACTIVE;

    /**
     * A transient AccessSpecification.
     */
    @Transient
    private AccessSpecification accessSpecification;

    @Transient
    private Map<String, Metadata> metadatas;

    /**
     * Default constructor.
     */
    public AMEEEntity() {
        super();
    }

    /**
     * Two AMEEEntity instances are considered equal if their UID matches, along with standard
     * object identity matching. The IAMEEEntityReference interface is used as the base identity
     * for all AMEEEntity instances.
     * <p/>
     * This needs to be kept the same as com.amee.domain.AMEEEntityReference#equals.
     *
     * @param o object to compare
     * @return true if the supplied object matches this object
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if ((o == null) || !IAMEEEntityReference.class.isAssignableFrom(o.getClass())) return false;
        IAMEEEntityReference entity = (IAMEEEntityReference) o;
        return getEntityUid().equals(entity.getEntityUid()) && getObjectType().equals(entity.getObjectType());
    }

    /**
     * Returns a hash code based on the entityId and entityType properties.
     * <p/>
     * This needs to be kept the same as com.amee.domain.AMEEEntityReference#hashCode.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == getEntityUid() ? 0 : getEntityUid().hashCode());
        hash = 31 * hash + (null == getObjectType() ? 0 : getObjectType().hashCode());
        return hash;
    }

    @Override
    public String toString() {
        return getObjectType() + "_" + getUid();
    }

    /**
     * Copy values from this instance to the supplied instance.
     *
     * @param o Object to copy values to
     */
    protected void copyTo(AMEEEntity o) {
        super.copyTo(o);
        o.status = status;
    }

    /**
     * Allows specific entities to interact with an AuthorizationContext and express
     * permissions that are implicit in the model.
     * <p/>
     * This default implementation does nothing and returns an empty list.
     *
     * @param authorizationContext to consider
     * @return permissions list
     */
    @Override
    public List<Permission> handleAuthorizationContext(AuthorizationContext authorizationContext) {
        return new ArrayList<Permission>();
    }

    /**
     * Sets the entity ID. Implements method declared in IAMEEEntityReference.
     *
     * @return the entity ID
     */
    @Override
    public Long getEntityId() {
        return getId();
    }

    /**
     * Get the entity UID. Implements method declared in IAMEEEntityReference.
     *
     * @return the entity UID
     */
    @Override
    public String getEntityUid() {
        return getUid();
    }

    /**
     * Fetch the entity status.
     *
     * @return entity status
     */
    @Override
    public AMEEStatus getStatus() {
        return status;
    }

    /**
     * Fetch the entity status as the ordinal of the AMEEStatus.
     *
     * @return ordinal of AMEEStatus
     */
    @Override
    public int getStatusCode() {
        return status.ordinal();
    }

    /**
     * Convienience method to determine if the entity state is TRASH.
     *
     * @return true if the entity state is TRASH
     */
    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH);
    }

    /**
     * Convienience method to determine if the entity state is ACTIVE.
     *
     * @return true if the entity state is ACTIVE
     */
    @Override
    public boolean isActive() {
        return status.equals(AMEEStatus.ACTIVE);
    }

    /**
     * Convienience method to determine if the entity state is DEPRECATED.
     *
     * @return true if the entity state is DEPRECATED
     */
    @Override
    public boolean isDeprecated() {
        return status.equals((AMEEStatus.DEPRECATED));
    }

    /**
     * Set the status of the entity.
     *
     * @param status to set
     */
    @Override
    public void setStatus(AMEEStatus status) {
        this.status = status;
    }

    /**
     * Set the status of the entity. The name is used to find the correct AMEEStatus.
     *
     * @param name represeting status
     */
    @Override
    public void setStatus(String name) {
        if (name != null) {
            try {
                setStatus(AMEEStatus.valueOf(name));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The supplied status name is invalid.");
            }
        }
    }

    /**
     * Returns the transient AccessSpecification for this entity. This will only be present if
     * an AccessSpecification for this entity has been created in the current thread.
     *
     * @return the AccessSpecification for this entity in the current thread
     */
    @Override
    public AccessSpecification getAccessSpecification() {
        return accessSpecification;
    }

    /**
     * Sets the AccessSpecification for this entity.
     *
     * @param accessSpecification for this entity
     */
    @Override
    public void setAccessSpecification(AccessSpecification accessSpecification) {
        this.accessSpecification = accessSpecification;
    }

    @Override
    public IAMEEEntity getEntity() {
        return this;
    }

    @Override
    public void setEntity(IAMEEEntity entity) {
        // do nothing
    }

    protected Metadata getMetadata(String key) {
        if (metadatas == null) {
            metadatas = new HashMap<String, Metadata>();
        }
        if (!metadatas.containsKey(key)) {
            metadatas.put(key, getMetadataService().getMetadataForEntity(this, key));
        }
        return metadatas.get(key);
    }

    protected String getMetadataValue(String key) {
        Metadata metadata = getMetadata(key);
        if (metadata != null) {
            return metadata.getValue();
        } else {
            return "";
        }
    }

    protected Metadata getOrCreateMetadata(String key) {
        Metadata metadata = getMetadata(key);
        if (metadata == null) {
            metadata = new Metadata(this, key);
            getMetadataService().persist(metadata);
            metadatas.put(key, metadata);
        }
        return metadata;
    }

    @Transient
    protected MetadataService getMetadataService() {
        return ThreadBeanHolder.get(MetadataService.class);
    }

    @Transient
    protected LocaleService getLocaleService() {
        return ThreadBeanHolder.get(LocaleService.class);
    }
}