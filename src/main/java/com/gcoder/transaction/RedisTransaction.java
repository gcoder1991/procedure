package com.gcoder.transaction;

import com.gcoder.database.redis.RedisAdapter;
import com.gcoder.table.RedisTableManager;
import com.gcoder.table.TTable;

import java.util.Optional;

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
    public Optional<TTable<String, String, byte[]>> getTable(String tableName) {
        return RedisTableManager.getInstance().getTable(tableName);
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
    protected void postCommit() {
    }

    @Override
    protected void preRollback() {

    }

    @Override
    protected void postRollback() {
    }

}
