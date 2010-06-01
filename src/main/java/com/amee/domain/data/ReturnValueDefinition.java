package com.amee.domain.data;

import com.amee.domain.AMEEEnvironmentEntity;
import com.amee.domain.AMEEStatus;
import com.amee.domain.ObjectType;
import com.amee.domain.ValueDefinition;
import com.amee.platform.science.AmountCompoundUnit;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "RETURN_VALUE_DEFINITION")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Configurable(autowire = Autowire.BY_TYPE)
public class ReturnValueDefinition extends AMEEEnvironmentEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "ITEM_DEFINITION_ID")
    private ItemDefinition itemDefinition;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "VALUE_DEFINITION_ID")
    private ValueDefinition valueDefinition;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "PER_UNIT")
    private String perUnit;

    @Column(name = "IS_DEFAULT")
    private boolean isDefault;

    public ReturnValueDefinition() {
        super();
    }

    public ReturnValueDefinition(ItemDefinition itemDefinition) {
        super(itemDefinition.getEnvironment());
        setItemDefinition(itemDefinition);
        itemDefinition.add(this);
    }

    public AmountUnit getUnit() {
        return (unit != null) ? AmountUnit.valueOf(unit) : AmountUnit.ONE;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public AmountPerUnit getPerUnit() {
        return (perUnit != null) ? AmountPerUnit.valueOf(perUnit) : AmountPerUnit.ONE;
    }

    public void setPerUnit(String perUnit) {
        this.perUnit = perUnit;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
    }

    public ValueDefinition getValueDefinition() {
        return valueDefinition;
    }

    public void setValueDefinition(ValueDefinition valueDefinition) {
        this.valueDefinition = valueDefinition;
    }

    @Override
    public String toString() {
        return "ItemValueDefinition_" + getUid();
    }

    public ObjectType getObjectType() {
        return ObjectType.RVD;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || itemDefinition.isTrash();
    }

    public AmountCompoundUnit getCompoundUnit() {
        return getUnit().with(getPerUnit());
    }
}