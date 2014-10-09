package com.amee.domain.item.profile;

import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.platform.science.ExternalTextValue;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PROFILE_ITEM_TEXT_VALUE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProfileItemTextValue extends BaseProfileItemValue implements ExternalTextValue {

    public final static int VALUE_SIZE = 255;

    @Column(name = "VALUE", nullable = false, length = VALUE_SIZE)
    private String value = "";

    public ProfileItemTextValue() {
        super();
    }

    public ProfileItemTextValue(ItemValueDefinition itemValueDefinition, ProfileItem profileItem) {
        super(itemValueDefinition, profileItem);
    }

    public ProfileItemTextValue(ItemValueDefinition itemValueDefinition, ProfileItem profileItem, String value) {
        this(itemValueDefinition, profileItem);
        setValue(value);
    }

    protected void copyTo(BaseItemValue o) {
        super.copyTo(o);
        ((ProfileItemTextValue) o).value = value;
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

    @Override
    public ObjectType getObjectType() {
        return ObjectType.PITV;
    }

}
