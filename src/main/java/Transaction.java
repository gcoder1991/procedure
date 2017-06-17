import java.util.Optional;

/**
 * Created by gcoder on 2017/6/16.
 */
public interface Transaction<T, K, V> {

    enum Status {
        RUNNING, COMMITTED, ROLLEDBACK
    }

    Status getStatus();

    void setRollbackOnly();

    boolean isRollbackOnly();

    void setMetadata(T tableName, K key, V value);

    Optional<V> getMetadata(T tableName, K key);

    void begin();

    void commit();

    void rollback();

    void setTemporaryData(T table, K key, TemporaryData<V> temporaryData);

    Optional<TemporaryData<V>> getTemporaryData(T table, K key);

    enum Operation {
        GET,SET,DEL
    }

    class TemporaryData<V> {

        private Operation operation;
        private V value;

        public TemporaryData() {};

        public TemporaryData(Operation operation, V value) {
            this.operation = operation;
            this.value = value;
        }

        public Operation getOperation() {
            return operation;
        }

        public void setOperation(Operation operation) {
            this.operation = operation;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public static TemporaryData operate(TemporaryData temporaryData, Operation operation, Object value) {

            if (operation == Operation.GET) {
                return temporaryData;
            }

            if (temporaryData == null) {
                temporaryData = new TemporaryData(operation, value);
            } else {
                temporaryData.setOperation(operation);
                temporaryData.setValue(value);
            }

            return temporaryData;
        }

        public TemporaryData operate(Operation operation, V value) {
            if (operation == Operation.GET) {
                return this;
            }
            setOperation(operation);
            setValue(value);
            return this;
        }

    }

}
