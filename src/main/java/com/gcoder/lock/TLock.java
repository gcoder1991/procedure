package com.gcoder.lock;

import java.util.concurrent.TimeUnit;

/**
 * Created by gocder on 2017/6/22.
 */
public interface TLock {

    void lock();

    void unlock();

    boolean tryLock();

    boolean tryLock(long time, TimeUnit unit);

}
