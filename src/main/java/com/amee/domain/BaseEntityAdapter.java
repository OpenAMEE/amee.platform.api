package com.amee.domain;

import com.amee.base.domain.DatedObject;
import com.amee.persist.BaseEntity;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseEntityAdapter implements DatedObject, Serializable {

    public BaseEntityAdapter() {
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
    public void onCreate() {
        getAdaptedEntity().onCreate();
    }

    @Override
    public void onModify() {
        getAdaptedEntity().onModify();
    }

    @Override
    public Long getId() {
        return getAdaptedEntity().getId();
    }

    @Override
    public void setId(Long id) {
        getAdaptedEntity().setId(id);
    }

    @Override
    public String getUid() {
        return getAdaptedEntity().getUid();
    }

    @Override
    public void setUid(String uid) {
        getAdaptedEntity().setUid(uid);
    }

    @Override
    public String getIdentityValue() {
        return getAdaptedEntity().getIdentityValue();
    }

    @Override
    public void setIdentityValue(String value) {
        getAdaptedEntity().setIdentityValue(value);
    }

    @Override
    public Date getCreated() {
        return getAdaptedEntity().getCreated();
    }

    @Override
    public void setCreated(Date created) {
        getAdaptedEntity().setCreated(created);
    }

    @Override
    public Date getModified() {
        return getAdaptedEntity().getModified();
    }

    @Override
    public void setModified(Date modified) {
        getAdaptedEntity().setModified(modified);
    }

    public boolean isLegacy() {
        if (getLegacyEntity() != null) {
            return true;
        } else if (getNuEntity() != null) {
            return false;
        } else {
            throw new IllegalStateException("Missing entity.");
        }
    }

    public BaseEntity getAdaptedEntity() {
        if (isLegacy()) {
            return getLegacyEntity();
        } else {
            return getNuEntity();
        }
    }

    public abstract BaseEntity getLegacyEntity();

    public abstract BaseEntity getNuEntity();
}