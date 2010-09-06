package com.amee.domain.data;

import com.amee.platform.science.StartEndDate;
import org.apache.commons.collections.Predicate;

import java.util.List;

/**
 * Predicate for obtaining the latest ItemValue in an historical sequence.
 */
class CurrentItemValuePredicate implements Predicate {

    private List<ItemValue> itemValues;

    public CurrentItemValuePredicate(List<ItemValue> itemValues) {
        this.itemValues = itemValues;
    }

    public boolean evaluate(Object o) {
        ItemValue iv = (ItemValue) o;
        StartEndDate startDate = iv.getStartDate();
        String path = iv.getItemValueDefinition().getPath();
        for (ItemValue itemValue : itemValues) {
            if (startDate.before(itemValue.getStartDate()) &&
                    itemValue.getItemValueDefinition().getPath().equals(path)) {
                return false;
            }
        }
        return true;
    }
}
