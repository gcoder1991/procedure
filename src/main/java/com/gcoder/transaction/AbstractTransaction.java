package com.gcoder.transaction;

import com.gcoder.exception.TransactionException;
import com.gcoder.table.TTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Map;
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

    public abstract Optional<TTable<T, K, V>> getTable(T tableName);

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

    @Override
    public void commit() {
        if (isRollbackOnly()){
            rollback();
            return;
        }
        try {
            preCommit();
            for (T tableName : temporaryDatas.rowKeySet()) {
                Optional<TTable<T, K, V>> ttable = getTable(tableName);
                if (ttable.isPresent()) {
                    Map<K, TemporaryData<V>> tableData = temporaryDatas.row(tableName);
                    for (Map.Entry<K, TemporaryData<V>> keyData : tableData.entrySet()) {
                        switch (keyData.getValue().getOperation()) {
                            case GET:
                                // do nothing
                                break;
                            case SET:
                                ttable.get().realSet(keyData.getKey(), keyData.getValue().getValue());
                                break;
                            case DEL:
                                ttable.get().realDelete(keyData.getKey());
                                break;
                        }
                    }
                } else {
                    throw new TransactionException("Commit error : no such table named ".concat(tableName.toString()));
                }
            }
            setStatus(Status.COMMITTED);
        } catch (Exception e) {
            setRollbackOnly();
            rollback();
        } finally {
            postCommit();
        }
    }

    protected abstract void postCommit();

    protected abstract void preRollback();

    @Override
    public void rollback() {
        try {
            preRollback();
            for (T tableName : temporaryDatas.rowKeySet()) {
                Optional<TTable<T, K, V>> tTable = getTable(tableName);
                if (tTable.isPresent()) {
                    TTable<T, K, V> table = tTable.get();
                    for (Map.Entry<K,TemporaryData<V>> keyData : temporaryDatas.row(tableName).entrySet()) {
                        MetaData<V> metaData = metadata.get(tableName, keyData.getKey());
                        if (metaData == null) {
                            throw new NullPointerException("Rollback error : no metadata.");
                        }
                        switch (keyData.getValue().getOperation()) {
                            case GET:
                                break;
                            case SET:
                                if (metaData.getValue().isPresent()) {
                                    table.realSet(keyData.getKey(), metaData.getValue().get());
                                } else {
                                    table.realDelete(keyData.getKey());
                                }
                                break;
                            case DEL:
                                if (metaData.getValue().isPresent()) {
                                    table.realSet(keyData.getKey(), metaData.getValue().get());
                                }
                                break;
                        }
                    }
                }
            }
            setStatus(Status.ROLLEDBACK);
        } catch (Exception e) {
            throw new TransactionException("Rollback error : failed to rollback", e);
        } finally {
            postRollback();
        }
    }

    protected abstract void postRollback();

    public static final Optional<Transaction> current() {
        return Optional.ofNullable(transactionThreadLocal.get());
    }

}
