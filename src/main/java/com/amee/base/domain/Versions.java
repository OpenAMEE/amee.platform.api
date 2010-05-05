package com.amee.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

public class Versions implements Serializable {

    private TreeSet<Version> versions = new TreeSet<Version>();

    public Versions() {
        super();
    }

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

    public List<Version> getVersions() {
        return new ArrayList<Version>(versions);
    }

    public void setVersions(List<Version> versions) {
        this.versions.clear();
        this.versions.addAll(versions);
    }
}
