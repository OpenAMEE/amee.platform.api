package com.amee.domain;

import com.amee.base.domain.DatedObject;
import com.amee.domain.auth.AuthorizationContext;
import com.amee.domain.auth.Permission;

import java.util.List;

public interface IAMEEEntity extends IAMEEEntityReference, DatedObject {

    public AMEEStatus getStatus();

    public int getStatusCode();

    public boolean isTrash();

    public boolean isActive();

    public boolean isDeprecated();

    public void setStatus(AMEEStatus status);

    public void setStatus(String name);

    public List<Permission> handleAuthorizationContext(AuthorizationContext authorizationContext);
}
