package com.gcoder.example;

import com.gcoder.table.RedisTable;
import com.gcoder.table.TableManager;

/**
 * Created by gcoder on 2017/6/21.
 */
public final class TableA extends RedisTable {

    public static final String TABLE_NAME = "TableA";

    public TableA(TableManager tableManager) {
        super(TABLE_NAME, tableManager.getDatabase(), tableManager.getCache(), false);
        tableManager.addTable(this);
    }

}
