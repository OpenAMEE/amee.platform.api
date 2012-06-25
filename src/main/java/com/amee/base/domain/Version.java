package com.amee.base.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents an encapsulates an API version.
 * <p/>
 * The rules represented here are vaguely based on SemVer (http://semver.org).
 */
public class Version implements Serializable, Comparable {

    private String version = "";
    private String description = "";

    /**
     * Constructs a default and empty Version instance.
     */
    public Version() {
        super();
    }

    /**
     * Constructs a Version instance with the supplied version String. Internally will call setVersion
     * which validates the version String.
     *
     * @param version the version String
     */
    public Version(String version) {
        this();
        setVersion(version);
    }

    /**
     * Constructs a Version instance with the supplied version and description Strings. Internally
     * will call setVersion which validates the version String.
     *
     * @param version     the version String
     * @param description the description String
     */
    public Version(String version, String description) {
        this(version);
        setDescription(description);
    }

    /**
     * Returns the version String as the String representation of the object.
     *
     * @return the version String
     */
    public String toString() {
        return getVersion();
    }

    /**
     * Returns true if this Version is equal to the supplied Object. Uses compareTo to assess equality.
     *
     * @param o object to compare
     * @return true if this object equals the supplied object
     */
    public boolean equals(Object o) {
        return (this == o) || Version.class.isAssignableFrom(o.getClass()) && (compareTo(o) == 0);
    }

    /**
     * Uses hashCode for the Version String.
     *
     * @return the hashCode for this Version
     */
    public int hashCode() {
        return getVersion().hashCode();
    }

    /**
     * Compares this Version to the supplied object.
     * <p/>
     * Two versions may be equal, before or after each other. Versions are made up of parts separated by full-stops.
     * There may be up to three parts. Digit parts have obvious numeric comparison rules. A wildcard part ('x') may be
     * used to indicate that this part of the version is equal to the last possible number in the series. This is
     * all best demonstrated by example.
     * <p/>
     * Here are some equal examples:
     * <p/>
     * <ul>
     * <li>1 equals 1
     * <li>1 equals 1.x
     * <li>01 equals 01
     * <li>01 equals 1
     * <li>1.1 equals 1.1
     * <li>1.01 equals 1.01
     * <li>10 equals 10
     * <li>10.20 equals 10.20
     * <li>1.2.3 equals 1.2.3
     * <li>1.x equals 1.x.x
     * <li>x equals x.x
     * <li>x.x equals x.x.x
     * </ul>
     * <p/>
     * Here are some examples where the first version is before the second.
     * <p/>
     * <ul>
     * <li>1 is before 2
     * <li>01 is before 02
     * <li>10 is before 20
     * <li>2.2 is before 2.3
     * <li>2.2.2 is before 2.2.3
     * <li>2.1.1 is before 2.1
     * <li>2.1.1 is before 2.1.x
     * <li>2.x is before 3
     * <li>2.2.2 is before 2.2.x
     * </ul>
     * <p/>
     * Here are some examples where the first version is after the second.
     * <p/>
     * <ul>
     * <li>2 is after 1
     * <li>02 is after 01
     * <li>20 is after 10
     * <li>2.3 is after 2.2
     * <li>2.2.3 is after 2.2.2
     * <li>2.1 is after 2.1.1
     * <li>2.1.x is after 2.1.1
     * <li>3 is after 2.x
     * <li>2.2.x is after 2.2.2
     * </ul>
     *
     * @param o the object to compare to
     * @return a negative integer, zero, or a positive integer as this object
     *         is less than, equal to, or greater than the specified object.
     */
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

    /**
     * Returns true if the supplied Version is before this Version.
     *
     * @param version Version to compare
     * @return true if the supplied Version is before this Version
     */
    public boolean before(Version version) {
        return this.compareTo(version) < 0;
    }

    /**
     * Returns true if the supplied Version is after this Version.
     *
     * @param version Version to compare
     * @return true if the supplied Version is after this Version
     */
    public boolean after(Version version) {
        return this.compareTo(version) > 0;
    }

    /**
     * Returns the different parts of the version as String List. The list will always contain at least
     * three parts. Missing parts will be supplemented by 'x' characters. The version String will be split on
     * full-stop characters.
     *
     * @return the different parts of the version as String List
     */
    public List<String> getVersionParts() {
        return getVersionParts(getVersion());
    }

    /**
     * Returns true if this version is valid.
     *
     * @return true if this version is valid
     */
    public boolean isValidVersion() {
        return isValidVersion(getVersion());
    }

    /**
     * Returns true if the version String is valid.
     * <p/>
     * The validation rules are:
     * <p/>
     * <ul>
     * <li>The version cannot be null or empty.
     * <li>Parts of the version are separated by full-stops.
     * <li>When parsed into parts the list most not be empty.
     * <li>There can only be three parts of a version.
     * <li>Each part cannot be null or empty.
     * <li>A version part can either be digits or a wildcard.
     * <li>A wildcard can be an 'x' or 'X' character.
     * <li>A part cannot be a digits if there has already been a wildcard.
     * <li>A part must be digits if there have been no wildcards so far.
     * </ul>
     * <p/>
     * Here are some example valid versions:
     * <p/>
     * <ul>
     * <li>x (any version)
     * <li>1 (any version in the 1 series)
     * <li>1.0 (lowest version in the 1 series)
     * <li>1.x (any version in the 1 series)
     * <li>1.1.1 (specific version)
     * <li>1.1.x (any version within the 1.1 series)
     * </ul>
     *
     * @param version version String to compare
     * @return true if the version String is valid
     */
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
            if (NumberUtils.isDigits(v) && foundWildcard) {
                return false;
            }
            // Is this a wildcard?
            if (v.equalsIgnoreCase("x")) {
                // Record discovery of wildcard and continue.
                foundWildcard = true;
                continue;
            }
            // Must have a number at this point.
            if (NumberUtils.isDigits(v)) {
                continue;
            }
            // Not valid if we get here.
            return false;
        }
        return true;
    }

    /**
     * Returns the different parts of the version String as String List. The list will always contain at least
     * three parts. Missing parts will be supplemented by 'x' characters. The version String will be split on
     * full-stop characters.
     *
     * @param version the version String
     * @return the different parts of the version String as String List
     */
    public static List<String> getVersionParts(String version) {
        List<String> versionParts = new ArrayList<String>(Arrays.asList(version.split("\\.")));
        while (versionParts.size() < 3) {
            versionParts.add("x");
        }
        return versionParts;
    }

    /**
     * Returns true if this Version is specific. E.g., it does not contain any 'x' or 'X' parts.
     *
     * @return true if this Version is specific
     */
    public boolean isSpecific() {
        return !getVersionParts().contains("x") && !getVersionParts().contains("X");
    }

    /**
     * Get the version String.
     *
     * @return the version String
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version String. The version string must not be empty or null. It must valid according to the
     * rules in isValidVersion.
     *
     * @param version the version String to set
     * @throws IllegalArgumentException if the version String is not valid
     */
    public void setVersion(String version) {
        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("The version field cannot be blank.");
        }
        if (!isValidVersion(version)) {
            throw new IllegalArgumentException("The version field is not valid.");
        }
        this.version = version;
    }

    /**
     * Get the description property.
     *
     * @return the description property
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description property.
     *
     * @param description property
     */
    public void setDescription(String description) {
        if (description == null) {
            description = "";
        }
        this.description = description;
    }
}
