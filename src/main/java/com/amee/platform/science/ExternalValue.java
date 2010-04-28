package com.amee.platform.science;

public interface ExternalValue {

    public String getUsableValue();

    public AmountUnit getUnit();

    public AmountPerUnit getPerUnit();

    public AmountUnit getCanonicalUnit();

    public AmountPerUnit getCanonicalPerUnit();

    public AmountCompoundUnit getCompoundUnit();

    public AmountCompoundUnit getCanonicalCompoundUnit();

    public StartEndDate getStartDate();

    public boolean isDecimal();

    public boolean isConvertible();

    public boolean hasUnit();

    public boolean hasPerUnit();

    public String getLabel();
}