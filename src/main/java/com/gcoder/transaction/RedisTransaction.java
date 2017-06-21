package com.gcoder.transaction;

import com.gcoder.database.redis.RedisAdapter;
import com.google.common.collect.Table;

/**
 * Created by gcoder on 2017/6/17.
 */
public class RedisTransaction extends AbstractTransaction<String, String, byte[]> {

    private RedisAdapter redis;

    public RedisTransaction() {
    }

    public RedisTransaction(RedisAdapter redis) {
        this.redis = redis;
    }

    @Override
    protected void preBegin() {
        if(this.getStatus() != Status.RUNNING) {
            return;
        }
    }

    @Override
    protected void postBegin() {

    }

    @Override
    protected void preCommit() {

    }

    @Override
    public void commit() {
        preCommit();

        Table<String, String, TemporaryData<byte[]>> temporaryDatas = getTemporaryDatas();

        postCommit();
    }

    @Override
    protected void postCommit() {
        setStatus(Status.COMMITTED);
    }

    @Override
    protected void preRollback() {

    }

    @Override
    public void rollback() {
        preRollback();
        postRollback();
    }

    @Override
    protected void postRollback() {
        setStatus(Status.ROLLEDBACK);
    }

}
