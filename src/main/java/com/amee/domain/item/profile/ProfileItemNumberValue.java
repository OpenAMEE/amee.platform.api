package com.amee.domain.item.profile;

import com.amee.domain.ObjectType;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.science.AmountCompoundUnit;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import com.amee.platform.science.ExternalNumberValue;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PROFILE_ITEM_NUMBER_VALUE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProfileItemNumberValue extends BaseProfileItemValue implements ExternalNumberValue {

    public final static int UNIT_SIZE = 255;
    public final static int PER_UNIT_SIZE = 255;

    @Column(name = "UNIT", nullable = true, length = UNIT_SIZE)
    private String unit;

    @Column(name = "PER_UNIT", nullable = true, length = PER_UNIT_SIZE)
    private String perUnit;

    @Column(name = "VALUE", nullable = false)
    private Double value = 0.0;

    public ProfileItemNumberValue() {
        super();
    }

    public ProfileItemNumberValue(ItemValueDefinition itemValueDefinition, NuProfileItem profileItem, Double value) {
        super(itemValueDefinition, profileItem);
        setValue(value);
    }

    protected void copyTo(ProfileItemNumberValue o) {
        super.copyTo(o);
        o.unit = unit;
        o.perUnit = perUnit;
        o.value = value;
    }

    @Override
    public boolean isDouble() {
        return true;
    }

    @Override
    public boolean isConvertible() {
        return true;
    }

    @Override
    public boolean isUsableValue() {
        return true;
    }

    @Override
    public AmountUnit getCanonicalUnit() {
        return getItemValueDefinition().getUnit();
    }

    @Override
    public boolean hasUnit() {
        return getItemValueDefinition().hasUnit();
    }

    @Override
    public boolean hasPerUnit() {
        return getItemValueDefinition().hasPerUnit();
    }

    @Override
    public AmountPerUnit getCanonicalPerUnit() {
        return getItemValueDefinition().getPerUnit();
    }

    @Override
    public AmountCompoundUnit getCompoundUnit() {
        return getUnit().with(getPerUnit());
    }

    @Override
    public AmountCompoundUnit getCanonicalCompoundUnit() {
        return getItemValueDefinition().getCanonicalCompoundUnit();
    }

    @Override
    public AmountUnit getUnit() {
        return StringUtils.isNotBlank(unit) ? AmountUnit.valueOf(unit) : getItemValueDefinition().getUnit();
    }

    public void setUnit(String unit) throws IllegalArgumentException {
        if (!getItemValueDefinition().isValidUnit(unit)) {
            throw new IllegalArgumentException("The unit argument is not valid: " + unit);
        }
        this.unit = unit;
    }

    @Override
    public AmountPerUnit getPerUnit() {
        if (perUnit != null) {
            if (perUnit.equals("none")) {
                // TODO: PL-3351
                // return AmountPerUnit.valueOf(getDataItem().getDuration());
                return null;
            } else {
                return AmountPerUnit.valueOf(perUnit);
            }
        } else {
            return getItemValueDefinition().getPerUnit();
        }
    }

    public void setPerUnit(String perUnit) throws IllegalArgumentException {
        if (!getItemValueDefinition().isValidPerUnit(perUnit)) {
            throw new IllegalArgumentException("The perUnit argument is not valid: " + perUnit);
        }
        this.perUnit = perUnit;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String getValueAsString() {
        return Double.toString(value);
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setValue(String value) {
        if (value != null) {
            // TODO: PL-3351
            // Ensure numbers are a valid format (double).
            if (getItemValueDefinition().isDouble() && !value.isEmpty()) {
                try {
                    this.value = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    log.warn("setValue() - Invalid number format: " + value);
                    throw new IllegalArgumentException("Invalid number format: " + value);
                }
            }
        }
    }

    @Override
    public ObjectType getObjectType() {
        return ObjectType.PINV;
    }
}