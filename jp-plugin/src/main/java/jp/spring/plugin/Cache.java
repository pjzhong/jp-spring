package jp.spring.plugin;

/**
 * Created by Administrator on 1/22/2017.
 */
public interface Cache<K, V> {

    V get(K key);

    void put(K key, V value);

    V remove(K key);

    void clear();
}
