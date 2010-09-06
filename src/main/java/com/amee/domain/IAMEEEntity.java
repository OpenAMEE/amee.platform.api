package com.amee.domain;

import com.amee.base.domain.DatedObject;
import com.amee.domain.auth.AuthorizationContext;
import com.amee.domain.auth.Permission;

import java.util.List;

public interface IAMEEEntity extends IAMEEEntityReference, DatedObject {

    AMEEStatus getStatus();

    int getStatusCode();

    boolean isTrash();

    boolean isActive();

    boolean isDeprecated();

    void setStatus(AMEEStatus status);

    void setStatus(String name);

    List<Permission> handleAuthorizationContext(AuthorizationContext authorizationContext);
}
