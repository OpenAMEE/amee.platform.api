package com.amee.base.domain;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Version implements Serializable, Comparable {

    private String version = "";
    private String description = "";

    public Version() {
        super();
    }

    public Version(String version) {
        this();
        setVersion(version);
    }

    public Version(String version, String description) {
        this(version);
        setDescription(description);
    }

    public String toString() {
        return getVersion();
    }

    public boolean equals(Object o) {
        return (this == o) || Version.class.isAssignableFrom(o.getClass()) && (compareTo(o) == 0);
    }

    public int hashCode() {
        return getVersion().hashCode();
    }

    public int compareTo(Object o) {
        if (this == o) return 0;
        if (!Version.class.isAssignableFrom(o.getClass())) throw new ClassCastException();
        Version other = (Version) o;
        List<String> thisParts = getVersionParts();
        List<String> otherParts = other.getVersionParts();
        int pos = 0;
        while (true) {
            // Are they equal, because there is nothing left to compare?
            if (pos > 2) {
                return 0;
            }
            // Get the parts.
            String thisPartStr = thisParts.get(pos);
            String otherPartStr = otherParts.get(pos);
            // Are they equal, because they are both wildcards?
            if (thisPartStr.equalsIgnoreCase("x") && otherPartStr.equalsIgnoreCase("x")) {
                pos++;
                continue;
            }
            // Is this greater than the other, because it is a wildcard?
            if (thisPartStr.equalsIgnoreCase("x")) {
                return 1;
            }
            // Is this less than the other, because other is a wildcard?
            if (otherPartStr.equalsIgnoreCase("x")) {
                return -1;
            }
            // Get the parts.
            int thisPart = Integer.parseInt(thisPartStr);
            int otherPart = Integer.parseInt(otherPartStr);
            // Are they equal?
            if (thisPart == otherPart) {
                pos++;
                continue;
            }
            // Is this greater than the other?
            if (thisPart > otherPart) {
                return 1;
            }
            // Is this less than the other?
            if (thisPart < otherPart) {
                return -1;
            }
            // Should never get here!
            throw new RuntimeException("Could not compare version.");
        }
    }

    public boolean before(Version o) {
        return this.compareTo(o) < 0;
    }

    public boolean after(Version o) {
        return this.compareTo(o) > 0;
    }

    public List<String> getVersionParts() {
        return getVersionParts(getVersion());
    }

    public boolean isValidVersion() {
        return isValidVersion(getVersion());
    }

    public static boolean isValidVersion(String version) {
        boolean foundWildcard = false;
        // version String cannot be blank.
        if (StringUtils.isBlank(version)) {
            return false;
        }
        List<String> versionParts = getVersionParts(version);
        // Version parts list must not be empty.
        if (versionParts.isEmpty()) {
            return false;
        }
        // Can only have three version parts max.
        if (versionParts.size() > 3) {
            return false;
        }
        // Validate each part.
        for (String v : versionParts) {
            // Cannot be empty.
            if (StringUtils.isEmpty(v)) {
                return false;
            }
            // Cannot have a number if there have been wildcards.
            if (NumberUtils.isNumber(v) && foundWildcard) {
                return false;
            }
            // Is this a wildcard?
            if (v.equalsIgnoreCase("x")) {
                // Record discovery of wildcard and continue.
                foundWildcard = true;
                continue;
            }
            // Must have a number at this point.
            if (NumberUtils.isNumber(v)) {
                continue;
            }
            // Not valid if we get here.
            return false;
        }
        return true;
    }

    public static List<String> getVersionParts(String version) {
        List<String> versionParts = new ArrayList<String>(Arrays.asList(version.split("\\.")));
        while (versionParts.size() < 3) {
            versionParts.add("x");
        }
        return versionParts;
    }

    public boolean isSpecific() {
        return !getVersionParts().contains("x") && !getVersionParts().contains("X");
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("The version field cannot be blank.");
        }
        if (!isValidVersion(version)) {
            throw new IllegalArgumentException("The version field is not valid.");
        }
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        this.description = description;
    }
}
