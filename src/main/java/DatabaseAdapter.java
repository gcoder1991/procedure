import java.util.Optional;

/**
 * Created by gcoder on 2017/6/19.
 */
public interface DatabaseAdapter<T, K, V> {

    Optional<V> get(T table, K key);

    void set(T table, K key, V value);

    void delete(T table, K key);

}
