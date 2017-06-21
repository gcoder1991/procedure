package com.gcoder.table;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by gcoder on 2017/6/20.
 */
public class TableManager<K, V> {

    private Map<String, TTable<K, V>> tableMap = new HashMap<>();

    public Optional<TTable<K, V>> getTable(String name) {
        return Optional.ofNullable(tableMap.get(name));
    }

    public void addTable(TTable<K, V> table) {
        tableMap.put(table.getTableName(), table);
    }

}
