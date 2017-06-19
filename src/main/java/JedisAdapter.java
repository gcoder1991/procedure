import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Optional;

/**
 * Created by gcoder on 2017/6/19.
 */
public class JedisAdapter implements DatabaseAdapter<String,String,byte[]> {

    private JedisPoolConfig jedisPoolConfig;
    private JedisPool jedisPool;

    public JedisAdapter() {
        this.jedisPoolConfig = new JedisPoolConfig();
        this.jedisPool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379, 1000, null);
    }

    @Override
    public Optional<byte[]> get(String table, String key) {
        byte[] result;
        try(Jedis jedis = jedisPool.getResource()) {
             result = jedis.get(RedisUtils.getKey(table, key));
        } catch (Exception e) {
            throw new JedisException("Redis get failed.");
        }
        return Optional.ofNullable(result);
    }

    @Override
    public void set(String table, String key, byte[] value) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.set(RedisUtils.getKey(table, key), value);
        } catch (Exception e) {
            throw new JedisException("Redis set failed.");
        }
    }

    @Override
    public void delete(String table, String key) {
        try(Jedis jedis = jedisPool.getResource()) {
            jedis.del(RedisUtils.getKey(table, key));
        } catch (Exception e) {
            throw new JedisException("Redis delete failed.");
        }
    }
}
