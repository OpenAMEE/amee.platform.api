package com.amee.base.cache;

import java.io.Serializable;

public interface CacheService extends Serializable {

    void set(String region, String key, Object o);

    Object get(String region, String key);

    void delete(String region, String key);
}
