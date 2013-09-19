package com.amee.base.resource;

import com.amee.base.domain.VersionBeanFinder;
import com.amee.base.validation.BaseValidator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

/**
 * A service bean to aid in discovery of {@link ResourceBuilder}s, {@link ResourceRenderer}s and {@link BaseValidator} whilst respecting the
 * {@link com.amee.base.domain.Since} and {@link com.amee.base.domain.Until} annotations.
 */
@Service
public class ResourceBeanFinder {

    @Autowired
    private VersionBeanFinder versionBeanFinder;

    // ResourceBuilder.

    /**
     * Get a {@link ResourceBuilder} matching the supplied Class and {@link RequestWrapper}.
     * 
     * @param clazz the Class the {@link ResourceBuilder} should match
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a matching {@link ResourceBuilder} instance
     */
    public ResourceBuilder getBuilder(final Class clazz, final RequestWrapper requestWrapper) {
        return getBuilder(clazz.getName(), requestWrapper);
    }

    /**
     * Get a {@link ResourceBuilder} matching the supplied Class and {@link RequestWrapper}.
     * 
     * @param className the name of the Class the {@link ResourceBuilder} should match
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a matching {@link ResourceBuilder} instance
     */
    public ResourceBuilder getBuilder(final String className, final RequestWrapper requestWrapper) {
        // Get the ResourceBuilder with the supplied class name.
        ResourceBuilder resourceBuilder = (ResourceBuilder) versionBeanFinder.getBeanForVersion(className, requestWrapper.getVersion());
        // A ResourceBuilder must exist or we shall throw a IllegalStateException.
        if (resourceBuilder != null) {
            return resourceBuilder;
        } else {
            throw new IllegalStateException("At least one ResourceBuilder was expected.");
        }
    }

    // ResourceRenderer.

    /**
     * Get a {@link ResourceRenderer} matching the supplied Class and {@link RequestWrapper}.
     * 
     * @param clazz the Class the {@link ResourceRenderer} should match
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a matching {@link ResourceRenderer} instance
     */
    public ResourceRenderer getRenderer(final Class clazz, final RequestWrapper requestWrapper) {
        return getRenderer(clazz.getName(), requestWrapper);
    }

    /**
     * Get a {@link ResourceRenderer} matching the supplied Class and {@link RequestWrapper}.
     * 
     * @param className the name of the Class the {@link ResourceRenderer} should match
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a matching {@link ResourceRenderer} instance
     */
    public ResourceRenderer getRenderer(final String className, final RequestWrapper requestWrapper) {
        // Get the Renderer with the supplied class name.
        ResourceRenderer renderer = (ResourceRenderer) versionBeanFinder.getBeanForVersion(className, requestWrapper.getVersion(),
                new VersionBeanFinder.VersionBeanMatcher() {
                    @Override
                    public boolean matches(Object bean) {
                        ResourceRenderer renderer = (ResourceRenderer) bean;
                        return requestWrapper.getAcceptedMediaTypes().contains(renderer.getMediaType());
                    }
                });
        // A Renderer must exist or we shall throw a MediaTypeNotSupportedException.
        if (renderer != null) {
            return renderer;
        } else {
            throw new MediaTypeNotSupportedException();
        }
    }

    // Validator.

    /**
     * Get a {@link Validator} matching the supplied Class and {@link RequestWrapper}.
     * 
     * @param clazz the Class the {@link Validator} should match
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a matching {@link Validator} instance
     */
    public Validator getValidator(final Class clazz, final RequestWrapper requestWrapper) {
        return getValidator(clazz.getName(), requestWrapper);
    }

    /**
     * Get a {@link Validator} matching the supplied Class and {@link RequestWrapper}.
     * 
     * @param className the name of the Class the {@link Validator} should match
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a matching {@link Validator} instance
     */
    public Validator getValidator(final String className, final RequestWrapper requestWrapper) {
        // Get the Validator with the supplied class name.
        Validator validator = (Validator) versionBeanFinder.getBeanForVersion(className, requestWrapper.getVersion());
        // A Validator must exist or we shall throw an IllegalStateException.
        if (validator != null) {
            return validator;
        } else {
            throw new IllegalStateException("At least one Validator was expected.");
        }
    }

    // BaseValidator.

    /**
     * Get a {@link BaseValidator} matching the supplied Class and {@link RequestWrapper}.
     * 
     * @param clazz the Class the {@link BaseValidator} should match
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a matching {@link BaseValidator} instance
     */
    public BaseValidator getBaseValidator(final Class clazz, final RequestWrapper requestWrapper) {
        return getBaseValidator(clazz.getName(), requestWrapper);
    }

    /**
     * Get a {@link BaseValidator} matching the supplied Class and {@link RequestWrapper}.
     * 
     * @param className the name of the Class the {@link BaseValidator} should match
     * @param requestWrapper the current {@link RequestWrapper}
     * @return a matching {@link BaseValidator} instance
     */
    public BaseValidator getBaseValidator(final String className, final RequestWrapper requestWrapper) {
        // Get the BaseValidator with the supplied class name.
        BaseValidator validator = (BaseValidator) versionBeanFinder.getBeanForVersion(className, requestWrapper.getVersion());
        // A BaseValidator must exist or we shall throw a IllegalStateException.
        if (validator != null) {
            return validator;
        } else {
            throw new IllegalStateException("At least one BaseValidator was expected.");
        }
    }
}
