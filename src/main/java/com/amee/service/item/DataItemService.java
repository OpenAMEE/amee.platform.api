package com.amee.service.item;

import com.amee.base.transaction.TransactionController;
import com.amee.base.utils.UidGen;
import com.amee.domain.AMEEStatus;
import com.amee.domain.IDataCategoryReference;
import com.amee.domain.IDataItemService;
import com.amee.domain.ValueType;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItem;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.*;
import com.amee.domain.sheet.Choice;
import com.amee.platform.science.StartEndDate;
import com.amee.service.data.DataService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataItemService extends ItemService implements IDataItemService {

    @Autowired
    private TransactionController transactionController;

    @Autowired
    private DataService dataService;

    @Autowired
    private DataItemServiceDAO dao;

    @Override
    public List<NuDataItem> getDataItems(IDataCategoryReference dataCategory) {
        return getDataItems(dataCategory, true);
    }

    @Override
    public List<NuDataItem> getDataItems(IDataCategoryReference dataCategory, boolean checkDataItems) {
        List<NuDataItem> dataItems = new ArrayList<NuDataItem>();
        for (NuDataItem nuDataItem : dao.getDataItems(dataCategory)) {
            dataItems.add(nuDataItem);
        }
        return activeDataItems(dataItems, checkDataItems);
    }

    private List<NuDataItem> activeDataItems(List<NuDataItem> dataItems, boolean checkDataItems) {
        List<NuDataItem> activeDataItems = new ArrayList<NuDataItem>();
        for (NuDataItem dataItem : dataItems) {
            if (!dataItem.isTrash()) {
                if (checkDataItems) {
                    checkDataItem(dataItem);
                }
                activeDataItems.add(dataItem);
            }
        }
        loadItemValuesForItems((List) activeDataItems);
        localeService.loadLocaleNamesForNuDataItems(activeDataItems);
        return activeDataItems;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<NuDataItem> getDataItems(Set<Long> dataItemIds) {
        List<NuDataItem> dataItems = dao.getDataItems(dataItemIds);
        loadItemValuesForItems((List) dataItems);
        localeService.loadLocaleNamesForNuDataItems(dataItems);
        return dataItems;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Map<String, NuDataItem> getDataItemMap(Set<Long> dataItemIds, boolean loadValues) {
        Map<String, NuDataItem> dataItemMap = new HashMap<String, NuDataItem>();
        Set<BaseItemValue> dataItemValues = new HashSet<BaseItemValue>();
        // Load all NuDataItems and BaseItemValues, if required.
        List<NuDataItem> dataItems = dao.getDataItems(dataItemIds);
        if (loadValues) {
            loadItemValuesForItems((List) dataItems);
        }
        // Add NuDataItems to map. Add BaseItemValue, if required.
        for (NuDataItem dataItem : dataItems) {
            dataItemMap.put(dataItem.getUid(), dataItem);
            if (loadValues) {
                dataItemValues.addAll(this.getItemValues(dataItem));
            }
        }
        localeService.loadLocaleNamesForNuDataItems(dataItemMap.values(), dataItemValues);
        return dataItemMap;
    }

    @Override
    public NuDataItem getDataItemByIdentifier(DataCategory parent, String path) {
        NuDataItem dataItem = null;
        if (!StringUtils.isBlank(path)) {
            if (UidGen.INSTANCE_12.isValid(path)) {
                dataItem = getDataItemByUid(parent, path);
            }
            if (dataItem == null) {
                dataItem = getDataItemByPath(parent, path);
            }
        }
        return dataItem;
    }

    @Override
    public NuDataItem getDataItemByUid(DataCategory parent, String uid) {
        NuDataItem dataItem = getItemByUid(uid);
        if ((dataItem != null) && dataItem.getDataCategory().equals(parent)) {
            return dataItem;
        } else {
            return null;
        }
    }

    @Override
    public NuDataItem getItemByUid(String uid) {
        NuDataItem dataItem = dao.getItemByUid(uid);
        if ((dataItem != null) && (!dataItem.isTrash())) {
            checkDataItem(dataItem);
            return dataItem;
        } else {
            return null;
        }
    }

    @Override
    public NuDataItem getDataItemByPath(DataCategory parent, String path) {
        NuDataItem dataItem = dao.getDataItemByPath(parent, path);
        if ((dataItem != null) && !dataItem.isTrash()) {
            checkDataItem(dataItem);
            return dataItem;
        } else {
            return null;
        }
    }

    @Override
    public String getLabel(NuDataItem dataItem) {
        String label = "";
        BaseItemValue itemValue;
        ItemDefinition itemDefinition = dataItem.getItemDefinition();
        for (Choice choice : itemDefinition.getDrillDownChoices()) {
            itemValue = getItemValue(dataItem, choice.getName());
            if ((itemValue != null) &&
                    (itemValue.getValueAsString().length() > 0) &&
                    !itemValue.getValueAsString().equals("-")) {
                if (label.length() > 0) {
                    label = label.concat(", ");
                }
                label = label.concat(itemValue.getValueAsString());
            }
        }
        if (label.length() == 0) {
            label = dataItem.getDisplayPath();
        }
        return label;
    }

    /**
     * Add to the {@link com.amee.domain.item.data.NuDataItem} any {@link com.amee.domain.item.data.BaseDataItemValue}s it is missing.
     * This will be the case on first persist (this method acting as a reification function), and between GETs if any
     * new {@link com.amee.domain.data.ItemValueDefinition}s have been added to the underlying
     * {@link com.amee.domain.data.ItemDefinition}.
     * <p/>
     * Any updates to the {@link com.amee.domain.item.data.NuDataItem} will be persisted to the database.
     *
     * @param dataItem - the DataItem to check
     */
    @SuppressWarnings(value = "unchecked")
    public void checkDataItem(NuDataItem dataItem) {

        if (dataItem == null) {
            return;
        }

        Set<ItemValueDefinition> existingItemValueDefinitions = getItemValueDefinitionsInUse(dataItem);
        Set<ItemValueDefinition> missingItemValueDefinitions = new HashSet<ItemValueDefinition>();

        // find ItemValueDefinitions not currently implemented in this Item
        for (ItemValueDefinition ivd : dataItem.getItemDefinition().getItemValueDefinitions()) {
            if (ivd.isFromData()) {
                if (!existingItemValueDefinitions.contains(ivd)) {
                    missingItemValueDefinitions.add(ivd);
                }
            }
        }

        // Do we need to add any ItemValueDefinitions?
        if (missingItemValueDefinitions.size() > 0) {

            // Ensure a transaction has been opened. The implementation of open-session-in-view we are using
            // does not open transactions for GETs. This method is called for certain GETs.
            transactionController.begin(true);

            // TODO: PL-6618 - Hard-coded here.
            boolean isHistory = false;

            // create missing ItemValues
            for (ItemValueDefinition ivd : missingItemValueDefinitions) {
                BaseDataItemValue itemValue;
                // Create a nu style value.
                if (ivd.getValueDefinition().getValueType().equals(ValueType.INTEGER) ||
                        ivd.getValueDefinition().getValueType().equals(ValueType.DOUBLE)) {
                    // Item is a number.
                    if (isHistory) {
                        itemValue = new DataItemNumberValueHistory(ivd, dataItem);
                    } else {
                        itemValue = new DataItemNumberValue(ivd, dataItem);
                    }
                } else {
                    // Item is text.
                    if (isHistory) {
                        itemValue = new DataItemTextValueHistory(ivd, dataItem, "");
                    } else {
                        itemValue = new DataItemTextValue(ivd, dataItem, "");
                    }
                }
                persist(itemValue);
            }

            // clear caches
            clearItemValues();
            dataService.invalidate(dataItem.getDataCategory());
        }
    }

    @Override
    public void remove(NuDataItem dataItem) {
        dataItem.setStatus(AMEEStatus.TRASH);
    }

    @Override
    public void persist(NuDataItem dataItem) {
        persist(dataItem, true);
    }

    @Override
    public void persist(NuDataItem dataItem, boolean checkDataItem) {
        dao.persist(dataItem);
        if (checkDataItem) {
            checkDataItem(dataItem);
        }
    }

    // ItemValues.

    /**
     * Get an {@link BaseItemValue} belonging to this Item using some identifier and prevailing datetime context.
     *
     * @param identifier - a value to be compared to the path and then the uid of the Item Values belonging
     *                   to this Item.
     * @return the matched {@link BaseItemValue} or NULL if no match is found.
     */
    @Override
    public BaseItemValue getItemValue(BaseItem item, String identifier) {
        if (!NuDataItem.class.isAssignableFrom(item.getClass()))
            throw new IllegalStateException("A NuDataItem instance was expected.");
        return getItemValue(item, identifier, item.getEffectiveStartDate());
    }

    @Override
    public void persist(BaseItemValue itemValue) {
        dao.persist(itemValue);
    }

    @Override
    public void remove(BaseItemValue itemValue) {
        itemValue.setStatus(AMEEStatus.TRASH);
    }

    @Override
    public StartEndDate getStartDate(NuDataItem dataItem) {
        return null;
    }

    @Override
    public StartEndDate getEndDate(NuDataItem dataItem) {
        return null;
    }

    @Override
    protected DataItemServiceDAO getDao() {
        return dao;
    }
}
