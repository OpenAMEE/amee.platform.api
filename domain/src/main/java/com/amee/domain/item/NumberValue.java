package com.amee.domain.item;

import com.amee.platform.science.ExternalNumberValue;

public interface NumberValue extends ExternalNumberValue {

    void setUnit(String unit);

    void setPerUnit(String perUnit);
}
