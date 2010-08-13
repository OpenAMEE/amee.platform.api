package com.amee.base.resource;

import com.amee.base.domain.VersionBeanFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service bean to aid in discovery of Renderers whilst respecting the Since and Until annotations.
 */
@Service
public class RendererBeanFinder {

    @Autowired
    private VersionBeanFinder versionBeanFinder;

    public Renderer getRenderer(final Class clazz, final RequestWrapper requestWrapper) {
        return getRenderer(clazz.getName(), requestWrapper);
    }

    public Renderer getRenderer(final String className, final RequestWrapper requestWrapper) {
        // Get the Renderer with the supplied class name.
        Renderer renderer = (Renderer) versionBeanFinder.getBeanForVersion(className, requestWrapper.getVersion(), new VersionBeanFinder.VersionBeanMatcher() {
            @Override
            public boolean matches(Object bean) {
                Renderer renderer = (Renderer) bean;
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
}
