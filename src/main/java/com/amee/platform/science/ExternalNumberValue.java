package com.amee.platform.science;

public interface ExternalNumberValue {

    public double getValueAsDouble();

    public String getUnit();

    public AmountUnit getUnitAsAmountUnit();

    public String getPerUnit();

    public AmountPerUnit getPerUnitAsAmountPerUnit();

    public AmountUnit getCanonicalUnit();

    public AmountPerUnit getCanonicalPerUnit();

    public AmountCompoundUnit getCompoundUnit();

    public AmountCompoundUnit getCanonicalCompoundUnit();

    public boolean hasUnit();

    public boolean hasPerUnit();

    public String getLabel();
}