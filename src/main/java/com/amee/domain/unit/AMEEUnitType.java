package com.amee.domain.unit;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.AMEEEntity;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.IDataService;
import com.amee.domain.ObjectType;
import com.amee.domain.path.Pathable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UNIT_TYPE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AMEEUnitType extends AMEEEntity implements Pathable {

    public final static int NAME_MAX_SIZE = 255;

    @Column(name = "NAME", nullable = false, length = NAME_MAX_SIZE)
    private String name = "";

    public AMEEUnitType() {
        super();
    }

    public AMEEUnitType(String name) {
        this();
        setName(name);
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
        return ObjectType.UT;
    }

    @Override
    @Transient
    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = new ArrayList<IAMEEEntityReference>();
        entities.add(getDataService().getRootDataCategory());
        entities.add(this);
        return entities;
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

    @Transient
    public IDataService getDataService() {
        return ThreadBeanHolder.get(IDataService.class);
    }
}