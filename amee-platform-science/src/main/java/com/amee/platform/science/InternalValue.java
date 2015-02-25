package com.amee.platform.science;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Provides a wrapper around external representations of values.
 * <p/>
 * Amount values will be converted to AMEE internal units if necessary. All other value types will be wrapped unchanged.
 */
@SuppressWarnings("unchecked")
public class InternalValue {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Logger slog = LoggerFactory.getLogger("science");
    private Object value;

    /**
     * Instantiate an InternalValue representation of the supplied value.
     *
     * @param value - the String representation of the value.
     */
    public InternalValue(String value) {
        this.value = value;
    }

    /**
     * Instantiate an InternalValue representation of the supplied value.
     *
     * @param value - the {@link ExternalGenericValue} representation of the value.
     */
    public InternalValue(ExternalGenericValue value) {
        if (ExternalNumberValue.class.isAssignableFrom(value.getClass()) && value.isDouble()) {
            ExternalNumberValue env = (ExternalNumberValue) value;
            this.value = asInternalDecimal(env).getValue();
        } else if (ExternalTextValue.class.isAssignableFrom(value.getClass())) {
            ExternalTextValue etv = (ExternalTextValue) value;
            this.value = etv.getUsableValue();
        } else {
            throw new IllegalStateException("Expected an ExternalNumberValue or ExternalTextValue.");
        }
    }

    /**
     * Instantiate an InternalValue representation of the supplied collection of values.
     *
     * @param values    - the List of {@link com.amee.platform.science.ExternalValue}s representing a sequence of values
     * @param startDate - the start Date to filter the series
     * @param endDate   - the end Date to filter the series
     */
    public InternalValue(List<? extends ExternalGenericValue> values, Date startDate, Date endDate) {
        slog.info("InternalValue() Diagnostics from filtering: " + values.size() + "," + new DateTime(startDate) + "," + new DateTime(endDate));

        if (ExternalNumberValue.class.isAssignableFrom(values.get(0).getClass())) {
            DataSeries ds = new DataSeries();
            for (ExternalGenericValue itemValue : filterItemValues(values, startDate, endDate)) {
                DateTime timestamp;
                if (isHistoricValue(itemValue)) {
                    timestamp = ((ExternalHistoryValue)itemValue).getStartDate().toDateTime();
                } else {
                    timestamp = new DateTime(0);
                }
                ds.addDataPoint(new DataPoint(timestamp, asInternalDecimal(itemValue)));
            }
            ds.setSeriesStartDate(new DateTime(startDate));
            ds.setSeriesEndDate(new DateTime(endDate));
            this.value = ds;
            slog.info("InternalValue() Series dates " + ds.getSeriesStartDate() + "->" + ds.getSeriesEndDate());
        } else {
            this.value = values;
        }
    }

    /**
     * Get the wrapped internal value.
     *
     * @return - the wrapped internal value.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Filter the ItemValue collection by the effective start and end dates of the owning Item.
     * ItemValues are excluded if they start prior to startDate and are not the final value in the sequence.
     * ItemValues are excluded if they start on or after the endDate.
     * The item value immediately prior to the start of the selection interval should be kept
     *
     * @param values    ItemValues to filter
     * @param startDate effective startDate of Item
     * @param endDate   effective endDate of Item
     * @return the filtered values
     */
    private List<ExternalGenericValue> filterItemValues(List<? extends ExternalGenericValue> values, Date startDate, Date endDate) {

        List<ExternalGenericValue> filteredValues = new ArrayList<ExternalGenericValue>();

        // sorted in descending order (most recent last, non-historical value first)
        // TODO: Extract this into a utility method? Also see ItemValueMap Comparator.
        Collections.sort(values, new Comparator<ExternalGenericValue>() {
            public int compare(ExternalGenericValue iv1, ExternalGenericValue iv2) {
                if (isHistoricValue(iv1) && isHistoricValue(iv2)) {
                    // Both values are part of a history, compare their startDates.
                    return ((ExternalHistoryValue) iv1).getStartDate().compareTo(((ExternalHistoryValue) iv2).getStartDate());
                } else if (isHistoricValue(iv1)) {
                    // The first value is historical, but the second is not, so it needs to
                    // come after the second value.
                    return 1;
                } else if (isHistoricValue(iv2)) {
                    // The second value is historical, but the first is not, so it needs to
                    // come after the first value.
                    return -1;
                } else {
                    // Both values are not historical. This should not happen but consider them equal.
                    // The new value will not be added to the TreeSet (see class note about inconsistency with equals).
                    log.warn("filterItemValues() Two non-historical values should not exist.");
                    return 0;

                    // Note: Java 7 may cause this branch to be executed due to changes in TreeMap implementation.
                    // See: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5045147
                    // and: http://www.oracle.com/technetwork/java/javase/jdk7-relnotes-418459.html#jdk7changes
                }
            }
        });

        // endDate can be nil, indicating range-of-interest extends to infinite future time
        // in this case, only the final value in the interval is of interest to anyone
        if (endDate == null) {
            filteredValues.add(values.get(values.size() - 1));
            return filteredValues;
        }

        // The earliest value
        ExternalGenericValue previous = values.get(0);
        StartEndDate latest;
        if (isHistoricValue(previous)) {
            latest = ((ExternalHistoryValue)previous).getStartDate();
        } else {

            // Set the epoch.
            latest = new StartEndDate(new Date(0));
        }

        for (ExternalGenericValue iv : values) {
            StartEndDate currentStart;
            if (isHistoricValue(iv)) {
                currentStart = ((ExternalHistoryValue)iv).getStartDate();
            } else {
                currentStart = new StartEndDate(new Date(0));
            }

            if (currentStart.before(endDate) && !currentStart.before(startDate)) {
                filteredValues.add(iv);
                slog.info("Adding point at " + currentStart);
            } else if (currentStart.before(startDate) && currentStart.after(latest)) {
                latest = currentStart;
                previous = iv;
            }
        }

        // Add the previous point to the start of the list
        if (isHistoricValue(previous)) {
            slog.info("Adding previous point at " + ((ExternalHistoryValue)previous).getStartDate());
        } else {
            slog.info("Adding previous point at " + new StartEndDate(new Date(0)));
        }
        filteredValues.add(0, previous);

        return filteredValues;
    }

    /**
     * Gets the amount in the units the algorithm is expecting.
     *
     * @param iv
     * @return
     */
    private Amount asInternalDecimal(ExternalGenericValue iv) {
        if (ExternalNumberValue.class.isAssignableFrom(iv.getClass())) {
            ExternalNumberValue value = (ExternalNumberValue)iv;

            if (!value.hasUnit() && !value.hasPerUnit()) {
                return new Amount(value.getValueAsDouble());
            } else {
                Amount amount = new Amount(value.getValueAsDouble(), value.getCompoundUnit());
                if (iv.isConvertible()) {
                    AmountCompoundUnit internalUnit = value.getCanonicalCompoundUnit();
                    if (amount.hasDifferentUnits(internalUnit)) {
                        log.debug("asInternalDecimal() label: {}, external: {}, internal: {}",
                                iv.getLabel(), amount + " " + amount.getUnit(), amount.convert(internalUnit) + " " + internalUnit);
                        amount = amount.convert(internalUnit);
                    }
                }
                return amount;
            }

        }
        throw new IllegalStateException("Expected an ExternalNumberValue.");
    }

    /**
     * If the supplied item value has unit or perUnit values, convert it to an amount in the canonical unit.
     *
     * @param iv the Item Value to convert to Amount.
     * @return an Amount in the canonical unit.
     */
    private Amount asInternalDecimal(ExternalNumberValue iv) {

        // If a unit or perUnit is not defined for this Item Value just return the double value.
        if (!iv.hasUnit() && !iv.hasPerUnit()) {
            return new Amount(iv.getValueAsDouble());
        } else {

            // Convert to canonical unit if required.
            Amount amount = new Amount(iv.getValueAsDouble(), iv.getCompoundUnit());
            AmountCompoundUnit internalUnit = iv.getCanonicalCompoundUnit();
            if (amount.hasDifferentUnits(internalUnit)) {
                log.debug("asInternalDecimal() label: {}, external: {}, internal: {}",
                        iv.getLabel(), amount + " " + amount.getUnit(), amount.convert(internalUnit) + " " + internalUnit);
                amount = amount.convert(internalUnit);
            }
            return amount;
        }
    }

    private boolean isHistoricValue(ExternalGenericValue itemValue) {
        return ExternalHistoryValue.class.isAssignableFrom(itemValue.getClass());
    }
}
