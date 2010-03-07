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

    public String getUsableValue() {
        return value;
    }

    public DecimalUnit getUnit() {
        return (unit != null) ? DecimalUnit.valueOf(unit) : getCanonicalUnit();
    }

    public DecimalPerUnit getPerUnit() {
        return (perUnit != null) ? DecimalPerUnit.valueOf(perUnit) : getCanonicalPerUnit();
    }

    public DecimalUnit getCanonicalUnit() {
        return (canonicalUnit != null) ? DecimalUnit.valueOf(canonicalUnit) : DecimalUnit.ONE;
    }

    public DecimalPerUnit getCanonicalPerUnit() {
        return (canonicalPerUnit != null) ? DecimalPerUnit.valueOf(canonicalPerUnit) : DecimalPerUnit.ONE;
    }

    public DecimalCompoundUnit getCompoundUnit() {
        return getUnit().with(getPerUnit());
    }

    public DecimalCompoundUnit getCanonicalCompoundUnit() {
        return getUnit().with(getPerUnit());
    }

    public StartEndDate getStartDate() {
        return new StartEndDate(startDate);
    }

    public boolean isDecimal() {
        return true;
    }

    public boolean isConvertible() {
        return true;
    }

    public boolean hasUnit() {
        return !StringUtils.isBlank(unit);
    }

    public boolean hasPerUnit() {
        return !StringUtils.isBlank(perUnit);
    }

    public String getLabel() {
        return "MockExternalValue: " + value;
    }
}
