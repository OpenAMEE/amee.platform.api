package com.amee.domain.data;

import com.amee.domain.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Locale;

/**
 * Provides a Locale-to-name mapping for an {@link AMEEEntity} instance.
 * <p/>
 * The name in this context is that of the {@link com.amee.domain.path.Pathable} interface.
 * <p/>
 * Modelled as a One-to-Many relationship with the owning entity.
 */
@Entity
@Table(name = "locale_name")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class LocaleName extends AMEEEntity {

    // The locale identifier (e.g. Fr or Fr_fr).
    @Column(name = "locale", nullable = false)
    private String locale;

    // The locale-specific name.
    @Column(name = "name", nullable = false)
    private String name = "";

    @Embedded
    private AMEEEntityReference entity = new AMEEEntityReference();

    public LocaleName() {
        super();
    }

    @Override
    public boolean isTrash() {
        // TODO: Currently entity.getEntity() may be null but should never be. 
        return status.equals(AMEEStatus.TRASH) || ((entity.getEntity() == null) || entity.getEntity().isTrash());
    }

    /**
     * Instantiate a new LocaleName.
     *
     * @param entity - the entity to which the LocaleName belongs.
     * @param locale - the {@link Locale} for this name.
     * @param name   - the locale-specific name.
     */
    public LocaleName(IAMEEEntityReference entity, Locale locale, String name) {
        this.entity = new AMEEEntityReference(entity);
        this.locale = locale.toString();
        this.name = name;
    }

    /**
     * Get the locale-specific name
     *
     * @return - the locale-specific name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the locale-specific name
     *
     * @param name - the locale-specific name
     */
    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    /**
     * Get the locale identifier (e.g. Fr or Fr_fr)
     *
     * @return - the locale identifier
     */
    public String getLocale() {
        return locale;
    }

    public ObjectType getObjectType() {
        return ObjectType.LN;
    }

    public IAMEEEntityReference getEntityReference() {
        return entity;
    }
}