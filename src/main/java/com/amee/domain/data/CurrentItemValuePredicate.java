package com.amee.domain.data;

import com.amee.domain.IDataItemService;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.data.BaseDataItemValue;
import com.amee.domain.item.profile.BaseProfileItemValue;
import com.amee.platform.science.ExternalHistoryValue;
import com.amee.platform.science.StartEndDate;
import org.apache.commons.collections.Predicate;

import java.util.List;

/**
 * Predicate for obtaining the latest ItemValue in an historical sequence.
 */
class CurrentItemValuePredicate implements Predicate {

    private List<BaseItemValue> itemValues;

    public CurrentItemValuePredicate(List<BaseItemValue> itemValues) {
        this.itemValues = itemValues;
    }

    public boolean evaluate(Object o) {
        BaseItemValue iv = (BaseItemValue) o;
        StartEndDate startDate = getStartDate(iv);
        String path = iv.getItemValueDefinition().getPath();
        for (BaseItemValue itemValue : itemValues) {
            if (startDate.before(getStartDate(itemValue)) &&
                    itemValue.getItemValueDefinition().getPath().equals(path)) {
                return false;
            }
        }
        return true;
    }

    /**
     * TODO: PL-6618 - This logic is copied in a number of places.
     */
    public StartEndDate getStartDate(BaseItemValue itemValue) {
        if (BaseProfileItemValue.class.isAssignableFrom(itemValue.getClass())) {
            return ((BaseProfileItemValue) itemValue).getProfileItem().getStartDate();
        } else if (BaseDataItemValue.class.isAssignableFrom(itemValue.getClass())) {
            if (ExternalHistoryValue.class.isAssignableFrom(itemValue.getClass())) {
                return ((ExternalHistoryValue) itemValue).getStartDate();
            } else {
                return new StartEndDate(IDataItemService.EPOCH);
            }
        } else {
            throw new IllegalStateException("A BaseProfileItemValue or BaseDataItemValue instance was expected.");
        }
    }
}
