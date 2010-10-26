package com.amee.domain.item.profile;

import com.amee.domain.LocaleHolder;
import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.science.ExternalTextValue;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PROFILE_ITEM_TEXT_VALUE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Configurable(autowire = Autowire.BY_TYPE)
public class ProfileItemTextValue extends BaseProfileItemValue implements ExternalTextValue {

    // 32767 because this is bigger than 255, smaller than 65535 and fits into an exact number of bits.
    public final static int VALUE_SIZE = 32767;

    @Column(name = "VALUE", nullable = false, length = VALUE_SIZE)
    private String value = "";

    public ProfileItemTextValue() {
        super();
    }

    public ProfileItemTextValue(ItemValueDefinition itemValueDefinition, NuProfileItem profileItem) {
        super(itemValueDefinition, profileItem);
        setValue(value);
    }

    public ProfileItemTextValue(ItemValueDefinition itemValueDefinition, NuProfileItem profileItem, String value) {
        this(itemValueDefinition, profileItem);
        setValue(value);
    }

    protected void copyTo(ProfileItemTextValue o) {
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

    @Override
    public ObjectType getObjectType() {
        return ObjectType.PITV;
    }
}
