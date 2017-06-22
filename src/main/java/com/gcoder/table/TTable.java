package com.gcoder.table;

import com.gcoder.cache.CacheAble;
import com.gcoder.cache.CacheAdapter;
import com.gcoder.database.DatabaseAdapter;
import com.gcoder.exception.TransactionException;
import com.gcoder.transaction.Transaction;
import com.gcoder.util.TransactionUtils;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.Optional;

/**
 * Created by gcoder on 2017/6/17.
 */
public abstract class TTable<T, K, V> implements CacheAble<K, V> {

    private final T tableName;
    private final DatabaseAdapter<T, K, V> database;

    private CacheAdapter<T, K, V> cache;
    private boolean enableCache;

    public TTable(T tableName, DatabaseAdapter<T, K, V> database) {
        this(tableName, database, null, false);
    }

    public TTable(T tableName, DatabaseAdapter<T, K, V> database, CacheAdapter<T, K, V> cache, boolean enableCache) {
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

    private Optional<V> getFromTransaction(@NotNull Transaction<T, K, V> transaction, @NotNull K key) {

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

    public Optional<V> get(@Nullable Transaction<T, K, V> transaction, @NotNull K key) {

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
        Optional<Transaction> transaction = TransactionUtils.current();
        return get(transaction.isPresent() ? transaction.get() : null, key);
    }

    public void set(@NotNull Transaction<T, K, V> transaction, @NotNull K key, @NotNull V value) {

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

    public void delete(@NotNull Transaction<T, K, V> transaction, @NotNull K key) {

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

    public void realSet(@NotNull K key, @NotNull V value) {
        database.set(tableName, key, value);
    }

    public void realDelete(@NotNull K key) {
        database.delete(tableName, key);
    }

    @Override
    public Optional<V> getCache(K key) {
        if (enableCache) {
            return cache.get(tableName, key);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void putCache(K key, V value) {
        if (enableCache) {
            cache.put(tableName, key, value);
        }
    }

    @Override
    public void putCache(K key, V value, long expire) {
        if (enableCache) {
            cache.put(tableName, key, value, expire);
        }
    }

    @Override
    public void removeCache(K key) {
        if (enableCache) {
            cache.remove(tableName, key);
        }
    }

    public T getTableName() {
        return tableName;
    }

    public boolean isEnableCache() {
        return enableCache;
    }
}
