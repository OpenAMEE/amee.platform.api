package com.amee.domain.unit;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.AMEEEntity;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.IDataService;
import com.amee.domain.ObjectType;
import com.amee.domain.path.Pathable;
import com.amee.platform.science.AmountUnit;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.measure.unit.Unit;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "unit")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AMEEUnit extends AMEEEntity implements Pathable {

    public final static int NAME_MAX_SIZE = 255;
    public final static int SYMBOL_MAX_SIZE = 255;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_type_id")
    private AMEEUnitType unitType;

    @Column(name = "name", nullable = false, length = NAME_MAX_SIZE)
    private String name = "";

    @Column(name = "internal_symbol", nullable = false, length = SYMBOL_MAX_SIZE)
    private String internalSymbol = "";

    @Column(name = "external_symbol", nullable = false, length = SYMBOL_MAX_SIZE)
    private String externalSymbol = "";

    public AMEEUnit() {
        super();
    }

    public AMEEUnit(AMEEUnitType unitType) {
        super();
        setUnitType(unitType);
    }

    public AMEEUnit(String name) {
        this();
        setName(name);
    }

    public AMEEUnit(String name, String internalSymbol) {
        this(name);
        setInternalSymbol(internalSymbol);
    }

    public AMEEUnit(String name, String internalSymbol, String externalSymbol) {
        this(name, internalSymbol);
        setExternalSymbol(externalSymbol);
    }

    public AMEEUnit(Unit unit, Unit perUnit) {
        this();
        setInternalSymbol(unit.divide(perUnit).toString());
    }

    public static AMEEUnit toAMEEUnit(Unit unit, Unit perUnit) {
        return new AMEEUnit(unit, perUnit);
    }

    @Override
    @Transient
    public String getPath() {
        return getUid();
    }

    @Override
    @Transient
    public String getDisplayName() {
        return getName();
    }

    @Override
    @Transient
    public String getDisplayPath() {
        return getPath();
    }

    @Override
    @Transient
    public String getFullPath() {
        return getUnitType().getFullPath() + "/units/" + getDisplayPath();
    }

    @Override
    @Transient
    public ObjectType getObjectType() {
        return ObjectType.UN;
    }

    @Override
    @Transient
    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = new ArrayList<IAMEEEntityReference>();
        entities.add(getDataService().getRootDataCategory());
        entities.add(getUnitType());
        entities.add(this);
        return entities;
    }

    @Override
    @Transient
    public boolean isTrash() {
        return super.isTrash() || getUnitType().isTrash();
    }

    @Transient
    public boolean hasExternalSymbol() {
        return !getExternalSymbol().isEmpty();
    }

    @Transient
    public String getSymbol() {
        if (getExternalSymbol().isEmpty()) {
            return getInternalSymbol();
        } else {
            return getExternalSymbol();
        }
    }

    @Transient
    public AmountUnit getAmountUnit() {
        return AmountUnit.valueOf(getInternalSymbol());
    }

    @Transient
    public Unit getInternalUnit() {
        return getAmountUnit().toUnit();
    }

    /**
     * Extracts the numerator unit from the unit. This is the bit before the '/' in some units.
     *
     * @return the numerator
     */
    @Transient
    public Unit getInternalNumeratorUnit() {
        String unitStr = getInternalUnit().toString();
        if (unitStr.contains("/")) {
            try {
                return AmountUnit.valueOf(unitStr.split("/")[0]).toUnit();
            } catch (IllegalStateException e) {
                throw new IllegalStateException("The unit does not have a numerator.");
            }
        } else {
            throw new IllegalStateException("The unit does not have a numerator.");
        }
    }

    /**
     * Extracts the denominator or per unit from the unit. This is the bit after the '/' in some units.
     *
     * @return the denominator
     */
    @Transient
    public Unit getInternalDenominatorUnit() {
        String unitStr = getInternalUnit().toString();
        if (unitStr.contains("/")) {
            try {
                return AmountUnit.valueOf(unitStr.split("/")[1]).toUnit();
            } catch (IllegalStateException e) {
                throw new IllegalStateException("The unit does not have a denominator.");
            }
        } else {
            throw new IllegalStateException("The unit does not have a denominator.");
        }
    }

    /**
     * Extracts the denominator or per unit from the unit. This is the bit after the '/' in some units.
     * <p/>
     * This is a convenient alias for getInternalDenominatorUnit.
     *
     * @return the denominator
     */
    @Transient
    public Unit getInternalPerUnit() {
        return getInternalDenominatorUnit();
    }

    public AMEEUnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(AMEEUnitType unitType) {
        this.unitType = unitType;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    public String getInternalSymbol() {
        return internalSymbol;
    }

    public void setInternalSymbol(String internalSymbol) {
        if (internalSymbol == null) {
            internalSymbol = "";
        }
        this.internalSymbol = internalSymbol;
    }

    public String getExternalSymbol() {
        return externalSymbol;
    }

    public void setExternalSymbol(String externalSymbol) {
        if (externalSymbol == null) {
            externalSymbol = "";
        }
        this.externalSymbol = externalSymbol;
    }

    @Transient
    public IDataService getDataService() {
        return ThreadBeanHolder.get(IDataService.class);
    }
}