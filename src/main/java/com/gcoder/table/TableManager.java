package com.gcoder.table;

import com.gcoder.cache.CacheAdapter;
import com.gcoder.database.DatabaseAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by gcoder on 2017/6/20.
 */
public class TableManager<T, K, V> {

    private Map<T, TTable<T, K, V>> tableMap = new HashMap<>();

    private DatabaseAdapter<T, K, V> database;
    private CacheAdapter<T, K, V> cache;

    public TableManager() {
    }

    public TableManager(DatabaseAdapter<T, K, V> database) {
        this(database, null);
    }

    public TableManager(DatabaseAdapter<T, K, V> database, CacheAdapter<T, K, V> cache) {
        this.database = database;
        this.cache = cache;
    }

    public Optional<TTable<T, K, V>> getTable(String name) {
        return Optional.ofNullable(tableMap.get(name));
    }

    public void addTable(TTable<T, K, V> table) {
        tableMap.put(table.getTableName(), table);
    }

    public DatabaseAdapter<T, K, V> getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseAdapter<T, K, V> database) {
        this.database = database;
    }

    public CacheAdapter<T, K, V> getCache() {
        return cache;
    }

    public void setCache(CacheAdapter<T, K, V> cache) {
        this.cache = cache;
    }

}
