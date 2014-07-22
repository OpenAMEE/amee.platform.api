package com.amee.base.validation;

import com.amee.base.utils.UidGen;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanWrapperImpl;
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

    public interface CustomValidation {
        public int validate(Object object, Object value, Errors errors);
    }

    public final static int STOP = 0;
    public final static int CONTINUE = 1;

    // The name of the value, such as 'password'.
    private String name = null;

    // Can the value be empty?
    private boolean allowEmpty = false;

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

    // Is the value a timestamp
    private String timestamp = null;

    // Is the value a number (integer)?
    private boolean integerNumber = false;

    // Is the value a number (double)?
    private boolean doubleNumber = false;

    // Can a number be negative?
    private boolean numberNegative = true;

    // Extension point for custom validation.
    private CustomValidation customValidation;

    public ValidationSpecification() {
        super();
    }

    /**
     * Performs validation of the named value in the supplied object against the specification held in the
     * ValidationSpecification instance. Will update the supplied Errors instance with the
     * validation results.
     *
     * @param object containing value to validate
     * @param e      the Errors instance for storing validation results
     * @return STOP or CONTINUE, indicating whether the caller should continue validating other values
     */
    public int validate(Object object, Errors e) {
        int result = CONTINUE;
        // Don't validate if this field already has errors.
        if (e.hasFieldErrors(name)) {
            result = STOP;
        }
        // Extract object value.
        BeanWrapperImpl bean = new BeanWrapperImpl(object);
        Object value = bean.getPropertyValue(getName());
        // Validate Strings or Objects separately.
        if (result != STOP) {
            if (value instanceof String) {
                result = validateString((String) value, e);
            } else {
                result = validateObject(value, e);
            }
        }
        // Handle CustomValidation.
        if ((result != STOP) && (customValidation != null)) {
            result = customValidation.validate(object, value, e);
        }
        return result;
    }

    private int validateString(String value, Errors e) {
        // Handle empty.
        if (allowEmpty && StringUtils.isBlank(value)) {
            // Allow empty.
            return CONTINUE;
        } else {
            // Don't allow empty.
            if (StringUtils.isBlank(value)) {
                e.rejectValue(name, "empty");
                return STOP;
            }
        }
        // Validate length.
        if (minSize != -1) {
            if (value.length() < minSize) {
                e.rejectValue(name, "short");
                return STOP;
            }
        }
        if (maxSize != -1) {
            if (value.length() > maxSize) {
                e.rejectValue(name, "long");
                return STOP;
            }
        }
        if (size != -1) {
            if (value.length() != size) {
                e.rejectValue(name, "length");
                return STOP;
            }
        }
        // Validate format.
        if (format != null) {
            if (!format.matcher(value).matches()) {
                e.rejectValue(name, "format");
                return STOP;
            }
        }
        // Validate url format.
        if (url) {
            try {
                new URL(value);
            } catch (MalformedURLException e1) {
                e.rejectValue(name, "format");
                return STOP;
            }
        }
        // Validate uid format.
        if (uid) {
            if (!UidGen.INSTANCE_12.isValid(value)) {
                e.rejectValue(name, "format");
                return STOP;
            }
        }
        // Validate uid list format.
        if (uidList) {
            for (String uid : value.split(",")) {
                if (!UidGen.INSTANCE_12.isValid(uid)) {
                    e.rejectValue(name, "format");
                    return STOP;
                }
            }
        }
        // Validate timestamp
        if (timestamp != null) {
            try {
                new DateTime(timestamp);
            } catch (IllegalArgumentException e1) {
                e.rejectValue(name, "format");
                return STOP;
            }
        }
        // Validate integer.
        if (integerNumber) {
            Integer i;
            try {
                i = Integer.valueOf(value);
            } catch (NumberFormatException e1) {
                e.rejectValue(name, "number");
                return STOP;
            }
            if (!numberNegative && i < 0) {
                e.rejectValue(name, "negative");
                return STOP;
            }
        }
        // Validate double.
        if (doubleNumber) {
            Double d;
            try {
                d = Double.valueOf(value);
            } catch (NumberFormatException e1) {
                e.rejectValue(name, "number");
                return STOP;
            }
            if (!numberNegative && d < 0) {
                e.rejectValue(name, "negative");
                return STOP;
            }
        }
        // Everything passed.
        return CONTINUE;
    }

    private int validateObject(Object value, Errors e) {
        // Handle empty.
        if (allowEmpty && (value == null)) {
            // Allow empty.
            return CONTINUE;
        } else {
            // Don't allow empty.
            if (value == null) {
                e.rejectValue(name, "empty");
                return STOP;
            }
        }
        // Everything passed.
        return CONTINUE;
    }

    /**
     * Get the name of the bean being validated.
     *
     * @return the name of the bean being validated
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the bean being validated.
     *
     * @param name of the bean being validated
     * @return {@link ValidationSpecification} to support method chaining
     */
    public ValidationSpecification setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the allowEmpty flag.
     *
     * @return allowEmpty flag
     */
    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public ValidationSpecification setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        return this;
    }

    public int getMinSize() {
        return minSize;
    }

    public ValidationSpecification setMinSize(int minSize) {
        this.minSize = minSize;
        return this;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public ValidationSpecification setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public int getSize() {
        return size;
    }

    public ValidationSpecification setSize(int size) {
        this.size = size;
        return this;
    }

    public String getFormat() {
        if (this.format != null) {
            return this.format.pattern();
        } else {
            return null;
        }
    }

    public ValidationSpecification setFormat(String format) {
        this.format = Pattern.compile(format);
        return this;
    }

    public boolean isUrl() {
        return url;
    }

    public ValidationSpecification setUrl(boolean url) {
        this.url = url;
        return this;
    }

    public ValidationSpecification setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public boolean isUid() {
        return uid;
    }

    public ValidationSpecification setUid(boolean uid) {
        this.uid = uid;
        return this;
    }

    public boolean isUidList() {
        return uidList;
    }

    public ValidationSpecification setUidList(boolean uidList) {
        this.uidList = uidList;
        return this;
    }

    public boolean isIntegerNumber() {
        return integerNumber;
    }

    public ValidationSpecification setIntegerNumber(boolean integerNumber) {
        this.integerNumber = integerNumber;
        return this;
    }

    public boolean isDoubleNumber() {
        return doubleNumber;
    }

    public ValidationSpecification setDoubleNumber(boolean doubleNumber) {
        this.doubleNumber = doubleNumber;
        return this;
    }

    public boolean isNumberNegative() {
        return numberNegative;
    }

    public ValidationSpecification setNumberNegative(boolean numberNegative) {
        this.numberNegative = numberNegative;
        return this;
    }

    public CustomValidation getCustomValidation() {
        return customValidation;
    }

    public ValidationSpecification setCustomValidation(CustomValidation customValidation) {
        this.customValidation = customValidation;
        return this;
    }
}

