package com.gcoder.table;

import com.gcoder.database.redis.JedisAdapter;
import redis.clients.jedis.JedisPool;

/**
 * Created by gcoder on 2017/6/21.
 */
public class RedisTableManager extends TableManager<String, String, byte[]> {

    private static final RedisTableManager INSTANCE = new RedisTableManager();

    public static RedisTableManager getInstance() {
        return INSTANCE;
    }

    private RedisTableManager() {
        super(new JedisAdapter(new JedisPool("10.236.36.169", 6379)));
    }

}
