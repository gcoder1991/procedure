package com.gcoder.transaction;

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

    void setMetadata(T tableName, K key, MetaData<V> metaData);

    Optional<MetaData<V>> getMetadata(T tableName, K key);

    void begin();

    void commit();

    void rollback();

    void setTemporaryData(T table, K key, TemporaryData<V> temporaryData);

    Optional<TemporaryData<V>> getTemporaryData(T table, K key);

    enum Operation {
        GET,SET,DEL
    }

    class MetaData<V> {

        private Optional<V> value;

        public MetaData(Optional<V> value) {
            this.value = value;
        }

        public Optional<V> getValue() {
            return value;
        }

        public void setValue(Optional<V> value) {
            this.value = value;
        }

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

        public TemporaryData<V> operate( Operation operation, V value) {

            if (operation == Operation.GET) {
                return this;
            }

            setOperation(operation);
            setValue(value);

            return this;
        }

    }

}
