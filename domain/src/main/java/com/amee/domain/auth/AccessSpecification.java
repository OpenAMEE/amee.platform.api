package com.amee.domain.auth;

import com.amee.domain.IAMEEEntityReference;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Associates PermissionEntries with an AMEEEntity to specify desired and actual access rights.
 */
public class AccessSpecification implements Serializable {

    private IAMEEEntityReference entityReference;
    private Set<PermissionEntry> actual;
    private Set<PermissionEntry> desired = new HashSet<PermissionEntry>();

    private AccessSpecification() {
        super();
    }

    public AccessSpecification(IAMEEEntityReference entityReference) {
        this();
        this.entityReference = entityReference;
        entityReference.setAccessSpecification(this);
    }

    public AccessSpecification(IAMEEEntityReference entityReference, PermissionEntry... desired) {
        this(entityReference);
        CollectionUtils.addAll(getDesired(), desired);
    }

    public AccessSpecification(IAMEEEntityReference entityReference, Set<PermissionEntry> actual, PermissionEntry... desired) {
        this(entityReference);
        setActual(actual);
        CollectionUtils.addAll(getDesired(), desired);
    }

    public IAMEEEntityReference getEntityReference() {
        return entityReference;
    }

    public Set<PermissionEntry> getActual() {
        return actual;
    }

    public boolean hasActual() {
        return actual != null;
    }

    public void setActual(Set<PermissionEntry> actual) {
        if (actual != null) {
            this.actual = new HashSet<PermissionEntry>(actual);
        } else {
            this.actual = null;
        }
    }

    public Set<PermissionEntry> getDesired() {
        return desired;
    }
}