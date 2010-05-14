package com.amee.domain.tag;

import com.amee.domain.AMEEEntity;
import com.amee.domain.ObjectType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Tag extends AMEEEntity {

    public final static int TAG_MIN_SIZE = 3;
    public final static int TAG_MAX_SIZE = 255;

    @Column(name = "TAG", nullable = false, length = TAG_MAX_SIZE)
    @Index(name = "TAG_IND")
    private String tag = "";

    public Tag() {
        super();
    }

    public Tag(String tag) {
        this();
        setTag(tag);
    }

    @Override
    public String toString() {
        return "Tag_" + getUid();
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
}
