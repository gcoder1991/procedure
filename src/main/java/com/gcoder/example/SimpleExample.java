package com.gcoder.example;

import com.gcoder.database.redis.RedisAdapter;
import com.gcoder.procedure.Procedure;
import com.gcoder.table.RedisTableManager;
import com.gcoder.transaction.RedisTransaction;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by gcoder on 2017/6/21.
 */
public class SimpleExample {

    public static void main(String[] args) {

        RedisTransaction t1 = new RedisTransaction((RedisAdapter) RedisTableManager.getInstance().getDatabase());

        t1.begin();

        TableA testTable = new TableA(RedisTableManager.getInstance());
        Optional<byte[]> test = testTable.get(t1, "test");
        if (!test.isPresent()) {
            testTable.set(t1, "test", new byte[]{1,2,3,4,5,6});
        }
        testTable.set(t1, "record", new byte[]{1});
        testTable.delete(t1, "record");

        t1.commit();

        System.out.println(Arrays.toString(testTable.get("test").get()));

        RedisTableManager.getInstance().addTable(TableB.getTable());

        RedisTransaction t2 = new RedisTransaction((RedisAdapter) RedisTableManager.getInstance().getDatabase());

        t2.begin();
        Optional<byte[]> hello = TableB.get("hello");
        if (hello.isPresent()) {
            System.out.println("hello world!".concat(Arrays.toString(hello.get())));
        } else {
            System.out.println("init tableB.");
            TableB.set("hello", new byte[]{1,1,0});
        }

        t2.commit();

        new Procedure() {
            @Override
            public void run() {

            }
        };

    }

}
