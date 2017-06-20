package com.gcoder.util;

import com.gcoder.transaction.Transaction;

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

}
