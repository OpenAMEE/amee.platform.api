package com.amee.domain.item;

import com.amee.platform.science.ExternalNumberValue;

public interface NumberValue extends ExternalNumberValue {

    public void setUnit(String unit);

    public void setPerUnit(String perUnit);
}
