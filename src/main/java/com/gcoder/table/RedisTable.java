package com.gcoder.table;

import com.gcoder.cache.CacheAdapter;
import com.gcoder.database.DatabaseAdapter;

/**
 * Created by gcoder on 2017/6/19.
 */
public class RedisTable extends TTable<String, byte[]> {


    public RedisTable(String tableName, DatabaseAdapter<String, String, byte[]> database) {
        super(tableName, database);
    }

    public RedisTable(String tableName, DatabaseAdapter<String, String, byte[]> database, CacheAdapter<String, String, byte[]> cache, boolean enableCache) {
        super(tableName, database, cache, enableCache);
    }
}
