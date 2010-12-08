package com.amee.domain.tag;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.AMEEEntity;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.IDataService;
import com.amee.domain.ObjectType;
import com.amee.domain.path.Pathable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tag extends AMEEEntity implements Pathable {

    public final static int TAG_MIN_SIZE = 2;
    public final static int TAG_MAX_SIZE = 255;

    // Mapped for HQL. We don't use this collection Java-side.
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private List<EntityTag> entityTags;

    @Column(name = "TAG", nullable = false, length = TAG_MAX_SIZE)
    @Index(name = "TAG_IND")
    private String tag = "";

    @Transient
    private Long count = null;

    public Tag() {
        super();
    }

    public Tag(String tag) {
        this();
        setTag(tag);
    }

    @Override
    public String getPath() {
        return getUid();
    }

    @Override
    public String getName() {
        return getTag();
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public String getDisplayPath() {
        return getPath();
    }

    @Override
    public String getFullPath() {
        return getPath();
    }

    public ObjectType getObjectType() {
        return ObjectType.TA;
    }

    @Override
    public List<IAMEEEntityReference> getHierarchy() {
        List<IAMEEEntityReference> entities = new ArrayList<IAMEEEntityReference>();
        entities.add(getDataService().getRootDataCategory());
        entities.add(this);
        return entities;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        if (tag == null) {
            tag = "";
        }
        this.tag = tag;
    }

    public Long getCount() {
        return count;
    }

    public boolean hasCount() {
        return count != null;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public IDataService getDataService() {
        return (IDataService) ThreadBeanHolder.get("dataService");
    }
}
