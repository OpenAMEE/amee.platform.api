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

import com.amee.domain.auth.AccessSpecification;
import com.amee.domain.auth.AuthorizationContext;
import com.amee.domain.auth.Permission;

import java.util.List;

public abstract class AMEEEntityAdapter extends BaseEntityAdapter implements IAMEEEntity {

    public AMEEEntityAdapter() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        return getAdaptedEntity().equals(o);
    }

    @Override
    public int hashCode() {
        return getAdaptedEntity().hashCode();
    }

    @Override
    public String toString() {
        return getAdaptedEntity().toString();
    }

    protected void copyTo(AMEEEntity o) {
        getAdaptedEntity().copyTo(o);
    }

    public List<Permission> handleAuthorizationContext(AuthorizationContext authorizationContext) {
        return getAdaptedEntity().handleAuthorizationContext(authorizationContext);
    }

    @Override
    public Long getEntityId() {
        return getAdaptedEntity().getEntityId();
    }

    @Override
    public String getEntityUid() {
        return getAdaptedEntity().getEntityUid();
    }

    public AMEEStatus getStatus() {
        return getAdaptedEntity().getStatus();
    }

    public int getStatusCode() {
        return getAdaptedEntity().getStatusCode();
    }

    public boolean isTrash() {
        return getAdaptedEntity().isTrash();
    }

    public boolean isActive() {
        return getAdaptedEntity().isActive();
    }

    public boolean isDeprecated() {
        return getAdaptedEntity().isDeprecated();
    }

    public void setStatus(AMEEStatus status) {
        getAdaptedEntity().setStatus(status);
    }

    public void setStatus(String name) {
        getAdaptedEntity().setStatus(name);
    }

    @Override
    public AccessSpecification getAccessSpecification() {
        return getAdaptedEntity().getAccessSpecification();
    }

    @Override
    public void setAccessSpecification(AccessSpecification accessSpecification) {
        getAdaptedEntity().setAccessSpecification(accessSpecification);
    }

    @Override
    public IAMEEEntity getEntity() {
        return getAdaptedEntity().getEntity();
    }

    @Override
    public void setEntity(IAMEEEntity entity) {
        getAdaptedEntity().setEntity(entity);
    }

    protected Metadata getMetadata(String key) {
        return getAdaptedEntity().getMetadata(key);
    }

    protected String getMetadataValue(String key) {
        return getAdaptedEntity().getMetadataValue(key);
    }

    protected Metadata getOrCreateMetadata(String key) {
        return getAdaptedEntity().getOrCreateMetadata(key);
    }

    @Override
    public AMEEEntity getAdaptedEntity() {
        if (isLegacy()) {
            return getLegacyEntity();
        } else {
            return getNuEntity();
        }
    }

    @Override
    public abstract AMEEEntity getLegacyEntity();

    @Override
    public abstract AMEEEntity getNuEntity();
}