package com.amee.base.resource;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class RendererHelper<R> {

    public R getRenderer(RequestWrapper requestWrapper, Map<String, Class> renderers) {
        R r = null;
        if (!renderers.isEmpty()) {
            try {
                for (String acceptedMediaType : requestWrapper.getAcceptedMediaTypes()) {
                    if (renderers.containsKey(acceptedMediaType)) {
                        r = (R) renderers
                                .get(acceptedMediaType)
                                .getConstructor()
                                .newInstance();
                        break;
                    }
                }
                if (r == null) {
                    r = (R) renderers.values().toArray(new Class[0])[0]
                            .getConstructor()
                            .newInstance();
                }
            } catch (InstantiationException e) {
                throw new RuntimeException("Caught InstantiationException: " + e.getMessage(), e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Caught IllegalAccessException: " + e.getMessage(), e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Caught NoSuchMethodException: " + e.getMessage(), e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException("Caught InvocationTargetException: " + e.getMessage(), e);
            }
        } else {
            throw new RuntimeException("No renderers defined.");
        }
        return r;
    }
}
