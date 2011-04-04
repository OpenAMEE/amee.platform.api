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
@Table(name = "UNIT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AMEEUnit extends AMEEEntity implements Pathable {

    public final static int NAME_MAX_SIZE = 255;
    public final static int SYMBOL_MAX_SIZE = 255;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UNIT_TYPE_ID")
    private AMEEUnitType unitType;

    @Column(name = "NAME", nullable = false, length = NAME_MAX_SIZE)
    private String name = "";

    @Column(name = "INTERNAL_SYMBOL", nullable = false, length = SYMBOL_MAX_SIZE)
    private String internalSymbol = "";

    @Column(name = "EXTERNAL_SYMBOL", nullable = false, length = SYMBOL_MAX_SIZE)
    private String externalSymbol = "";

    public AMEEUnit() {
        super();
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
        return getPath();
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