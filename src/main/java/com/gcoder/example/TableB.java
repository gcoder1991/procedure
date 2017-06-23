package com.gcoder.example;

import com.gcoder.table.RedisTable;
import com.gcoder.table.RedisTableManager;
import com.gcoder.util.TransactionUtils;

import java.util.Optional;

/**
 * Created by gcoder on 2017/6/22.
 */
public final class TableB {

    public static final String TABLE_NAME = "TableB";

    private static final RedisTable TABLE = new RedisTable(TABLE_NAME, RedisTableManager.getInstance().getDatabase(),
            RedisTableManager.getInstance().getCache(), false);

    public static final Optional<byte[]> get(String key){
        return TABLE.get(key);
    }

    public static final void set(String key, byte[] value) {
        TABLE.set(TransactionUtils.current().get(), key, value);
    }

    public static final void delete(String key) {
        TABLE.delete(TransactionUtils.current().get(), key);
    }

    public static RedisTable getTable() {
        return TABLE;
    }

}
