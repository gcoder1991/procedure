package com.gcoder.procedure;

import com.gcoder.database.redis.RedisAdapter;
import com.gcoder.table.RedisTableManager;
import com.gcoder.transaction.RedisTransaction;
import com.gcoder.transaction.Transaction;

/**
 * Created by gcoder on 2017/6/23.
 */
public abstract class Procedure implements Runnable {

    private int retCode;

    private Transaction transaction;

    public Procedure() {
        this.transaction = new RedisTransaction((RedisAdapter) RedisTableManager.getInstance().getDatabase());
    }

    public void execute() {
        int retCode = 0;
        try {
            transaction.begin();
            run();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            this.retCode = retCode;
        }
    }

    public int getRetCode() {
        return retCode;
    }
}
