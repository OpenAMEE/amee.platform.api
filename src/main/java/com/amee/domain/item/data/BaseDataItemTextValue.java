package com.amee.domain.item.data;

import com.amee.domain.LocaleHolder;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.science.ExternalTextValue;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Configurable(autowire = Autowire.BY_TYPE)
public abstract class BaseDataItemTextValue extends BaseDataItemValue implements ExternalTextValue {

    public final static int VALUE_SIZE = 255;

    @Column(name = "VALUE", nullable = false, length = VALUE_SIZE)
    private String value = "";

    public BaseDataItemTextValue() {
        super();
    }

    public BaseDataItemTextValue(ItemValueDefinition itemValueDefinition, NuDataItem dataItem) {
        super(itemValueDefinition, dataItem);
    }

    public BaseDataItemTextValue(ItemValueDefinition itemValueDefinition, NuDataItem dataItem, String value) {
        super(itemValueDefinition, dataItem);
        setValue(value);
    }

    protected void copyTo(BaseDataItemTextValue o) {
        super.copyTo(o);
        o.value = value;
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
        if (getItemValueDefinition().isText() && !LocaleHolder.isDefaultLocale()) {
            return localeService.getLocaleNameValue(this, value);
        } else {
            return value;
        }
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
