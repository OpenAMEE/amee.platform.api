package com.amee.domain;

import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.data.LocaleName;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.DataItem;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Some entities will expect a bean implementing this bean to be present.
 */
public interface LocaleService {

    public Map<String, LocaleName> getLocaleNames(IAMEEEntityReference entity);

    public LocaleName getLocaleName(IAMEEEntityReference entity);

    public LocaleName getLocaleName(IAMEEEntityReference entity, String locale);

    public String getLocaleNameValue(IAMEEEntityReference entity);

    public String getLocaleNameValue(IAMEEEntityReference entity, String defaultName);

    public void clearLocaleName(IAMEEEntityReference entity, String locale);

    public void setLocaleName(IAMEEEntityReference entity, String locale, String name);

    public void loadLocaleNamesForDataCategories(Collection<DataCategory> dataCategories);

    public void loadLocaleNamesForDataCategoryReferences(Collection<IDataCategoryReference> dataCategories);

    public void loadLocaleNamesForDataItems(Collection<DataItem> dataItems);

    public void loadLocaleNamesForDataItems(Collection<DataItem> dataItems, Set<BaseItemValue> values);

    public void loadLocaleNamesForItemValueDefinitions(Collection<ItemValueDefinition> itemValueDefinitions);

    public void loadLocaleNames(ObjectType objectType, Collection<IAMEEEntityReference> entities);

    public void clearLocaleNames();

    public void persist(LocaleName localeName);

    public void remove(LocaleName localeName);
}
