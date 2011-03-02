package com.amee.platform.science;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

public class MockExternalValue implements ExternalValue {

    private String value;
    private Date startDate;
    private String unit;
    private String perUnit;
    private String canonicalUnit;
    private String canonicalPerUnit;

    public MockExternalValue() {
        super();
    }

    public MockExternalValue(String value) {
        this();
        this.value = value;
    }

    public MockExternalValue(String value, Date startDate) {
        this(value);
        this.startDate = startDate;
    }

    @Override
    public String getUsableValue() {
        return value;
    }

    @Override
    public double getValueAsDouble() {
        return Double.valueOf(value);
    }

    @Override
    public AmountUnit getUnit() {
        return StringUtils.isNotBlank(unit) ? AmountUnit.valueOf(unit) : getCanonicalUnit();
    }

    @Override
    public AmountPerUnit getPerUnit() {
        return StringUtils.isNotBlank(perUnit) ? AmountPerUnit.valueOf(perUnit) : getCanonicalPerUnit();
    }

    @Override
    public AmountUnit getCanonicalUnit() {
        return StringUtils.isNotBlank(canonicalUnit) ? AmountUnit.valueOf(canonicalUnit) : AmountUnit.ONE;
    }

    @Override
    public AmountPerUnit getCanonicalPerUnit() {
        return StringUtils.isNotBlank(canonicalPerUnit) ? AmountPerUnit.valueOf(canonicalPerUnit) : AmountPerUnit.ONE;
    }

    @Override
    public AmountCompoundUnit getCompoundUnit() {
        return getUnit().with(getPerUnit());
    }

    @Override
    public AmountCompoundUnit getCanonicalCompoundUnit() {
        return getUnit().with(getPerUnit());
    }

    @Override
    public StartEndDate getStartDate() {
        return new StartEndDate(startDate);
    }

    @Override
    public boolean isDouble() {
        return true;
    }

    @Override
    public boolean isConvertible() {
        return true;
    }

    @Override
    public boolean hasUnit() {
        return !StringUtils.isBlank(unit);
    }

    @Override
    public boolean hasPerUnit() {
        return !StringUtils.isBlank(perUnit);
    }

    @Override
    public String getLabel() {
        return "MockExternalValue: " + value;
    }
}
