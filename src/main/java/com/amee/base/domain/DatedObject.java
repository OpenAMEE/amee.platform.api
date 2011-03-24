package com.amee.base.domain;

import java.util.Date;

/**
 * An interface for domain entities which provide created and modified date auditing attributes.
 */
public interface DatedObject extends IdentityObject {

    /**
     * A callback for the JPA PrePersist annotation.
     */
    public void onCreate();

    /**
     * Get the {@link Date} the entity was created.
     *
     * @return the created {@link Date}
     */
    public Date getCreated();

    /**
     * Set the {@link Date} the entity created.
     *
     * @param created the created {@link Date}
     */
    public void setCreated(Date created);

    /**
     * A callback for the JPA PreUpdate annotation.
     */
    public void onModify();

    /**
     * Get the {@link Date} the entity was last modified.
     *
     * @return the last modified {@link Date}
     */
    public Date getModified();

    /**
     * Set the {@link Date} the entity was last modified.
     *
     * @param modified the last modified {@link Date}
     */
    public void setModified(Date modified);
}