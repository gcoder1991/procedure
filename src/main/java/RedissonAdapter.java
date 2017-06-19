import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Optional;

/**
 * Created by Administrator on 2017/6/19.
 */
public class RedissonAdapter implements DatabaseAdapter<String, String, byte[]> {

    private Config config;
    private RedissonClient redisson;

    public RedissonAdapter(Config config) {
        this.config = config;
        this.redisson = Redisson.create(config);
    }

    @Override
    public Optional<byte[]> get(String table, String key) {
        return null;
    }

    @Override
    public void set(String table, String key, byte[] value) {

    }

    @Override
    public void delete(String table, String key) {

    }
}
