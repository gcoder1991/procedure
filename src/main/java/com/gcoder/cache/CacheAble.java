package com.gcoder.cache;

import java.util.Optional;

/**
 * Created by gcoder on 2017/6/19.
 */
public interface CacheAble<K, V> {

    Optional<V> getCache(K key);

    void putCache(K key, V value);

    void putCache(K key, V value, long expire);

    void removeCache(K key);

}
