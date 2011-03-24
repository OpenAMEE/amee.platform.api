package com.amee.base.cache;

/**
 * An facade interface abstracting away the detail of 3rd party caching libraries.
 */
public interface CacheService {

    /**
     * Set a object into a cache with the given key.
     *
     * @param region cache region
     * @param key    key for object
     * @param o      object to cache
     */
    void set(String region, String key, Object o);

    /**
     * Get an object from the cache with the given key.
     *
     * @param region cache region
     * @param key    key for object
     * @return the cached object
     */
    Object get(String region, String key);

    /**
     * Delete an object in the cache with the given key.
     *
     * @param region cache region
     * @param key    key for object
     */
    void delete(String region, String key);
}
