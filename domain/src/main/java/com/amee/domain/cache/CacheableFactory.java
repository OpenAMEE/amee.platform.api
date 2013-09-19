package com.amee.domain.cache;

public interface CacheableFactory {

    String getKey();

    String getCacheName();

    Object create();
}
