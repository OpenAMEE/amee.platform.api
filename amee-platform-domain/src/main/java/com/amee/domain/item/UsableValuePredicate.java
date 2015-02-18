package com.amee.domain.item;


import org.apache.commons.collections4.Predicate;

/**
 * Basic Predicate testing {@link BaseItemValue} instances for usable values.
 * {@see DataItemValue#isUsableValue()}
 */
public class UsableValuePredicate implements Predicate {
    public boolean evaluate(Object o) {
        return ((BaseItemValue) o).isUsableValue();
    }
}
