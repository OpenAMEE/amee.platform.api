package com.amee.domain;

import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.data.DataItem;

import java.util.Collection;

/**
 * Some entities will expect a bean implementing this bean to be present.
 */
public interface MetadataService {

    public Metadata getMetadataForEntity(IAMEEEntityReference entity, String name);

    public void loadMetadatasForDataCategories(Collection<DataCategory> dataCategories);

    public void loadMetadatasForDataItems(Collection<DataItem> dataItems);

    public void loadMetadatasForItemDefinitions(Collection<ItemDefinition> itemDefinitions);

    public void loadMetadatasForItemValueDefinitions(Collection<ItemValueDefinition> itemValueDefinitions);

    public void loadMetadatas(ObjectType objectType, Collection<IAMEEEntityReference> entities);

    public void clearMetadatas();

    public void persist(Metadata metadata);

    public void remove(Metadata metadata);

}
