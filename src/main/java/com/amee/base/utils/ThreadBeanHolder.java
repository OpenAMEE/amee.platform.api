package com.amee.base.utils;

import java.util.HashMap;
import java.util.Map;

public abstract class ThreadBeanHolder {

    private static final ThreadLocal<Map<Class<?>, Object>> BEAN_HOLDER = new ThreadLocal<Map<Class<?>, Object>>();

    public static <T> T get(Class<T> type) {
        if (BEAN_HOLDER.get() != null) {
            return type.cast(BEAN_HOLDER.get().get(type));
        } else {
            return null;
        }
    }

    public static <T> void set(Class<T> type, T obj) {
        if (type == null) {
            throw new NullPointerException("Type is null");
        }
        if (BEAN_HOLDER.get() == null) {
            BEAN_HOLDER.set(new HashMap<Class<?>, Object>());
        }
        BEAN_HOLDER.get().put(type, obj);
    }

    public static void clear() {
        BEAN_HOLDER.set(null);
    }
}