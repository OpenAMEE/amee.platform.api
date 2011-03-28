package com.amee.base.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to manage storage of objects keyed by their class in a {@link ThreadLocal}. This simplifies
 * and centralises some usages of {@link ThreadLocal}s.
 * <p/>
 * Care should be taken to call clear() at the start and end of a thread usage (e.g. start and end of a web
 * request).
 * <p/>
 * Usage of this should be avoided if there are better methods available.
 */
public abstract class ThreadBeanHolder {

    private static final ThreadLocal<Map<Class<?>, Object>> BEAN_HOLDER = new ThreadLocal<Map<Class<?>, Object>>();

    /**
     * Get an object with the given type from the {@link ThreadLocal}.
     *
     * @param type of object to retrieve
     * @param <T>  type of object to retrieve
     * @return the object matching the type
     */
    public static <T> T get(Class<T> type) {
        if (BEAN_HOLDER.get() != null) {
            return type.cast(BEAN_HOLDER.get().get(type));
        } else {
            return null;
        }
    }

    /**
     * Set an object with the given type into a {@link ThreadLocal}.
     *
     * @param type of object to set - this is the key.
     * @param obj  object to store
     * @param <T>  type of object to set
     */
    public static <T> void set(Class<T> type, T obj) {
        if (type == null) {
            throw new NullPointerException("Type is null");
        }
        if (BEAN_HOLDER.get() == null) {
            BEAN_HOLDER.set(new HashMap<Class<?>, Object>());
        }
        BEAN_HOLDER.get().put(type, obj);
    }

    /**
     * Clear all {@link ThreadLocal} stored objects managed by ThreadBeanHolder.
     */
    public static void clear() {
        BEAN_HOLDER.set(null);
    }
}