package com.amee.persist;

import com.amee.base.domain.DatedObject;
import com.amee.base.utils.UidGen;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity implements DatedObject, Serializable {

    public final static int UID_SIZE = 12;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @NaturalId
    @Column(name = "UID", unique = true, nullable = false, length = UID_SIZE)
    private String uid = "";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false)
    private Date created = null;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFIED", nullable = false)
    private Date modified = null;

    public BaseEntity() {
        super();
        setUid(UidGen.INSTANCE_12.getUid());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!BaseEntity.class.isAssignableFrom(o.getClass())) return false;
        BaseEntity baseEntity = (BaseEntity) o;
        return getUid().equals(baseEntity.getUid());
    }

    @Override
    public int hashCode() {
        return getUid().hashCode();
    }

    @PrePersist
    public void onCreate() {
        Date now = Calendar.getInstance().getTime();
        setCreated(now);
        setModified(now);
    }

    @PreUpdate
    public void onModify() {
        setModified(Calendar.getInstance().getTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        if (uid == null) {
            uid = "";
        }
        this.uid = uid;
    }

    public String getValue() {
        return getUid();
    }

    public void setValue(String value) {
        setUid(value);
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}