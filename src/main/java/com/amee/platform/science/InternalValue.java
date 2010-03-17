package com.amee.platform.science;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.joda.time.DateTime;

import java.util.*;

/**
 * Provides a wrapper around external representations of values.
 * <p/>
 * Decimal values will be converted to AMEE internal units if necessary. All other value types will be wrapped unchanged.
 */
@SuppressWarnings("unchecked")
public class InternalValue {

    private final Log log = LogFactory.getLog(getClass());
    private final Log slog =   LogFactory.getLog("science");
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
     * @param value - the {@link ExternalValue} representation of the value.
     */
    public InternalValue(ExternalValue value) {
        if (value.isDecimal()) {
            this.value = asInternalDecimal(value).getValue();
        } else {
            this.value = value.getUsableValue();
        }
    }

    /**
     * Instantiate an InternalValue representation of the supplied collection of values.
     *
     * @param values    - the List of {@link ExternalValue}s representing a sequence of values
     * @param startDate - the start Date to filter the series
     * @param endDate   - the end Date to filter the series
     */
    public InternalValue(List<ExternalValue> values, Date startDate, Date endDate) {
        slog.info ("Diagnostics from filtering:"+ values.size() + "," + new DateTime(startDate) +","+new DateTime(endDate))  ;
  
        if (values.get(0).isDecimal()) {
            DataSeries ds = new DataSeries();
            for (ExternalValue itemValue : filterItemValues(values, startDate, endDate)) {
                ds.addDataPoint(new DataPoint(itemValue.getStartDate().toDateTime(), asInternalDecimal(itemValue)));
            }
            ds.setSeriesStartDate(new DateTime(startDate));
            ds.setSeriesEndDate(new DateTime(endDate));
            this.value = ds;
            slog.info ("Series dates"+ds.getSeriesStartDate()+"->"+ds.getSeriesEndDate());
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
    private List<ExternalValue> filterItemValues(List<ExternalValue> values, Date startDate, Date endDate) {

        List<ExternalValue> filteredValues = new ArrayList<ExternalValue>();

        Collections.sort(values, new Comparator<ExternalValue>() {
            public int compare(ExternalValue a, ExternalValue b) {
                if (a.getStartDate() == b.getStartDate()) return 0 ;
                if (a.getStartDate().before(b.getStartDate())) return -1 ;
                return 1;
            }
        });

        // endDate can be nil, indicating range-of-interest extends to infinite future time
        // in this case, only the final value in the interval is of interest to anyone
        if (endDate==null) {
            filteredValues.add(values.get(values.size()-1));
            return filteredValues;
        }

        ExternalValue previous = values.get(0);
        StartEndDate latest = previous.getStartDate();

        for (ExternalValue iv : values) {
            StartEndDate currentStart = iv.getStartDate();
            if (currentStart.before(endDate) && !currentStart.before(startDate)) {
                filteredValues.add(iv);
                slog.info("Adding point at"+iv.getStartDate());
            } else if (currentStart.before(startDate) && currentStart.after(latest)) {
                latest = currentStart;
                previous = iv;
            }
        }

        slog.info("Adding previous point at"+previous.getStartDate());
        filteredValues.add(0,previous);

        return filteredValues;
    }

    private Decimal asInternalDecimal(ExternalValue iv) {
        if (!iv.hasUnit() && !iv.hasPerUnit()) {
            return new Decimal(iv.getUsableValue());
        } else {
            Decimal decimal = new Decimal(iv.getUsableValue(), iv.getCompoundUnit());
            if (iv.isConvertible()) {
                DecimalCompoundUnit internalUnit = iv.getCanonicalCompoundUnit();
                if (decimal.hasDifferentUnits(internalUnit)) {
                    if (log.isDebugEnabled()) {
                        log.debug("asInternalDecimal() " +
                                "label: " + iv.getLabel() + "," +
                                "external: " + decimal + " " + decimal.getUnit() + "," +
                                "internal: " + decimal.convert(internalUnit) + " " + internalUnit);
                    }
                    decimal = decimal.convert(internalUnit);
                }
            }
            return decimal;
        }
    }
}