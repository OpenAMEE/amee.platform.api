package com.amee.platform.resource;

import com.amee.domain.AMEEStatus;

import java.io.Serializable;

public class EntityFilter implements Serializable {

    private AMEEStatus status = AMEEStatus.ACTIVE;

    public EntityFilter() {
        super();
    }

    public AMEEStatus getStatus() {
        return status;
    }

    public void setStatus(AMEEStatus status) {
        this.status = status;
    }
}
