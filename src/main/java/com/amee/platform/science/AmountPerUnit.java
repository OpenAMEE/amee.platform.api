package com.amee.platform.science;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;

import javax.measure.unit.Dimension;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public class AmountPerUnit extends AmountUnit {

    public static final AmountPerUnit ONE = new AmountPerUnit(Unit.ONE);
    public static final AmountPerUnit MONTH = AmountPerUnit.valueOf("month");

    private String string;

    public AmountPerUnit(Unit unit) {
        super(unit);
        this.string = unit.toString();
    }

    private AmountPerUnit(Duration duration) {
        super(SI.MILLI(SI.SECOND).times(duration.getMillis()));
        this.string = ISOPeriodFormat.standard().print(duration.toPeriod());
    }

    public static AmountPerUnit valueOf(String unit) {
        return new AmountPerUnit(internalValueOf(unit));
    }

    public static AmountPerUnit valueOf(Duration duration) {
        return new AmountPerUnit(duration);
    }

    @Override
    public boolean isCompatibleWith(String unit) {
        return StringUtils.isNotBlank(unit) && ("none".equals(unit) || this.unit.isCompatible(internalValueOf(unit)));
    }

    public boolean isTime() {
        return toUnit().getDimension().equals(Dimension.TIME);
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        
        // We ignore the string value.
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
