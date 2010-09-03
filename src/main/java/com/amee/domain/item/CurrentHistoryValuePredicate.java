package com.amee.domain.item;

import com.amee.platform.science.ExternalHistoryValue;
import org.apache.commons.collections.Predicate;

import java.util.List;

/**
 * Predicate for obtaining the latest DataItemValue in an historical sequence.
 */
class CurrentHistoryValuePredicate implements Predicate {

    private List<ExternalHistoryValue> values;

    public CurrentHistoryValuePredicate(List<ExternalHistoryValue> values) {
        this.values = values;
    }

    /**
     * Use the specified parameter to perform a test that returns true or false.
     *
     * @param o the DataItemValue under test.
     * @return true if o is the latest DataItemValue in an historical sequence.
     */
    public boolean evaluate(Object o) {
        // TODO: PL-3351
//        ExternalHistoryValue div = (ExternalHistoryValue) o;
//        Date startDate = div.getStartDate();
//        String path = div.getItemValueDefinition().getPath();
//        for (ExternalHistoryValue dataItemValue : dataItemValues) {
//            if (startDate.before(dataItemValue.getStartDate()) &&
//                    dataItemValue.getItemValueDefinition().getPath().equals(path)) {
//                return false;
//            }
//        }
        return true;
    }
}