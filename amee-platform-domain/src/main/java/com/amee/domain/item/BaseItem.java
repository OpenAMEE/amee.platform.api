package com.amee.domain.item;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.amee.domain.AMEEEntity;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.path.Pathable;

@MappedSuperclass
public abstract class BaseItem extends AMEEEntity implements Pathable {

    public final static int NAME_MAX_SIZE = 255;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_definition_id")
    private ItemDefinition itemDefinition;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "data_category_id")
    private DataCategory dataCategory;

    @Column(name = "name", length = NAME_MAX_SIZE, nullable = false)
    private String name = "";

    @Transient
    private transient String fullPath;

    @Transient
    private Date effectiveStartDate;

    @Transient
    private Date effectiveEndDate;

    public BaseItem() {
        super();
    }

    public BaseItem(DataCategory dataCategory, ItemDefinition itemDefinition) {
        this();
        setDataCategory(dataCategory);
        setItemDefinition(itemDefinition);
    }

    /**
     * Copy values from this instance to the supplied instance.
     * <p/>
     * Does not copy ItemValues.
     *
     * @param o Object to copy values to
     */
    protected void copyTo(BaseItem o) {
        super.copyTo(o);
        o.itemDefinition = itemDefinition;
        o.dataCategory = dataCategory;
        o.name = name;
        o.effectiveStartDate = (effectiveStartDate != null) ? (Date) effectiveStartDate.clone() : null;
        o.effectiveEndDate = (effectiveEndDate != null) ? (Date) effectiveEndDate.clone() : null;
    }

    /**
     * Get the full path of this Item.
     *
     * @return the full path
     */
    @Override
    public String getFullPath() {
        // Need to build the fullPath?
        if (fullPath == null) {
            // Is there a parent.
            if (getDataCategory() != null) {
                // There is a parent.
                fullPath = getDataCategory().getFullPath() + "/" + getDisplayPath();
            } else {
                // There must be a parent.
                throw new RuntimeException("Item has no parent.");
            }
        }
        return fullPath;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        if (itemDefinition != null) {
            this.itemDefinition = itemDefinition;
        }
    }

    public DataCategory getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(DataCategory dataCategory) {
        if (dataCategory != null) {
            this.dataCategory = dataCategory;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        if (!getName().isEmpty()) {
            return getName();
        } else {
            return getDisplayPath();
        }
    }

    @Override
    public String getDisplayPath() {
        if (!getPath().isEmpty()) {
            return getPath();
        } else {
            return getUid();
        }
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    /**
     * @return returns true if this Item supports CO2 amounts, otherwise false.
     */
    public boolean supportsCalculation() {
        return !getItemDefinition().getAlgorithms().isEmpty();
    }

    /**
     * Set the effective start date.
     *
     * @param effectiveStartDate
     */
    public void setEffectiveStartDate(Date effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    /**
     * Get the effective start date.
     *
     * @return the effective start date.
     */
    public Date getEffectiveStartDate() {
        return effectiveStartDate;
    }

    /**
     * Set the effective end date.
     *
     * @param effectiveEndDate
     */
    public void setEffectiveEndDate(Date effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    /**
     * Get the effective end date.
     *
     * @return the effective end date.
     */
    public Date getEffectiveEndDate() {
        return effectiveEndDate;
    }
}