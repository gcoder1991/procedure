package com.gcoder.example;

import com.gcoder.database.redis.RedisAdapter;
import com.gcoder.table.RedisTableManager;
import com.gcoder.transaction.RedisTransaction;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by gcoder on 2017/6/21.
 */
public class SimpleExample {

    public static void main(String[] args) {

        RedisTransaction transaction = new RedisTransaction((RedisAdapter) RedisTableManager.getInstance().getDatabase());

        transaction.begin();

        TableA testTable = new TableA(RedisTableManager.getInstance());
        Optional<byte[]> test = testTable.get(transaction, "test");
        if (!test.isPresent()) {
            testTable.set(transaction, "test", new byte[]{1,2,3,4,5,6});
        }
        testTable.set(transaction, "record", new byte[]{1});
        testTable.delete(transaction, "record");

        transaction.commit();

        System.out.println(Arrays.toString(testTable.get("test").get()));
    }

}
