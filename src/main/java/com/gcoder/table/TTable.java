package com.gcoder.table;

import com.gcoder.exception.TransactionException;
import com.gcoder.transaction.Transaction;
import com.gcoder.util.TransactionUtils;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Optional;

/**
 * Created by gcoder on 2017/6/17.
 */
public abstract class TTable<K, V> implements CacheAble<K, V> {

    private final String tableName;
    private final DatabaseAdapter<String, K, V> database;

    private CacheAdapter<String, K, V> cache;
    private boolean enableCache;

    public TTable(String tableName, DatabaseAdapter<String, K, V> database) {
        this.tableName = tableName;
        this.database = database;
    }

    public TTable(String tableName, DatabaseAdapter<String, K, V> database, CacheAdapter<String, K, V> cache, boolean enableCache) {
        this.tableName = tableName;
        this.database = database;
        this.cache = cache;
        this.enableCache = enableCache;
    }

    public Optional<V> getFromMedium(K key) {
        if (enableCache && cache != null){
            try {
                Optional<V> result = getCache(key);
                if (!result.isPresent()) {
                    return database.get(tableName, key);
                } else {
                    return result;
                }
            } catch (Exception e) {
                return database.get(tableName, key);
            }
        } else {
            return database.get(tableName, key);
        }
    }

    private Optional<V> getFromTransaction(@NotNull Transaction<String, K, V> transaction, @NotNull K key) {

        if(TransactionUtils.isNull(transaction)) {
            throw new TransactionException("The transaction can not be null.");
        }

        if (key == null) {
            throw new NullPointerException("Get from transaction error : key is null.");
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
            Optional<Transaction.MetaData<V>> metadata = transaction.getMetadata(tableName, key);
            if (metadata.isPresent()) {
                return metadata.get().getValue();
            } else {
                Optional<V> value = getFromMedium(key);
                transaction.setMetadata(tableName, key, new Transaction.MetaData<>(value));
                return value;
            }
        }
    }

    public Optional<V> get(@Nullable Transaction<String, K, V> transaction, @NotNull K key) {

        if (key == null) {
            throw new NullPointerException("Get error : key is null.");
        }

        if (TransactionUtils.isNull(transaction)) {
            return getFromMedium(key);
        } else {
            return getFromTransaction(transaction, key);
        }
    }

    public Optional<V> get(@NotNull K key) {
        return get(null, key);
    }

    public void set(@NotNull Transaction<String, K, V> transaction, @NotNull K key, @NotNull V value) {

        if (TransactionUtils.isNull(transaction)) {
            throw new TransactionException("Set error : can not set with out exception.");
        }

        if (key == null) {
            throw new NullPointerException("Set error : key is null.");
        }

        Optional<Transaction.MetaData<V>> metadata = transaction.getMetadata(tableName, key);
        if (!metadata.isPresent()) {
            transaction.setMetadata(tableName, key, new Transaction.MetaData<>(getFromMedium(key)));
        }

        Optional<Transaction.TemporaryData<V>> temporaryData = transaction.getTemporaryData(tableName, key);
        if (temporaryData.isPresent()) {
            transaction.setTemporaryData(tableName, key, temporaryData.get().operate(Transaction.Operation.SET, value));
        } else {
            transaction.setTemporaryData(tableName, key ,new Transaction.TemporaryData<>(Transaction.Operation.SET, value));
        }
    }

    public void delete(@NotNull Transaction<String, K, V> transaction, @NotNull K key) {

        if (TransactionUtils.isNull(transaction)) {
            throw new TransactionException("Delete error : can not delete with out exception.");
        }

        if (key == null) {
            throw new NullPointerException("Delete error : key is null.");
        }

        Optional<Transaction.MetaData<V>> metadata = transaction.getMetadata(tableName, key);
        if (!metadata.isPresent()) {
            transaction.setMetadata(tableName, key, new Transaction.MetaData<>(getFromMedium(key)));
        }

        Optional<Transaction.TemporaryData<V>> temporaryData = transaction.getTemporaryData(tableName, key);
        if (temporaryData.isPresent()) {
            transaction.setTemporaryData(tableName, key, temporaryData.get().operate(Transaction.Operation.DEL, null));
        } else {
            transaction.setTemporaryData(tableName, key ,new Transaction.TemporaryData<V>(Transaction.Operation.DEL, null));
        }

    }

    @Override
    public Optional<V> getCache(K key) {
        return cache.get(tableName, key);
    }

    @Override
    public void putCache(K key, V value) {
        cache.put(tableName, key, value);
    }

    @Override
    public void putCache(K key, V value, long expire) {
        cache.put(tableName, key, value, expire);
    }

    @Override
    public void removeCache(K key) {
        cache.remove(tableName, key);
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isEnableCache() {
        return enableCache;
    }
}
