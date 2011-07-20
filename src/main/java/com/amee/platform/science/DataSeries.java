package com.amee.platform.science;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * A class representing a series of {@link DataPoint} values. Provides various mathematical operations
 * such as plus, subtract and multiply along with the crucial integrate method.
 */
public class DataSeries {

    private final Log log = LogFactory.getLog("science");
    private SortedSet<DataPoint> dataPoints = new TreeSet<DataPoint>();

    /// These dates will be used to define a query window on the series.
    private DateTime seriesStartDate;
    private DateTime seriesEndDate;

    /**
     * Construct an empty series.
     */
    public DataSeries() {
        this(new TreeSet<DataPoint>());
    }

    /**
     * Construct a series from the set of {@link DataPoint} values.
     *
     * @param dataPoints - the list of {@link DataPoint} values
     */
    public DataSeries(Set<DataPoint> dataPoints) {
        this.dataPoints = new TreeSet<DataPoint>(dataPoints);
    }

    /**
     * A copy constructor.
     *
     * @param dataSeries to copy
     */
    protected DataSeries(DataSeries dataSeries) {
        for (DataPoint dataPoint : dataSeries.dataPoints) {
            this.addDataPoint(new DataPoint(dataPoint));
        }
    }

    /**
     * Return a copy of this object.
     *
     * @return a copy
     */
    public DataSeries copy() {
        return new DataSeries(this);
    }

    public String toString() {
        try {
            return getJSONObject().toString();
        } catch (JSONException e) {
            throw new RuntimeException("Caught JSONException: " + e.getMessage(), e);
        }
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for (DataPoint dataPoint : dataPoints) {
            arr.put(dataPoint.getJSONArray());
        }
        obj.put("dataPoints", arr);
        if (seriesStartDate != null) {
            obj.put("seriesStartDate", seriesStartDate.toString());
        }
        if (seriesEndDate != null) {
            obj.put("seriesEndDate", seriesEndDate.toString());
        }
        return obj;
    }

    protected Long getSeriesTimeInMillis() {
        if (dataPoints.isEmpty()) {
            return 0l;
        }
        DateTime seriesStart = getSeriesStartDate();
        DateTime seriesEnd = getSeriesEndDate();
        if ((seriesEnd == null || seriesStart == null)) {
            // the range of interest is undef
            return null;
        }
        return seriesEnd.getMillis() - seriesStart.getMillis();
    }

    DateTime getSeriesStartDate() {
        if (!dataPoints.isEmpty()) {
            DateTime first = dataPoints.first().getDateTime();
            return (seriesStartDate != null) && seriesStartDate.isAfter(first) ? seriesStartDate : first;
        } else {
            return null;
        }
    }

    DateTime getSeriesEndDate() {
        if (!dataPoints.isEmpty()) {
            return (seriesEndDate != null) ? seriesEndDate : dataPoints.last().getDateTime();
        } else {
            return null;
        }
    }

    /**
     * Combine this DataSeries with another DataSeries using the given Operation.
     *
     * @param series
     * @param operation
     * @return
     */
    @SuppressWarnings("unchecked")
    private DataSeries combine(DataSeries series, Operation operation) {

        // Create a union of all DateTime points in the two DataSeries and sort the resultant collection (DESC).
        List<DateTime> dateTimePoints = (List) CollectionUtils.union(getDateTimePoints(), series.getDateTimePoints());
        Collections.sort(dateTimePoints);

        // For each DateTime point, find the nearest corresponding DataPoint in each series and apply the desired
        // Operation.
        SortedSet<DataPoint> combinedSeries = new TreeSet<DataPoint>();
        for (DateTime dateTimePoint : dateTimePoints) {
            DataPoint lhs = getDataPoint(dateTimePoint);
            DataPoint rhs = series.getDataPoint(dateTimePoint);
            operation.setOperands(lhs, rhs);
            combinedSeries.add(new DataPoint(dateTimePoint, operation.operate().getValue()));
        }
        DataSeries result = new DataSeries(combinedSeries);

        // the new series's start/end dates in terms of the 'window of interest' are then the largest overlap of the two
        // although usually they'll be the same as they'll be set by the query range
        result.setSeriesStartDate(getSeriesStartDate().isBefore(series.getSeriesStartDate()) ?
                getSeriesStartDate() : series.getSeriesStartDate()
        );
        result.setSeriesEndDate(getSeriesEndDate().isAfter(series.getSeriesEndDate()) ?
                getSeriesEndDate() : series.getSeriesEndDate()
        );
        return result;
    }

    /**
     * Add a DataSeries to this DataSeries.
     *
     * @param series - the DataSeries to add
     * @return a new DataSeries representing the addition of the two DataSeries
     */
    public DataSeries plus(DataSeries series) {
        return combine(series, new PlusOperation());
    }

    /**
     * Add a DataPoint to this DataSeries.
     *
     * @param dataPoint - the DataPoint to add
     * @return a new DataSeries representing the addition of the DataSeries and the DataPoint
     */
    public DataSeries plus(DataPoint dataPoint) {
        DataSeries series = new DataSeries();
        series.addDataPoint(dataPoint);
        // make the window of interest be the same as the current one
        series.setSeriesStartDate(getSeriesStartDate());
        series.setSeriesEndDate(getSeriesEndDate());
        return plus(series);
    }

    /**
     * Add a double value to this DataSeries.
     *
     * @param d - the double value to add
     * @return a new DataSeries representing the addition of the double value and the DataSeries
     */
    public DataSeries plus(double d) {
        return plus(d, false);
    }

    /**
     * Add a double value to this DataSeries.
     *
     *
     * @param d the double value to add
     * @param invert dummy parameter to keep the apis consistent. Addition is commutative so invert doesn't do anything.
     * @return a new DataSeries representing the addition of the double value and the DataSeries
     */
    public DataSeries plus(double d, boolean invert) {
        SortedSet<DataPoint> combinedDataPoints = new TreeSet<DataPoint>();
        for (DataPoint dp : dataPoints) {
            combinedDataPoints.add(dp.plus(d));
        }
        DataSeries result = new DataSeries(combinedDataPoints);
        // make the window of interest be the same as the current one
        result.setSeriesStartDate(getSeriesStartDate());
        result.setSeriesEndDate(getSeriesEndDate());
        return result;
    }

    /**
     * Subtract a DataSeries from this DataSeries.
     *
     * @param series - the DataSeries to subtract
     * @return a new DataSeries representing the subtraction of the DataSeries from this DataSeries
     */
    public DataSeries subtract(DataSeries series) {
        return combine(series, new SubtractOperation());
    }

    /**
     * Subtract a DataPoint from this DataSeries.
     *
     * @param dataPoint - the DataPoint to subtract
     * @return a new DataSeries representing the subtraction of the DataPoint from this DataSeries
     */
    public DataSeries subtract(DataPoint dataPoint) {
        DataSeries series = new DataSeries();
        series.addDataPoint(dataPoint);
        // make the window of interest be the same as the current one
        series.setSeriesStartDate(getSeriesStartDate());
        series.setSeriesEndDate(getSeriesEndDate());
        return subtract(series);
    }

    /**
     * Subtract a double value from this DataSeries.
     *
     * @param d - the double value to subtract
     * @return a new DataSeries representing the subtraction of the double value from this DataSeries
     */
    public DataSeries subtract(double d) {
        return subtract(d, false);
    }

    /**
     * Subtract a double value from this DataSeries.
     *
     * @param d the double value to subtract
     * @param invert invert the operands.
     * @return a new DataSeries representing the subtraction of the double value from this DataSeries
     */
    public DataSeries subtract(double d, boolean invert) {
        SortedSet<DataPoint> combinedDataPoints = new TreeSet<DataPoint>();
        for (DataPoint dp : dataPoints) {
            combinedDataPoints.add(dp.subtract(d, invert));
        }
        DataSeries result = new DataSeries(combinedDataPoints);
        // make the window of interest be the same as the current one
        result.setSeriesStartDate(getSeriesStartDate());
        result.setSeriesEndDate(getSeriesEndDate());
        return result;
    }

    /**
     * Divide this DataSeries by another DataSeries.
     *
     * @param series - the DataSeries value by which to divide this DataSeries
     * @return a new DataSeries representing the division of this DataSeries by the DataSeries
     */
    public DataSeries divide(DataSeries series) {
        return combine(series, new DivideOperation());
    }

    /**
     * Divide this DataSeries by a DataPoint.
     *
     * @param dataPoint - the DataPoint value by which to divide this DataSeries
     * @return a new DataSeries representing the division of this DataSeries by the DataPoint
     */
    public DataSeries divide(DataPoint dataPoint) {
        DataSeries series = new DataSeries();
        series.addDataPoint(dataPoint);
        // make the window of interest be the same as the current one
        series.setSeriesStartDate(getSeriesStartDate());
        series.setSeriesEndDate(getSeriesEndDate());
        return divide(series);
    }

    /**
     * Divide this DataSeries by a double value.
     *
     * @param d - the double value by which to divide this DataSeries
     * @return a new DataSeries representing the division of this DataSeries by the double value
     */
    public DataSeries divide(double d) {
        return divide(d, false);
    }

    /**
     * Divide this DataSeries by a double value.
     *
     *
     * @param d the double value by which to divide this DataSeries
     * @param invert invert the operands
     * @return a new DataSeries representing the division of this DataSeries by the double value
     */
    public DataSeries divide(double d, boolean invert) {
        SortedSet<DataPoint> combinedDataPoints = new TreeSet<DataPoint>();
        for (DataPoint dp : dataPoints) {
            combinedDataPoints.add(dp.divide(d, invert));
        }
        DataSeries result = new DataSeries(combinedDataPoints);
        // make the window of interest be the same as the current one
        result.setSeriesStartDate(getSeriesStartDate());
        result.setSeriesEndDate(getSeriesEndDate());
        return result;
    }

    /**
     * Multiply this DataSeries by another DataSeries.
     *
     * @param series - the DataSeries to multiply this DataSeries
     * @return a new DataSeries representing the multiplication of the two DataSeries
     */
    public DataSeries multiply(DataSeries series) {
        return combine(series, new MultiplyOperation());
    }

    /**
     * Multiply this DataSeries by a DataPoint.
     *
     * @param dataPoint - the DataPoint value to multiply this DataPoint
     * @return a new DataSeries representing the multiplication of the DataSeries and the DataPoint
     */
    public DataSeries multiply(DataPoint dataPoint) {
        DataSeries series = new DataSeries();
        series.addDataPoint(dataPoint);
        // make the window of interest be the same as the current one
        series.setSeriesStartDate(getSeriesStartDate());
        series.setSeriesEndDate(getSeriesEndDate());
        return multiply(series);
    }

    /**
     * Multiply this DataSeries by a double value.
     *
     * @param d - the double value to multiply this DataSeries
     * @return a new DataSeries representing the multiplication of the DataSeries and the double value
     */
    public DataSeries multiply(double d) {
        return multiply(d, false);
    }

    /**
     * Multiply this DataSeries by a double value.
     *
     * @param d - the double value to multiply this DataSeries
     * @param invert dummy parameter to keep the apis consistent. Multiplication is commutative so invert doesn't do anything.
     * @return a new DataSeries representing the multiplication of the DataSeries and the double value
     */
    public DataSeries multiply(double d, boolean invert) {
        SortedSet<DataPoint> combinedDataPoints = new TreeSet<DataPoint>();
        for (DataPoint dp : dataPoints) {
            combinedDataPoints.add(dp.multiply(d));
        }
        DataSeries result = new DataSeries(combinedDataPoints);
        // make the window of interest be the same as the current one
        result.setSeriesStartDate(getSeriesStartDate());
        result.setSeriesEndDate(getSeriesEndDate());
        return result;
    }

    /**
     * Get the single-valued average of the DataPoints within the DataSeries that occur during the
     * specified query time-period.
     * <p/>
     * If there is no time-period (the query time-period is zero) then the result will be zero.
     *
     * @return - the average as a {@link Amount} value
     */
    public Amount integrate() {

        double integral = 0.0;
        Long seriesTimeInMillis = getSeriesTimeInMillis();

        if (log.isDebugEnabled()) {
            log.debug("integrate() Integrating, time range: " + getSeriesStartDate() + "->" + getSeriesEndDate() + ", series length: " + dataPoints.size());
        }

        if (seriesTimeInMillis == null) {
            integral = dataPoints.last().getValue().getValue();
        } else if (seriesTimeInMillis > 0) {
            DataPoint[] pointArray = dataPoints.toArray(new DataPoint[dataPoints.size()]);
            for (int i = 0; i < pointArray.length; i++) {
                // Work out segment time series.
                DataPoint current = pointArray[i];
                DateTime end;
                if (i == (pointArray.length - 1)) {
                    end = getSeriesEndDate();
                } else {
                    DataPoint next = pointArray[i + 1];
                    end = getSeriesEndDate().isBefore(next.getDateTime()) ? getSeriesEndDate() : next.getDateTime();
                }
                DateTime start = getSeriesStartDate().isAfter(current.getDateTime()) ?
                        getSeriesStartDate() : current.getDateTime();
                double segmentInMillis = end.getMillis() - start.getMillis();
                // the filtering should have removed points after the end of the window of interest
                // but in case it hasn't (and for direct testing not via internal value)

                // Add weighted average value.
                double weightedAverage = current.getValue().getValue() * segmentInMillis / seriesTimeInMillis.doubleValue();
                if (log.isDebugEnabled()) {
                    log.debug("integrate() " +
                            "Diagnostics from integrate() weightedAverage: " + weightedAverage + ", current value: " + current.getValue() + ", " + i + ", datapoints size: " + pointArray.length +
                            ", segment millis / series millis: " + segmentInMillis / (seriesTimeInMillis.doubleValue()));
                }
                if (start.isAfter(end)) continue;
                integral = integral + weightedAverage;
            }
        }
        return new Amount(integral);
    }

    /**
     * Get the Collection of {@link org.joda.time.DateTime} points in the DataSeries.
     *
     * @return the Collection of {@link org.joda.time.DateTime} points in the DataSeries
     */
    @SuppressWarnings("unchecked")
    public Collection<DateTime> getDateTimePoints() {
        return (Collection<DateTime>) CollectionUtils.collect(dataPoints, new Transformer() {
            public Object transform(Object input) {
                DataPoint dataPoint = (DataPoint) input;
                return dataPoint.getDateTime();
            }
        });
    }

    /**
     * Get the active {@link DataPoint} at a specific point in time.
     *
     * @param dateTime - the point in time for which to return the {@link DataPoint}
     * @return the {@link DataPoint} at dateTime
     */
    public DataPoint getDataPoint(DateTime dateTime) {
        DataPoint selected = DataPoint.NULL;

        // datapoints must be sorted in ascending order (earliest first)
        for (DataPoint dataPoint : dataPoints) {
            if (!dataPoint.getDateTime().isAfter(dateTime)) {
                selected = dataPoint;
            } else {
                break;
            }
        }
        return selected;
    }

    /**
     * Add a {@link DataPoint} to this series.
     *
     * @param dataPoint - the {@link DataPoint} to add to this series.
     */
    public void addDataPoint(DataPoint dataPoint) {
        dataPoints.add(dataPoint);
    }

    /**
     * Set the start of the query window.
     *
     * @param seriesStartDate - the start of the query window
     */
    public void setSeriesStartDate(DateTime seriesStartDate) {
        if (seriesStartDate == null)
            return;
        this.seriesStartDate = seriesStartDate;
    }

    /**
     * Set the end of the query window.
     *
     * @param seriesEndDate - the end of the query window
     */
    public void setSeriesEndDate(DateTime seriesEndDate) {
        if (seriesEndDate == null)
            return;
        this.seriesEndDate = seriesEndDate;
    }
}

/**
 * Represents an abstract mathematical operation
 * one would want to perform on a pair of {@link DataPoint} values.
 */
abstract class Operation {

    protected DataPoint lhs;
    protected DataPoint rhs;

    void setOperands(DataPoint lhs, DataPoint rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    abstract DataPoint operate();
}

class PlusOperation extends Operation {
    public DataPoint operate() {
        return lhs.plus(rhs);
    }
}

class SubtractOperation extends Operation {
    public DataPoint operate() {
        return lhs.subtract(rhs);
    }
}

class DivideOperation extends Operation {
    public DataPoint operate() {
        return lhs.divide(rhs);
    }
}

class MultiplyOperation extends Operation {
    public DataPoint operate() {
        return lhs.multiply(rhs);
    }
}
