import java.util.Optional;

/**
 * Created by gcoder on 2017/6/17.
 */
public abstract class TTable<K, V> {

    private String tableName;

    public Optional<V> get(Transaction<String,K,V> transaction, K key) {

        if (key == null) {
            throw new NullPointerException("Get error : key is null.");
        }

        Optional<Transaction.TemporaryData<V>> result = transaction.getTemporaryData(tableName, key);

        if (result.isPresent()) {
            Transaction.TemporaryData<V> temporaryData = result.get();
            if (temporaryData.getOperation() != Transaction.Operation.DEL) {
                return Optional.empty();
            } else {
                return Optional.of(temporaryData.getValue());
            }
        } else {
            return Optional.ofNullable(getFromDB());
        }
    }

    public abstract V getFromDB();

}
