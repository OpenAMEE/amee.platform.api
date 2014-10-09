package com.amee.domain.item.data;

import com.amee.domain.LocaleHolder;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.platform.science.ExternalTextValue;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseDataItemTextValue extends BaseDataItemValue implements ExternalTextValue {

    public final static int VALUE_SIZE = 255;

    @Column(name = "VALUE", nullable = false, length = VALUE_SIZE)
    private String value = "";

    public BaseDataItemTextValue() {
        super();
    }

    public BaseDataItemTextValue(ItemValueDefinition itemValueDefinition, DataItem dataItem) {
        super(itemValueDefinition, dataItem);
    }

    public BaseDataItemTextValue(ItemValueDefinition itemValueDefinition, DataItem dataItem, String value) {
        super(itemValueDefinition, dataItem);
        setValue(value);
    }

    protected void copyTo(BaseItemValue o) {
        super.copyTo(o);
        BaseDataItemTextValue v = (BaseDataItemTextValue) o;
        v.value = value;
    }

    public void checkItemValueDefinition() {
        if ((getItemValueDefinition().isDouble() || getItemValueDefinition().isInteger())) {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isDouble() {
        return false;
    }

    @Override
    public boolean isConvertible() {
        return false;
    }

    @Override
    public boolean isUsableValue() {
        return !StringUtils.isBlank(getValue());
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }

    @Override
    public String getUsableValue() {
        return getValueAsString();
    }

    public void setValue(String value) {
        // Make sure value is not null and is not too long.
        if (value != null) {
            if (value.length() > VALUE_SIZE) {
                value = value.substring(0, VALUE_SIZE - 1);
            }
            this.value = value;
        } else {
            this.value = "";
        }
    }
}
