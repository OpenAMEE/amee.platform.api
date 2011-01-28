package com.amee.domain.item;

import org.apache.commons.collections.Predicate;

public class UsableValuePredicate implements Predicate {
    public boolean evaluate(Object o) {
        return ((BaseItemValue) o).isUsableValue();
    }
}
