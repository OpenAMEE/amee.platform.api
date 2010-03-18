package com.amee.base.validation;

import com.amee.base.utils.UidGen;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * A bean to specify validation rules and perform validation for a single field submitted via a web form (or other).
 * Allows for various types of fields and rules to be checked against. See below for the available checks.
 */
public class ValidationSpecification implements Serializable {

    public final static int STOP = 0;
    public final static int CONTINUE = 1;

    // The name of the value, such as 'password'.
    private String name = null;

    // Can the value be blank?
    private boolean allowBlank = false;

    // The minimum size of the value, or -1 to ignore.
    private int minSize = -1;

    // The maximum size of the value, or -1 to ignore.
    private int maxSize = -1;

    // The precise size of the value, or -1 to ignore.
    private int size = -1;

    // A RegEx Pattern to validate with, or null to ignore.
    private Pattern format = null;

    // Is the value a URL?
    private boolean url = false;

    // Is the value a UID?
    private boolean uid = false;

    // Is the value a comma separated list of UIDs?
    private boolean uidList = false;

    // Is the value a number (integer)?
    private boolean number = false;

    // Can a number be negative?
    private boolean numberNegative = true;

    public ValidationSpecification() {
        super();
    }

    /**
     * Performs validation of the supplied value against the specification held in the
     * ValidationSpecification instance. Will update the supplied Errors instance with the
     * validation results.
     *
     * @param value to validate
     * @param e     the Errors instance for storing validation results
     * @return STOP or CONTINUE, indicating whether the caller should continue validating other values
     */
    public int validate(Object value, Errors e) {
        if (value instanceof String) {
            return validateString((String) value, e);
        } else {
            return CONTINUE;
        }
    }

    private int validateString(String value, Errors e) {
        // Handle empty.
        if (allowBlank && StringUtils.isBlank(value)) {
            // Allow empty.
            return CONTINUE;
        } else {
            // Don't allow empty.
            if (StringUtils.isBlank(value)) {
                e.rejectValue(name, name + ".empty");
                return STOP;
            }
        }
        // Validate length.
        if (minSize != -1) {
            if (value.length() < minSize) {
                e.rejectValue(name, name + ".short");
                return STOP;
            }
        }
        if (minSize != -1) {
            if (value.length() > maxSize) {
                e.rejectValue(name, name + ".long");
                return STOP;
            }
        }
        if (getSize() != -1) {
            if (value.length() != getSize()) {
                e.rejectValue(name, name + ".length");
                return STOP;
            }
        }
        // Validate format.
        if (format != null) {
            if (!format.matcher(value).matches()) {
                e.rejectValue(name, name + ".format");
                return STOP;
            }
        }
        // Validate url format.
        if (url) {
            try {
                new URL(value);
            } catch (MalformedURLException e1) {
                e.rejectValue(name, name + ".format");
                return STOP;
            }
        }
        // Validate uid format.
        if (uid) {
            if (!UidGen.INSTANCE_12.isValid(value)) {
                e.rejectValue(name, name + ".format");
                return STOP;
            }
        }
        // Validate uid list format.
        if (uidList) {
            for (String uid : value.split(",")) {
                if (!UidGen.INSTANCE_12.isValid(uid)) {
                    e.rejectValue(name, name + ".format");
                    return STOP;
                }
            }
        }
        // Validate number.
        if (number) {
            Integer i;
            try {
                i = Integer.valueOf(value);
            } catch (NumberFormatException e1) {
                e.rejectValue(name, name + ".number");
                return STOP;
            }
            if (!numberNegative && i < 0) {
                e.rejectValue(name, name + ".negative");
                return STOP;
            }
        }
        // Everything passed.
        return CONTINUE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAllowBlank() {
        return allowBlank;
    }

    public void setAllowBlank(boolean allowBlank) {
        this.allowBlank = allowBlank;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFormat() {
        if (this.format != null) {
            return this.format.pattern();
        } else {
            return null;
        }
    }

    public void setFormat(String format) {
        this.format = Pattern.compile(format);
    }

    public boolean isUrl() {
        return url;
    }

    public void setUrl(boolean url) {
        this.url = url;
    }

    public boolean isUid() {
        return uid;
    }

    public void setUid(boolean uid) {
        this.uid = uid;
    }

    public boolean isUidList() {
        return uidList;
    }

    public void setUidList(boolean uidList) {
        this.uidList = uidList;
    }

    public boolean isNumber() {
        return number;
    }

    public void setNumber(boolean number) {
        this.number = number;
    }

    public boolean isNumberNegative() {
        return numberNegative;
    }

    public void setNumberNegative(boolean numberNegative) {
        this.numberNegative = numberNegative;
    }
}

