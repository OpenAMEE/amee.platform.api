package com.amee.domain.item;


import org.apache.commons.collections4.Predicate;

/**
 * Basic Predicate testing {@link BaseItemValue} instances for usable values.
 * {@see DataItemValue#isUsableValue()}
 */
public class UsableValuePredicate implements Predicate<BaseItemValue> {

    @Override
    public boolean evaluate(BaseItemValue o) {
        return o.isUsableValue();
    }
}
