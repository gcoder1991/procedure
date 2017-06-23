package com.gcoder.lock;

import java.util.concurrent.TimeUnit;

/**
 * Created by gcoder on 2017/6/22.
 */
public interface LockAble<K> {

    String getLockKey(K key);

    void lock(K key);

    void unlock(K key);

    boolean tryLock(K key);

    boolean tryLock(K key, long time, TimeUnit unit);

}
