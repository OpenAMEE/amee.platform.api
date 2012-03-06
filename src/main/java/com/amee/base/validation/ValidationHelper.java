package com.amee.base.validation;

import org.springframework.context.MessageSource;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;


/**
 * A base class providing a number of utility functions to validate objects. A {@link MessageSource} and
 * a {@link DataBinder} are used internally, integrating into the Spring validation framework.
 * <p/>
 * TODO: Merge this with {@link BaseValidator}.
 *
 * @deprecated This class should be merged into {@link BaseValidator}.
 */
@Deprecated
public abstract class ValidationHelper {

    /**
     * Constructor.
     */
    public ValidationHelper() {
        super();
    }

    /**
     * Get the current {@link Validator} implementation.
     *
     * @return the current {@link Validator} implementation
     */
    protected Validator getValidator() {
        throw new UnsupportedOperationException();
    }
}
