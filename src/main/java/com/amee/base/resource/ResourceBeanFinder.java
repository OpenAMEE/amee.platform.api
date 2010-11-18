package com.amee.base.resource;

import com.amee.base.domain.VersionBeanFinder;
import com.amee.base.validation.ValidationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service bean to aid in discovery of Renderers whilst respecting the Since and Until annotations.
 */
@Service
public class ResourceBeanFinder {

    @Autowired
    private VersionBeanFinder versionBeanFinder;

    // ResourceBuilder.

    public ResourceBuilder getBuilder(final Class clazz, final RequestWrapper requestWrapper) {
        return getBuilder(clazz.getName(), requestWrapper);
    }

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

    public ResourceRenderer getRenderer(final Class clazz, final RequestWrapper requestWrapper) {
        return getRenderer(clazz.getName(), requestWrapper);
    }

    public ResourceRenderer getRenderer(final String className, final RequestWrapper requestWrapper) {
        // Get the Renderer with the supplied class name.
        ResourceRenderer renderer = (ResourceRenderer) versionBeanFinder.getBeanForVersion(className, requestWrapper.getVersion(), new VersionBeanFinder.VersionBeanMatcher() {
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

    // ValidationHelper.

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
