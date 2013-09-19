package com.amee.base.cache;

import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * An implementation of {@link CacheService} for Memcached. This is experimental and has never been used fully.
 */
public class MemcachedCacheService implements CacheService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private MemcachedClient client;

    public MemcachedCacheService() {
        super();
        try {
            client = new MemcachedClient(new InetSocketAddress("localhost", 11211));
        } catch (IOException e) {
            log.error("CacheService() Caught IOException: " + e.getMessage(), e);
            throw new RuntimeException("CacheService() Caught IOException: " + e.getMessage(), e);
        }
    }

    public void set(String region, String key, Object o) {
        client.set(region + "_" + key, 3600, o);
    }

    public Object get(String region, String key) {
        return client.get(region + "_" + key);
    }

    public void delete(String region, String key) {
        client.delete(region + "_" + key);
    }
}
