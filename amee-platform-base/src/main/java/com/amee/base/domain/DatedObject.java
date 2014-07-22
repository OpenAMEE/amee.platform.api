package com.amee.base.domain;

import java.util.Date;

/**
 * An interface for domain entities which provide created and modified date auditing attributes.
 */
public interface DatedObject extends IdentityObject {

    /**
     * A callback for the JPA PrePersist annotation.
     */
    void onCreate();

    /**
     * Get the {@link Date} the entity was created.
     *
     * @return the created {@link Date}
     */
    Date getCreated();

    /**
     * Set the {@link Date} the entity created.
     *
     * @param created the created {@link Date}
     */
    void setCreated(Date created);

    /**
     * A callback for the JPA PreUpdate annotation.
     */
    void onModify();

    /**
     * Get the {@link Date} the entity was last modified.
     *
     * @return the last modified {@link Date}
     */
    Date getModified();

    /**
     * Set the {@link Date} the entity was last modified.
     *
     * @param modified the last modified {@link Date}
     */
    void setModified(Date modified);
}