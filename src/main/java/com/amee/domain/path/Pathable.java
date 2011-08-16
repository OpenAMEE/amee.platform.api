package com.amee.domain.path;

import com.amee.base.domain.IdentityObject;
import com.amee.domain.IAMEEEntity;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;

import java.util.List;

public interface Pathable extends IdentityObject {

    public String getPath();

    public String getName();

    public String getDisplayName();

    public String getDisplayPath();

    public String getFullPath();

    public boolean isDeprecated();

    public ObjectType getObjectType();

    public IAMEEEntity getEntity();

    public List<IAMEEEntityReference> getHierarchy();
}

