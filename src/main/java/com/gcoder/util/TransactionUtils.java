package com.gcoder.util;

import com.gcoder.transaction.AbstractTransaction;
import com.gcoder.transaction.Transaction;

import java.util.Optional;

/**
 * Created by gcoder on 2017/6/19.
 */
public final class TransactionUtils {

    public static final boolean isNull(Transaction transaction) {
        if (transaction == null) {
            return true;
        }
        return false;
    }

    public static final boolean isNotNull(Transaction transaction) {
        return !isNull(transaction);
    }

    public static final Optional<Transaction> current() {
        return AbstractTransaction.current();
    }

}
