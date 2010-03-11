package com.amee.base.cache;

import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.Resource;

/**
 * Extends EhCacheManagerFactoryBean and does nothing.
 * Allows EhCache and Memcached caching configurations to be swappable.
 */
public class NoOpEhCacheManagerFactoryBean extends EhCacheManagerFactoryBean {

    public void setConfigLocation(Resource configLocation) {
    }

    public void setShared(boolean shared) {
    }

    public void setCacheManagerName(String cacheManagerName) {
    }

    public void afterPropertiesSet() {
    }

    public Object getObject() {
        return null;
    }

    public Class getObjectType() {
        return null;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
    }
}
