import java.util.Optional;

/**
 * Created by gcoder on 2017/6/19.
 */
public interface CacheAdapter<T, K, V> {

    Optional<V> get(T table, K key);

    void put(T table, K key, V value);

    void put(T table, K key, V value, long expire);

    void remove(T table, K key);

}
