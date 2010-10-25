package com.amee.domain.item;

import org.apache.commons.collections.Predicate;

/**
 * Basic Predicate testing {@link BaseItemValue} instances for usable values.
 * {@see DataItemValue#isUsableValue()}
 */
public class NuUsableValuePredicate implements Predicate {
    public boolean evaluate(Object o) {
        return ((BaseItemValue) o).isUsableValue();
    }
}