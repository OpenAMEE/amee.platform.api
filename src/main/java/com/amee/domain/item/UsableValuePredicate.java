package com.amee.domain.item;

import com.amee.domain.data.ItemValue;
import org.apache.commons.collections.Predicate;

public class UsableValuePredicate implements Predicate {
    public boolean evaluate(Object o) {
        return ((ItemValue) o).isUsableValue();
    }
}
