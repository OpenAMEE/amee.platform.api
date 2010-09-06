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
        return getLegacyEntity().equals(o);
    }

    @Override
    public int hashCode() {
        return getLegacyEntity().hashCode();
    }

    @Override
    public void onCreate() {
        getLegacyEntity().onCreate();
    }

    @Override
    public void onModify() {
        getLegacyEntity().onModify();
    }

    @Override
    public Long getId() {
        return getLegacyEntity().getId();
    }

    @Override
    public void setId(Long id) {
        getLegacyEntity().setId(id);
    }

    @Override
    public String getUid() {
        return getLegacyEntity().getUid();
    }

    @Override
    public void setUid(String uid) {
        getLegacyEntity().setUid(uid);
    }

    @Override
    public String getIdentityValue() {
        return getLegacyEntity().getIdentityValue();
    }

    @Override
    public void setIdentityValue(String value) {
        getLegacyEntity().setIdentityValue(value);
    }

    @Override
    public Date getCreated() {
        return getLegacyEntity().getCreated();
    }

    @Override
    public void setCreated(Date created) {
        getLegacyEntity().setCreated(created);
    }

    @Override
    public Date getModified() {
        return getLegacyEntity().getModified();
    }

    @Override
    public void setModified(Date modified) {
        getLegacyEntity().setModified(modified);
    }

    public abstract BaseEntity getLegacyEntity();
}