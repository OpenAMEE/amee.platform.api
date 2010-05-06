package com.amee.domain.cache;

import com.amee.base.domain.IdentityObject;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.List;

public class CacheHelper implements Serializable {

    private final Log log = LogFactory.getLog(getClass());

    private static CacheHelper instance = new CacheHelper();

    private CacheHelper() {
        super();
    }

    public static CacheHelper getInstance() {
        return instance;
    }

    public Object getInternal() {
        return CacheManager.getInstance();
    }

    public CacheManager getCacheManager() {
        return CacheManager.getInstance();
    }

    public BlockingCache getBlockingCache(String cacheName) {
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
        return (BlockingCache) cacheManager.getEhcache(cacheName);
    }

    public Object getCacheable(CacheableFactory factory) {
        Object o = null;
        String cacheName = factory.getCacheName();
        String key = factory.getKey();
        BlockingCache cache = getBlockingCache(cacheName);
        if (cache != null) {
            if (log.isTraceEnabled()) {
                log.trace("getCacheable() - cache: " + cacheName + " key: " + key);
            }
            String originalThreadName = Thread.currentThread().getName();
            try {
                Element element = cache.get(key);
                if (element == null) {
                    try {
                        // element is not cached - build it
                        o = factory.create();
                        cache.put(new Element(key, o));
                    } catch (final Throwable throwable) {
                        // must unlock the cache if the above fails
                        cache.put(new Element(key, null));
                        // TODO: what should we really be throwing here?
                        throw new RuntimeException(throwable);
                    }
                } else {
                    o = element.getObjectValue();
                }
            } finally {
                Thread.currentThread().setName(originalThreadName);
            }
        } else {
            log.warn("getCacheable() - cache NOT found: " + cacheName);
            o = factory.create();
        }
        return o;
    }

    public void clearCache(CacheableFactory factory) {
        remove(factory.getCacheName(), factory.getKey());
    }

    public void clearCache(String cacheName, String elementKeyPrefix) {
        BlockingCache cache = getBlockingCache(cacheName);
        if (cache != null) {
            log.debug("cache: " + cacheName + " elementKeyPrefix: " + elementKeyPrefix);
            for (Object o : cache.getKeys()) {
                String elementKey = (String) o;
                if (elementKey.startsWith(elementKeyPrefix)) {
                    log.debug("removing: " + elementKey);
                    cache.remove(elementKey);
                }
            }
        }
    }

    public void clearCache(String cacheName) {
        BlockingCache cache = getBlockingCache(cacheName);
        if (cache != null) {
            log.debug("clearCache() - cache: " + cacheName);
            cache.removeAll();
            cache.getKeys();
        }
    }

    private String getCacheKey(String key, String scope) {
        return "S_" + scope + "_" + key;
    }

    public void remove(String cacheName, String key, String scope) {
        remove(cacheName, getCacheKey(key, scope));
    }

    public void remove(String cacheName, String key) {
        BlockingCache cache = getBlockingCache(cacheName);
        if (cache != null) {
            log.debug("remove() - cache: " + cacheName + " key: " + key);
            cache.remove(key);
        }
    }

    public void add(String cacheName, String scope, String key, Object o) {
        add(cacheName, getCacheKey(key, scope), o);
    }

    public void add(String cacheName, String key, Object o) {
        BlockingCache cache = getBlockingCache(cacheName);
        if (cache != null) {
            log.debug("add() - cache: " + cacheName + " key: " + key);
            cache.put(new Element(key, o));
        }
    }

    public List getKeys(String cacheName) {
        BlockingCache cache = getBlockingCache(cacheName);
        return cache.getKeys();
    }

    public Object get(String cacheName, String scope, String key) {
        return get(cacheName, getCacheKey(key, scope));
    }

    public Object get(String cacheName, Object key) {
        BlockingCache cache = getBlockingCache(cacheName);
        if ((cache != null) && cache.isKeyInCache(key)) {
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

    public Object getAndBlock(String cacheName, String key, String scope) {
        return getAndBlock(cacheName, getCacheKey(key, scope));
    }

    public Object getAndBlock(String cacheName, Object key) {
        BlockingCache cache = getBlockingCache(cacheName);
        if ((cache != null) && cache.isKeyInCache(key)) {
            Element element = cache.get(key);
            if (element != null) {
                return element.getValue();
            }
        }
        return null;
    }

    public Object getAndRemove(String cacheName, String key, String scope) {
        return getAndRemove(cacheName, getCacheKey(key, scope));
    }

    public Object getAndRemove(String cacheName, Object key) {
        BlockingCache cache = getBlockingCache(cacheName);
        if ((cache != null) && cache.isKeyInCache(key)) {
            Element element = cache.get(key);
            if (element != null) {
                cache.remove(key);
                return element.getValue();
            } else {
                // unlock the blocking cache if cache.get fails
                cache.put(new Element(key, null));
            }
        }
        return null;
    }

    public void add(String cacheName, IdentityObject identityObject, String scope) {
        add(cacheName, getCacheKey(identityObject.getUid(), scope), identityObject);
    }

    public void add(String cacheName, IdentityObject identityObject) {
        add(cacheName, identityObject.getUid(), identityObject);
    }

    public void remove(String cacheName, IdentityObject identityObject, String scope) {
        remove(cacheName, getCacheKey(identityObject.getUid(), scope));
    }

    public void remove(String cacheName, IdentityObject identityObject) {
        remove(cacheName, identityObject.getUid());
    }
}