package com.amee.domain.data;

import com.amee.domain.item.BaseItemValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.Transient;
import java.util.*;

public class NuItemValueMap extends HashMap {

    Log log = LogFactory.getLog(getClass());

    @Transient
    private transient ItemValueMap adapter;

    /**
     * Get the head {@link BaseItemValue} in the historical sequence.
     *
     * @param path - the {@link BaseItemValue} path.
     * @return the head {@link BaseItemValue} in the historical sequence.
     */
    public BaseItemValue get(String path) {
        BaseItemValue itemValue = null;
        TreeSet<BaseItemValue> series = (TreeSet<BaseItemValue>) super.get(path);
        if (series != null) {
            itemValue = series.first();
        }
        return itemValue;
    }

    /**
     * Get the list of active {@link BaseItemValue}s at the passed start Date.
     *
     * @param startDate - the active {@link BaseItemValue} will be that starting immediately prior-to or on this date.
     * @return the set of active {@link BaseItemValue}s at the passed start Date.
     */
    public List<BaseItemValue> getAll(Date startDate) {
        List<BaseItemValue> itemValues = new ArrayList();
        for (Object path : super.keySet()) {
            BaseItemValue itemValue = get((String) path, startDate);
            if (itemValue != null) {
                itemValues.add(itemValue);
            } else {
                log.warn("getAll() Got null BaseItemValue: path=" + path + ", startDate=" + startDate);
            }
        }
        return itemValues;
    }

    /**
     * Get the active {@link BaseItemValue} at the passed start Date.
     *
     * @param path      - the {@link BaseItemValue} path.
     * @param startDate - the active {@link BaseItemValue} will be that starting immediately prior-to or on this date.
     * @return the active {@link BaseItemValue} at the passed start Date.
     */
    public BaseItemValue get(String path, Date startDate) {
        BaseItemValue itemValue = null;
        Set<BaseItemValue> series = (TreeSet<BaseItemValue>) super.get(path);
        if (series != null) {
            itemValue = find(series, startDate);
        }
        return itemValue;
    }

    /**
     * Get all instances of {@link BaseItemValue} with the passed path.
     *
     * @param path - the {@link BaseItemValue} path.
     * @return the List of {@link BaseItemValue}. Will be empty is there exists no {@link BaseItemValue}s with this path.
     */
    public List<BaseItemValue> getAll(String path) {
        return new ArrayList((TreeSet<BaseItemValue>) super.get(path));
    }

    /**
     * Find the active BaseItemValue at startDate. The active BaseItemValue is the one occurring at or
     * immediately before startDate.
     *
     * @param itemValues
     * @param startDate
     * @return the discovered BaseItemValue, or null if not found
     */
    private BaseItemValue find(Set<BaseItemValue> itemValues, Date startDate) {
        return null;

//        BaseItemValue selected = null;
//        for (BaseItemValue itemValue : itemValues) {
//            if (!itemValue.getStartDate().after(startDate)) {
//                selected = itemValue;
//                selected.setHistoryAvailable(itemValues.size() > 1);
//                break;
//            }
//        }
//        return selected;
    }

    public ItemValueMap getAdapter() {
        return adapter;
    }

    public void setAdapter(ItemValueMap adapter) {
        this.adapter = adapter;
    }

    public Object fail() {
        throw new UnsupportedOperationException();
    }


}
