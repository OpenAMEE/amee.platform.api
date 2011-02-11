package com.amee.domain.tag;

import com.amee.domain.AMEEEntity;
import com.amee.domain.AMEEEntityReference;
import com.amee.domain.IAMEEEntityReference;
import com.amee.domain.ObjectType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ENTITY_TAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EntityTag extends AMEEEntity {

    @Embedded
    private AMEEEntityReference entityReference = new AMEEEntityReference();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TAG_ID")
    private Tag tag;

    @Transient
    private List<Tag> tags;

    public EntityTag() {
        super();
    }

    public EntityTag(IAMEEEntityReference entityReference, Tag tag) {
        this();
        setEntityReference(entityReference);
        setTag(tag);
    }

    public ObjectType getObjectType() {
        return ObjectType.ET;
    }

    public IAMEEEntityReference getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(IAMEEEntityReference entityReference) {
        this.entityReference = new AMEEEntityReference(entityReference);
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public List<Tag> getTags() {
        if (tags == null) {
            tags = new ArrayList<Tag>();
        }
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.getTags().clear();
        this.getTags().addAll(tags);
    }
}