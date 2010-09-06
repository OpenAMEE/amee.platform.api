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
        return getLegacyEntity().equals(o);
    }

    @Override
    public int hashCode() {
        return getLegacyEntity().hashCode();
    }

    @Override
    public String toString() {
        return getLegacyEntity().toString();
    }

    protected void copyTo(AMEEEntity o) {
        getLegacyEntity().copyTo(o);
    }

    public List<Permission> handleAuthorizationContext(AuthorizationContext authorizationContext) {
        return getLegacyEntity().handleAuthorizationContext(authorizationContext);
    }

    @Override
    public Long getEntityId() {
        return getLegacyEntity().getEntityId();
    }

    @Override
    public String getEntityUid() {
        return getLegacyEntity().getEntityUid();
    }

    public AMEEStatus getStatus() {
        return getLegacyEntity().getStatus();
    }

    public int getStatusCode() {
        return getLegacyEntity().getStatusCode();
    }

    public boolean isTrash() {
        return getLegacyEntity().isTrash();
    }

    public boolean isActive() {
        return getLegacyEntity().isActive();
    }

    public boolean isDeprecated() {
        return getLegacyEntity().isDeprecated();
    }

    public void setStatus(AMEEStatus status) {
        getLegacyEntity().setStatus(status);
    }

    public void setStatus(String name) {
        getLegacyEntity().setStatus(name);
    }

    @Override
    public AccessSpecification getAccessSpecification() {
        return getLegacyEntity().getAccessSpecification();
    }

    @Override
    public void setAccessSpecification(AccessSpecification accessSpecification) {
        getLegacyEntity().setAccessSpecification(accessSpecification);
    }

    @Override
    public IAMEEEntity getEntity() {
        return getLegacyEntity().getEntity();
    }

    @Override
    public void setEntity(IAMEEEntity entity) {
        getLegacyEntity().setEntity(entity);
    }

    protected Metadata getMetadata(String key) {
        return getLegacyEntity().getMetadata(key);
    }

    protected String getMetadataValue(String key) {
        return getLegacyEntity().getMetadataValue(key);
    }

    protected Metadata getOrCreateMetadata(String key) {
        return getLegacyEntity().getOrCreateMetadata(key);
    }

    public void setMetadataService(IMetadataService metadataService) {
        getLegacyEntity().setMetadataService(metadataService);
    }

    public void setLocaleService(ILocaleService localeService) {
        getLegacyEntity().setLocaleService(localeService);
    }

    @Override
    public abstract AMEEEntity getLegacyEntity();
}