package com.amee.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * A Spring Bean providing storage and utility functions for Versions supported by the application.
 * <p/>
 * Configuration of this bean is in '/conf/applicationContext-versions.xml'.
 */
public class Versions implements Serializable {

    private TreeSet<Version> versions = new TreeSet<Version>();

    public Versions() {
        super();
    }

    /**
     * Returns true if the supplied Version is contained within the current set of Versions.
     *
     * @param version to check
     * @return true if the supplied Version is present
     */
    public boolean contains(Version version) {
        return versions.contains(version);
    }

    /**
     * Get the Version that supports the supplied Version.
     * <p/>
     * TODO: Don't allow silly specific major & minor Versions beyond available range.
     *
     * @param version to find support for
     * @return the Version that supports the supplied version, or null
     */
    public Version getSupportedVersion(Version version) {
        // Are there any Versions?
        if (this.versions.isEmpty()) {
            return null;
        }
        // Must be within available range.
        if (version.before(this.versions.first())) {
            return null;
        }
        if (version.isSpecific() && version.after(this.versions.last())) {
            return null;
        }
        // Iterate from latest to earliest Versions to find a supported Version.
        List<Version> versions = getVersions();
        Collections.reverse(versions);
        for (Version v : versions) {
            if (!v.after(version)) {
                // Found the latest Version that supports the supplied Version.
                return v;
            }
        }
        // Not supported.
        return null;
    }

    /**
     * Get a flattened list of the contained Versions.
     *
     * @return flattened list of the contained Versions
     */
    public List<Version> getVersions() {
        return new ArrayList<Version>(versions);
    }

    /**
     * Set the supplied List of Versions to this bean.
     *
     * @param versions to set
     */
    public void setVersions(List<Version> versions) {
        this.versions.clear();
        this.versions.addAll(versions);
    }
}
