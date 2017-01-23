package jp.spring.plugin;

/**
 * Created by Administrator on 1/22/2017.
 */
public interface CacheManager {
    void createCache(String cacheName);

    <K, V>  Cache<K, V> getCache(String cacheName);

    Iterable<String> getCacheNames();

    void destroyCach(String cacheName);

    void destroyCacheAll();
}
