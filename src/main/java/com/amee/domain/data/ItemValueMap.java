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
package com.amee.domain.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * A Map of {@link LegacyItemValue} instances.
 * <p/>
 * The keys will be the {@link LegacyItemValue} paths. The entries will be a Set of {@link LegacyItemValue} instances. The Set will
 * consist of a single entry for single-valued {@link LegacyItemValue} histories.
 */
@SuppressWarnings("unchecked")
public class ItemValueMap extends HashMap {

    Log log = LogFactory.getLog(getClass());

    /**
     * Get the head {@link LegacyItemValue} in the historical sequence.
     *
     * @param path - the {@link LegacyItemValue} path.
     * @return the head {@link LegacyItemValue} in the historical sequence.
     */
    public LegacyItemValue get(String path) {
        LegacyItemValue itemValue = null;
        TreeSet<LegacyItemValue> series = (TreeSet<LegacyItemValue>) super.get(path);
        if (series != null) {
            itemValue = series.first();
        }
        return itemValue;
    }

    /**
     * Get the list of active {@link LegacyItemValue}s at the passed start Date.
     *
     * @param startDate - the active {@link LegacyItemValue} will be that starting immediately prior-to or on this date.
     * @return the set of active {@link LegacyItemValue}s at the passed start Date.
     */
    public List<LegacyItemValue> getAll(Date startDate) {
        List<LegacyItemValue> itemValues = new ArrayList();
        for (Object path : super.keySet()) {
            LegacyItemValue itemValue = get((String) path, startDate);
            if (itemValue != null) {
                itemValues.add(itemValue);
            } else {
                log.warn("getAll() Got null LegacyItemValue: path=" + path + ", startDate=" + startDate);
            }
        }
        return itemValues;
    }

    /**
     * Get all instances of {@link LegacyItemValue} with the passed path.
     *
     * @param path - the {@link LegacyItemValue} path.
     * @return the List of {@link LegacyItemValue}. Will be empty is there exists no {@link LegacyItemValue}s with this path.
     */
    public List<LegacyItemValue> getAll(String path) {
/*
        if (log.isDebugEnabled()) {
            ArrayList<LegacyItemValue> v = new ArrayList((TreeSet<LegacyItemValue>) super.get(path));
            String[] a = new String[v.size()];
            String name = v.get(0).getItem().getDisplayName();
            for(int i = 0; i < a.length; i++) {
                a[i] = v.get(i).getStartDate() + " : " + v.get(i).getValue();
            }
            log.debug("getAll() - Item: " + name + " - all LegacyItemValues for path: " + path + " => " + Arrays.toString(a));

        }
*/
        return new ArrayList((TreeSet<LegacyItemValue>) super.get(path));
    }

    /**
     * Get the active {@link LegacyItemValue} at the passed start Date.
     *
     * @param path      - the {@link LegacyItemValue} path.
     * @param startDate - the active {@link LegacyItemValue} will be that starting immediately prior-to or on this date.
     * @return the active {@link LegacyItemValue} at the passed start Date.
     */
    public LegacyItemValue get(String path, Date startDate) {
        LegacyItemValue itemValue = null;
        Set<LegacyItemValue> series = (TreeSet<LegacyItemValue>) super.get(path);
        if (series != null) {
            itemValue = find(series, startDate);
            //if (log.isDebugEnabled())
            //    log.debug("get() - Item: " + itemValue.getItem().getDisplayName() + " - LegacyItemValue for path: " + path + " => " +
            //            itemValue.getStartDate() + " : " + itemValue.getValue());
        }
        return itemValue;
    }

    public void put(String path, LegacyItemValue itemValue) {
        if (!containsKey(path)) {
            super.put(path, new TreeSet<LegacyItemValue>(new Comparator<LegacyItemValue>() {
                public int compare(LegacyItemValue iv1, LegacyItemValue iv2) {
                    return iv2.getStartDate().compareTo(iv1.getStartDate());
                }
            }));
        }

        Set<LegacyItemValue> itemValues = (Set<LegacyItemValue>) super.get(path);
        itemValues.add(itemValue);
    }

    /**
     * Find the active LegacyItemValue at startDate. The active LegacyItemValue is the one occurring at or
     * immediately before startDate.
     *
     * @param itemValues
     * @param startDate
     * @return the discovered LegacyItemValue, or null if not found
     */
    private LegacyItemValue find(Set<LegacyItemValue> itemValues, Date startDate) {
        LegacyItemValue selected = null;
        for (LegacyItemValue itemValue : itemValues) {
            if (!itemValue.getStartDate().after(startDate)) {
                selected = itemValue;
                selected.setHistoryAvailable(itemValues.size() > 1);
                break;
            }
        }
        return selected;
    }
}
