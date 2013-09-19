package com.amee.domain.path;

import com.amee.base.domain.IdentityObject;
import com.amee.domain.IAMEEEntity;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;

import java.util.List;

public interface Pathable extends IdentityObject {

    String getPath();

    String getName();

    String getDisplayName();

    String getDisplayPath();

    String getFullPath();

    boolean isDeprecated();

    ObjectType getObjectType();

    IAMEEEntity getEntity();

    /**
     * Gets the ownership hierarchy for the entity. Eg, DC -> DC -> DI or PR -> PI. Used for permissions.
     *
     * @return a List of entities in the hierarchy. The first entry is the root, the last entry is the current entity.
     */
    List<IAMEEEntityReference> getHierarchy();
}

