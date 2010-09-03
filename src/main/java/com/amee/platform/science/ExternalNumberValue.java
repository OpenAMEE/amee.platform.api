package com.amee.platform.science;

public interface ExternalNumberValue {

    public AmountUnit getUnit();

    public AmountPerUnit getPerUnit();

    public AmountUnit getCanonicalUnit();

    public AmountPerUnit getCanonicalPerUnit();

    public AmountCompoundUnit getCompoundUnit();

    public AmountCompoundUnit getCanonicalCompoundUnit();

    public boolean hasUnit();

    public boolean hasPerUnit();

    public String getLabel();
}