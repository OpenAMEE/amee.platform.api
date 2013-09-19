package com.amee.base.domain;

/**
 * An interface for domain entities which provide basic identity attributes
 */
public interface IdentityObject {

    /**
     * Get the entity ID.
     *
     * @return the entity ID as a Long
     */
    Long getId();

    /**
     * Set the entity ID.
     *
     * @param id the entity ID as a Long
     */
    void setId(Long id);

    /**
     * Set the entity UID.
     *
     * @return the entity UID.
     * @see com.amee.base.utils.UidGen
     */
    String getUid();

    /**
     * Get the entity UID.
     *
     * @param uid the entity UID
     *            * @see com.amee.base.utils.UidGen
     */
    void setUid(String uid);

    /**
     * Get a friendly String value representing the identity of the entity. This would normally be the UID.
     *
     * @return String identity value
     */
    String getIdentityValue();

    /**
     * Get a friendly String value representing the identity of the entity. This would normally be the UID.
     *
     * @param value String identity value
     */
    void setIdentityValue(String value);
}