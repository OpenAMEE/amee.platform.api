package com.amee.platform.science;

public interface ExternalValue {

    public String getUsableValue();

    public DecimalUnit getUnit();

    public DecimalPerUnit getPerUnit();

    public DecimalUnit getCanonicalUnit();

    public DecimalPerUnit getCanonicalPerUnit();

    public DecimalCompoundUnit getCompoundUnit();

    public DecimalCompoundUnit getCanonicalCompoundUnit();

    public StartEndDate getStartDate();

    public boolean isDecimal();

    public boolean isConvertible();

    public boolean hasUnit();

    public boolean hasPerUnit();

    public String getLabel();
}