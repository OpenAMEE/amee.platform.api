package com.amee.base.resource;

import com.amee.base.domain.VersionBeanFinder;
import com.amee.base.validation.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service bean to aid in discovery of ValidationHelpers whilst respecting the Since and Until annotations.
 */
@Service
public class ValidationHelperBeanFinder {

    @Autowired
    private VersionBeanFinder versionBeanFinder;

    public ValidationHelper getValidationHelper(final Class clazz, final RequestWrapper requestWrapper) {
        return getValidationHelper(clazz.getName(), requestWrapper);
    }

    public ValidationHelper getValidationHelper(final String className, final RequestWrapper requestWrapper) {
        // Get the ValidationHelper with the supplied class name.
        ValidationHelper validationHelper = (ValidationHelper) versionBeanFinder.getBeanForVersion(className, requestWrapper.getVersion());
        // A ValidationHelper must exist or we shall throw a IllegalStateException.
        if (validationHelper != null) {
            return validationHelper;
        } else {
            throw new IllegalStateException("At least one ValidationHelper was expected.");
        }
    }
}
