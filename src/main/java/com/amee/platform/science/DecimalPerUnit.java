package com.amee.platform.science;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;

import javax.measure.unit.Dimension;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public class DecimalPerUnit extends DecimalUnit {

    public static final DecimalPerUnit ONE = new DecimalPerUnit(Unit.ONE);
    public static final DecimalPerUnit MONTH = DecimalPerUnit.valueOf("month");

    private String string;

    public DecimalPerUnit(Unit unit) {
        super(unit);
        this.string = unit.toString();
    }

    private DecimalPerUnit(Duration duration) {
        super(SI.MILLI(SI.SECOND).times(duration.getMillis()));
        this.string = ISOPeriodFormat.standard().print(duration.toPeriod());
    }

    public static DecimalPerUnit valueOf(String unit) {
        return new DecimalPerUnit(internalValueOf(unit));
    }

    public static DecimalPerUnit valueOf(Duration duration) {
        return new DecimalPerUnit(duration);
    }

    public boolean isCompatibleWith(String unit) {
        return StringUtils.isNotBlank(unit) && ("none".equals(unit) || this.unit.isCompatible(internalValueOf(unit)));
    }

    public boolean isTime() {
        return toUnit().getDimension().equals(Dimension.TIME);
    }

    public Unit toUnit() {
        return unit;
    }

    public String toString() {
        return string;
    }
}
