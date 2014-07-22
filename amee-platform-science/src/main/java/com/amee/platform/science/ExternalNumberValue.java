package com.amee.platform.science;

public interface ExternalNumberValue {

    Double getValueAsDouble();

    AmountUnit getUnit();

    AmountPerUnit getPerUnit();

    AmountUnit getCanonicalUnit();

    AmountPerUnit getCanonicalPerUnit();

    AmountCompoundUnit getCompoundUnit();

    AmountCompoundUnit getCanonicalCompoundUnit();

    boolean hasUnit();

    boolean hasPerUnit();

    String getLabel();
}