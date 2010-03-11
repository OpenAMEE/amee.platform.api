package com.amee.base.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EhCacheCacheService implements CacheService {

    private final Log log = LogFactory.getLog(getClass());

    public EhCacheCacheService() {
        super();
    }

    public void set(String region, String key, Object o) {
        getBlockingCache(region).put(new Element(key, o));
    }

    public Object get(String region, String key) {
        BlockingCache cache = getBlockingCache(region);
        if (cache.isKeyInCache(key)) {
            Element element = cache.get(key);
            if (element != null) {
                return element.getValue();
            } else {
                // unlock the blocking cache if cache.get fails
                cache.put(new Element(key, null));
            }
        }
        return null;
    }

    public void delete(String region, String key) {
        log.debug("remove() - cache: " + region + " key: " + key);
        getBlockingCache(region).remove(key);
    }


    private BlockingCache getBlockingCache(String cacheName) {
        CacheManager cacheManager = getCacheManager();
        Ehcache cache = cacheManager.getEhcache(cacheName);
        if ((cache != null) && !(cache instanceof BlockingCache)) {
            synchronized (this) {
                cache = cacheManager.getEhcache(cacheName);
                if ((cache != null) && !(cache instanceof BlockingCache)) {
                    BlockingCache newBlockingCache = new BlockingCache(cache);
                    cacheManager.replaceCacheWithDecoratedCache(cache, newBlockingCache);
                }
            }
        }
        if (cache == null) {
            throw new RuntimeException("Could not get BlockingCache instance.");
        }
        return (BlockingCache) cacheManager.getEhcache(cacheName);
    }

    private CacheManager getCacheManager() {
        return CacheManager.getInstance();
    }
}