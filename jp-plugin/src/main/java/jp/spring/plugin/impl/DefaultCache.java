package jp.spring.plugin.impl;

import jp.spring.plugin.Cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 1/22/2017.
 */
public class DefaultCache<K, V> implements Cache<K, V> {

    private final Map<K, V> ddataMap = new ConcurrentHashMap<K, V>();

    @Override
    public V get(K key) {
        if(key == null) {
            throw new NullPointerException(" Key can't not  be null");
        }

        return ddataMap.get(key);
    }

    @Override
    public void put(K key, V value) {
        if(key == null || value == null) {
            throw new NullPointerException("key or value can't not be null");
        }
        ddataMap.put(key, value);
    }

    @Override
    public V remove(K key) {
        return ddataMap.remove(key);
    }

    @Override
    public void clear() {
        ddataMap.clear();
    }
}
