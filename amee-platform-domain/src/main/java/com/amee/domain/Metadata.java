package com.amee.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * An entity for storing misc. metadata values for other entities.
 */
@Entity
@Table(name = "metadata")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Metadata extends AMEEEntity {

    public final static int NAME_MAX_SIZE = 255;

    // MySQL mediumtext: http://stackoverflow.com/a/3507664
    public final static int VALUE_MAX_SIZE = 16_777_215;

    @Embedded
    private AMEEEntityReference entityReference = new AMEEEntityReference();

    @Column(name = "name", nullable = false, length = NAME_MAX_SIZE)
    private String name = "";

    @Lob
    @Column(name = "value", nullable = false, length = VALUE_MAX_SIZE)
    private String value = "";

    public Metadata() {
        super();
    }

    public Metadata(IAMEEEntityReference entityReference, String name) {
        this();
        setEntityReference(entityReference);
        setName(name);
    }

    public Metadata(IAMEEEntityReference entityReference, String name, String value) {
        this(entityReference, name);
        setValue(value);
    }

    public ObjectType getObjectType() {
        return ObjectType.MD;
    }

    public IAMEEEntityReference getEntityReference() {
        return entityReference;
    }

    public void setEntityReference(IAMEEEntityReference entityReference) {
        this.entityReference = new AMEEEntityReference(entityReference);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if (value == null) {
            value = "";
        }
        this.value = value;
    }

    @Override
    public boolean isTrash() {
        return status.equals(AMEEStatus.TRASH) || getEntityReference().getEntity().isTrash();
    }
}
