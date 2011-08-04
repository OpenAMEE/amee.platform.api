package com.amee.domain.data;

import com.amee.domain.ItemService;
import com.amee.domain.item.BaseItemValue;
import com.amee.platform.science.StartEndDate;
import org.apache.commons.collections.Predicate;

import java.util.List;

/**
 * Predicate for obtaining the latest ItemValue in an historical sequence.
 */
class CurrentItemValuePredicate implements Predicate {

    private List<BaseItemValue> itemValues;
    private ItemService itemService;

    public CurrentItemValuePredicate(List<BaseItemValue> itemValues) {
        this.itemValues = itemValues;
    }

    public boolean evaluate(Object o) {
        BaseItemValue iv = (BaseItemValue) o;
        StartEndDate startDate = itemService.getStartDate(iv);
        String path = iv.getItemValueDefinition().getPath();
        for (BaseItemValue itemValue : itemValues) {
            if (startDate.before(itemService.getStartDate(itemValue)) &&
                    itemValue.getItemValueDefinition().getPath().equals(path)) {
                return false;
            }
        }
        return true;
    }
}
