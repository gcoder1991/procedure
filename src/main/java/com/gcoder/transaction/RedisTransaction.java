package com.gcoder.transaction;

/**
 * Created by gcoder on 2017/6/17.
 */
public class RedisTransaction extends AbstractTransaction<String, String, byte[]> {


    @Override
    protected AbstractTransaction create() {
        return new RedisTransaction();
    }

    @Override
    protected void preBegin() {

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
        postCommit();
    }

    @Override
    protected void postCommit() {

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

    }

}
