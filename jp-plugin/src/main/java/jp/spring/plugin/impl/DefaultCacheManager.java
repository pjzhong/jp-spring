package jp.spring.plugin.impl;

import jp.spring.plugin.Cache;
import jp.spring.plugin.CacheManager;

/**
 * Created by Administrator on 1/22/2017.
 */
public class DefaultCacheManager implements CacheManager {

    @Override
    public void createCache(String cacheName) {

    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return null;
    }

    @Override
    public Iterable<String> getCacheNames() {
        return null;
    }

    @Override
    public void destroyCach(String cacheName) {

    }

    @Override
    public void destroyCacheAll() {

    }
}
