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
package com.amee.domain.item;

import com.amee.platform.science.ExternalHistoryValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * A Map of {@link ExternalHistoryValue} instances.
 * <p/>
 * The keys will be the {@link ExternalHistoryValue} paths. The entries will be a Set of {@link ExternalHistoryValue} instances.
 * The Set will consist of a single entry for single-valued {@link ExternalHistoryValue} histories.
 */
public class HistoryValueMap extends HashMap<String, Set<ExternalHistoryValue>> {

    private final Log log = LogFactory.getLog(getClass());

    /**
     * Get the head (earliest) {@link ExternalHistoryValue} in the historical sequence.
     *
     * @param path - the {@link ExternalHistoryValue} path.
     * @return the head {@link ExternalHistoryValue} in the historical sequence or null if the path is not present.
     */
    public ExternalHistoryValue get(String path) {
        ExternalHistoryValue value = null;
        TreeSet<ExternalHistoryValue> series = new TreeSet<ExternalHistoryValue>(super.get(path));
        if (!series.isEmpty()) {
            value = series.first();
        }
        return value;
    }

    /**
     * Get the active {@link ExternalHistoryValue} at the passed start Date.
     *
     * @param path      the {@link ExternalHistoryValue} path.
     * @param startDate the active {@link ExternalHistoryValue} will be that starting immediately prior to or on this date.
     * @return the active {@link ExternalHistoryValue} at the passed start Date.
     */
    public ExternalHistoryValue get(String path, Date startDate) {
        ExternalHistoryValue value = null;
        Set<ExternalHistoryValue> series = super.get(path);
        if (series != null) {
            value = find(series, startDate);
        }
        return value;
    }

    /**
     * Get the list of active {@link ExternalHistoryValue}s at the passed start Date.
     *
     * @param startDate the active {@link ExternalHistoryValue} will be that starting immediately prior-to or on this date.
     * @return the set of active {@link ExternalHistoryValue}s at the passed start Date.
     */
    public List<ExternalHistoryValue> getAll(Date startDate) {
        List<ExternalHistoryValue> values = new ArrayList<ExternalHistoryValue>();
        for (String path : super.keySet()) {
            ExternalHistoryValue value = get(path, startDate);
            if (value != null) {
                values.add(value);
            } else {
                log.warn("getAll() Got null Value with path '" + path + "' and startDate '" + startDate + "'");
            }
        }
        return values;
    }

    /**
     * Get all instances of {@link ExternalHistoryValue} with the given path.
     *
     * @param path the {@link ExternalHistoryValue} path.
     * @return the List of {@link ExternalHistoryValue}. Will be empty is there exists no {@link ExternalHistoryValue}s with this path.
     */
    public List<ExternalHistoryValue> getAll(String path) {
        return new ArrayList<ExternalHistoryValue>(super.get(path));
    }

    /**
     * Add a ExternalHistoryValue
     *
     * @param path
     * @param value
     */
    public void put(String path, ExternalHistoryValue value) {
        if (!containsKey(path)) {
            super.put(path, new TreeSet<ExternalHistoryValue>(new Comparator<ExternalHistoryValue>() {
                public int compare(ExternalHistoryValue v1, ExternalHistoryValue v2) {
                    return v2.getStartDate().compareTo(v1.getStartDate());
                }
            }));
        }

        Set<ExternalHistoryValue> values = super.get(path);
        values.add(value);
    }

    /**
     * Find the active ExternalHistoryValue at startDate.
     * The active ExternalHistoryValue is the one occurring at or immediately before startDate.
     *
     * @param values
     * @param startDate
     * @return the discovered ItemValue, or null if not found
     */
    private ExternalHistoryValue find(Set<ExternalHistoryValue> values, Date startDate) {
        ExternalHistoryValue selected = null;
        for (ExternalHistoryValue value : values) {
            if (!value.getStartDate().after(startDate)) {
                selected = value;
                // TODO: PL-3351
                // selected.setHistoryAvailable(dataItemValues.size() > 1);
                break;
            }
        }
        return selected;
    }
}
