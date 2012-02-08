package com.amee.domain.data;

import com.amee.domain.item.BaseItemValue;
import com.amee.platform.science.ExternalHistoryValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Comparator;

/**
 * A {@link Comparator} to compare {@link BaseItemValue}s against each other based on the startDate. The values
 * will be sorted in ascending order with the earliest startDate first.
 */
public class BaseItemValueStartDateComparator implements Comparator<BaseItemValue> {

    private final Log log = LogFactory.getLog(getClass());

    @Override
    public int compare(BaseItemValue iv1, BaseItemValue iv2) {
        if (isHistoricValue(iv1) && isHistoricValue(iv2)) {
            // Both BaseItemValue are part of a history, compare their startDates.
            return ((ExternalHistoryValue) iv1).getStartDate().compareTo(((ExternalHistoryValue) iv2).getStartDate());
        } else if (isHistoricValue(iv1)) {
            // The first BaseItemValue is historical, but the second is not, so it needs to
            // come after the second BaseItemValue.
            return 1;
        } else if (isHistoricValue(iv2)) {
            // The second BaseItemValue is historical, but the first is not, so it needs to
            // come after the first BaseItemValue.
            return -1;
        } else {
            // Both BaseItemValues are not historical. This should not happen but consider them equal.
            // The new BaseItemValue will not be added to the TreeSet (see class note about inconsistency with equals).
            log.warn("put() Two non-historical BaseItemValues with the same path should not exist.");
            return 0;

            // Note: Java 7 may cause this branch to be executed due to changes in TreeMap implementation.
            // See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5045147
            // and: http://www.oracle.com/technetwork/java/javase/jdk7-relnotes-418459.html#jdk7changes
        }
    }

    public static boolean isHistoricValue(BaseItemValue itemValue) {
        return ExternalHistoryValue.class.isAssignableFrom(itemValue.getClass());
    }
}
