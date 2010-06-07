package com.amee.domain.tag;

import com.amee.domain.AMEEEntity;
import com.amee.domain.ObjectType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

@Entity
@Table(name = "TAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tag extends AMEEEntity {

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
    private long count = 0;

    public Tag() {
        super();
    }

    public Tag(String tag) {
        this();
        setTag(tag);
    }

    public ObjectType getObjectType() {
        return ObjectType.TA;
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
