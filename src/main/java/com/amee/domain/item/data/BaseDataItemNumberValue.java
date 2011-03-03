package com.amee.domain.item.data;

import com.amee.domain.data.ItemValueDefinition;
import com.amee.domain.item.BaseItemValue;
import com.amee.domain.item.NumberValue;
import com.amee.platform.science.AmountCompoundUnit;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@MappedSuperclass
public abstract class BaseDataItemNumberValue extends BaseDataItemValue implements NumberValue {

    public final static int UNIT_SIZE = 255;
    public final static int PER_UNIT_SIZE = 255;

    @Column(name = "UNIT", nullable = true, length = UNIT_SIZE)
    private String unit = "";

    @Column(name = "PER_UNIT", nullable = true, length = PER_UNIT_SIZE)
    private String perUnit = "";

    @Column(name = "VALUE", nullable = true)
    private Double value = null;

    public BaseDataItemNumberValue() {
        super();
    }

    public BaseDataItemNumberValue(ItemValueDefinition itemValueDefinition, DataItem dataItem) {
        super(itemValueDefinition, dataItem);
    }

    public BaseDataItemNumberValue(ItemValueDefinition itemValueDefinition, DataItem dataItem, Double value) {
        this(itemValueDefinition, dataItem);
        setValue(value);
    }

    public BaseDataItemNumberValue(ItemValueDefinition itemValueDefinition, DataItem dataItem, Integer value) {
        this(itemValueDefinition, dataItem);
        setValue(value);
    }

    public BaseDataItemNumberValue(ItemValueDefinition itemValueDefinition, DataItem dataItem, String value) {
        this(itemValueDefinition, dataItem);
        setValue(value);
    }

    protected void copyTo(BaseItemValue o) {
        super.copyTo(o);
        BaseDataItemNumberValue v = (BaseDataItemNumberValue) o;
        v.unit = unit;
        v.perUnit = perUnit;
        v.value = value;
    }

    public void checkItemValueDefinition() {
        if (!(getItemValueDefinition().isDouble() || getItemValueDefinition().isInteger())) {
            throw new IllegalStateException();
        }
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
        return value != null;
    }

    @Override
    public AmountUnit getCanonicalUnit() {
        return getItemValueDefinition().getUnit();
    }

    @Override
    public boolean hasUnit() {
        return (StringUtils.isNotBlank(unit) && getItemValueDefinition().isAnyUnit()) || getItemValueDefinition().hasUnit();
    }

    @Override
    public boolean hasPerUnit() {
        return (StringUtils.isNotBlank(perUnit) && getItemValueDefinition().isAnyPerUnit()) || getItemValueDefinition().hasPerUnit();
    }

    public boolean hasPerTimeUnit() {
        return hasPerUnit() && getPerUnit().isTime();
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
        if (unit == null) {
            unit = "";
        }
        if (!getItemValueDefinition().isValidUnit(unit)) {
            throw new IllegalArgumentException("The unit argument is not valid: " + unit);
        }
        this.unit = unit;
    }

    @Override
    public AmountPerUnit getPerUnit() {
        return StringUtils.isNotBlank(perUnit) ? AmountPerUnit.valueOf(perUnit) : getItemValueDefinition().getPerUnit();
    }

    public void setPerUnit(String perUnit) throws IllegalArgumentException {
        if (perUnit == null) {
            perUnit = "";
        }
        if (!getItemValueDefinition().isValidPerUnit(perUnit)) {
            throw new IllegalArgumentException("The perUnit argument is not valid: " + perUnit);
        }
        this.perUnit = perUnit;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public Double getValueAsDouble() {
        return value;
    }

    @Override
    public String getValueAsString() {
        if (value != null) {
            NumberFormat f = NumberFormat.getInstance();
            if (f instanceof DecimalFormat) {
                ((DecimalFormat) f).applyPattern("0.#################");
            }
            return f.format(value);
        } else {
            return "";
        }
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setValue(Integer value) {
        this.value = value.doubleValue();
    }

    public void setValue(String value) {
        if (StringUtils.isNotBlank(value) && !value.equals("-")) {
            // Ensure numbers are a valid format (double).
            try {
                this.value = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                log.warn("setValue() Invalid number format: " + value);
                throw new IllegalArgumentException("Invalid number format: " + value);
            }
        } else {
            // Number is empty.
            this.value = null;
        }
    }
}
