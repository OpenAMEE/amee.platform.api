package com.amee.base.cache;

public interface CacheService {

    void set(String region, String key, Object o);

    Object get(String region, String key);

    void delete(String region, String key);
}
