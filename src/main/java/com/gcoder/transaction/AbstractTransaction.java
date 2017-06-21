package com.gcoder.transaction;

import com.gcoder.exception.TransactionException;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Optional;

/**
 * Created by gcoder on 2017/6/16.
 */
public abstract class AbstractTransaction<T, K, V> implements Transaction<T, K, V> {

    private static ThreadLocal<AbstractTransaction> transactionThreadLocal = new ThreadLocal<>();

    private Table<T, K, MetaData<V>> metadata = HashBasedTable.create();
    private Table<T, K, TemporaryData<V>> temporaryDatas = HashBasedTable.create();

    private boolean isRollbackOnly = false;
    private Status status = Status.RUNNING;

    public void setStatus(Status status) {
        this.status = status;
    }

    public Table<T, K, TemporaryData<V>> getTemporaryDatas() {
        return temporaryDatas;
    }

    public Table<T, K, MetaData<V>> getMetadatas() {
        return metadata;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setRollbackOnly() {
        this.isRollbackOnly = true;
    }

    @Override
    public boolean isRollbackOnly() {
        return isRollbackOnly;
    }

    @Override
    public void setMetadata(T tableName, K key, MetaData<V> value) {
        metadata.put(tableName, key, value);
    }

    @Override
    public Optional<MetaData<V>> getMetadata(T tableName, K key) {
        return Optional.ofNullable(metadata.get(tableName, key));
    }

    @Override
    public void setTemporaryData(T table, K key, TemporaryData<V> temporaryData) {
        if (temporaryData.getOperation() == Operation.GET) {
            return;
        }
        temporaryDatas.put(table, key, temporaryData);
    }

    @Override
    public Optional<TemporaryData<V>> getTemporaryData(T table, K key) {
        return Optional.ofNullable(temporaryDatas.get(table, key));
    }

    protected abstract void preBegin();

    @Override
    public void begin() {
        preBegin();
        AbstractTransaction transaction = transactionThreadLocal.get();
        if (transaction != null && transaction.getStatus() == Status.RUNNING) {
            throw new TransactionException("Create com.gcoder.transaction.Transaction error : There is a transaction has not be committed.");
        }
        if (transaction != null) {
            transactionThreadLocal.remove();
        }
        transactionThreadLocal.set(this);
        postBegin();
    }

    protected abstract void postBegin();

    protected abstract void preCommit();

    protected abstract void postCommit();

    protected abstract void preRollback();

    protected abstract void postRollback();

    public static final Optional<Transaction> current() {
        return Optional.ofNullable(transactionThreadLocal.get());
    }

}
